package at.aau.anti_mon.client.events;

public class DiceNumbersEvent {
    private final String name;

    private String number;

    public DiceNumbersEvent(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getNumber() {
        return this.number;
    }
}
