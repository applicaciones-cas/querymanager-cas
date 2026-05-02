package ph.com.guanzongroup.querymanager.views;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.guanzon.appdriver.base.GRiderCAS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * SQLSmartEditor — drop-in replacement for plain TextArea in
 * MainInterfaceController.
 *
 * FEATURES: 1. Auto-capitalize SQL keywords on SPACE / ENTER / SEMICOLON 2.
 * Autocomplete popup — keywords, functions, tables, columns (color-coded) 3.
 * Dot-notation: "tableName." shows that table's columns immediately 4.
 * Ctrl+Space forces suggestions at any time 5. UP/DOWN to navigate, TAB or
 * ENTER to accept, ESC to dismiss 6. Popup NEVER steals focus — typing always
 * goes to the editor
 *
 * USAGE — one-line change inside MainInterfaceController.NewTab():
 *
 *   // Before: TextArea txtField = new TextArea();
 *
 *   // After: SQLSmartEditor txtField = new SQLSmartEditor(poGRider);
 *
 * Everything else (tab.setContent, getSelecTedTextArea, key listeners) stays
 * identical.
 */
public class SQLSmartEditor extends TextArea {

    // ── SQL Keywords ──────────────────────────────────────────────────────────
    private static final Set<String> KEYWORD_SET = new LinkedHashSet<>(Arrays.asList(
            "SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "INSERT", "INTO",
            "VALUES", "UPDATE", "SET", "DELETE", "CREATE", "TABLE", "DROP",
            "ALTER", "ADD", "COLUMN", "INDEX", "VIEW", "JOIN", "LEFT", "RIGHT",
            "INNER", "OUTER", "FULL", "ON", "GROUP", "BY", "ORDER", "HAVING",
            "LIMIT", "OFFSET", "AS", "DISTINCT", "ALL", "UNION", "EXCEPT",
            "INTERSECT", "EXISTS", "IN", "BETWEEN", "LIKE", "IS", "NULL",
            "CASE", "WHEN", "THEN", "ELSE", "END", "IF", "BEGIN", "COMMIT",
            "ROLLBACK", "TRANSACTION", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
            "UNIQUE", "DEFAULT", "AUTO_INCREMENT", "DESCRIBE", "SHOW",
            "TABLES", "DATABASES", "USE", "TRUNCATE", "REPLACE", "CALL",
            "PROCEDURE", "FUNCTION", "TRIGGER", "CONSTRAINT", "CHECK",
            "ASC", "DESC", "WITH", "RECURSIVE", "EXPLAIN"
    ));

    // ── MySQL Functions ───────────────────────────────────────────────────────
    private static final List<String> SQL_FUNCTIONS = Arrays.asList(
            "COUNT(", "SUM(", "AVG(", "MIN(", "MAX(", "COALESCE(", "IFNULL(",
            "NULLIF(", "IF(", "CONCAT(", "CONCAT_WS(", "LENGTH(", "CHAR_LENGTH(",
            "UPPER(", "LOWER(", "TRIM(", "LTRIM(", "RTRIM(", "REPLACE(",
            "SUBSTRING(", "SUBSTR(", "LEFT(", "RIGHT(", "INSTR(", "LOCATE(",
            "LPAD(", "RPAD(", "REPEAT(", "REVERSE(", "SPACE(",
            "NOW()", "CURDATE()", "CURTIME()", "DATE(", "TIME(", "YEAR(",
            "MONTH(", "DAY(", "HOUR(", "MINUTE(", "SECOND(", "DATEDIFF(",
            "DATE_ADD(", "DATE_SUB(", "DATE_FORMAT(", "STR_TO_DATE(",
            "TIMESTAMPDIFF(", "UNIX_TIMESTAMP()", "FROM_UNIXTIME(",
            "ROUND(", "FLOOR(", "CEIL(", "ABS(", "MOD(", "POWER(", "SQRT(",
            "RAND()", "FORMAT(", "CONVERT(", "CAST(",
            "GROUP_CONCAT(", "JSON_OBJECT(", "JSON_ARRAY(", "JSON_EXTRACT(",
            "ROW_NUMBER()", "RANK()", "DENSE_RANK()", "LAG(", "LEAD(",
            "FIRST_VALUE(", "LAST_VALUE(", "NTH_VALUE(", "NTILE("
    );

    // ── State ─────────────────────────────────────────────────────────────────
    private final GRiderCAS poGRider;
    private final Map<String, List<String>> tableColumnMap = new LinkedHashMap<>();
    private final List<String> tableNames = new ArrayList<>();

    // Suggestion state — managed by index, no ListView needed
    private final List<SuggestionItem> suggestions = new ArrayList<>();
    private int selectedIdx = 0;

    // ContextMenu is JavaFX's built-in non-focus-stealing overlay
    private ContextMenu contextMenu;
    private final List<HBox> menuRows = new ArrayList<>();

    private boolean isSuppressing = false;
    private boolean isDarkModeOn = false;
    private boolean triggeredByDot = false;

    public boolean isIsDarkModeOn() {
        return isDarkModeOn;
    }

    public void setIsDarkModeOn(boolean isDarkModeOn) {
        this.isDarkModeOn = isDarkModeOn;
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public SQLSmartEditor(GRiderCAS foGRider) {
        super();
        this.poGRider = foGRider;
        applyStyle();
        buildContextMenu();
        attachListeners();
        Platform.runLater(this::loadSchema);
    }

    // ── Style ─────────────────────────────────────────────────────────────────
    private void applyStyle() {
        if (isDarkModeOn) {
            setStyle(
                    "-fx-font-family: 'Consolas', 'Courier New', monospace;"
                    + "-fx-font-size: 13px;"
                    + "-fx-control-inner-background: #1e1e2e;"
                    + "-fx-text-fill: #cdd6f4;"
                    + "-fx-prompt-text-fill: #585b70;"
            );
            setPromptText(
                    "-- Write your SQL query here  (F9 or Execute to run)\n"
                    + "-- Ctrl+Space for suggestions  |  type '.' after table name for columns"
            );
            setWrapText(false);
        } else {
            setStyle(
                    "-fx-font-family: 'Consolas', 'Courier New', monospace;"
                    + "-fx-font-size: 13px;"
                    + "-fx-control-inner-background: #FFFFFF;"
                    + "-fx-text-fill: #1a1a1a;"
                    + "-fx-prompt-text-fill: #AAAAAA;"
            );
            setPromptText(
                    "-- Write your SQL query here  (F9 or Execute to run)\n"
                    + "-- Ctrl+Space for suggestions  |  type '.' after table name for columns"
            );
            setWrapText(false);
        }
    }

    // ── Schema Loading ────────────────────────────────────────────────────────
    private void loadSchema() {
        if (poGRider == null) {
            return;
        }
        Thread t = new Thread(() -> {
            try {
                ResultSet tables = poGRider.executeQuery("SHOW TABLES");
                List<String> names = new ArrayList<>();
                while (tables.next()) {
                    names.add(tables.getString(1));
                }
                tables.close();

                Platform.runLater(() -> {
                    tableNames.addAll(names);
                    names.forEach(n -> tableColumnMap.put(n.toLowerCase(), new ArrayList<>()));
                });

                for (String tbl : names) {
                    try {
                        List<String> cols = new ArrayList<>();
                        ResultSet rs = poGRider.executeQuery("DESCRIBE `" + tbl + "`");
                        while (rs.next()) {
                            cols.add(rs.getString(1));
                        }
                        rs.close();
                        final List<String> colsCopy = new ArrayList<>(cols);
                        Platform.runLater(() -> tableColumnMap.put(tbl.toLowerCase(), colsCopy));
                    } catch (SQLException ignored) {
                    }
                }
            } catch (SQLException ignored) {
            }
        }, "sql-schema-loader");
        t.setDaemon(true);
        t.start();
    }

    // ── ContextMenu setup ─────────────────────────────────────────────────────
    private void buildContextMenu() {
        contextMenu = new ContextMenu();

        if (isDarkModeOn) {
            contextMenu.setStyle(
                    "-fx-background-color: #313244;"
                    + "-fx-border-color: #89b4fa;"
                    + "-fx-border-width: 1px;"
                    + "-fx-padding: 2;"
            );
        } else {
            contextMenu.setStyle(
                    "-fx-background-color: #FFFFFF;"
                    + "-fx-border-color: #D0D0D0;"
                    + "-fx-border-width: 1px;"
                    + "-fx-padding: 2;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 2);"
            );
        }
        contextMenu.setAutoHide(true);
    }

    /**
     * Rebuild the ContextMenu items from the current suggestions list.
     */
    private void rebuildMenuItems() {
        contextMenu.getItems().clear();
        menuRows.clear();

        for (int i = 0; i < suggestions.size(); i++) {
            SuggestionItem item = suggestions.get(i);
            final int idx = i;

            Label badge = new Label(kindLabel(item.kind));
            badge.setMinWidth(58);
            badge.setStyle(
                    "-fx-font-family: 'Consolas', monospace;"
                    + "-fx-font-size: 10px; -fx-padding: 1 4 1 4;"
                    + "-fx-background-radius: 3;"
                    + badgeStyle(item.kind)
            );

            Label text = new Label(item.display);
            text.setStyle(
                    "-fx-font-family: 'Consolas', monospace;"
                    + "-fx-font-size: 12px;"
                    + textColor(item.kind)
            );
            HBox.setHgrow(text, Priority.ALWAYS);

            HBox row = new HBox(6, badge, text);
            row.setPrefWidth(310);
            row.setStyle(rowStyle(i == 0));
            menuRows.add(row);

            // false = hideOnClick disabled; we close manually after insertion
            CustomMenuItem mi = new CustomMenuItem(row, false);
            mi.setStyle("-fx-padding: 0;");
            mi.setOnAction(e -> {
                selectedIdx = idx;
                applySuggestion();
            });

            contextMenu.getItems().add(mi);
        }
    }

    private void updateHighlight() {
        for (int i = 0; i < menuRows.size(); i++) {
            menuRows.get(i).setStyle(rowStyle(i == selectedIdx));
        }
    }

    private String rowStyle(boolean selected) {
        if (isDarkModeOn) {
            return "-fx-padding: 3 8 3 4; -fx-alignment: CENTER_LEFT;"
                    + (selected ? "-fx-background-color: #45475a;" : "-fx-background-color: transparent;");
        } else {
            return "-fx-padding: 3 8 3 4; -fx-alignment: CENTER_LEFT;"
                    + (selected ? "-fx-background-color: #F4F4F4;" : "-fx-background-color: #FFFFFF;");

        }
    }

    // ── Listeners ─────────────────────────────────────────────────────────────
    private void attachListeners() {

        // KEY PRESSED — intercept before the TextArea sees it
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (contextMenu.isShowing()) {
                switch (e.getCode()) {
                    case DOWN:
                        selectedIdx = Math.min(selectedIdx + 1, suggestions.size() - 1);
                        updateHighlight();
                        e.consume();
                        return;
                    case UP:
                        selectedIdx = Math.max(selectedIdx - 1, 0);
                        updateHighlight();
                        e.consume();
                        return;
                    case TAB:
                        // Accept suggestion; consume so TAB doesn't leave the TextArea
                        applySuggestion();
                        e.consume();
                        return;
                    case ENTER:
                        // Accept suggestion; consume so ENTER doesn't insert a newline
                        if (!suggestions.isEmpty()) {
                            applySuggestion();
                            e.consume();
                            return;
                        }
                        break;
                    case ESCAPE:
                        contextMenu.hide();
                        e.consume();
                        return;
                    default:
                        // Any other key: let it type normally AND update suggestions below
                        break;
                }
            } else {
                // Popup NOT showing — TAB inserts spaces (your original behaviour)
                if (e.getCode() == KeyCode.TAB) {
                    insertText(getCaretPosition(), "   ");
                    e.consume();
                    return;
                }
            }

            // Ctrl+Space → force popup open
            if (e.getCode() == KeyCode.SPACE && e.isControlDown()) {
                e.consume();
                String tok = getCurrentToken();
                showSuggestions(tok.isEmpty() ? "S" : tok); // default to showing SELECT group
            }
        });

        // KEY RELEASED — auto-capitalize and refresh suggestions
        addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (isSuppressing) {
                return;
            }
            KeyCode code = e.getCode();

            // Auto-capitalize on word-terminators
            // ✅ Add e.isControlDown() check — Ctrl+Space should NOT capitalize/hide
            if ((code == KeyCode.SPACE || code == KeyCode.ENTER || code == KeyCode.SEMICOLON)
                    && !e.isControlDown()) {
                autoCapitalizeLastWord();
                contextMenu.hide();
                return;
            }

            // Capitalize on word-terminators
            if (code == KeyCode.SPACE || code == KeyCode.ENTER || code == KeyCode.SEMICOLON) {
                autoCapitalizeLastWord();
                contextMenu.hide(); // fresh word starting, don't carry the popup over
                return;
            }

            if (code == KeyCode.PERIOD) {
                showColumnSuggestions();
                return;
            }

            // Ignore keys that shouldn't retrigger suggestions
            if (code.isModifierKey() || code == KeyCode.ESCAPE
                    || code == KeyCode.LEFT || code == KeyCode.RIGHT
                    || code == KeyCode.HOME || code == KeyCode.END
                    || code == KeyCode.PAGE_UP || code == KeyCode.PAGE_DOWN
                    || code == KeyCode.UP || code == KeyCode.DOWN
                    || code == KeyCode.TAB) {
                return;
            }

            String token = getCurrentToken();
            if (token.length() >= 1) {
                showSuggestions(token);
            } else {
                contextMenu.hide();
            }
        });

        // Hide when editor loses focus
        focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                contextMenu.hide();
            }
        });
    }

    // ── Auto-capitalize ───────────────────────────────────────────────────────
    private void autoCapitalizeLastWord() {
        String text = getText();
        int caret = getCaretPosition();
        if (caret < 2) {
            return;
        }

        // Delimiter is at caret-1; the word occupies [start, caret-1)
        int end = caret - 1;
        int start = end - 1;
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))
                && text.charAt(start - 1) != ';' && text.charAt(start - 1) != '(') {
            start--;
        }
        if (start >= end) {
            return;
        }

        String word = text.substring(start, end);
        String upper = word.toUpperCase();
        if (!word.equals(upper) && KEYWORD_SET.contains(upper)) {
            isSuppressing = true;
            replaceText(start, end, upper);
            positionCaret(caret);
            isSuppressing = false;
        }
    }

    // ── Current token ─────────────────────────────────────────────────────────
    private String getCurrentToken() {
        String text = getText();
        int caret = getCaretPosition();
        if (caret == 0) {
            return "";
        }
        int start = caret - 1;
        while (start > 0) {
            char c = text.charAt(start - 1);
            if (Character.isWhitespace(c) || c == ',' || c == ';'
                    || c == '(' || c == ')' || c == '.') {
                break;  // ✅ stop at dot
            }
            start--;
        }
        return text.substring(start, caret);
    }

    // ── Show suggestions ──────────────────────────────────────────────────────
    private void showSuggestions(String token) {
        triggeredByDot = false;
        String lower = token.toLowerCase();

        List<SuggestionItem> results = new ArrayList<>();

        KEYWORD_SET.stream()
                .filter(k -> k.toLowerCase().startsWith(lower) && !k.equalsIgnoreCase(token))
                .limit(6)
                .map(k -> new SuggestionItem(k, SuggestionKind.KEYWORD))
                .forEach(results::add);

        SQL_FUNCTIONS.stream()
                .filter(f -> f.toLowerCase().startsWith(lower))
                .limit(5)
                .map(f -> new SuggestionItem(f, SuggestionKind.FUNCTION))
                .forEach(results::add);

        tableNames.stream()
                .filter(t -> t.toLowerCase().startsWith(lower))
                .limit(6)
                .map(t -> new SuggestionItem(t, SuggestionKind.TABLE))
                .forEach(results::add);

        tableColumnMap.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.toLowerCase().startsWith(lower))
                .distinct()
                .limit(6)
                .map(c -> new SuggestionItem(c, SuggestionKind.COLUMN))
                .forEach(results::add);

        if (results.isEmpty()) {
            contextMenu.hide();
            return;
        }

        suggestions.clear();
        suggestions.addAll(results);
        selectedIdx = 0;
        rebuildMenuItems();
        positionAndShow();
    }

    private void showColumnSuggestions() {
        String text = getText();
        int dotPos = getCaretPosition() - 1;
        if (dotPos <= 0) {
            return;
        }

        int start = dotPos - 1;
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))
                && text.charAt(start - 1) != ',' && text.charAt(start - 1) != '(') {
            start--;
        }
        String typed = text.substring(start, dotPos); // "a" or "po_master"

        List<String> cols = tableColumnMap.getOrDefault(typed.toLowerCase(), Collections.emptyList());

        if (cols.isEmpty()) {
            Map<String, String> aliases = parseAliases();
            String resolvedTable = aliases.get(typed.toLowerCase());
            if (resolvedTable != null) {
                cols = tableColumnMap.getOrDefault(resolvedTable.toLowerCase(), Collections.emptyList());
            }
        }

        if (cols.isEmpty()) {
            return;
        }

        triggeredByDot = true;
        suggestions.clear();
        cols.forEach(c -> suggestions.add(new SuggestionItem(c, SuggestionKind.COLUMN)));
        selectedIdx = 0;
        rebuildMenuItems();
        positionAndShow();
    }

    private Map<String, String> parseAliases() {
        Map<String, String> aliases = new HashMap<>();
        String text = getText().toLowerCase();

        // Match patterns like: po_master a  |  po_master AS a  |  (po_master) a
        for (String tableName : tableNames) {
            String tbl = tableName.toLowerCase();
            // Look for: tablename<space(s)>[as<space(s)>]alias
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                    tbl + "\\s+(?:as\\s+)?(\\w+)"
            );
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                String alias = m.group(1);
                // Skip SQL keywords accidentally captured as alias
                if (!KEYWORD_SET.contains(alias.toUpperCase())) {
                    aliases.put(alias, tableName); // e.g. "a" -> "po_master"
                }
            }
        }
        return aliases;
    }

    private void positionAndShow() {
        Platform.runLater(() -> {
            if (getScene() == null || getScene().getWindow() == null) {
                return;
            }

            // Approximate Y by counting newlines up to the caret
            String upToCaret = getText(0, Math.min(getCaretPosition(), getText().length()));
            int lineCount = upToCaret.split("\n", -1).length;
            double lineH = getFont().getSize() * 1.65;

            double screenX = localToScreen(getBoundsInLocal()).getMinX() + 14;
            double screenY = localToScreen(getBoundsInLocal()).getMinY() + (lineCount * lineH);

            // Always hide first to reposition correctly
            contextMenu.hide();
            contextMenu.show(this, screenX, screenY);
        });
    }

    // ── Apply selected suggestion ─────────────────────────────────────────────
    private void applySuggestion() {
        if (suggestions.isEmpty() || selectedIdx >= suggestions.size()) {
            contextMenu.hide();
            return;
        }
        SuggestionItem chosen = suggestions.get(selectedIdx);

        String text = getText();
        int caret = getCaretPosition();

        int start = caret;
        while (start > 0) {
            char c = text.charAt(start - 1);
            if (Character.isWhitespace(c) || c == ',' || c == ';'
                    || c == '(' || c == ')' || c == '.') {
                break;
            }
            start--;
        }

        isSuppressing = true;
        replaceText(start, caret, chosen.display);
        positionCaret(start + chosen.display.length());
        isSuppressing = false;

        triggeredByDot = false;
        contextMenu.hide();
    }

    private String kindLabel(SuggestionKind kind) {
        switch (kind) {
            case KEYWORD:
                return "keyword";
            case FUNCTION:
                return "function";
            case TABLE:
                return "table";
            default:
                return "column";
        }
    }

    private String badgeStyle(SuggestionKind kind) {

        if (isDarkModeOn) {
            switch (kind) {
                case KEYWORD:
                    return "-fx-background-color: #1e3a5f; -fx-text-fill: #89b4fa;";
                case FUNCTION:
                    return "-fx-background-color: #1a3a2a; -fx-text-fill: #a6e3a1;";
                case TABLE:
                    return "-fx-background-color: #3a2e0a; -fx-text-fill: #f9e2af;";
                default:
                    return "-fx-background-color: #2a1a3a; -fx-text-fill: #cba6f7;";
            }
        } else {
            switch (kind) {
                case KEYWORD:
                    return "-fx-background-color: #DDEEFF; -fx-text-fill: #1565C0;";
                case FUNCTION:
                    return "-fx-background-color: #DDFFEE; -fx-text-fill: #2E7D32;";
                case TABLE:
                    return "-fx-background-color: #FFF8DD; -fx-text-fill: #E65100;";
                default:
                    return "-fx-background-color: #F3DDFF; -fx-text-fill: #6A1B9A;";
            }
        }
    }

    private String textColor(SuggestionKind kind) {

        if (isDarkModeOn) {
            switch (kind) {
                case KEYWORD:
                    return "-fx-text-fill: #89b4fa;";
                case FUNCTION:
                    return "-fx-text-fill: #a6e3a1;";
                case TABLE:
                    return "-fx-text-fill: #f9e2af;";
                default:
                    return "-fx-text-fill: #cba6f7;";

            }
        } else {
            switch (kind) {
                case KEYWORD:
                    return "-fx-text-fill: #1565C0;";
                case FUNCTION:
                    return "-fx-text-fill: #2E7D32;";
                case TABLE:
                    return "-fx-text-fill: #E65100;";
                default:
                    return "-fx-text-fill: #6A1B9A;";
            }
        }
    }

// ── Inner types ───────────────────────────────────────────────────────────
    enum SuggestionKind {
        KEYWORD, FUNCTION, TABLE, COLUMN
    }

    static class SuggestionItem {

        final String display;
        final SuggestionKind kind;

        SuggestionItem(String display, SuggestionKind kind) {
            this.display = display;
            this.kind = kind;
        }
    }
}
