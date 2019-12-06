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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HotelBookingController implements Initializable{

    @FXML
    private Text txtCityName;

    @FXML
    private Button txtBookHotel1;

    @FXML
    private Text txtHotelName1;

    @FXML
    private Text txtHotel1Cost;

    @FXML
    private Button txtBookHotel2;

    @FXML
    private Text txtHotelName2;

    @FXML
    private Text txtHotel2Cost;

    @FXML
    private Button txtBookHotel3;

    @FXML
    private Text txtHotelName3;

    @FXML
    private Text txtHotel3Cost;

    @FXML
    private Button btnBack;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    private ArrayList<String> hotelNames = new ArrayList<String>();
    private ArrayList<Integer> hotelCosts = new ArrayList<Integer>();
    private ArrayList<Integer> hotelIDs = new ArrayList<Integer>();

    private java.sql.Date date;


    public static int randomInt(int digits) {
        int minimum = (int) Math.pow(10, digits - 1); // minimum value with 2 digits is 10 (10^1)
        int maximum = (int) Math.pow(10, digits) - 1; // maximum value with 2 digits is 99 (10^2 - 1)
        Random random = new Random();
        return minimum + random.nextInt((maximum - minimum) + 1);
    }

    @FXML
    void bookHotel1(ActionEvent event) {
        checkBookingExistsAndUpdate(hotelIDs.get(0));
    }

    @FXML
    void bookHotel2(ActionEvent event) {
        checkBookingExistsAndUpdate(hotelIDs.get(1));
    }

    @FXML
    void bookHotel3(ActionEvent event) {
        checkBookingExistsAndUpdate(hotelIDs.get(2));
    }

    @FXML
    void onBack(ActionEvent event) throws IOException {
        txtBookHotel1.getScene().getWindow().hide();

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
        String grabHotelsQuery = "select * from hotel where city_id = ?";

        try {

            pst = connection.prepareStatement(grabHotelsQuery);
            pst.setInt(1, PlanViewController.getInstance().getSelectedCityID());


            ResultSet rs = pst.executeQuery();

            int count = 0;


            while (rs.next()) {
                hotelNames.add(rs.getString("name"));
                hotelCosts.add(rs.getInt("cost"));
                hotelIDs.add(rs.getInt("id"));
                count += 1;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtHotelName1.setText(hotelNames.get(0));
        txtHotelName2.setText(hotelNames.get(1));
        txtHotelName3.setText(hotelNames.get(2));

        txtHotel1Cost.setText("$" + Integer.toString(hotelCosts.get(0)));
        txtHotel2Cost.setText("$" + Integer.toString(hotelCosts.get(1)));
        txtHotel3Cost.setText("$" + Integer.toString(hotelCosts.get(2)));


        txtCityName.setText(PlanViewController.getInstance().getSelectedCityName());

    }

    public void checkBookingExistsAndUpdate (int hotelID) {
        connection = handler.getConnection();
        int confirmation = 0;
//        String grabExistingBooking = "select hotel_booking.confirmation from hotel_booking, hotel where hotel_booking.user_id = ? and hotel.city_id =?";
        String grabExistingBooking = "select * from hotel_booking where user_id = ? and hotel_id in (select id from hotel where city_id = ?)";
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
                String updateHotelQuery = "UPDATE hotel_booking SET confirmation = ?, date = ?, hotel_id = ? WHERE confirmation = ?";

                connection = handler.getConnection();
                try {
                    pst = connection.prepareStatement(updateHotelQuery);
                    pst.setInt(1, randomInt(8));
                    pst.setDate(2, date);
                    pst.setInt(3, hotelID);
                    pst.setInt(4, confirmation);

                    pst.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Hotel updated successfully!");

                alert.showAndWait();

            } else if(count == 0) {
                String insertBooking = "INSERT INTO hotel_booking (confirmation, date, hotel_id, user_id) VALUES (?,?,?,?)";

                connection = handler.getConnection();
                try {
                    pst = connection.prepareStatement(insertBooking);
                    pst.setInt(1, randomInt(8));
                    pst.setDate(2, date);
                    pst.setInt(3, hotelIDs.get(1));
                    pst.setInt(4, LoginController.getInstance().activeID());

                    pst.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Hotel booked successfully!");

                alert.showAndWait();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
