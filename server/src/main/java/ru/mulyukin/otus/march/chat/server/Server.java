package ru.mulyukin.otus.march.chat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new LinkedList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: %d, ожидаем подключения клиентов\n " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler elem : clients) {
            elem.sendMessage(message);
        }
    }
    public synchronized void sendDirectMessage(String userName){
        for(ClientHandler elem: clients ){
            if(userName.equals("user1")){
                elem.sendMessage(userName);
            } else if (userName.equals("user2")) {
                elem.sendMessage(userName);
            }else if (userName.equals("user3")){
                elem.sendMessage(userName);
            }
        }
    }
}
