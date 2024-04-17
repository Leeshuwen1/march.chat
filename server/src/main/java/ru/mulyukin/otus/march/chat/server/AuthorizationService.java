package ru.mulyukin.otus.march.chat.server;

public interface AuthorizationService {
    String getNickNameByLoginAndPassword(String login, String password);
    boolean register(String login, String password, String nickname);

    boolean isLoginAlreadyExsist(String login);
    boolean isNickNameAlreadyExsist(String nickName);
}

