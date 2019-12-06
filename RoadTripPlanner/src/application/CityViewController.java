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

    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnCarBooking.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PlanView.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }

    @FXML
    void addActivity(ActionEvent event) throws IOException{
        btnCarBooking.getScene().getWindow().hide();

        Stage viewCityCars = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ActivitySelection.fxml"));
        Scene scene = new Scene(root);
        viewCityCars.setScene(scene);
        viewCityCars.show();
    }

    @FXML
    void bookCar(ActionEvent event) throws IOException{
        btnCarBooking.getScene().getWindow().hide();

        Stage viewCityCars = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("RentalCarBooking.fxml"));
        Scene scene = new Scene(root);
        viewCityCars.setScene(scene);
        viewCityCars.show();
    }

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
        txtCity1.setText(PlanViewController.getInstance().getSelectedCityName());

        handler = new DBHandler();

        connection = handler.getConnection();
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
                hotelID = rs.getInt("hotel_id");
                confirmationNumber = rs.getInt("confirmation");
            }

            if (count == 1) {

                connection = handler.getConnection();
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

                txtHotelName.setText(hotelName);
                txtHotelConfirmation.setText(Integer.toString(confirmationNumber));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = handler.getConnection();
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
                rentalID = rs.getInt("rentalcar_id");
                carConfirmationNumber = rs.getInt("confirmation");
            }

            if (count == 1) {

                connection = handler.getConnection();
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

                txtCarModel.setText(carName);
                txtCarConfirmation.setText(Integer.toString(carConfirmationNumber));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection = handler.getConnection();
        String grabActivities = "select * from activity where city_id = ?";
        String activityName = "";
        int activityCost = 0;
        lvActivities.setItems(items);

        try {

            pst = connection.prepareStatement(grabActivities);
            pst.setInt(1, PlanViewController.getInstance().getSelectedCityID());

            ResultSet rs = pst.executeQuery();


            while (rs.next()) {
                activityName = rs.getString("name");
                activityCost = activityCost + rs.getInt("cost");
                items.add(activityName);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

//    public boolean existsHotelBooking(int user_id, int city_id) {
//
//    }
}
