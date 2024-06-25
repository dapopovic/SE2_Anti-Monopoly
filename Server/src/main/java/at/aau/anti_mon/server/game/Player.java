package at.aau.anti_mon.server.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    private String name;
    private int money;
    private List<PropertyGameCard> properties;

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
        this.properties = new ArrayList<>();
    }
    
    public void decreaseMoney(int amount) { this.money -= amount; }
    public void increaseMoney(int amount) { this.money += amount; }
    public void addProperty(PropertyGameCard property) { this.properties.add(property); }
    public void removeProperty(PropertyGameCard property) { this.properties.remove(property); }
    public boolean ownsProperty(PropertyGameCard property) { return this.properties.contains(property); }
}
