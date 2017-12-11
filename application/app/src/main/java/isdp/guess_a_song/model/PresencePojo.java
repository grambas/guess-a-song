package isdp.guess_a_song.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;


/**
 * Created on 16/10/2017, 16:42 PM
 */

/**
 * This is data holder for presence
 * Converting JSON to Java class
 * with basic methods
 *
 * @Author Mindaugas Milius
 */

public class PresencePojo {
    private final String sender;
    private final String presence;
    private final String timestamp;
    private String name;
    private boolean auth;

    public PresencePojo(@JsonProperty("sender") String sender, @JsonProperty("name") String name, @JsonProperty("auth") boolean auth, @JsonProperty("presence") String presence, @JsonProperty("timestamp") String timestamp) {
        this.sender = sender;
        this.presence = presence;
        this.timestamp = timestamp;
        this.name = name;
        this.auth = auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSender() {
        return sender;
    }

    public String getPresence() {
        return presence;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isAuth() {
        return auth;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PresencePojo other = (PresencePojo) obj;

        return Objects.equal(this.sender, other.sender)
                && Objects.equal(this.presence, other.presence)
                && Objects.equal(this.timestamp, other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, presence, timestamp);
    }

    public String getSenderOrName() {
        if (this.name != null)
            return this.name;
        return this.sender;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(PresencePojo.class)
                .add("sender", sender)
                .add("presence", presence)
                .add("timestamp", timestamp)
                .toString();
    }
}
