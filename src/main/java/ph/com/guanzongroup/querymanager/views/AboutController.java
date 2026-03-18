package ph.com.guanzongroup.querymanager.views;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;

public class AboutController implements Initializable {

    @FXML
    private AnchorPane acMain;
    @FXML
    private Button btnExit;
    @FXML
    private FontAwesomeIconView glyphExit;
    private Button cmdOk;
    @FXML
    private Label lblProduct;
    @FXML
    private Label lblAddress;
    @FXML
    private Label lblTelephone;
    
    private GRiderCAS poGRider;
    @FXML
    private Label lblProgramName;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblProduct.setText("");
        lblAddress.setText("");
        lblTelephone.setText("");
        lblProgramName.setText("");
        
        lblProgramName.setText(poGRider.getProductName());
        lblProduct.setText(poGRider.getBranchName());
        lblAddress.setText(poGRider.getAddress() + ", " + poGRider.getTownName() + " " + poGRider.getProvince());
        if (!poGRider.getTelNo().equals("")) lblTelephone.setText("Tel. No. " + poGRider.getTelNo() + " ");
        if (!poGRider.getFaxNo().equals("")) lblTelephone.setText(lblTelephone.getText() + "Fax No. " + poGRider.getFaxNo());
    }    

    @FXML
    private void btnExit(ActionEvent event) {
        CommonUtils.closeStage(btnExit);
    }
    
    public void setGRider(GRiderCAS foGRider){this.poGRider = foGRider;}
    
}
