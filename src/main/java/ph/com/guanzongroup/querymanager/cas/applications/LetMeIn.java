package ph.com.guanzongroup.querymanager.cas.applications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import ph.com.guanzongroup.querymanager.views.QueryManagerFxV2;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;

public class LetMeIn {
    public static void main (String args[]){
        try {
            String lsProdctID = "gRider";
            String lsUserIDxx = "M001111122";
            
            String path;
            
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                path = "D:/GGC_Java_Systems";
            }
            else{
                path = "/srv/GGC_Java_Systems";
            }
            
            System.setProperty("sys.default.path.config", path);
            
            GRiderCAS poGRider = new GRiderCAS(lsProdctID);
            
            if (!poGRider.loadEnv(lsProdctID)){
                System.out.println(poGRider.getMessage()+poGRider.getMessage());
                System.exit(0);
            }
            if (!poGRider.loadUser(lsProdctID, lsUserIDxx)){
                System.out.println(poGRider.getMessage()+poGRider.getMessage());
                System.exit(0);
            }
            
            QueryManagerFxV2 query = new QueryManagerFxV2();
            query.setGRider(poGRider);
            
            Application.launch(query.getClass());
        } catch (SQLException ex) {
            Logger.getLogger(LetMeIn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(LetMeIn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LetMeIn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
