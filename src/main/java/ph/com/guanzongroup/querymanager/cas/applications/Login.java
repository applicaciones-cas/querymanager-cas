
package ph.com.guanzongroup.querymanager.cas.applications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import ph.com.guanzongroup.querymanager.views.QueryManagerFxV2;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;

public class Login {    
    public static void main(String [] args){       
        try {
            String lsProdctID = args[0];
            String lsUserIDxx = args[1];
            
            String path;
            
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                path = "D:/GGC_Java_Systems";
            }
            else{
                path = "/srv/GGC_Java_Systems";
            }
            
            System.setProperty("sys.default.path.config", path);
            
            
            GRiderCAS instance = new GRiderCAS(lsProdctID);
            
            if (!instance.loadEnv(lsProdctID)) System.exit(0);
            
            if (!instance.loadUser(lsProdctID, lsUserIDxx)) System.exit(0);
            
            QueryManagerFxV2 mainController = new QueryManagerFxV2();
            mainController.setGRider(instance);
            
            Application.launch(mainController.getClass());
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
