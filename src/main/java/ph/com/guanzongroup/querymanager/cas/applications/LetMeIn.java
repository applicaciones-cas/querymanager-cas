package ph.com.guanzongroup.querymanager.cas.applications;

import javafx.application.Application;
import ph.com.guanzongroup.querymanager.views.QueryManagerFxV2;

public class LetMeIn {
    public static void main (String args[]){
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
        
        GRider poGRider = new GRider(lsProdctID);
        
        if (!poGRider.loadEnv(lsProdctID)){
            System.out.println(poGRider.getMessage()+poGRider.getErrMsg());
            System.exit(0);
        }
        if (!poGRider.loadUser(lsProdctID, lsUserIDxx)){
            System.out.println(poGRider.getMessage()+poGRider.getErrMsg());
            System.exit(0);
        }
        
        QueryManagerFxV2 query = new QueryManagerFxV2();
        query.setGRider(poGRider);
        
        Application.launch(query.getClass());
    }
}
