package at.aau.anti_mon.client.game;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonDeserialize(builder = User.UserBuilder.class)
public class User implements IUser{

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("isOwner")
    private boolean isOwner;

    @JsonProperty("isReady")
    private boolean isReady;

    @JsonProperty("playerMoney")
    private int playerMoney;

    @JsonProperty("playerRole")
    private Roles playerRole;

    @JsonProperty("playerFigure")
    private Figures playerFigure;

    @JsonProperty("currentPlayer")
    private boolean currentPlayer;

    @JsonProperty("lostGame")
    private boolean hasLostGame;

    @JsonProperty("location")
    int playerLocation;

    @JsonProperty("propertyGameCards")
    Set<PropertyGameCardDTO> propertyGameCards;

    @JsonProperty("lobbyPin")
    Integer lobbyPin;

    public User() {
    }

    private User(UserBuilder builder) {
        this.userName = builder.userName;
        this.isOwner = builder.isOwner;
        this.isReady = builder.isReady;
        this.playerMoney = builder.playerMoney;
        this.playerRole = builder.playerRole;
        this.playerFigure = builder.playerFigure;
        this.currentPlayer = builder.currentPlayer;
        this.hasLostGame = builder.lostGame;
        this.playerLocation = builder.playerLocation;
        this.propertyGameCards = builder.propertyGameCards != null ? builder.propertyGameCards : new HashSet<>();
        this.lobbyPin = builder.lobbyPin;
    }

    @Override
    public boolean getHasLostGame() {
        return hasLostGame;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserBuilder {
        private final String userName;
        private boolean isOwner;
        private boolean isReady;
        private int playerMoney = 1500; // default value
        private Roles playerRole;
        private Figures playerFigure;
        private boolean currentPlayer = false; // default value
        private boolean lostGame = false; // default value
        private int playerLocation = 0; // default value
        private Set<PropertyGameCardDTO> propertyGameCards = new HashSet<>(); // default value
        private Integer lobbyPin;

        @JsonCreator
        public UserBuilder(@JsonProperty("userName") String userName,
                           @JsonProperty("isOwner") boolean isOwner,
                           @JsonProperty("isReady") boolean isReady) {
            this.userName = userName;
            this.isOwner = isOwner;
            this.isReady = isReady;
        }


        public UserBuilder playerMoney(@JsonProperty("playerMoney") int playerMoney) {
            this.playerMoney = playerMoney;
            return this;
        }

        public UserBuilder playerRole(@JsonProperty("playerRole") Roles playerRole) {
            this.playerRole = playerRole;
            return this;
        }

        public UserBuilder playerFigure(@JsonProperty("playerFigure") Figures playerFigure) {
            this.playerFigure = playerFigure;
            return this;
        }

        public UserBuilder currentPlayer(@JsonProperty("currentPlayer") boolean currentPlayer) {
            this.currentPlayer = currentPlayer;
            return this;
        }

        public UserBuilder lostGame(@JsonProperty("lostGame") boolean lostGame) {
            this.lostGame = lostGame;
            return this;
        }

        public UserBuilder playerLocation(@JsonProperty("playerLocation") int playerLocation) {
            this.playerLocation = playerLocation;
            return this;
        }

        public UserBuilder propertyGameCards(@JsonProperty("propertyGameCards") Set<PropertyGameCardDTO> propertyGameCards) {
            this.propertyGameCards = propertyGameCards;
            return this;
        }

        public UserBuilder lobbyPin(@JsonProperty("lobbyPin") Integer lobbyPin) {
            this.lobbyPin = lobbyPin;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public void update(User user) {
        // Update the fields if needed
        Log.d("User", "update: " + user);
    }

    public void buyProperty(PropertyGameCard property){
        propertyGameCards.add(new PropertyGameCardDTO(property));
        Log.d("User", "buyProperty: " + propertyGameCards);
    }

    public void sellProperty(PropertyGameCard property){
        propertyGameCards.remove(new PropertyGameCardDTO(property));
        Log.d("User", "sellProperty: " + propertyGameCards);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User user)) {
            return false;
        }
        return userName.equals(user.userName) && isOwner == user.isOwner && isReady == user.isReady && playerMoney == user.playerMoney;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, isOwner, isReady, playerMoney);
    }


}



    // Gleiche User Objekte wenn Username gleich ist.
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

     */

    /* // Gleiche User Objekte wenn alle Variablen gleich sind.
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return isOwner == user.isOwner &&
                isReady == user.isReady &&
                money == user.money &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, isOwner, isReady, money);
    }
     */


