package at.aau.anti_mon.server.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// fixme avoid using different names for code and database
// fixme this applies to all entities
/**
 * Represents an event card in the game
 * @author ottzoeke
 */
@Getter
@Setter
@Entity
@Table(name = "EVENTCARD")
public class EventCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventcard_id")
    private Integer id;

    @Column(name = "eventcard_name")
    private String name;

    @Column(name = "eventcard_description")
    private String description;

    @Column(name = "eventcard_value")
    private Integer value;

    /**
     * Needed so the Tables can be automatically created in the DB
     * Protected since it should not be used directly
     */
    protected EventCard() {
    }

    private EventCard(EventCard.Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.value = builder.value;
    }

    /**
     * Builder for the EventCards
     */
    public static class Builder {
        private String name = "TestCard";  // Standardwert
        private String description;
        private Integer value;

        public EventCard.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public EventCard.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EventCard.Builder withValue(Integer value) {
            this.value = value;
            return this;
        }

        public EventCard build() {
            return new EventCard(this);
        }
    }

}
