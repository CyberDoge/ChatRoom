package main;

import helper.Flag;
import models.Client;
import models.ConnectionFromClient;
import models.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class Core {

    private ServerSocket serverSocket;
    private Room room;

    public Core() {
        try {
            serverSocket = new ServerSocket(5555);
            room = new Room();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startChat() {
        try {
            System.out.println("starting chat");
            while (true) {
                ConnectionFromClient connection = new ConnectionFromClient(serverSocket.accept());
                String logining = connection.getLine();
                if (logining.startsWith(Flag.LOGIN.name)) {
                    int id;
                    if ((id = connection.tryConnect(logining, room)) != 0) {
                        Client client = new Client(connection, room, id, connection.getUserName());
                        client.getConnection().sendLine(Flag.SUCCESS.name + client.getUserId());
                        room.addClient(client, connection);
                    } else {
                        connection.sendLine(Flag.ERROR.name);
                        connection.dispose();
                    }
                } else if (logining.startsWith(Flag.REGISTER.name)) {
                    int id;
                    if ((id = connection.tryRegister(logining)) != 0) {
                        Client client = new Client(connection, room, id, connection.getUserName());
                        client.getConnection().sendLine(Flag.SUCCESS.name + client.getUserId());
                        room.addClient(client, connection);
                    } else {
                        connection.sendLine(Flag.ERROR.name);
                        connection.dispose();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
