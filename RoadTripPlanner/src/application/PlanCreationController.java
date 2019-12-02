package application;

import dbconnection.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlanCreationController implements Initializable{

    @FXML
    private TextField tfPlanName;

    @FXML
    private DatePicker dpStart;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private ComboBox<String> cbCity1;

    @FXML
    private ComboBox<String> cbCity2;

    @FXML
    private ComboBox<String> cbCity3;

    @FXML
    private Button btnAddPlan;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    private List<String> cities;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        handler = new DBHandler();
        cities = new ArrayList<>();

        String getCities = "select name from city";

        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(getCities);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
               cities.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cbCity3.setItems(FXCollections.observableArrayList(cities));
        cbCity2.setItems(FXCollections.observableArrayList(cities));
        cbCity1.setItems(FXCollections.observableArrayList(cities));

    }

    @FXML
    void addPlan(ActionEvent event) throws IOException {
        String insert = "INSERT INTO plan(name, start, end, user_id) VALUES (?,?,?,?)";

        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pst.setString(1, tfPlanName.getText());
            pst.setDate(2, java.sql.Date.valueOf(dpStart.getValue()));
            pst.setDate(3, java.sql.Date.valueOf(dpEnd.getValue()));
            pst.setInt(4, LoginController.getInstance().activeID());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnAddPlan.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
    }

}
