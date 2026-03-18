
package ph.com.guanzongroup.querymanager.cas.applications;

import javafx.application.Application;
import ph.com.guanzongroup.querymanager.views.QueryManagerFxV2;

public class Login {    
    public static void main(String [] args){       
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
        
        
        GRider instance = new GRider(lsProdctID);
        
        if (!instance.loadEnv(lsProdctID)) System.exit(0);
        
        if (!instance.loadUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        QueryManagerFxV2 mainController = new QueryManagerFxV2();
        mainController.setGRider(instance);
        
        Application.launch(mainController.getClass());
    }  
}
