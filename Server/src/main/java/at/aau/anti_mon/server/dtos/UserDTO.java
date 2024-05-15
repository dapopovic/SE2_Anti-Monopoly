package at.aau.anti_mon.server.dtos;

import java.io.Serializable;

import at.aau.anti_mon.server.enums.Figures;
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
    private String username;
    private boolean isOwner;
    private boolean isReady;
    @NotBlank(message = "Money darf nicht leer sein")
    private int money;

    public UserDTO(String username, boolean isOwner, boolean isReady) {
        this.username = username;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
    }

}
