package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class ChangeBalanceEvent {
    private final String username;
    private Integer balance;

    public ChangeBalanceEvent(String username, Integer balance) {
        this.username = username;
        this.balance = balance;
    }
}
