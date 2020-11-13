package ApplicationCliente.Controller;

import ApplicationCliente.LoginLogoutCliente;
import Implementation.ClientServerImplementation;
import Implementation.ImpFactory;
import classes.User;
import exceptions.LoginNoExistException;
import exceptions.NoConnectionDBException;
import exceptions.NoServerConnectionException;
import exceptions.PasswordErrorException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller of the login view
 *
 * @author Rubén
 */
public class LoginController {

    private static final Logger logger = Logger.getLogger("ApplicationClient.Controller.LoginController");
    //Declaration of attributes
    @FXML
    private Stage stage;

    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField tfLogin;
    @FXML
    private TextField tfPasswd;

    /**
     * Set the stage of the view
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialize the view and set actions on different widgets
     *
     * @param root
     */
    public void initStage(Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.show();

        tfLogin.textProperty().addListener(this::textChange);
        tfPasswd.textProperty().addListener(this::textChange);
        btnLogin.setOnAction(this::handleButtonLogin);
        btnRegister.setOnAction(this::handleButtonRegister);
    }

    /**
     * Text changed event handler. If both fields are empty the button is
     * disable
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void textChange(ObservableValue observable, String oldValue, String newValue) {
        //disable the Login button

        //If password field is higher than 12
        if (tfPasswd.getText().length() > 12) {
            btnLogin.setDisable(true);
        } //If text fields are empty 
        else if (tfLogin.getText().trim().isEmpty()
                || tfPasswd.getText().trim().isEmpty()) {
            btnLogin.setDisable(true);
        } //Else, enable accept button
        else {
            btnLogin.setDisable(false);
        }

    }

    /**
     * Action event handler. It validate the login and password fields. If both
     * fields are ok open logout view.
     *
     * @param event
     */
    private void handleButtonLogin(ActionEvent event) {
        if (tfLogin.getText().isEmpty() || tfPasswd.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.APPLY);
        } else {
            User myUser = new User();
            myUser.setLogIn(tfLogin.getText());
            myUser.setPasswd(tfPasswd.getText());
            ClientServerImplementation imp = ImpFactory.getImplement();
            User serverUser = null;
            try {
                serverUser = imp.signIn(myUser);
            } catch (LoginNoExistException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PasswordErrorException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoServerConnectionException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("No se ha podido conectar con el servidor. Inténtelo más tarde.");
                alert.showAndWait();
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoConnectionDBException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("No se ha podido conectar con la base de datos. Inténtelo más tarde.");
                alert.showAndWait();
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ConnectException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (serverUser.getPasswd() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Contraseña incorrecta");
                alert.showAndWait();
                tfPasswd.requestFocus();
            }
            

            if (serverUser.getPasswd() != null && serverUser.getLogIn() != null ) {

                LogoutController controller = new LogoutController();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Logout.fxml"));
                Parent root;
                controller.setMensaje(myUser);

                try {
                    root = (Parent) loader.load();
                    controller = (loader.getController());
                    controller.setStage(stage);                  
                    controller.initStage(root);
                } catch (IOException ex) {
                    Logger.getLogger(LogoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                logger.info("User null");
            }
        }

    }

    /**
     * Action event handler. It open register view.
     *
     * @param event
     */
    private void handleButtonRegister(ActionEvent event) {

        Parent root;
        try {
            SignUpController controller = new SignUpController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            root = (Parent) loader.load();
            controller = (loader.getController());
            controller.setStage(stage);
            controller.initStage(root);
        } catch (IOException ex) {
            Logger.getLogger(LoginLogoutCliente.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.WARNING, "No se ha podido cargar la ventana", ButtonType.OK);
        }
    }
}