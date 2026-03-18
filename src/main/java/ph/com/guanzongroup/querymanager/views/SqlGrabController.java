
package ph.com.guanzongroup.querymanager.views;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.CommonUtils;

public class SqlGrabController implements Initializable {

    @FXML
    private TextArea txtField12;
    @FXML
    private Button btnOk;
    @FXML
    private AnchorPane acMain;
    @FXML
    private Button btnExit;
    @FXML
    private FontAwesomeIconView glyphExit;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtField12.setText(psValue);
    } 
    
    public static void setEnteredText(String lsValue){
        psValue=lsValue;
    }
    
    public void setGRider(GRider foGRider){
    this.poGRider = foGRider;}
    
    private static GRider poGRider;
    public final static String pxeModuleName = "SqlGrab.fxml";
    private static String psValue= "";
    final MenuItem lsCopied = new MenuItem("Copied to clipboard");
    final ContextMenu contextMenu = new ContextMenu(lsCopied); 
    
    @FXML
    private void onClick(MouseEvent event) {
        //adding text to clipboard
        StringSelection lsCopiedTxt = new StringSelection(psValue);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(lsCopiedTxt, null);
            
        //closing the scene
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close(); 
    }

    @FXML
    private void OnExit(ActionEvent event) {
        CommonUtils.closeStage(btnExit);
    }
}
