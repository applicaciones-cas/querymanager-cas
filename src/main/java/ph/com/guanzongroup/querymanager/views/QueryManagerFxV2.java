package ph.com.guanzongroup.querymanager.views;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class QueryManagerFxV2 extends Application {
    
    public static GRider poGRider;
    public final static String pxeMainFormTitle = "Queryy ManagerFx Fx V 2.0";
    public final static String pxeMainForm = "MainInterface.fxml";
    public final static String pxeStageIcon = "org/rmj/querymngr/images/GFx.png";
    final KeyCombination ctrlT = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    final KeyCombination ALTL = new KeyCodeCombination(KeyCode.L, KeyCombination.ALT_ANY);
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(pxeMainForm));
        
        //get the controller of the main interface
        MainInterfaceController loControl = new MainInterfaceController();
        
        //set the GRider Application Driver to the controller
        loControl.setGRider(poGRider);
        
        //the controller class to the main interface
        fxmlLoader.setController(loControl);
        
        //load the main interface
        Parent parent = fxmlLoader.load();
        //set the main interface as the scene
        Scene scene = new Scene(parent);
        
        //get the screen size
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle(pxeMainFormTitle);
        
        // set stage as maximized but not full screen
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.centerOnScreen();
        stage.show();
        
        //making the stage draggable
        loControl.acTiltleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        loControl.acTiltleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        loControl.rootPane.setOnKeyReleased(this::handleKeyCode);
        
        loControl.rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (ctrlT.match(event)){
                    loControl.NewTab();
                }else if (ALTL.match(event)){
                    loControl.isTabOk();
                }
            }
        });
    }
    
    public void setGRider(GRider foGRider){
        this.poGRider = foGRider;
    }
    
    public void handleKeyCode(KeyEvent event) {
        MainInterfaceController loControl = new MainInterfaceController();
        
        switch(event.getCode()){
            case F1:
                try {
                        loControl.LoadScene("Help.fxml");
                    }catch (IOException ex) {
                        Logger.getLogger(QueryManagerFxV2.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case F2:
                break;
            case F4:
                break;
            case F6:
                break;
            case F7:
                break;
            case ESCAPE:
                if(ShowMessageFX.OkayCancel("Do you want to exit?", pxeMainFormTitle, "All changes made will not save!")== true){
                   System.exit(0);
                   break;
                }
        }
        
    }
}
