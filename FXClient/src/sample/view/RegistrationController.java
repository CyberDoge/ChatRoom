package sample.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.model.Connection;
import sample.model.Flag;

import java.io.IOException;
import java.rmi.UnknownHostException;

public class RegistrationController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private TextField loginField;
    @FXML
    private Label eventLabel;

    public void cancelAction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/main.fxml"));
            Stage primaryStage = (Stage) eventLabel.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Chat-room");
            primaryStage.show();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void registerAction(ActionEvent event) {
        if (passwordField.getText().equals(confirmField.getText())) {
            try {
                Connection connection = new Connection();
                connection.tryToRegister(loginField.getText(), passwordField.getText());
                Platform.runLater(() -> {
                    try {
                        String res;
                        if ((res = connection.getLine()).startsWith(Flag.SUCCESS.name)) {
                            openChat(connection);
                            ((Stage) eventLabel.getScene().getWindow()).close();
                        } else if (res.equals("e/")) {
                            Platform.runLater(() -> {
                                eventLabel.setText("User with this name is already exist");
                            });
                            connection.dispose();
                        }
                    } finally {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (UnknownHostException e) {
                eventLabel.setText("connection error");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else eventLabel.setText("passwords are not confirm");
    }

    private void openChat(Connection connection) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResourceAsStream("/sample/view/chat_room.fxml"));


            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Chat-room");
            primaryStage.show();

            ChatRoomController controller = loader.getController();
            controller.init(connection, root);
            controller.render();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
