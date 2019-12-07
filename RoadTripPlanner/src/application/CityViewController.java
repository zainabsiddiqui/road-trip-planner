package application;
import dbconnection.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CityViewController implements Initializable {
    @FXML
    private Text txtCity1;

    @FXML
    private Text txtHotelName;

    @FXML
    private Button btnHotelBooking;

    @FXML
    private Text txtHotelConfirmation;

    @FXML
    private Text txtCarModel;

    @FXML
    private Text txtCarConfirmation;

    @FXML
    private Button btnCarBooking;

    @FXML
    private Button btnAddActivities;

    @FXML
    private ListView<String> lvActivities;

    @FXML
    private Button btnBack;

    private ObservableList<String> items = FXCollections.observableArrayList();


    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;


    // If user clicks on back button, send them back to the Plan View
    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnCarBooking.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PlanView.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }

    // If user clicks on the Add Activities button, transfer control over to ActivitySelection.fxml
    @FXML
    void addActivity(ActionEvent event) throws IOException{
        btnCarBooking.getScene().getWindow().hide();

        Stage viewCityCars = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ActivitySelection.fxml"));
        Scene scene = new Scene(root);
        viewCityCars.setScene(scene);
        viewCityCars.show();
    }

    // If user clicks on Add/Change Booking for car rentals, send them to the car booking page
    @FXML
    void bookCar(ActionEvent event) throws IOException{
        btnCarBooking.getScene().getWindow().hide();

        Stage viewCityCars = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("RentalCarBooking.fxml"));
        Scene scene = new Scene(root);
        viewCityCars.setScene(scene);
        viewCityCars.show();
    }

    // If user clicks on Add/Change Booking for hotels, send them to the hotel booking page
    @FXML
    void bookHotel(ActionEvent event) throws IOException {
        btnCarBooking.getScene().getWindow().hide();

        Stage viewCityHotels = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("HotelBooking.fxml"));
        Scene scene = new Scene(root);
        viewCityHotels.setScene(scene);
        viewCityHotels.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Display city name at the top of the page
        txtCity1.setText(PlanViewController.getInstance().getSelectedCityName());

        handler = new DBHandler();

        connection = handler.getConnection();

        // Query to grab hotel bookings already associated with the user and city
        String grabHotelBooking = "select * from hotel_booking where user_id = ? and hotel_id in (select id from hotel where city_id = ?); ";
        String hotelName = "";
        int hotelID = 0;
        int confirmationNumber = 0;

        try {

            pst = connection.prepareStatement(grabHotelBooking);
            pst.setInt(1, LoginController.getInstance().activeID());
            pst.setInt(2, PlanViewController.getInstance().getSelectedCityID());


            ResultSet rs = pst.executeQuery();

            int count = 0;

            while (rs.next()) {
                count += 1;
                // Grab associated hotel ID and confirmation number for hotel booking
                hotelID = rs.getInt("hotel_id");
                confirmationNumber = rs.getInt("confirmation");
            }

            if (count == 1) {

                connection = handler.getConnection();

                // Query to grab hotel name from HOTEL table given hotel ID
                String grabHotelName = "select name from hotel where id = ?";


                try {
                    pst = connection.prepareStatement(grabHotelName);
                    pst.setInt(1, hotelID);
                    ResultSet rs2 = pst.executeQuery();

                    while(rs2.next()) {

                        hotelName = rs2.getString("name");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Display the hotel name for already-made hotel booking, as well as confirmation number for that booking
                txtHotelName.setText(hotelName);
                txtHotelConfirmation.setText(Integer.toString(confirmationNumber));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = handler.getConnection();

        // Query to grab car rental bookings already associated with the city and user
        String grabCarBooking = "select * from rentalcar_booking where user_id = ? and rentalcar_id in (select id from rentalcar where city_id = ?); ";
        String carName = "";
        int rentalID = 0;
        int carConfirmationNumber = 0;

        try {

            pst = connection.prepareStatement(grabCarBooking);
            pst.setInt(1, LoginController.getInstance().activeID());
            pst.setInt(2, PlanViewController.getInstance().getSelectedCityID());


            ResultSet rs = pst.executeQuery();

            int count = 0;

            while (rs.next()) {
                count += 1;
                // Grab rental ID and confirmation number from the booking
                rentalID = rs.getInt("rentalcar_id");
                carConfirmationNumber = rs.getInt("confirmation");
            }

            if (count == 1) {

                connection = handler.getConnection();

                // Query to grab car name from RENTALCAR table given car ID
                String grabCarName = "select model from rentalcar where id = ?";


                try {
                    pst = connection.prepareStatement(grabCarName);
                    pst.setInt(1, rentalID);
                    ResultSet rs2 = pst.executeQuery();

                    while(rs2.next()) {

                        carName = rs2.getString("model");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Display car model and confirmation for the already-made booking
                txtCarModel.setText(carName);
                txtCarConfirmation.setText(Integer.toString(carConfirmationNumber));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = handler.getConnection();

        // Query to grab activities for an associated city
        String grabActivities = "select * from activity where city_id = ?";
        String activityName = "";
        int activityCost = 0;
        lvActivities.setItems(items);

        try {

            pst = connection.prepareStatement(grabActivities);
            pst.setInt(1, PlanViewController.getInstance().getSelectedCityID());

            ResultSet rs = pst.executeQuery();


            while (rs.next()) {
                // Grab activity names and increment activity costs for the single city
                activityName = rs.getString("name");
                activityCost = activityCost + rs.getInt("cost");
                items.add(activityName);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
