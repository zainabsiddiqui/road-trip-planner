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

    @FXML
    void createAccount(ActionEvent event) throws IOException {
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

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnCreateAccount.getScene().getWindow().hide();

        Stage dashboard = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        dashboard.setScene(scene);
        dashboard.show();
    }

    @FXML
    void loginAction(ActionEvent event) throws IOException {
        btnCreateAccount.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
    }


}
