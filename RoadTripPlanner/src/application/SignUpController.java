package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import dbconnection.DBHandler;

public class SignUpController implements Initializable{

    @FXML
    private TextField tfFirstName;

    @FXML
    private TextField tfUsername;

    @FXML
    private TextField tfLastName;

    @FXML
    private TextField tfPhone;

    @FXML
    private Button btnCreateAccount;

    @FXML
    private PasswordField tfPass;

    @FXML
    private PasswordField tfConfirmPass;

    @FXML
    private Button btnBackLogin;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
    }

    // This function fires once the user clicks the Create Account button
    @FXML
    void createAccount(ActionEvent event) throws IOException {
        // If any of the fields on the sign up page are empty, throw an error
        if (tfFirstName.getText().isEmpty() || tfLastName.getText().isEmpty() || tfPass.getText().isEmpty() || tfConfirmPass.getText().isEmpty()
                || tfPhone.getText().isEmpty() || tfUsername.getText().isEmpty()) {

            new Alert(Alert.AlertType.ERROR, "Please fill in all the fields.").showAndWait();

        } else {
            // If the passwords entered on the page do not match, throw an error
            if (!tfPass.getText().equals(tfConfirmPass.getText())) {

                new Alert(Alert.AlertType.ERROR, "Passwords don't match. Please try again!").showAndWait();

            } else {
                // Query to insert all of the information on the page into USER table
                String insert = "INSERT INTO user(username, pass, fname, lname, phone) VALUES (?,?,?,?,?)";

                connection = handler.getConnection();
                try {
                    pst = connection.prepareStatement(insert);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    pst.setString(1, tfUsername.getText());
                    pst.setString(2, tfPass.getText());
                    pst.setString(3, tfFirstName.getText());
                    pst.setString(4, tfLastName.getText());
                    pst.setString(5, tfPhone.getText());

                    // Execute the above query
                    pst.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Send an alert to the user notifying them that their account has been created
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Account created successfully!");

                alert.showAndWait();

                // Hide the SignUp screen and go back to the Login page, allowing them to enter their credentials
                btnCreateAccount.getScene().getWindow().hide();

                Stage dashboard = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Scene scene = new Scene(root);
                dashboard.setScene(scene);
                dashboard.show();
            }
        }
    }


    // This function fires if the user clicks the back button, which leads them back to the Login page
    @FXML
    void loginAction(ActionEvent event) throws IOException {
        btnCreateAccount.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
        login.setResizable(false);

    }


}
