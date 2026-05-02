package ph.com.guanzongroup.querymanager.views;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.F8;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomTextField;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import ph.com.guanzongroup.querymanager.cas.base.GQuery;

public class MainInterfaceController implements Initializable {

    @FXML
    private Label lblIpAddress;
    @FXML
    private MenuItem mnuSave;
    @FXML
    private MenuItem mnuSaveAs;
    @FXML
    private SplitPane splPane;
    @FXML
    private ToggleButton btnToggle;
    @FXML
    private Button btnUser;
    @FXML
    private TextField txtField00;
    @FXML
    private CheckBox chk00;
    @FXML
    private CheckMenuItem chkMobile;
    @FXML
    private CheckMenuItem chkMotor;
    @FXML
    private TextField txtField03;
    @FXML
    private FontAwesomeIconView faToggle;
    @FXML
    private Button btnNewEditor;
    @FXML
    private Button btnClear;
    @FXML
    private ToggleButton btnRestoreDown;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btnClose;
    @FXML
    private MenuButton chkDivision;
    @FXML
    private CheckMenuItem chkAuto;
    @FXML
    private CheckMenuItem chkHospitality;
    @FXML
    private TableView tblResult;
    @FXML
    private Label lblResult;
    @FXML
    private Button btnExcecute;
    @FXML
    private Label lblUser;
    @FXML
    private FontAwesomeIconView faCompress;
    @FXML
    private Label lblDate;
    @FXML
    private MenuItem mnuOpen;
    @FXML
    private MenuItem mnuNewEditor;
    @FXML
    private MenuItem mnuCloseTab;
    @FXML
    private MenuItem mnuExit;
    @FXML
    private TabPane tabPane;
    @FXML
    private TreeView treeView;
    @FXML
    private Button btnDatabase;
    @FXML
    private Tooltip tpSideMenu;
    @FXML
    private Tooltip tpRestore;
    @FXML
    private MenuItem mnuAbout;
    @FXML
    private Button btnClearCurrent;
    @FXML
    private SplitPane splHorizontal;
    @FXML
    private CustomTextField txtSearch;
    @FXML
    private AnchorPane acHorizontal;
    @FXML
    private CustomTextField txtField01;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblLength;
    @FXML
    private Label lblLines;
    @FXML
    private Label lblWords;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblDay;
    @FXML
    private Label lblSeconds;

    Node vertiCalPane;
    Node horizontalPane;
    public AnchorPane acTiltleBar;
    private GQuery p_oTrans;
    private GRiderCAS poGRider;
    private double xOffset = 0;
    private double yOffset = 0;
    private String psOldRecord;
    private String DivValue;
    private String psValue = "";
    private final String pxeModuleName = "Guanzon Query Manager";
    private ObservableList<ObservableList> data;
    final MenuItem copyAllRow = new MenuItem("Copy All Rows To Clipboard\t Ctrl + A");
    final MenuItem copyRow = new MenuItem("Copy Current Row To Clipboard\t Ctrl + R");
    final MenuItem copyCell = new MenuItem("Copy Cell Data Clipboard\t\t Ctrl + C");
    final MenuItem closeCurrentTab = new MenuItem("Close Tab\t ALT + L");
    final MenuItem copyColumn = new MenuItem("Copy");
    final MenuItem copyTable = new MenuItem("Copy Table Columns");
    final MenuItem exportExcel = new MenuItem("Export data to Excel");
    final ContextMenu treeContext = new ContextMenu(copyColumn, copyTable);
    final ContextMenu contextMenu = new ContextMenu(copyAllRow, copyRow, copyCell, exportExcel);
    final ContextMenu closeTab = new ContextMenu(closeCurrentTab);
    final ClipboardContent rowValue = new ClipboardContent();
    final ClipboardContent allRow = new ClipboardContent();
    final KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    final KeyCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    final KeyCombination ctrlA = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
    private final Node rootIcon = new ImageView(new Image(
            getClass().getResourceAsStream("/ph/com/guanzongroup/querymanager/cas/images/database.png")));
    private static Image search = new Image("/ph/com/guanzongroup/querymanager/cas/images/search.png");
    final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private boolean pbLoaded = false;
    public AnchorPane rootPane;
    public int numTabs = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (poGRider == null) {
                ShowMessageFX.Warning("GhostRider Application not set..", pxeModuleName, "Please inform MIS department.");
                System.exit(0);
            }

            p_oTrans = new GQuery();
            p_oTrans.setGRiderX(poGRider);
            System.out.println(poGRider.Encrypt(poGRider.getUserID(), "sysmgr"));
//            p_oTrans.setConfig("/config/queryfx", "GCasys_DBF");

            vertiCalPane = splPane.getItems().get(1);
            splPane.setDividerPosition(0, 0.2055);

            horizontalPane = splPane.getItems().get(1);
            splHorizontal.setDividerPosition(0, 0.7600);

            btnExcecute.setOnAction(this::cmdButton_Click);
            btnClose.setOnAction(this::cmdButton_Click);
            btnMinimize.setOnAction(this::cmdButton_Click);
            btnClear.setOnAction(this::cmdButton_Click);
            btnUser.setOnAction(this::cmdButton_Click);
            btnNewEditor.setOnAction(this::cmdButton_Click);
            btnDatabase.setOnAction(this::cmdButton_Click);
            btnClearCurrent.setOnAction(this::cmdButton_Click);
            tabPane.setOnMouseClicked(this::tab_Click);

            btnToggle.setOnAction(this::toggleButton_Click);
            btnRestoreDown.setOnAction(this::toggleButton_Click);

            chkMobile.setOnAction(this::menuItem_Click);
            chkMotor.setOnAction(this::menuItem_Click);
            chkAuto.setOnAction(this::menuItem_Click);
            chkHospitality.setOnAction(this::menuItem_Click);
            mnuSaveAs.setOnAction(this::menuItem_Click);
            mnuOpen.setOnAction(this::menuItem_Click);
            mnuNewEditor.setOnAction(this::menuItem_Click);
            mnuCloseTab.setOnAction(this::menuItem_Click);
            mnuAbout.setOnAction(this::menuItem_Click);
            mnuSave.setOnAction(this::menuItem_Click);
            mnuExit.setOnAction(this::menuItem_Click);

            txtField00.focusedProperty().addListener(txtField_Focus);
            txtField01.focusedProperty().addListener(txtField_Focus);
            txtField03.focusedProperty().addListener(txtField_Focus);

            txtField00.setOnKeyPressed(this::txtField_KeyPressed);
            txtField01.setOnKeyPressed(this::txtField_KeyPressed);
            txtField03.setOnKeyPressed(this::txtField_KeyPressed);

            tblResult.setColumnResizePolicy((param) -> true);
            Platform.runLater(() -> customResize(tblResult));

            txtSearch.setLeft(new ImageView(search));
            txtField01.setLeft(new ImageView(search));
            clearFields();
            loadTransaction();
            NewTab();
            getTime();
            pbLoaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public TextArea getSelecTedTextArea() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        TextArea txtArea = (TextArea) selectedTab.getContent();

        return txtArea;
    }

    public Tab getTabTitle() {
        Tab selectedtab = tabPane.getSelectionModel().getSelectedItem();
        return selectedtab;
    }

    public void setAllTextAreaKeyPressed() {
        for (Tab tab : tabPane.getTabs()) {
            TextArea txtArea = (TextArea) tab.getContent();
            txtArea.setOnKeyPressed(this::txtArea_KeyPressed);
            txtArea.setOnKeyReleased(this::txtArea_KeyRealeased);
        }
    }

    public int getCharAtPosition() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        TextArea txtArea = (TextArea) selectedTab.getContent();
        return txtArea.getCaretPosition();
    }

    public void loadTreeView() {
        ResultSet tables;
        ResultSet columns;
        TreeItem<String> MainTreeItem = new TreeItem<>("GCASys_DBF", rootIcon);
        ObservableList<TreeItem<String>> firstLevel = FXCollections.observableArrayList();

        MainTreeItem.setExpanded(true);
        String lsQuery = "SHOW TABLES";

        try {
            tables = poGRider.executeQuery(lsQuery);

            while (tables.next()) {
                TreeItem<String> item = new TreeItem<>(tables.getString(1));
                MainTreeItem.getChildren().addAll(item);

                firstLevel.add(item);

                String lsSQL = "DESCRIBE `" + tables.getString(1) + "`";
                columns = poGRider.executeQuery(lsSQL);
                try {
                    while (columns.next()) {
                        TreeItem<String> item2 = new TreeItem<>(columns.getString(1));
                        item.getChildren().addAll(item2);
                    }
                    columns.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            tables.close();
        } catch (SQLException ex) {
            Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        FilteredList<TreeItem<String>> filteredList = new FilteredList<>(firstLevel, item -> true);
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            String filter = txtSearch.getText().toLowerCase();
            if (filter.isEmpty()) {
                return item -> true;
            }
            return item -> item.getValue().startsWith(filter.toLowerCase());
        }, txtSearch.textProperty()));

        Bindings.bindContent(MainTreeItem.getChildren(), filteredList);
        treeView.setRoot(MainTreeItem);
        treeView.setShowRoot(true);

        treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {

                treeView.setOnContextMenuRequested(y -> treeContext.show(treeView, y.getScreenX(), y.getScreenY()));

                copyColumn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Utilities.CopyColumn(treeView);
                    }
                });

                copyTable.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Utilities.CopyAllColumns(treeView);
                    }
                });
            } else {
                //any other click cause hiding menu 
                treeContext.hide();
            }
        });
    }

    public void displayitem() {
        switch (getCheckedItem()) {
            case 1:
                chkDivision.setText(chkMobile.getText());
                break;
            case 2:
                chkDivision.setText(chkMotor.getText());
                break;
            case 3:
                chkDivision.setText(chkAuto.getText());
                break;
            case 4:
                chkDivision.setText(chkHospitality.getText());
                break;
            default:
                chkDivision.setText("");
                break;
        }
    }

    public void setGRider(GRiderCAS foGRider) {
        this.poGRider = foGRider;
    }

    public void menuItem_Click(ActionEvent event) {
        String mnuItem = ((MenuItem) event.getSource()).getId().toLowerCase();

        switch (mnuItem) {
            case "chkmobile":
            case "chkmotor":
            case "chkauto":
            case "chkhospitality":
                displayitem();
                break;
            case "mnusaveas":
                if (!getSelecTedTextArea().getText().equals("")) {
                    Utilities.saveAs(getSelecTedTextArea().getText(), getTabTitle());
                } else {
                    ShowMessageFX.Error(null, pxeModuleName, "No query to be save!.");
                }
                break;
            case "mnuopen":
                Utilities.readFile(getSelecTedTextArea(), getTabTitle());
                break;
            case "mnuneweditor":
                NewTab();
                break;
            case "mnuclosetab":
                isTabOk();
                break;
            case "mnuabout":
                try {
                LoadScene("/ph/com/guanzongroup/querymanager/cas/views/About.fxml");
            } catch (IOException ex) {
                Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
            case "mnusave":
                if (getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                    Utilities.saveAs(getSelecTedTextArea().getText(), getTabTitle());
                } else if (!getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                    saveOpenFile(getSelecTedTextArea().getText());
                }
                break;
            case "mnuexit":
                if (ShowMessageFX.OkayCancel("Do you want to exit?", pxeModuleName, "All changes made will not save!") == true) {
                    System.exit(0);
                    break;
                }
        }

    }

    public void saveOpenFile(String content) {
        String filePathString = getTabTitle().getText();
        try {
            File newTextFile = new File(filePathString);

            try (FileWriter fw = new FileWriter(newTextFile)) {
                fw.write(content);
                fw.close();
            }

        } catch (IOException e) {
            System.err.print(e);
        }
    }

    public void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        if (event.getCode() == F3 || event.getCode() == ENTER) {
            try {
                switch (lnIndex) {
                    case 0:
                        if (p_oTrans.searchBranch(lsValue, false)) {
                            txtField00.setText((String) p_oTrans.getBranchName());
                            lblIpAddress.setText((String) p_oTrans.getBranchIP());
                            psOldRecord = p_oTrans.getBranchName();
                        } else if (p_oTrans.searchBranch(lsValue, true)) {
                            txtField00.setText((String) p_oTrans.getBranchName());
                            lblIpAddress.setText((String) p_oTrans.getBranchIP());
                            psOldRecord = p_oTrans.getBranchName();
                        } else {
                            ShowMessageFX.Error(null, pxeModuleName, p_oTrans.getMessage());
                        }
                        break;
                    case 1:
                        if (p_oTrans.searchDestination(lsValue, false) == true) {
                            txtField01.setText((String) p_oTrans.getDestination());
                        } else if (p_oTrans.searchDestination(lsValue, true) == true) {
                            txtField01.setText((String) p_oTrans.getDestination());
                        } else {
                            ShowMessageFX.Error(null, pxeModuleName, p_oTrans.getMessage());
                        }
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ShowMessageFX.Error(e.getMessage(), "Error", null);
            }
        }
        if (event.getCode() == DOWN || event.getCode() == ENTER) {
            CommonUtils.SetNextFocus(txtField);
        }
        if (event.getCode() == UP) {
            CommonUtils.SetPreviousFocus(txtField);
        }
    }

    public void txtArea_KeyPressed(KeyEvent event) {
        String[] lsValue = getSelecTedTextArea().getText().replace("	", " ").split(";");
        int[] charNum = new int[lsValue.length];
        int sum = 0;

        if (event.getCode() == KeyCode.F9) {
            try {
                for (int i = 0; i < lsValue.length; i++) {
                    charNum[i] = lsValue[i].length() + 1;
                }

                for (int i = 0; i < charNum.length; i++) {
                    sum += charNum[i];
                    if (sum >= getCharAtPosition()) {
                        if (!txtField01.getText().isEmpty() && !getDivisionValue().equals("0")) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Destination Branch and Division is not empty!");
                            if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to proceed with the division") == true) {
                            } else {
                                return;
                            }
                        }

                        if (p_oTrans.execute(lsValue[i], chk00.isSelected(), getDivisionValue()) == true) {
                            if (p_oTrans.getCount() >= 0) {
                                if (p_oTrans.getResult() != null) {
                                    resulSetTable();
                                }

                                if (lsValue[i].toLowerCase().contains("Select".toLowerCase())
                                        || lsValue[i].toLowerCase().contains("Describe".toLowerCase())
                                        || lsValue[i].toLowerCase().contains("Show".toLowerCase())) {
                                    lblResult.setText(MiscUtil.RecordCount(p_oTrans.getResult()) + " row(s) in set.");
                                } else {
                                    tblResult.getColumns().clear();
                                    ShowMessageFX.Information(null, pxeModuleName, "Query OK" + " " + p_oTrans.getCount() + " " + "row(s) affected.");
                                }
                                if (!p_oTrans.getSolutionMessage().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to execute auto solution query?") == true) {
                                        p_oTrans.executeSolution();
                                    }
                                }
                            }
                        } else {
                            ShowMessageFX.Error(p_oTrans.getMessage(), pxeModuleName, "Please inform SEG!");
                            return;
                        }

                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ShowMessageFX.Error(e.getMessage(), "Error", null);
            }
        }

        if (event.getCode() == KeyCode.TAB) {
            getSelecTedTextArea().insertText(getSelecTedTextArea().getCaretPosition(), "   ");
            event.consume();
        } else if (ctrlS.match(event)) {
            if (getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                Utilities.saveAs(getSelecTedTextArea().getText(), getTabTitle());
            } else if (!getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                saveOpenFile(getSelecTedTextArea().getText());
            }
        }
    }

    public void txtArea_KeyRealeased(KeyEvent event) {
        lblLength.setText(getSelecTedTextArea().getText().length() + "");
        lblLines.setText((getSelecTedTextArea().getText() + "|").split("\n").length + "");
        lblWords.setText(getSelecTedTextArea().getText().trim().split("\\s+").length + "");
    }

    private void toggleButton_Click(ActionEvent event) {
        String toggleButton = ((ToggleButton) event.getSource()).getId().toLowerCase();
        switch (toggleButton) {
            case "btntoggle":
                if (btnToggle.isSelected()) {
                    splPane.setDividerPosition(0, 0);
                    faToggle.setGlyphName("TOGGLE_OFF");
                    tpSideMenu.setText("Open Side Menu");
                } else {
                    faToggle.setGlyphName("TOGGLE_ON");
                    tpSideMenu.setText("Hide Side Menu");
                    splPane.setDividerPosition(0, 0.1500);
                }
                break;
            case "btnrestoredown":
                Stage stage = (Stage) rootPane.getScene().getWindow();
                if (btnRestoreDown.isSelected()) {
                    faCompress.setGlyphName("EXPAND");
                    tpRestore.setText("Maximize");
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    stage.setY(bounds.getMinY());
                    stage.setX(bounds.getMinX());
                    stage.setWidth(1024);
                    stage.setHeight(740);
                    stage.centerOnScreen();
                } else {
                    faCompress.setGlyphName("COMPRESS");
                    tpRestore.setText("Restore Down");
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    stage.setY(bounds.getMinY());
                    stage.setX(bounds.getMinX());
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                    stage.centerOnScreen();
                }

        }

    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId().toLowerCase();
        String[] lsValue = getSelecTedTextArea().getText().replace("	", " ").split(";");
        int[] charNum = new int[lsValue.length];
        int sum = 0;

        switch (lsButton) {
            case "btnclose":  //Close
                isTabOk();
                break;
            case "btnminimize": //Minimize
                CommonUtils.minimizeStage(btnMinimize);
                break;
            case "btnclear": //Clear
                clearFields();
                break;
            case "btnexcecute": //Execute
                try {
                for (int i = 0; i < lsValue.length; i++) {
                    charNum[i] = lsValue[i].length() + 1;
                }

                for (int i = 0; i < charNum.length; i++) {
                    sum += charNum[i];
                    if (sum >= getCharAtPosition()) {
                        if (!getSelecTedTextArea().equals("") && !getDivisionValue().equals("0")) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Destination Branch and Division is not empty!");
                            if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to proceed with the division") == false) {
                                return;
                            }
                        }

                        if (p_oTrans.execute(lsValue[i], chk00.isSelected(), getDivisionValue()) == true) {
                            if (p_oTrans.getCount() >= 0) {
                                if (p_oTrans.getResult() != null) {
                                    resulSetTable();
                                }

                                if (lsValue[i].toLowerCase().contains("Select".toLowerCase())
                                        || lsValue[i].toLowerCase().contains("Describe".toLowerCase())
                                        || lsValue[i].toLowerCase().contains("Show".toLowerCase())) {
                                    lblResult.setText(MiscUtil.RecordCount(p_oTrans.getResult()) + " row(s) in set.");
                                } else {
                                    tblResult.getColumns().clear();
                                    ShowMessageFX.Information(null, pxeModuleName, "Query OK" + " " + p_oTrans.getCount() + " " + "row(s) affected.");
                                }

                                if (!p_oTrans.getSolutionMessage().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to execute auto solution query?") == true) {
                                        p_oTrans.executeSolution();
                                    }
                                }
                            }
                        } else {
                            ShowMessageFX.Error(p_oTrans.getMessage(), pxeModuleName, "Please inform SEG!");
                            return;
                        }
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ShowMessageFX.Error(e.getMessage(), "Error", null);
            }
            break;
            case "btnclearcurrent":
                getSelecTedTextArea().setText("");
                break;
            case "btnuser":
                ShowMessageFX.Information(null, pxeModuleName, "Upcoming feature!");
                /*Platform.runLater(new Runnable() {
                      public void run() {
                          try{
                              new SysUserFX().start(new Stage());
                          }catch(Exception e){
                              System.err.println(e);
                          }
                          
                      }
                   });*/
                break;
            case "btndatabase":
                loadTreeView();
                break;
            case "btnneweditor":
                NewTab();
                break;
        }
    }

    public void NewTab() {
        SQLSmartEditor txtField = new SQLSmartEditor(poGRider);

        numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Query Editor " + (numTabs + 1));
        tab.setContent(txtField);
        tab.setClosable(false);
        tabPane.getTabs().addAll(tab);
        tabPane.getSelectionModel().select(tab);
        txtField.requestFocus();
        setAllTextAreaKeyPressed();
    }

    private void tab_Click(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            tabPane.setOnContextMenuRequested(e -> closeTab.show(tabPane, e.getScreenX(), e.getScreenY()));

            closeCurrentTab.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    isTabOk();
                }
            });

        }
        closeTab.hide();
    }

    private void resulSetTable() {
        lblResult.setText("");
        tblResult.getColumns().clear();

        try {
            data = FXCollections.observableArrayList();

            for (int i = 0; i < p_oTrans.getResult().getMetaData().getColumnCount(); i++) {
                //create of column
                final int j = i;

                TableColumn col = new TableColumn(p_oTrans.getResult().getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        if (param.getValue().get(j) != null) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        } else {
                            return new SimpleStringProperty("(NULL)");
                        }
                    }
                });

                txtField03.setText(p_oTrans.getResult().getMetaData().getTableName(i + 1));
                tblResult.getColumns().addAll(col);
                col.setSortable(false);
            }
            //Data added to ObservableList 
            while (p_oTrans.getResult().next()) {
                try {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= p_oTrans.getResult().getMetaData().getColumnCount(); i++) {
                        //Iterate Column
                        //row.add(p_oTrans.getResult().getString(i));
                        row.add(String.valueOf(p_oTrans.getResult().getObject(i)));
                    }
                    data.add(row);
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                }
            }
            //ADDED TO TableView
            tblResult.setItems(data);
            p_oTrans.getResult().first();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String getDivisionValue() {
        int total, val1, val2, val3, val4;
        val1 = (chkMobile.isSelected() ? 1 : 0);
        val2 = (chkMotor.isSelected() ? 2 : 0);
        val3 = (chkAuto.isSelected() ? 4 : 0);
        val4 = (chkHospitality.isSelected() ? 8 : 0);

        total = val1 + val2 + val3 + val4;
        DivValue = String.valueOf(total);

        return DivValue;
    }

    private void loadTransaction() {
        ResultSet name;
        txtField00.setText((String) p_oTrans.getBranchName());
        lblIpAddress.setText((String) p_oTrans.getBranchIP());
        lblUser.setText(poGRider.getLogName());
    }

    private void clearFields() {
        try {
            txtField01.setText("");
            txtField03.setText("");
            lblResult.setText("");
            chkDivision.setText("");
            p_oTrans.searchDestination("", true);

            tblResult.getColumns().clear();
            chkMobile.selectedProperty().setValue(false);
            chkMotor.selectedProperty().setValue(false);
            chkAuto.selectedProperty().setValue(false);
            chkHospitality.selectedProperty().setValue(false);
            chk00.selectedProperty().setValue(true);

            for (Tab tab : tabPane.getTabs()) {
                TextArea txtArea = (TextArea) tab.getContent();
                txtArea.setText("");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getCheckedItem() {
        int index00, index01, index02, index03;
        index00 = (chkMobile.isSelected() ? 1 : 0);
        index01 = (chkMotor.isSelected() ? 2 : 0);
        index02 = (chkAuto.isSelected() ? 4 : 0);
        index03 = (chkHospitality.isSelected() ? 8 : 0);

        int[] lnVal;
        lnVal = new int[]{index00, index01, index02, index03};
        int lnLow = 0;
        for (int lnCtr = 0; lnCtr <= lnVal.length - 1; lnCtr++) {
            if (lnVal[lnCtr] != 0) {
                if (lnLow == 0) {
                    lnLow = lnVal[lnCtr];
                } else {
                    if (lnLow > lnVal[lnCtr]) {
                        lnLow = lnVal[lnCtr];
                    }
                }
            }
        }
        return lnLow;
    }

    public void customResize(TableView<?> view) {
        AtomicLong width = new AtomicLong();
        view.getColumns().forEach(col -> {
            width.addAndGet((long) col.getWidth());
        });
        double tableWidth = view.getWidth();

        if (tableWidth > width.get()) {
            view.getColumns().forEach(col -> {
                col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size()));
            });
        }
    }

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        if (lsValue == null) {
            return;
        }

        if (!nv) {
            /*Lost Focus*/
            try {
                switch (lnIndex) {
                    case 0:
                        if (!txtField.getText().equals(psOldRecord)) {
                            txtField00.setText((String) p_oTrans.getBranchName());
                            lblIpAddress.setText((String) p_oTrans.getBranchIP());
                        }
                        break;
                    case 1:
                        if (txtField01.getText().isEmpty()) {
                            p_oTrans.searchDestination("", true);
                        }
                        break;
                    case 3:
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ph.com.guanzongroup.querymanager.views.MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            txtField.selectAll();
        }
    };

    @FXML
    private void TableClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            tblResult.setOnContextMenuRequested(e -> contextMenu.show(tblResult, e.getScreenX(), e.getScreenY()));

            copyRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Utilities.copyRowValue(tblResult);
                }
            });

            copyAllRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Utilities.copyTableValue(tblResult);
                }
            });

            copyCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Utilities.copyCellValue(tblResult);
                }
            });

            exportExcel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (Utilities.ExportToExcel(tblResult) == true) {
                        ShowMessageFX.Information(null, pxeModuleName, "Data successfully exported!");
                    } else {
                        ShowMessageFX.Information(null, pxeModuleName, "Exporting of data aborted!");
                    };
                }
            });
        }
        contextMenu.hide();
    }

    private void getTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try {
                Calendar cal = Calendar.getInstance();
                int second = cal.get(Calendar.SECOND);

                Date date;
                date = poGRider.getServerDate();

                String fmtHourMin = "h:mm";
                String fmtSeconds = "ss";
                String fmtCurrentDay = "EEEE";

                DateFormat dftHourMin = new SimpleDateFormat(fmtHourMin);
                DateFormat dftSeconds = new SimpleDateFormat(fmtSeconds);
                DateFormat dftCurrentDay = new SimpleDateFormat(fmtCurrentDay);

                String formattedTime = dftHourMin.format(date);
                String formattedSec = dftSeconds.format(date);
                String formattedDay = dftCurrentDay.format(date);

                lblTime.setText(formattedTime);
                lblSeconds.setText(formattedSec);
                lblDay.setText(formattedDay);
                lblDate.setText(SQLUtil.dateFormat(date, SQLUtil.FORMAT_LONG_DATE));
            } catch (SQLException ex) {
                Logger.getLogger(MainInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void SQL_Grab(KeyEvent event) throws IOException {
        contextMenu.hide();
        int lnRow = 1 + tblResult.getSelectionModel().getSelectedIndex();
        String lsValue = txtField03.getText();
        if (event.getCode() == F8) {
            if (!lsValue.isEmpty() && !getSelecTedTextArea().getText().isEmpty()) {
                psValue = p_oTrans.grabSQL(lsValue, lnRow);
                if (!psValue.equals("")) {
                    LoadScene("/ph/com/guanzongroup/querymanager/cas/views/SqlGrab.fxml");
                }
            } else {
                ShowMessageFX.Error(null, pxeModuleName, "Table Name/Sql Statement should not be empty!");
            }
        }

        if (ctrlC.match(event)) {
            Utilities.copyCellValue(tblResult);
        } else if (ctrlA.match(event)) {
            Utilities.copyTableValue(tblResult);
        } else if (ctrlR.match(event)) {
            Utilities.copyRowValue(tblResult);
        }
    }

    public Parent LoadScene(String foURL) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(foURL));

        Object fxObj = getController(foURL);
        fxmlLoader.setController(fxObj);

        Parent parent;
        parent = fxmlLoader.load();

        Stage stage = new Stage();
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);

            }
        });

        Scene scene = new Scene(parent);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.showAndWait();

        return parent;
    }

    private Object getController(String fsValue) {
        switch (fsValue) {
            case "/ph/com/guanzongroup/querymanager/cas/views/SqlGrab.fxml":
                SqlGrabController loSqlGrabber = new SqlGrabController();
                loSqlGrabber.setGRider(poGRider);
                loSqlGrabber.setEnteredText(psValue);

                return loSqlGrabber;

            case "/ph/com/guanzongroup/querymanager/cas/views/Help.fxml":
                HelpController loHelp = new HelpController();
                loHelp.setGRider(poGRider);

                return loHelp;

            case "/ph/com/guanzongroup/querymanager/cas/views/About.fxml":
                AboutController loAbout = new AboutController();
                loAbout.setGRider(poGRider);

                return loAbout;

            default:
                return null;
        }
    }

    public void isTabOk() {
        if (tabPane.getTabs().size() != 1) {
            if (getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                ButtonType lnButton = Utilities.ShowYesNoCancel("The content of this tab has been changed.", pxeModuleName, "Do you want to save changes?");
                if (lnButton == ButtonType.YES) {
                    if (Utilities.saveAs(getSelecTedTextArea().getText(), getTabTitle()) == true) {
                        Utilities.closeCurrentTab(tabPane);
                    };
                } else if (lnButton == ButtonType.NO) {
                    Utilities.closeCurrentTab(tabPane);
                } else {
                    return;
                }
            } else if (!getTabTitle().getText().contains("Query Editor")) {
                ButtonType lnButton = Utilities.ShowYesNoCancel("The content of this tab has been changed.", pxeModuleName, "Do you want to save changes?");
                if (lnButton == ButtonType.YES) {
                    saveOpenFile(getSelecTedTextArea().getText());
                    Utilities.closeCurrentTab(tabPane);
                } else if (lnButton == ButtonType.NO) {
                    Utilities.closeCurrentTab(tabPane);
                } else {
                    return;
                }
            } else {
                Utilities.closeCurrentTab(tabPane);
            }
        } else {
            if (getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                ButtonType lnButton = Utilities.ShowYesNoCancel("The content of this tab has been changed.", pxeModuleName, "Do you want to save?");
                if (lnButton == ButtonType.YES) {
                    if (Utilities.saveAs(getSelecTedTextArea().getText(), getTabTitle()) == true) {
                        CommonUtils.closeStage(btnClose);
                    };
                } else if (lnButton == ButtonType.NO) {
                    CommonUtils.closeStage(btnClose);
                } else {
                    return;
                }
            } else if (!getTabTitle().getText().contains("Query Editor") && !getSelecTedTextArea().getText().equals("")) {
                ButtonType lnButton = Utilities.ShowYesNoCancel("The content of this tab has been changed.", pxeModuleName, "Do you want to save?");
                if (lnButton == ButtonType.YES) {
                    saveOpenFile(getSelecTedTextArea().getText());
                    CommonUtils.closeStage(btnClose);
                } else if (lnButton == ButtonType.NO) {
                    CommonUtils.closeStage(btnClose);
                } else {
                    return;
                }
            } else {
                CommonUtils.closeStage(btnClose);
            }
        }

    }

    public void startTask() {
        // Create a Runnable
        Runnable task = new Runnable() {
            public void run() {
                runTask();
            }
        };

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
        if (!backgroundThread.isAlive()) {
            ShowMessageFX.Error(null, null, "Done");
        };
    }

    public void runTask() {
        for (int i = 1; i <= 10; i++) {
            try {
                // Get the Status
                final String status = "Processing " + i + " of " + 10;

                // Update the Label on the JavaFx Application Thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lblStatus.setText(status);
                    }
                });
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
