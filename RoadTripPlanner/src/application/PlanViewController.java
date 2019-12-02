package application;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PlanViewController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(DashboardController.getInstance().clickedPlan());
    }
}
