package ru.mulyukin.otus.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String nickName;


    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                System.out.println("Подключился новый клиент");
                if (tryToAuthenticate()) {
                    communicate();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void communicate() throws IOException {
        while (true) {
            String message = inputStream.readUTF();
            if (message.startsWith("/")) {
                if (message.startsWith("/exit")) {
                    break;
                }
                if (message.startsWith("/w")) {
                    String[] elems = message.split(" ", 3);
                    server.sendDirectMessage(elems[1], elems[2]);
                }
                if (message.startsWith("/kick ")) {
                    String[] elems = message.split(" ");
                    server.kick(elems[1]);
                }
                continue;
            }
            server.broadcastMessage(nickName + ": " + message);
        }
    }


    private boolean tryToAuthenticate() throws IOException {
        while (true) {
            // /auth login1 pass1
            String message = inputStream.readUTF();
            if (message.startsWith("/auth ")) {
                String[] token = message.split(" ");
                if (token.length != 3) {
                    sendMessage("Некорректный формат запроса");
                    continue;
                }
                String login = token[1];
                String password = token[2];

                String nickName = server.getAuthorizationService().getNickNameByLoginAndPassword(login, password);
                if (nickName == null) {
                    sendMessage("Неправильный логин/пароль");
                    continue;
                }
                if (server.isNickNameBusy(nickName)) {
                    sendMessage("Указанная учетная запись занята. Попробуйте зайти позднее");
                    continue;
                }

                this.nickName = nickName;
                server.subscribe(this);
                sendMessage(nickName + ", добро пожаловать в чат!");
                return true;

            } else if (message.startsWith("/register ")) {
                // /register login pass nickname
                String[] token = message.split(" ");
                if (token.length != 5) {
                    sendMessage("Некорректный формат запроса");
                    continue;
                }
                String login = token[1];
                String password = token[2];
                String nickName = token[3];
                String role = token[4];
                if (server.getAuthorizationService().isLoginAlreadyExsist(login)) {
                    sendMessage("Указанный логин занят");
                    continue;
                }
                if (server.getAuthorizationService().isNickNameAlreadyExsist(nickName)) {
                    sendMessage("Указанный никнэйм занят");
                    continue;
                }
                if (server.getAuthorizationService().isAdminAlreadyExsist(role)) {
                    sendMessage("Указанная роль занята");
                    continue;
                }
                if (!server.getAuthorizationService().register(login, password, nickName, role)) {
                    sendMessage("Не удалось пройти регистрацию");
                    continue;
                }
                this.nickName = nickName;
                server.subscribe(this);
                sendMessage("Вы успешно зарегистрировались " + nickName + ", добро пожаловать в чат!");
                return true;

            } else if (message.equals("/exit")) {
                return false;
            } else {
                sendMessage("Вам необходимо авторизоваться");
            }
        }
    }

    public void sendMessage(String messege) {
        try {
            outputStream.writeUTF(messege);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String disconect(String nickName) {
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

        return nickName;
    }

    public String getNickName() {
        return nickName;
    }

}
