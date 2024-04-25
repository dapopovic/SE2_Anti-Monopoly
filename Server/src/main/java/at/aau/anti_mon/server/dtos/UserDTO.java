package at.aau.anti_mon.server.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO implements Serializable {

    @NotBlank(message = "Benutzername darf nicht leer sein")
    @Size(min = 3, max = 20, message = "Benutzername muss zwischen 3 und 20 Zeichen lang sein")
    private String username;

}
