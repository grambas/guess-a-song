package isdp.guess_a_song.model;

/**
 * Created on 16/10/2017, 6:11 PM
 */
//TODO PARCABLE IMPLEMENTATION
/**
 * Player model
 * @Author Mindaugas Milius
 */

public class UserProfile {
    private String name;

    public UserProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                '}';
    }
}
