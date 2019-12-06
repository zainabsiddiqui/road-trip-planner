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
import javafx.scene.control.*;
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

    @FXML
    private Button btnBack;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    private List<String> cities;

    private int currentPlanID;
    private int city1ID;
    private int city2ID;
    private int city3ID;


    private static PlanCreationController instance;

    public PlanCreationController() {
        instance = this;
    }

    public static PlanCreationController getInstance() {
        return instance;
    }

    public int activePlanID() {
        return currentPlanID;
    }

    public int getCity1ID() {
        return city1ID;
    }

    public int getCity2ID() {
        return city2ID;
    }

    public int getCity3ID() {
        return city3ID;
    }

    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnAddPlan.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }



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
        String insertPlan = "INSERT INTO plan(name, start, end, user_id) VALUES (?,?,?,?)";

        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(insertPlan);
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

        String getplanID = "select id from plan where name = ? and user_id =?";

        try {
            pst = connection.prepareStatement(getplanID);
            pst.setString(1, tfPlanName.getText());
            pst.setInt(2, LoginController.getInstance().activeID());


            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                currentPlanID = rs.getInt("id");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String getCity1ID = "select id from city where name = ?";

        try {
            pst = connection.prepareStatement(getCity1ID);
            pst.setString(1, cbCity1.getValue());


            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city1ID = rs.getInt("id");
            }

            System.out.println(city1ID);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String getCity2ID = "select id from city where name = ?";

        try {
            pst = connection.prepareStatement(getCity2ID);
            pst.setString(1, cbCity2.getValue());


            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city2ID = rs.getInt("id");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String getCity3ID = "select id from city where name = ?";

        try {
            pst = connection.prepareStatement(getCity3ID);
            pst.setString(1, cbCity3.getValue());


            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city3ID = rs.getInt("id");
            }

            System.out.println(city3ID);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertVisits = "INSERT INTO visits(plan_id, city_id) VALUES (?,?)";

        try {
            pst = connection.prepareStatement(insertVisits);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            // Insert same plan, different city 3 times
            pst.setInt(1, currentPlanID);
            pst.setInt(2, city1ID);

            pst.executeUpdate();

            pst.setInt(1, currentPlanID);
            pst.setInt(2, city2ID);

            pst.executeUpdate();

            pst.setInt(1, currentPlanID);
            pst.setInt(2, city3ID);

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Plan created successfully!");

        alert.showAndWait();

        btnAddPlan.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();


    }

}
