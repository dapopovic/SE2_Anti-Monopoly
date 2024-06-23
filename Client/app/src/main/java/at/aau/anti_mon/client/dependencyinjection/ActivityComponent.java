package at.aau.anti_mon.client.dependencyinjection;

import at.aau.anti_mon.client.ui.creategame.CreateGameActivity;
import at.aau.anti_mon.client.ui.gameboard.GameBoardActivity;
import at.aau.anti_mon.client.ui.joingame.JoinGameActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.ui.main.MainActivity;
import at.aau.anti_mon.client.ui.startmenu.StartMenuActivity;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ActivityModule.class, ActivityBindingModule.class , ViewModelModule.class})
public interface ActivityComponent {

    void inject(MainActivity activity);
    void inject(StartMenuActivity activity);
    void inject(CreateGameActivity activity);
    void inject(JoinGameActivity joinGameActivity);
    void inject(LobbyActivity lobbyActivity);
    void inject(GameBoardActivity gameBoardActivity);

    @Subcomponent.Factory
    interface Factory {
        ActivityComponent create(ActivityModule module);
    }

}


