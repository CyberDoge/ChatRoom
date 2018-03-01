package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.DataBase;
import helper.Flag;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private Map<Integer, Client> clientMap;
    private StringBuilder history;
    private ObjectMapper mapper;


    public Room() {
        mapper = new ObjectMapper();

        clientMap = Collections.synchronizedMap(new HashMap<>());
        history = new StringBuilder();
    }

    public void addClient(Client client, ConnectionFromClient connection) {
        connection.setClient(client);

        new Thread(client).start();
        try {
            client.getConnection().sendLine(mapper.writeValueAsString(clientMap));
            clientMap.put(client.getUserId(), client);
            Message message = new Message(Flag.LOGIN, new Date(), client.getUserName(), client.getUserId());
            DataBase.addMessage(message);
            sendMessageForAll(message);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public Map<Integer, Client> getClientMap() {
        return clientMap;
    }

    public void removeClient(Client client) {
        clientMap.remove(client.getUserId());
        sendMessageForAll(new Message(Flag.EXIT, new Date(), client.getUserName(), client.getUserId()));
    }

    public void sendMessageForAll(Message message) {
        new Thread(() -> {
            try {
                String line = mapper.writeValueAsString(message);
                for (Integer clientID : clientMap.keySet()) {
                    clientMap.get(clientID).getConnection().sendLine(line);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
