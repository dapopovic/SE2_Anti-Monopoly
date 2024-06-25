package at.aau.anti_mon.client.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyGameCardDTO{

    String cityName;
    String streetName;
    int price;
    int rent;
    int baseRent;

    int hypotek;
    User owner;
    int houses;
    int housePrice;
    int hotels;
    int hotelPrice;
    boolean isForSale;
    int position;



    public PropertyGameCardDTO(PropertyGameCard propertyGameCard) {
        this.cityName = propertyGameCard.getCityName();
        this.streetName = propertyGameCard.getStreetName();
        this.price = propertyGameCard.getPrice();
        this.position = propertyGameCard.getPosition();
        this.isForSale = propertyGameCard.isForSale();
        this.owner = propertyGameCard.getOwner();
        this.houses = propertyGameCard.getHouses();
        this.hotels = propertyGameCard.getHotels();
        this.baseRent = propertyGameCard.getBaseRent();
        this.housePrice = propertyGameCard.getHousePrice();
        this.hotelPrice = propertyGameCard.getHotelPrice();
    }


}