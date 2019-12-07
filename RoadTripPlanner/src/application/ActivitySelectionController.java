package application;

import dbconnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ActivitySelectionController implements Initializable {

    @FXML
    private TextField tfActivityName;

    @FXML
    private DatePicker dpActivityDate;

    @FXML
    private TextField tfActivityCost;

    @FXML
    private Button btnAddAnother;

    @FXML
    private Text txtCityName;

    @FXML
    private Button btnAddActivity;

    @FXML
    private Button btnBack;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    // This function fires once the user clicks on the Add Activity button
    @FXML
    void addActivity(ActionEvent event) {

        // Query to insert activity fields into the ACTIVITIES table
        String insertActivity = "INSERT INTO activity(date, name, cost, city_id) VALUES (?,?,?,?)";

        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(insertActivity);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pst.setDate(1, java.sql.Date.valueOf(dpActivityDate.getValue()));
            pst.setString(2, tfActivityName.getText());
            pst.setString(3, tfActivityCost.getText());
            pst.setInt(4, PlanViewController.getInstance().getSelectedCityID());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Confirmation sent to the user that activity has been added
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Activity added successfully!");

        alert.showAndWait();
    }

    // Function fires when user clicks Add Another button, at which point text fields are cleared
    @FXML
    void addAnother(ActionEvent event) {
        tfActivityName.clear();
        tfActivityCost.clear();
        dpActivityDate.getEditor().clear();
    }

    // When user clicks on the back button, user is taken back to the CityView
    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnAddActivity.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CityView.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();

        // Display city name at the top of the page
        txtCityName.setText(PlanViewController.getInstance().getSelectedCityName());
    }
}
