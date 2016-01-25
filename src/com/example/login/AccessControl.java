package com.example.login;
public interface AccessControl {

    public boolean signIn(String username, String password);

    public boolean isUserSignedIn();

    public String getPrincipalName();
}