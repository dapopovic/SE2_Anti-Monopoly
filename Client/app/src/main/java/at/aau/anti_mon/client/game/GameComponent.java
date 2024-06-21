package at.aau.anti_mon.client.game;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameComponent {

    public final static Integer DEFAULT_MONEY = 1500;
    private final int id;
    int position;

    public GameComponent(int id) {
        this.id = id;
    }

    public GameComponent(int id, int position) {
        this.id = id;
        this.position = position;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameComponent{" +
                "id=" + id +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameComponent that = (GameComponent) o;

        if (id != that.id) return false;
        return position == that.position;
    }
}
