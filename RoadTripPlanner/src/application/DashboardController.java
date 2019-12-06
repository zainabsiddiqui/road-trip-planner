package application;

import dbconnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
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



public class DashboardController implements Initializable{

    @FXML
    private TilePane tpPlanDisplay;

    @FXML
    private Text txtUserName;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnCreateNew;

    @FXML
    private Label lblPlanCount;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;

    private String clickedPlanName;

    public String clickedPlan() {
        return clickedPlanName;
    }

    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        handler = new DBHandler();
//        setUsername(LoginController.getInstance().username());

        List<Button> buttonlist = new ArrayList<>();

        connection = handler.getConnection();
        String getPlans = "SELECT * FROM plan WHERE user_id=?";

            try {
                pst = connection.prepareStatement(getPlans);
                pst.setInt(1, LoginController.getInstance().activeID());
                ResultSet rs = pst.executeQuery();

                tpPlanDisplay.setHgap(10);
                tpPlanDisplay.setVgap(10);


                int count = 0;


                while(rs.next()) {
                    buttonlist.add(new Button(rs.getString("name")));
                    tpPlanDisplay.getChildren().clear();
                    tpPlanDisplay.getChildren().addAll(buttonlist);


//                    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
//                        public void handle(ActionEvent e)
//                        {
//                            try {
//                                activePlanID = rs.getInt("ID");
//                            } catch (SQLException e1) {
//                                e1.printStackTrace();
//                            }
//
//                            btnLogout.getScene().getWindow().hide();
//
//                            Stage viewPlan = new Stage();
//                            Parent root = null;
//                            try {
//                                root = FXMLLoader.load(getClass().getResource("PlanView.fxml"));
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            }
//                            Scene scene = new Scene(root);
//                            viewPlan.setScene(scene);
//                            viewPlan.show();
//                        }
//                    };
                    buttonlist.get(count).setOnAction(event -> {
                            Object node = event.getSource();
                            Button b = (Button) node;
                            clickedPlanName = b.getText();

                            btnLogout.getScene().getWindow().hide();

                            Stage viewPlan = new Stage();
                            Parent root = null;
                            try {
                                root = FXMLLoader.load(getClass().getResource("PlanView.fxml"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            Scene scene = new Scene(root);
                            viewPlan.setScene(scene);
                            viewPlan.show();
                            });
                    count += 1;
                }

                lblPlanCount.setText(Integer.toString(count));

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            setUsername(LoginController.getInstance().username());


    }

    public void setUsername(String user) {
        this.txtUserName.setText(user);
    }

    @FXML
    void createNewPlan(ActionEvent event) throws IOException {
        btnLogout.getScene().getWindow().hide();

        Stage newPlan = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PlanCreation.fxml"));
        Scene scene = new Scene(root);
        newPlan.setScene(scene);
        newPlan.show();
    }

    @FXML
    void logOut(ActionEvent event) throws IOException {
        btnLogout.getScene().getWindow().hide();

        Stage backToLogin = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        backToLogin.setScene(scene);
        backToLogin.show();
    }

}