package at.aau.anti_mon.client.game;

import androidx.collection.SparseArrayCompat;

import at.aau.anti_mon.client.R;

public class PropertyGameCardInitializer {

    public static SparseArrayCompat<PropertyGameCard> initializePropertyGameCards() {
        SparseArrayCompat<PropertyGameCard> propertyGameCards = new SparseArrayCompat<>();

        propertyGameCards.put(R.id.field2, new PropertyGameCard(
                R.id.field2, "Rom", "Corso Impero", 60, 2, R.drawable.rom_1, R.string.rom1));

        propertyGameCards.put(R.id.field3, new PropertyGameCard(
                R.id.field3, "Rom", "Unter den Linden", 100, 3, R.drawable.rom_2,R.string.rom2));

        propertyGameCards.put(R.id.field6, new PropertyGameCard(
                R.id.field6, "Flughafen", "Süden", 200, 6, R.drawable.flughafen,R.string.airport_field_description));

        propertyGameCards.put(R.id.field7, new PropertyGameCard(
                R.id.field7, "Berlin", "Alexanderplatz", 100, 7, R.drawable.berlin_1,R.string.berlin1));

        propertyGameCards.put(R.id.field9, new PropertyGameCard(
                R.id.field9, "Berlin", "Kurfürstendamm", 100, 9, R.drawable.berlin_2,R.string.berlin2));

        propertyGameCards.put(R.id.field10, new PropertyGameCard(
                R.id.field10, "Berlin", "Potsdamer Straße", 120, 10, R.drawable.berlin_3,R.string.berlin3));

        propertyGameCards.put(R.id.field12, new PropertyGameCard(
                R.id.field12, "Madrid", "Plaza Mayor", 140, 12, R.drawable.madrid_1,R.string.madrid1));

        propertyGameCards.put(R.id.field13, new PropertyGameCard(
                R.id.field13, "Elektrizitätswerk", "Elektrizitätswert", 150, 13, R.drawable.power_station___kopie,R.string.power_station));

        propertyGameCards.put(R.id.field14, new PropertyGameCard(
                R.id.field14, "Madrid", "Gran Via", 140, 14, R.drawable.madrid_2___kopie,R.string.madrid2));

        propertyGameCards.put(R.id.field15, new PropertyGameCard(
                R.id.field15, "Madrid", "Paseo de la Castellana", 160, 15, R.drawable.madrid_3___kopie,R.string.madrid3));

        propertyGameCards.put(R.id.field16, new PropertyGameCard(
                R.id.field16, "Straßenbahn", "Westen", 200, 16, R.drawable.tram___kopie,R.string.tram));

        propertyGameCards.put(R.id.field17, new PropertyGameCard(
                R.id.field17, "Amsterdam", "Dam", 180, 17, R.drawable.amsterdam_dam___kopie,R.string.amsterdam1));

        propertyGameCards.put(R.id.field19, new PropertyGameCard(
                R.id.field19, "Amsterdam", "Leidestraat", 180, 19, R.drawable.amsterdam_leidsestraat___kopie,R.string.amsterdam2));

        propertyGameCards.put(R.id.field20, new PropertyGameCard(
                R.id.field20, "Amsterdam", "Kalverstraat", 200, 20, R.drawable.amsterdam_kalverstraat___kopie,R.string.amsterdam3));

        propertyGameCards.put(R.id.field22, new PropertyGameCard(
                R.id.field22, "Paris", "Rue de la Fayette", 220, 22, R.drawable.paris_rue,R.string.paris1));

        propertyGameCards.put(R.id.field24, new PropertyGameCard(
                R.id.field24, "Paris", "Rue de la Paix", 220, 24, R.drawable.paris_rue2,R.string.paris2));

        propertyGameCards.put(R.id.field25, new PropertyGameCard(
                R.id.field25, "Paris", "Champs Elysees", 240, 25, R.drawable.paris_champs,R.string.paris3));

        propertyGameCards.put(R.id.field26, new PropertyGameCard(
                R.id.field26, "Bahnhof", "Nord", 200, 26, R.drawable.train_station,R.string.train_station));

        propertyGameCards.put(R.id.field27, new PropertyGameCard(
                R.id.field27, "Brüssel", "Grote Markt", 260, 27, R.drawable.brussels_markt,R.string.bruessel1));

        propertyGameCards.put(R.id.field28, new PropertyGameCard(
                R.id.field28, "Brüssel", "Hoogstraat", 260, 28, R.drawable.brussels_hoogstraat,R.string.bruessel2));

        propertyGameCards.put(R.id.field29, new PropertyGameCard(
                R.id.field29, "Gaswerk", "Gaswerk", 150, 29, R.drawable.gasworks,R.string.gaswerk));

        propertyGameCards.put(R.id.field30, new PropertyGameCard(
                R.id.field30, "Brüssel", "Nieuwstraat", 280, 30 , R.drawable.brussels_nieuwstraat,R.string.bruessel3));

        propertyGameCards.put(R.id.field32, new PropertyGameCard(
                R.id.field32, "London", "Park Lane", 300,32 , R.drawable.london_piccadilly___kopie,R.string.london1));

        propertyGameCards.put(R.id.field33, new PropertyGameCard(
                R.id.field33, "London", "Picadilly", 300, 33, R.drawable.london_piccadilly___kopie,R.string.london2));

        propertyGameCards.put(R.id.field35, new PropertyGameCard(
                R.id.field35, "London", "Oxford Street", 320, 35, R.drawable.london_oxford___kopie,R.string.london3));

        propertyGameCards.put(R.id.field36, new PropertyGameCard(
                R.id.field36, "Busbetriebe", "Osten", 200, 36, R.drawable.bus_operators___kopie,R.string.bus_operators));

        propertyGameCards.put(R.id.field38, new PropertyGameCard(
                R.id.field38, "Athen", "La Plaka", 350, 38, R.drawable.athen_laplaka___kopie,R.string.athen1));

        propertyGameCards.put(R.id.field40, new PropertyGameCard(
                R.id.field40, "Athen", "Syntagma", 400, 40, R.drawable.athen_syntagma___kopie,R.string.athen2));



        return propertyGameCards;
    }
}
