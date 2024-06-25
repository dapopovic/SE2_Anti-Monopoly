package at.aau.anti_mon.client.dependencyinjection;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.ui.joingame.JoinGameViewModel;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;
import at.aau.anti_mon.client.ui.startmenu.StartMenuViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreateGameViewModel.class)
    abstract ViewModel bindCreateGameViewModel(CreateGameViewModel createGameViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GameBoardViewModel.class)
    abstract ViewModel bindGameFieldViewModel(GameBoardViewModel gameBoardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LobbyViewModel.class)
    abstract ViewModel bindLobbyViewModel(LobbyViewModel lobbyViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(JoinGameViewModel.class)
    abstract ViewModel bindJoinGameViewModel(JoinGameViewModel joinGameViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StartMenuViewModel.class)
    abstract ViewModel bindStartMenuViewModel(StartMenuViewModel startMenuViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

}
