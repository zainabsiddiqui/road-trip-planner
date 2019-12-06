package application;

import dbconnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import dbconnection.DBHandler;

public class LoginController implements Initializable{

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnSignUp;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    private int currentUserID;

    private static LoginController instance;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public int activeID() {
        return currentUserID;
    }

    public String username() {
        return tfUsername.getText();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
    }

    @FXML
    public void loginAction(ActionEvent event) throws IOException {
        connection = handler.getConnection();
        String loginQuery = "SELECT * FROM user WHERE username=? AND pass=?";

        try {
            pst = connection.prepareStatement(loginQuery);
            pst.setString(1, tfUsername.getText());
            pst.setString(2, tfPassword.getText());

            ResultSet rs = pst.executeQuery();

            int count = 0;



            while(rs.next()) {
                count += 1;
                currentUserID = rs.getInt("ID");
            }

            if(count == 1) {
                btnLogin.getScene().getWindow().hide();

                Stage dashboard = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                Scene scene = new Scene(root);
                dashboard.setScene(scene);
                dashboard.show();
            } else {
                System.out.println("Login unsuccessful.");
                new Alert(Alert.AlertType.ERROR, "Incorrect username or password. Please try again!").showAndWait();
                tfUsername.clear();
                tfPassword.clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void signUpAction(ActionEvent event) throws IOException{
        btnLogin.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

}
