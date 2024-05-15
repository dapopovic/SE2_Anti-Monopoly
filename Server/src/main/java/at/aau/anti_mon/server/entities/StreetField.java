package at.aau.anti_mon.server.entities;

import at.aau.anti_mon.server.enums.GameFieldInformation;
import at.aau.anti_mon.server.enums.GameFieldType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Represents a street field in the game
 */
@Getter
@Setter
@Entity
@Table(name = "STREETFIELDS")
@PrimaryKeyJoinColumn(name = "street_gamefield_id")
public class StreetField extends GameField implements Serializable {

    @Column(name = "price")
    private int price;

    @Column(name = "rent")
    private int rent;

    @Column(name = "house_price")
    private int housePrice;

    @Column(name = "hotel_price")
    private int hotelPrice;

    @Column(name = "number_of_houses")
    private int numberOfHouses;

    @Column(name = "number_of_hotels")
    private int numberOfHotels;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Player owner;

    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected StreetField() {
    }

    /**
     * Builder-Constructor for the StreetField
     * @param builder the builder for the StreetField
     */
    private StreetField(StreetField.Builder builder) {
        this.setName(builder.name);
        this.setDescription(builder.description);
        this.setType(builder.type);
        this.setPosition(builder.position);
        this.setGameFieldType(builder.gameFieldType);
        this.setGameFieldInformation(builder.gameFieldInformation);
        this.setGame(builder.game);

        this.price = builder.price;
        this.rent = builder.rent;
        this.housePrice = builder.housePrice;
        this.hotelPrice = builder.hotelPrice;
        this.numberOfHouses = builder.numberOfHouses;
        this.numberOfHotels = builder.numberOfHotels;
        this.owner = builder.owner;
    }

    /**
     * Builder for the StreetFields
     */
    public static class Builder {
        private String name = "TestStreetField";
        // Hinzufügen der Felder von GameField
        private String description = "TestDescription";
        private String type = "TestType";
        private Integer position = 0;
        private GameFieldType gameFieldType = GameFieldType.STREET;
        private GameFieldInformation gameFieldInformation = GameFieldInformation.AMSTERDAM1;
        private Game game = null;
        ///////////////////////////////////////////////////
        private int price = 0;
        private int rent = 0;
        private int housePrice = 0;
        private int hotelPrice = 0;
        private int numberOfHouses = 0;
        private int numberOfHotels = 0;
        private Player owner = null;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withPosition(Integer position) {
            this.position = position;
            return this;
        }

        public Builder withGameFieldType(GameFieldType gameFieldType) {
            this.gameFieldType = gameFieldType;
            return this;
        }

        public Builder withGameFieldPlace(GameFieldInformation gameFieldInformation) {
            this.gameFieldInformation = gameFieldInformation;
            return this;
        }

        public Builder withGame(Game game) {
            this.game = game;
            return this;
        }

        public Builder withPrice(int price) {
            this.price = price;
            return this;
        }

        public Builder withRent(int rent) {
            this.rent = rent;
            return this;
        }

        public Builder withHousePrice(int housePrice) {
            this.housePrice = housePrice;
            return this;
        }

        public Builder withHotelPrice(int hotelPrice) {
            this.hotelPrice = hotelPrice;
            return this;
        }

        public Builder withNumberOfHouses(int numberOfHouses) {
            this.numberOfHouses = numberOfHouses;
            return this;
        }

        public Builder withNumberOfHotels(int numberOfHotels) {
            this.numberOfHotels = numberOfHotels;
            return this;
        }

        public Builder withOwner(Player owner) {
            this.owner = owner;
            return this;
        }

        public StreetField build() {
            return new StreetField(this);
        }
    }


}
