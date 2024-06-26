package at.aau.anti_mon.client.events;

import lombok.Getter;

@Getter
public class ReportCheatingEvent {
    private String username;
    private String reporterName;
    private Boolean isCheater;

    public ReportCheatingEvent(String username, String reporterName, Boolean isCheater) {
        this.username = username;
        this.reporterName = reporterName;
        this.isCheater = isCheater;
    }
}
