package sample.model;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    public Connection() throws UnknownHostException, IOException{

            socket = new Socket("localhost", 5555);

        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("error while creating streams \n" + e.getMessage());
        }
    }

    public String getLine() {
        try {
            String line = null;

            while (line == null || line.equals("")) {
                    line =reader.readUTF();
            }
            System.out.println("read: " + line);

            return line;
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage() + " while reading line");
            return "er/";
        }
    }

    public void write(String message) {
        try {
            writer.writeUTF(message);
            writer.flush();
        } catch (IOException e) {
            System.err.println("error while sending message");
        }
    }

    public void tryToConnect(String name, String password) {
        write("l/" + name + "$" + password);
    }
    public void tryToRegister(String name, String password){
        write("r/"+ name + "$" + password);
    }

    public void dispose() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("oh, shit!");

        }
    }
}
