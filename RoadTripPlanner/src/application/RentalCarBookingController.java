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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.ResourceBundle;

public class RentalCarBookingController implements Initializable{

    @FXML
    private Text txtCityName;

    @FXML
    private Button btnCar1Booking;

    @FXML
    private Text txtCar1Name;

    @FXML
    private Text txtCar1Cost;

    @FXML
    private Button btnCar2Booking;

    @FXML
    private Text txtCar2Name;

    @FXML
    private Text txtCar2Cost;

    @FXML
    private Button btnCar3Booking;

    @FXML
    private Text txtCar3Name;

    @FXML
    private Text txtCar3Cost;

    @FXML
    private Button btnBack;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    private ArrayList<String> carModels = new ArrayList<String>();
    private ArrayList<Integer> carCosts = new ArrayList<Integer>();
    private ArrayList<Integer> carIDs = new ArrayList<Integer>();

    private java.sql.Date date;

    public static int randomInt(int digits) {
        int minimum = (int) Math.pow(10, digits - 1); // minimum value with 2 digits is 10 (10^1)
        int maximum = (int) Math.pow(10, digits) - 1; // maximum value with 2 digits is 99 (10^2 - 1)
        Random random = new Random();
        return minimum + random.nextInt((maximum - minimum) + 1);
    }


    @FXML
    void bookCar1(ActionEvent event) {
        checkBookingExistsAndUpdate(carIDs.get(0));

    }

    @FXML
    void bookCar2(ActionEvent event) {
        checkBookingExistsAndUpdate(carIDs.get(0));

    }

    @FXML
    void bookCar3(ActionEvent event) {
        checkBookingExistsAndUpdate(carIDs.get(0));

    }

    @FXML
    void onBack(ActionEvent event) throws IOException {
        btnCar1Booking.getScene().getWindow().hide();

        Stage backTo = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CityView.fxml"));
        Scene scene = new Scene(root);
        backTo.setScene(scene);
        backTo.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();

        date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        connection = handler.getConnection();
        String grabCarsQuery = "select * from rentalcar where city_id = ?";

        try {

            pst = connection.prepareStatement(grabCarsQuery);
            pst.setInt(1, PlanViewController.getInstance().getSelectedCityID());


            ResultSet rs = pst.executeQuery();

            int count = 0;


            while (rs.next()) {
                carModels.add(rs.getString("model"));
                carCosts.add(rs.getInt("cost"));
                carIDs.add(rs.getInt("id"));
                count += 1;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtCar1Name.setText(carModels.get(0));
        txtCar2Name.setText(carModels.get(1));
        txtCar3Name.setText(carModels.get(2));

        txtCar1Cost.setText("$" + Integer.toString(carCosts.get(0)));
        txtCar2Cost.setText("$" + Integer.toString(carCosts.get(1)));
        txtCar3Cost.setText("$" + Integer.toString(carCosts.get(2)));


        txtCityName.setText(PlanViewController.getInstance().getSelectedCityName());
    }

    public void checkBookingExistsAndUpdate (int carID) {
        connection = handler.getConnection();
        int confirmation = 0;
        String grabExistingBooking = "select * from rentalcar_booking where user_id = ? and rentalcar_id in (select id from rentalcar where city_id = ?)";

        try {

            pst = connection.prepareStatement(grabExistingBooking);
            pst.setInt(1, LoginController.getInstance().activeID());
            pst.setInt(2, PlanViewController.getInstance().getSelectedCityID());




            ResultSet rs = pst.executeQuery();

            int count = 0;


            while (rs.next()) {
                confirmation = rs.getInt("confirmation");
                count += 1;

            }

            if(count > 0) {
                String updateCarQuery = "UPDATE rentalcar_booking SET confirmation = ?, rentalcar_id = ?, date = ? WHERE confirmation = ?";

                connection = handler.getConnection();
                try {
                    pst = connection.prepareStatement(updateCarQuery);
                    pst.setInt(1, randomInt(8));
                    pst.setInt(2, carID);
                    pst.setDate(3, date);
                    pst.setInt(4, confirmation);

                    pst.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Car rental updated successfully!");

                alert.showAndWait();

            } else if(count == 0) {
                String insertBooking = "INSERT INTO rentalcar_booking (confirmation, date, rentalcar_id, user_id) VALUES (?,?,?,?)";

                connection = handler.getConnection();
                try {
                    pst = connection.prepareStatement(insertBooking);
                    pst.setInt(1, randomInt(8));
                    pst.setDate(2, date);
                    pst.setInt(3, carIDs.get(1));
                    pst.setInt(4, LoginController.getInstance().activeID());


                    pst.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Car rental booked successfully!");

                alert.showAndWait();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
