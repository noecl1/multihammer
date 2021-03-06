package multihammer;

import Controller.LoginController;
import Model.LoginModel;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

public class Multihammer extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader login = new FXMLLoader(getClass().getResource("/View/Login.fxml"));
            Parent root = login.load();
            LoginController loginController = login.getController();
            LoginModel loginModel = new LoginModel();
            loginController.initModel(loginModel);
            
            Scene scene = new Scene(root);
//            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            //Cambia el icono de la aplicación
            Image icon = new Image("/Resources/logo_icon.png");
            primaryStage.getIcons().add(icon);
           
            primaryStage.setMaximized(true);
            primaryStage.setTitle("Login");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch(IOException e) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
