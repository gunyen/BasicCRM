package com.nguyen.capstonecrm.controller;

import com.nguyen.capstonecrm.DAO.AppointmentDaoImpl;
import com.nguyen.capstonecrm.DAO.DBConnection;
import com.nguyen.capstonecrm.DAO.UserDaoImpl;
import com.nguyen.capstonecrm.Scheduler;
import com.nguyen.capstonecrm.helper.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;

/**
 * Controller for Login.fxml
 */
public class LoginController implements Initializable {

    public TextField userNameText;
    public PasswordField passwordText;
    public Button submitLogin;
    public Label userZone;
    public Button translateEng;
    public Button translateFr;
    public Label userNameLabel;
    public Label passwordLabel;
    public Label loginLabel;
    public Label userZoneLabel;
    public Label verifyLabel;

    /**
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(ENGLISH);
        userZone.setText(ZoneId.systemDefault().toString());
    }

    /**
     * <p>User input information goes through verification. Lambda expression used for iterating through a file to record
     * data into login_activity.txt. User is notified if the information they input is incorrect. Correct input information
     * creates scene for Appointments.fxml</p>
     *
     * @param actionEvent the actionEvent to set
     */
    public void onSubmit(ActionEvent actionEvent) {
        DBConnection.openConnection();
        try {
            InputReportInterface input = (s1, s2) -> {
                String loginFile = "login_activity.txt";

                String loginDate = LocalDate.now().toString();
                String loginTimestamp = Timestamp.valueOf(LocalDateTime.now()).toString();

                File file = new File(loginFile);

                FileWriter loginFW = new FileWriter(file, true);

                PrintWriter loginPW = new PrintWriter(loginFW);

                if (UserDaoImpl.getUser(s1) == null) {
                    loginPW.println("nonexistent : " + loginDate + " : " + loginTimestamp + " : " + s2);
                } else {
                    loginPW.println(s1 + " : " + loginDate + " : " + loginTimestamp + " : " + s2);
                }

                loginPW.close();
                System.out.println("Login attempt recorded.");
            };
            if (VerifyUser.getUserPassword(userNameText.getText(), passwordText.getText())) {
                TimeHandler.meetingAlert(AppointmentDaoImpl.getAllAppointments("week"));
                input.inputReport(userNameText.getText(), "true");
                DBConnection.closeConnection();
                Scheduler.navigate(actionEvent, "Appointments.fxml");
            } else {
                input.inputReport(userNameText.getText(), "false");
                ResourceBundle rb = ResourceBundle.getBundle("Nat", Locale.getDefault());
                verifyLabel.setText(rb.getString("LoginError"));
                DBConnection.closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Translates all text within Login.fxml to English</p>
     *
     * @param actionEvent the actionEvent to set
     */
    public void transEng(ActionEvent actionEvent) {
        Locale.setDefault(ENGLISH);
        Translate.trans(loginLabel, userNameLabel, passwordLabel, userZoneLabel, translateFr, translateEng, submitLogin, verifyLabel);
    }

    /**
     * <p>Translates all text within Login.fxml to French</p>
     *
     * @param actionEvent the actionEvent to set
     */
    public void transFr(ActionEvent actionEvent) {
        Locale.setDefault(FRENCH);
        Translate.trans(loginLabel, userNameLabel, passwordLabel, userZoneLabel, translateFr, translateEng, submitLogin, verifyLabel);
    }
}
