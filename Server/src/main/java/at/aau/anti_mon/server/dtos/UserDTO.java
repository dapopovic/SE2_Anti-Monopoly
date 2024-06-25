package at.aau.anti_mon.server.dtos;

import java.io.Serializable;

import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.enums.Roles;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    @NotBlank(message = "Benutzername darf nicht leer sein")
    @Size(min = 3, max = 20, message = "Benutzername muss zwischen 3 und 20 Zeichen lang sein")
    @JsonProperty("userName")
    private String userName;

    @JsonProperty("isOwner")
    private boolean isOwner;

    @JsonProperty("isReady")
    private boolean isReady;

    @NotBlank(message = "Money darf nicht leer sein")
    @JsonProperty("playerMoney")
    private int money;

    @JsonProperty("playerRole")
    private Roles playerRole;

    @JsonProperty("playerFigure")
    private Figures playerFigure;

    public UserDTO(String userName, boolean isOwner, boolean isReady, Roles playerRole, Figures playerFigure) {
        this.userName = userName;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
        this.playerRole = playerRole;        this.playerFigure = playerFigure;
    }

}
