package sample.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.model.Connection;

import java.io.IOException;
import java.rmi.UnknownHostException;

public class SignInController {
    @FXML
    private Button signInBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label eventLabel;

    public void signInAction(ActionEvent event) {
        if (!(loginField.getText().equals("") && passwordField.getText().equals(""))) {
            try {
                Connection connection = new Connection();
                connection.tryToConnect(loginField.getText(), new String(passwordField.getText()));

                Platform.runLater(() -> {
                    String res;
                    if ((res = connection.getLine()).startsWith("s/")) {
                        System.out.println("success");
                        openChat(connection);
                        ((Stage) signInBtn.getScene().getWindow()).close();
                    } else if (res.equals("e/")) {
                        eventLabel.setText("wrong password or login");
                        connection.dispose();
                    }
                });
            } catch (UnknownHostException e) {
                eventLabel.setText("connection error");
            } catch (IOException e) {
                eventLabel.setText("Error");
                System.out.println(e.fillInStackTrace());
            }

        }
    }

    public void signUpAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/registration.fxml"));
        Stage primaryStage = (Stage) signUpBtn.getScene().getWindow();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat-room");
        primaryStage.show();
    }

    private void openChat(Connection connection) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResourceAsStream("/sample/view/chat_room.fxml"));


            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Chat-room");
            ChatRoomController controller = loader.getController();
            controller.init(connection, root);
            controller.render();

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
