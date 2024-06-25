package at.aau.anti_mon.client.game;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyGameCard extends GameComponent {

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


    private int imageResId;
    private int descriptionResId;

    public PropertyGameCard(int fieldId, String cityName, String streetName, int price, int position, int imageResId, int descriptionResId) {
        super(fieldId, position);
        this.cityName = cityName;
        this.streetName = streetName;
        this.price = price;

        this.position = position;
        this.imageResId = imageResId;
        this.descriptionResId = descriptionResId;
        this.isForSale = true;
        this.owner = null;
        this.houses = 0;
        this.hotels = 0;
        this.baseRent = price/10;

        // TODO: Change Values
        this.housePrice = price / 2;
        this.hotelPrice = price;
        actualize();
        Log.d(DEBUG_TAG, "PropertyGameCard created: " + streetName + " at position " + position + " with price " + price + "€" + " and base rent " + baseRent + "€");
    }

    public PropertyGameCard(int imageResId, int descriptionResId, int position) {
        super(imageResId,position);
        this.imageResId = imageResId;
        this.descriptionResId = descriptionResId;
        this.isForSale = true;
        this.owner = null;
        this.houses = 0;
        this.hotels = 0;


        // TODO: Change Values
        this.housePrice = price / 2;
        this.hotelPrice = price;
        actualize();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void addHouse() {
        houses++;
        actualize();
        Log.d(DEBUG_TAG, "House added to property " + streetName);
    }

    public void addHotel() {
        hotels++;
        actualize();
        Log.d(DEBUG_TAG, "Hotel added to property " + streetName);
    }

    public void removeHouse() {
        houses--;
        actualize();
        Log.d(DEBUG_TAG, "House removed from property " + streetName);
    }

    public void removeHotel() {
        hotels--;
        actualize();
        Log.d(DEBUG_TAG, "Hotel removed from property " + streetName);
    }

    public void buyProperty(User user) {
        if (user == null) {
            Log.d(DEBUG_TAG, "User is null");
        } else {
            user.buyProperty(this);
            isForSale = false;
            this.owner = user;
            Log.d(DEBUG_TAG, "User " + user.getUserName() + " bought property " + streetName + " for " + price + "€");
        }
    }

    public void sellProperty(User user){
        user.sellProperty(this);
        isForSale = true;
        owner = null;
    }

    public void actualize() {
        calculateRent();
        calculateHypotek();
    }

    public void calculateRent(){
        this.rent = (houses * housePrice) + (hotels * hotelPrice) + baseRent;
    }

    public void calculateHypotek(){
        this.hypotek = (price + (houses * housePrice) + (hotels * hotelPrice)) / 2;
    }



}
