package at.aau.anti_mon.client.dependencyinjection;

import at.aau.anti_mon.client.ui.creategame.CreateGameActivity;
import at.aau.anti_mon.client.ui.gameboard.GameBoardActivity;
import at.aau.anti_mon.client.ui.instructions.GameInstructionsActivity;
import at.aau.anti_mon.client.ui.joingame.JoinGameActivity;
import at.aau.anti_mon.client.ui.loadgame.LoadGameActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.ui.startmenu.StartMenuActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ContributesAndroidInjector
    abstract StartMenuActivity contributeStartMenuActivity();

    @ContributesAndroidInjector
    abstract LobbyActivity contributeLobbyActivity();

    @ContributesAndroidInjector
    abstract CreateGameActivity contributeCreateGameActivity();

    @ContributesAndroidInjector
    abstract JoinGameActivity contributeJoinGameActivity();

    @ContributesAndroidInjector
    abstract LoadGameActivity contributeLoadGameActivity();

    @ContributesAndroidInjector
    abstract GameInstructionsActivity contributeGameInstructionsActivity();

    @ContributesAndroidInjector
    abstract GameBoardActivity contributeGameFieldActivity();
}
