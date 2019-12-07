package application;

import dbconnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.transform.Result;

public class PlanViewController implements Initializable {

    @FXML
    private Text txtPlanName;

    @FXML
    private Text txtPlanStart;

    @FXML
    private Text txtPlanEnd;

    @FXML
    private Text txtCity1Name;

    @FXML
    private Button btnCity1;

    @FXML
    private Text txtCity2Name;

    @FXML
    private Button btnCity2;

    @FXML
    private Text txtCity3Name;

    @FXML
    private Button btnCity3;

    @FXML
    private Button btnDeletePlan;

    @FXML
    private Button btnBack;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    private int currentPlanID;

    ArrayList<Integer> cityIDs = new ArrayList<Integer>();

    private int city1ID;

    private int city2ID;

    private int city3ID;

    private String city1Name;

    private String city2Name;

    private String city3Name;

    private int selectedCityID;

    private String selectedCityName;


    private static PlanViewController instance;

    public PlanViewController() {
        instance = this;
    }

    public static PlanViewController getInstance() {
        return instance;
    }

    public int activePlanID() {
        return currentPlanID;
    }

    public int getSelectedCityID() {
        return selectedCityID;
    }

    public String getSelectedCityName() {
        return selectedCityName;
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

    public String getCity1Name() {
        return city1Name;
    }

    public String getCity2Name() {
        return city2Name;
    }

    public String getCity3Name() {
        return city3Name;
    }

    // When user clicks back button, go back to Dashboard.fxml
    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnDeletePlan.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }

    // If user clicks the Delete Plan button, this function is fired
    @FXML
    void deletePlan(ActionEvent event) throws IOException {
        // Query to delete the current plan given the current plan's ID
        String planDeletionQuery = "DELETE FROM plan where id = ?";

        connection = handler.getConnection();

        try {
            pst = connection.prepareStatement(planDeletionQuery);
            pst.setInt(1, currentPlanID);

            // Delete
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Once plan is deleted, hide current screen and go back to Dashboard, where user can no longer find their deleted plan
        btnDeletePlan.getScene().getWindow().hide();

        Stage dashboard = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene scene = new Scene(root);
        dashboard.setScene(scene);
        dashboard.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();

        // Query to grab other plan details, given plan name and userID
        String planNameQuery = "select * from plan where name = ? and user_id = ?";

        // Initialize variables
        String planName = "Your Plan";
        String startDate = "Start date";
        String endDate = "End date";

        connection = handler.getConnection();

        try {
            pst = connection.prepareStatement(planNameQuery);
            pst.setString(1, DashboardController.getInstance().clickedPlan());
            pst.setInt(2, LoginController.getInstance().activeID());



            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                // Grab plan name
                planName = rs.getString("name");

                // Set and display start and end date of plan as received by above query and in the given format
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                startDate = dateFormat.format(rs.getDate("start"));
                endDate = dateFormat.format(rs.getDate("end"));

                currentPlanID = rs.getInt("id");

            }

            // Display plan name and dates at the top of the page
            txtPlanName.setText(planName);
            txtPlanStart.setText(startDate);
            txtPlanEnd.setText(endDate);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Query to grab cities associated with given plan ID
        String grabCitiesQuery = "select city_id from visits where plan_id = ?";
        try {
            pst = connection.prepareStatement(grabCitiesQuery);
            pst.setInt(1, currentPlanID);

            ResultSet rs = pst.executeQuery();

            int count = 0;

            while(rs.next()) {
                cityIDs.add(rs.getInt("city_id"));
                count+=1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set all cityID variables
        city1ID = cityIDs.get(0);
        city2ID = cityIDs.get(1);
        city3ID = cityIDs.get(2);

        // Query to grab name of city 1
        String city1Query = "select name from city where id = ?";
         try {
            pst = connection.prepareStatement(city1Query);
            pst.setInt(1, city1ID);

            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city1Name = rs.getString("name");

            }

            txtCity1Name.setText(city1Name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Query to grab name of city 2
        String city2Query = "select name from city where id = ?";
        try {
            pst = connection.prepareStatement(city2Query);
            pst.setInt(1, city2ID);

            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city2Name = rs.getString("name");

            }

            txtCity2Name.setText(city2Name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Query to grab name of city 3
        String city3Query = "select name from city where id = ?";
        try {
            pst = connection.prepareStatement(city3Query);
            pst.setInt(1, city3ID);

            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                city3Name = rs.getString("name");

            }

            txtCity3Name.setText(city3Name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // If user clicks on the first city's button, display the CityView page for that city
    @FXML
    void viewCity1(ActionEvent event) throws IOException{
        btnDeletePlan.getScene().getWindow().hide();

        selectedCityName = city1Name;
        selectedCityID = city1ID;

        Stage viewCity = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CityView.fxml"));
        Scene scene = new Scene(root);
        viewCity.setScene(scene);
        viewCity.show();
    }

    // If user clicks on the second city's button, display the CityView page for that city

    @FXML
    void viewCity2(ActionEvent event) throws IOException {
        btnDeletePlan.getScene().getWindow().hide();

        selectedCityID = city2ID;
        selectedCityName = city2Name;

        Stage viewCity = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CityView.fxml"));
        Scene scene = new Scene(root);
        viewCity.setScene(scene);
        viewCity.show();
    }

    // If user clicks on the third city's button, display the CityView page for that city
    @FXML
    void viewCity3(ActionEvent event) throws IOException {
        btnDeletePlan.getScene().getWindow().hide();

        selectedCityID = city3ID;
        selectedCityName = city3Name;

        Stage viewCity = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CityView.fxml"));
        Scene scene = new Scene(root);
        viewCity.setScene(scene);
        viewCity.show();
    }
}
