package com.example.finalproject;

public class Model {
    String username,email,password;

    public Model(String u,String e,String p){
        username = u;
        email = e;
        password = p;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

