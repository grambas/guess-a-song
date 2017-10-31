package isdp.guess_a_song.controller;

import isdp.guess_a_song.model.UserProfile;

/**
 * Created on 10/30/2017, 4:47 PM
 */

public class PlayerGame {
    private int status;
    private int auth;
    private UserProfile user;

    public PlayerGame(int status, int auth, UserProfile user) {
        this.status = status;
        this.auth = auth;
        this.user = user;

    }
    public PlayerGame(int status, int auth) {
        this.status = status;
        this.auth = auth;
        this.user =  new UserProfile();

    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }
}
