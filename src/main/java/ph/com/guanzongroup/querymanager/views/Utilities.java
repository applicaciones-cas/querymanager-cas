package ph.com.guanzongroup.querymanager.views;

//import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
//import com.sun.javafx.scene.control.skin.TabPaneSkin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public class Utilities {
    UndoManager undo = new UndoManager(); 
    
    public static void copyCellValue(TableView<?> tblView) {
        StringBuilder clipboardString = new StringBuilder();

        ObservableList<TablePosition> positionList = tblView.getSelectionModel().getSelectedCells();
            for (TablePosition position : positionList) {
                int row = position.getRow();
                int col = position.getColumn();

                Object cell = (Object) tblView.getColumns().get(col).getCellData(row);
                if(cell!=null){
                    // create string from cell
                    String text = cell.toString();

                    // add new item to clipboard
                    clipboardString.append(text);
                    // create clipboard content
                    final ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(clipboardString.toString());

                    // set clipboard content
                    Clipboard.getSystemClipboard().setContent(clipboardContent);
                }
            }
    }
    
     public static void copyRowValue(TableView<?> tblView) {
        StringBuilder clipboardString = new StringBuilder();
        String rowContent = "";
        try {
            ObservableList<TablePosition> positionList = tblView.getSelectionModel().getSelectedCells();
            for (TablePosition position : positionList) {

            int row = position.getRow();
            int col = 0;
            
           for (int i = 0; i < tblView.getColumns().size(); i++) {
                rowContent =rowContent+"\t"+tblView.getColumns().get(col).getCellData(row);
                if (col < tblView.getColumns().size()) {
                    col++;            
                }
            }       
            // create string from rowcontent
            String text = rowContent.toString().substring(1);

            // add new item to clipboard
            clipboardString.append(text);
            }
            // create clipboard content
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString());

            // set clipboard content
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }catch(IndexOutOfBoundsException ex){
            System.err.println(ex);
        }
    }
     
    public static void copyTableValue(TableView<?> tblView) {
        StringBuilder clipboardString = new StringBuilder();
        String lsValue = "";
        String rowContent = "";
        try {
            int row = 0;
            for (int i = 0; i < tblView.getItems().size(); i++) {
                 rowContent = rowContent+"\n"+tblView.getItems().get(row);
                 if (row < tblView.getItems().size()) {
                     row++;
                 }
             }
            // create string from rowcontent
            String text = rowContent.toString().replace(",", "\t");
            lsValue = text.replace("[", "");
            text = lsValue.replace("]", "");

            // add new item to clipboard
            clipboardString.append(text);
            
            // create clipboard content
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString());

            // set clipboard content
            Clipboard.getSystemClipboard().setContent(clipboardContent);
            }catch(IndexOutOfBoundsException ex){
                System.err.println(ex);
            } 
    }
    
    public static void CopyColumn(TreeView<Object> treeView) {
        StringBuilder clipboardString = new StringBuilder();
        try {
                TreeItem<Object> selected = treeView.getSelectionModel().getSelectedItem();
                if(selected!=null){
                    String text = selected.getValue().toString();

                 // add new item to clipboard
                 clipboardString.append(text);

                 // create clipboard content
                 final ClipboardContent clipboardContent = new ClipboardContent();
                 clipboardContent.putString(clipboardString.toString());

                 // set clipboard content
                 Clipboard.getSystemClipboard().setContent(clipboardContent);
                }
            }catch(IndexOutOfBoundsException ex){
                System.err.println(ex);
            } 
    }
    
    public static void CopyAllColumns(TreeView<Object> treeView) {
        StringBuilder clipboardString = new StringBuilder();
        try {
            List<TreeItem<Object>> children = treeView.getSelectionModel().getSelectedItem().getChildren();
                if (children != null){
                    for (TreeItem<Object> child : children) {
                    String text = child.getValue().toString();

                    clipboardString.append("\n"+text);

                    final ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(clipboardString.toString());

                    Clipboard.getSystemClipboard().setContent(clipboardContent);
                    }
                }
            }catch(IndexOutOfBoundsException ex){
                System.err.println(ex);
            } 
    }
    
    public static boolean saveAs(String content, Tab tab){
        FileChooser fileChooser = new FileChooser();
  
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);
        if(file != null){
            SaveFile(content, file);
            tab.setText(file.getPath());
            return true;
        }else{
            return false;
        }
       
    }
    
    public static void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void readFile(TextArea txtArea, Tab tab){
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            txtArea.setText(readFile(file));
            tab.setText(file.getPath());
        }
    }
   
    
    private static String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
         
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append("\n"+text);
            }
 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        return stringBuffer.toString();
    }
    
// kalyptus - 2026.03.18 06:58pm
// replaced the following 2 functions with the function below
//    public static void closeCurrentTab(TabPane tabPane) {
//        TabPaneBehavior behavior = getBehavior(tabPane);
//           if(behavior.canCloseTab(tabPane.getSelectionModel().getSelectedItem())) {
//               behavior.closeTab(tabPane.getSelectionModel().getSelectedItem());}
//    }
//    
//    private static TabPaneBehavior getBehavior(TabPane tabPane) {
//        return ((TabPaneSkin) tabPane.getSkin()).getBehavior();
//    }
    
    public static void closeCurrentTab(TabPane tabPane) {
        Tab current = tabPane.getSelectionModel().getSelectedItem();
        if (current != null && current.isClosable()) {
            tabPane.getTabs().remove(current);
        }
    }
    
    public static ButtonType ShowYesNoCancel(String fsHeader, String fsTitle, String fsMessage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(fsTitle);
        alert.setContentText(fsMessage);
        alert.setHeaderText(fsHeader);
        
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        return result.orElse(ButtonType.NO);
    }
    
    public static boolean ExportToExcel(TableView<?> tblView){
        
        HSSFWorkbook workbook = new HSSFWorkbook();
        
        HSSFSheet sheet = workbook.createSheet("Sample Data");
        
        //create FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to Excel");
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Documents (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        
        //Show save file dialog
            File file = fileChooser.showSaveDialog(null);
            if(file != null){
            HSSFRow row = sheet.createRow(0);
                    for (int j = 0; j < tblView.getColumns().size(); j++) {
                     row.createCell(j).setCellValue(tblView.getColumns().get(j).getText());
                }
                for (int i = 0; i < tblView.getItems().size(); i++) {
                    row = sheet.createRow(i+1);
                    for (int j = 0; j < tblView.getColumns().size(); j++) {
                        row.createCell(j).setCellValue(tblView.getColumns().get(j).getCellData(i).toString());
                        
                    }
                }
                saveExcel(file, workbook);
            return true;
        }else{
            return false;
        }
    
    }
    
    public static void saveExcel(File file, Workbook workBook){
        try {
            FileOutputStream out;
            
            out = new FileOutputStream(new File(file.getAbsolutePath()));
            workBook.write(out);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void undo() {
        try {
            if (undo.canUndo()) {
                undo.undo();
            }
        } catch (CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
        }
    }
    
    public void redo() {
        try {
            if (undo.canRedo()) {
                undo.redo();
            }
        } catch (CannotRedoException ex) {
            System.out.println("Unable to redo: " + ex);
        }
    }
    
}
