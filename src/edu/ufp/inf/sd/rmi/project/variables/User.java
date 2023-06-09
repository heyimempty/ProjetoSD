package edu.ufp.inf.sd.rmi.project.variables;

import java.util.UUID;

public class User {

    private String username;
    private String password;
    private Token token;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = new Token(username);
    }

    private boolean verifyToken(){
        return token.verify();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(Token token){
        this.token=token;
    }
    public Token getToken() {
        return this.token;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + this.username + '\'' +
                ", password='" + this.password + '\'' +
                ", token=" + this.token +
                '}';
    }
}