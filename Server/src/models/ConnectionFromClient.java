package models;

import helper.DataBase;
import helper.Flag;

import java.io.*;
import java.net.Socket;

public class ConnectionFromClient {
    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;
    private String userName;
    private Client client = null;
    public ConnectionFromClient(Socket serverSocket) {
        this.socket = serverSocket;
        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLine(String string) {
        try {
            writer.writeUTF(string);
            writer.flush();
        } catch (IOException e) {
            System.err.println(Flag.ERROR + " massage wasn't sanded");
            e.printStackTrace();
        }
    }

    public String getLine() {
        String line;
        try {
            line = reader.readUTF();
        } catch (IOException e) {
            System.err.println(e);
            System.out.println("error reading line");
            return Flag.ERROR + "error";
        }
        return line;
    }

    public String getUserName() {
        return userName;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int tryConnect(String line, Room room) {
        int splitIndex;
        userName = line.substring(2, splitIndex = line.indexOf("$"));
        String password = line.substring(splitIndex + 1);
        //todo check if client is already online
        for ( Integer l : room.getClientMap().keySet()){
            if (room.getClientMap().get(l).getUserName().equals(userName))
                return 0;
        }
        return  DataBase.login(userName, password);
    }

    public int tryRegister(String line) {
        int splitIndex;
        userName = line.substring(2, splitIndex = line.indexOf("$"));
        String password = line.substring(splitIndex + 1);
        return DataBase.register(userName, password);
        //return DataBase.addClient(userName, password);
    }

    public void dispose() {
        try {
            socket.close();
            reader.close();
            writer.close();
          /*  if(client != null)
            client.getRoom().removeClient(client);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
