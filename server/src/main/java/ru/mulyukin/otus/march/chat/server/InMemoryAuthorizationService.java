package ru.mulyukin.otus.march.chat.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthorizationService implements AuthorizationService{
    private class User{
        private String login;
        private String password;
        private String nickName;

        public User(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }
    }
private List<User> users;

    public InMemoryAuthorizationService() {
        this.users = new ArrayList<>();
        for (int i = 1; i <=10  ; i++) {
            this.users.add(new User("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        for(User u: users){
            if(u.login.equals(login) && u.password.equals(password)){
                return u.nickName;
            }
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String nickname) {
        if(isLoginAlreadyExsist(login)){
            return false;
        }
        if (isNickNameAlreadyExsist(nickname)){
            return false;
        }
        users.add(new User(login, password, nickname));
        return true;
    }

    @Override
    public boolean isLoginAlreadyExsist(String login) {
        for(User u: users){
            if(u.login.equals(login) ){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNickNameAlreadyExsist(String nickName) {
        for(User u: users){
            if(u.login.equals(nickName) ){
                return true;
            }
        }
        return false;
    }
}
