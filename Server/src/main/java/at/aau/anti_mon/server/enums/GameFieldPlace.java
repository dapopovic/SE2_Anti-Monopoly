package at.aau.anti_mon.server.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GameFieldPlace {

    START("Start"),
    ROM1("Corso Impero"),
    EVENT1("Aktionskarte"),
    ROM2("Villa Appina"),
    TAX1("Einkommenssteuer"),
    TRAFFIC1("Flughafen"),
    BERLIN1("Alexanderplatz"),
    EVENT2("Aktionskarte"),
    BERLIN2("Kurfürstendamm"),
    BERLIN3("Potsdamer Platz"),
    PRISON1("Sightseeing Tour"),
    MADRID1("Plaza Major"),
    POWER1("Elektrizitätswerk"),
    MADRID2("Gran Via"),
    MADRID3("Paeso de la Castellana"),
    TRAFFIC2("Straßenbahn"),
    AMSTERDAM1("Dam"),
    EVENT3("Aktionskarte"),
    AMSTERDAM2("Leidenstraat"),
    AMSTERDAM3("Klavierstraat"),
    PRICEWAR("Preiskrieg"),
    PARIS1("Rue la Fayette"),
    EVENT4("Aktionskarte"),
    PARIS2("Rue de la Paix"),
    PARIS3("Champs Elysees"),
    TRAFFIC3("Bahnhof"),
    BRUESSEL1("Grand Markt"),
    BRUESSEL2("Hoogstraat"),
    POWER2("Gaswerk"),
    BRUESSEL3("Nieuwstraat"),
    PRISON2("Gefängnis"),
    LONDON1("Park Lane"),
    LONDON2("Picadelly Circus"),
    EVENT5("Aktionskarte"),
    LONDON3("Oxford Street"),
    TRAFFIC4("Busbetriebe"),
    EVENT6("Aktionskarte"),
    ATHEN1("La Plaka"),
    TAX2("Zusatzsteuer"),
    ATHEN2("Syntagma");


    private final String name;

    GameFieldPlace(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}

