package ru.mulyukin.otus.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String userName;

    private static int userCounter = 0;

    private void generateUsername() {
        userCounter++;
        this.userName = "user" + userCounter;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.generateUsername();
        new Thread(() -> {
            try {
                System.out.println("Подключился новый клиент");
                while (true) {
                    String message = inputStream.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith("/exit")) {
                            disconect();
                            break;
                        }
                        continue;
                    }
                        if (message.startsWith("/w")) {
                            String[] elems = message.split(" ", 3);
                                server.sendDirectMessage( elems[1], elems[2]);
                        } else {
                            server.broadcastMessage(userName + ": " + message);
                        }
                    }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconect();
            }
        }).start();
    }

    public void sendMessage(String messege) {
        try {
            outputStream.writeUTF(messege);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconect() {
        server.unSubscribe(this);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null && socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "ClientHandler{" +
                "server=" + server +
                ", socket=" + socket +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(server, that.server) && Objects.equals(socket, that.socket) && Objects.equals(inputStream, that.inputStream) && Objects.equals(outputStream, that.outputStream) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, socket, inputStream, outputStream, userName);
    }
}
