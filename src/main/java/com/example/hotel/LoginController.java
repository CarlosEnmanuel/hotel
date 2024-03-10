package com.example.hotel;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

public class LoginController {
    private HelloApplication helloApplication;

    public void setHelloApplication(HelloApplication helloApplication) {
        this.helloApplication = helloApplication;
    }




    @FXML
    private TextField username;

    @FXML
    private PasswordField contra;

    @FXML
    private Button button;

    @FXML
    private Label wrongLogin;

    @FXML
    private Label wrongLogin2;

    @FXML
    private void userLogin(ActionEvent event) throws IOException {
        String enteredUsername = username.getText();
        String enteredPassword = contra.getText();

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            if (enteredUsername.isEmpty()) {
                wrongLogin2.setText("Llenar campo de usuario");
            } else {
                wrongLogin2.setText("Llenar campo de contraseña");
            }
        } else {
            if (enteredUsername.equals("usuario") && enteredPassword.equals("contraseña")) {

                wrongLogin2.setText("");


                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                currentStage.close();

                //redirecciona a la ventana de login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Inicio.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setTitle("Inicio");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

            } else {
                if (!enteredUsername.equals("usuario")) {
                    wrongLogin2.setText("Datos Incorrectos");
                } else {
                    wrongLogin2.setText("Datos Incorrectos");
                }
            }
        }
    }
}
