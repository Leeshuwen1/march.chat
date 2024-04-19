package ru.mulyukin.otus.march.chat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthorizationService authorizationService;
    private ClientHandler clientHandler;


    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new LinkedList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.authorizationService = new InMemoryAuthorizationService();
            System.out.println("сервис аутентификации запущен " + authorizationService.getClass().getSimpleName());
            System.out.println("Сервер запущен на порту: %d, ожидаем подключения клиентов\n " + port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket);
                } catch (Exception e) {
                    System.out.println("Возникла ошибка при обработке подлючившегося клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("К чату присоединился " + clientHandler.getNickName());
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getNickName());
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler elem : clients) {
            elem.sendMessage(message);
        }
    }

    public synchronized boolean isNickNameBusy(String nickName) {
        for (ClientHandler c : clients) {
            if (c.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendDirectMessage(String userName, String message) {
        for (ClientHandler elem : clients) {
            if (elem.getNickName().equals(userName)) {
                elem.sendMessage(message);
            }
        }
    }

    public void kick(String nickName) {
        for (ClientHandler elem : clients) {
            if (elem.getNickName().equals(nickName)) {
                clientHandler.disconect(nickName);
            }
        }
    }
}



