package sample.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sample.model.Connection;
import sample.model.Flag;
import sample.model.Message;
import sample.model.User;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomController {
    @FXML
    private VBox usersBox;
    @FXML
    private SplitPane mainSplit;
    @FXML
    private TextArea messageHistory;
    @FXML
    private TextField messageField;

    private Connection connection;
    private StringBuilder history;
    private Message message;

    private ObjectMapper mapper;

    private Map<Integer, User> userMap;
    private Thread receiveThread = new Thread(new MessageReceiver());
    private Runnable updateMessageThread = new Thread(new MessageUpdater());
    private Runnable updateUserThread = new Thread(new UserListUpdater());

    public ChatRoomController() {

    }

    public void init(Connection connection, Parent root) {
        messageHistory.setEditable(false);
        history = new StringBuilder("");
        mapper = new ObjectMapper();
        this.connection = connection;
        root.getScene().getWindow().setOnCloseRequest(e -> {
            try {
                connection.write(mapper.writeValueAsString(new Message(Flag.EXIT, new Date(), "")));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            } finally {
                Platform.exit();
                System.exit(0);
            }
        });

    }


    public void sendAction(ActionEvent event) {
        System.out.println("sending");
        Message message = new Message(Flag.MESSAGE, new Date(), messageField.getText());
        messageField.clear();
        try {
            connection.write(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        receiveThread.start();
    }

    private void userInfo(Integer id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(userMap.get(id).getName() + " messages:");
        alert.setHeaderText(null);
        StringBuilder text = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd-MM");
        for (Message m : userMap.get(id).getMessages()) {
            text.append(dateFormat.format(m.date) + ": " + m.mes + "\n");
        }
        alert.setContentText(text.toString());

        alert.showAndWait();
        System.out.println(id);
    }

    private class MessageReceiver implements Runnable {

        @Override
        public void run() {
            try {
                TypeReference types = new TypeReference<HashMap<Integer, User>>() {
                };

                userMap = mapper.readValue(connection.getLine(), types);
                Platform.runLater(() -> {
                    for (Integer id : userMap.keySet()) {
                        usersBox.getChildren().add(new Button(userMap.get(id).getName()) {
                            {
                                setMaxWidth(400);
                                setPrefWidth(400);
                                setOnAction((e) -> {
                                    userInfo(id);
                                });
                            }
                        });
                    }
                });

                while (true) {
                    System.out.println("reading");
                    message = mapper.readValue(connection.getLine(), Message.class);
                    System.out.println("message = " + message);
                    switch (message.flag) {
                        case MESSAGE:
                            Platform.runLater(updateMessageThread);
                            break;
                        case LOGIN:
                            Platform.runLater(updateUserThread);
                            break;
                        case EXIT:
                            Platform.runLater(updateUserThread);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UserListUpdater implements Runnable {
        @Override
        public void run() {
            if (message.flag == Flag.LOGIN) {
                userMap.put(message.userID, new User(message.userName, message.userID));
                usersBox.getChildren().add(new Button(message.userName) {
                    Integer id = message.userID;

                    {
                        setMaxWidth(400);
                        setPrefWidth(400);
                        setOnAction((e) -> {
                            userInfo(id);
                        });
                    }
                });
            } else if (message.flag == Flag.EXIT) {
                ObservableList<Node> buttons = usersBox.getChildren();
                for (int i = 0; i < buttons.size(); i++) {
                    if (((Button) buttons.get(i)).getText().equals(message.userName)) {
                        usersBox.getChildren().remove(i);
                        break;
                    }
                    userMap.remove(message.userID);
                }
            }
        }
    }

    private class MessageUpdater implements Runnable {
        @Override
        public void run() {
            System.out.println("update message");
            DateFormat format = new SimpleDateFormat("hh:mm dd-MM");
            String line = format.format(message.date) + " " + message.userName + ": " + message.mes + "\n";
            history.append(line);
            messageHistory.appendText(line);
            userMap.get(message.userID).getMessages().add(message);
        }
    }
}
