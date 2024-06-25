package at.aau.anti_mon.client.dependencyinjection;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Singleton;

import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.ui.joingame.JoinGameViewModel;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class LiveDataModule {

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ERROR_JOIN_GAME")
    static MutableLiveData<String> provideJoinGameErrorLiveData(JoinGameViewModel joinGameViewModel) {
        return joinGameViewModel.getErrorLiveData();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ERROR_CREATE_GAME")
    static MutableLiveData<String> provideCreateGameErrorLiveData(CreateGameViewModel createGameViewModel) {
        return createGameViewModel.getErrorLiveData();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("INFO_JOIN_GAME")
    static MutableLiveData<String> provideJoinGameInfoLiveData(JoinGameViewModel joinGameViewModel) {
        return joinGameViewModel.getInfoLiveData();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("INFO_CREATE_GAME")
    static MutableLiveData<String> provideCreateGameInfoLiveData(CreateGameViewModel createGameViewModel) {
        return createGameViewModel.getInfoLiveData();

    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("INFO_LOBBY")
    static MutableLiveData<String> provideLobbyInfoLiveData(LobbyViewModel lobbyViewModel) {
        return lobbyViewModel.getInfoLiveData();

    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ERROR_LOBBY")
    static MutableLiveData<String> provideLobbyErrorLiveData(LobbyViewModel lobbyViewModel) {
        return lobbyViewModel.getErrorLiveData();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("INFO_GAME_BOARD")
    static MutableLiveData<String> provideGameBoardInfoLiveData(GameBoardViewModel gameBoardViewModel) {
        return gameBoardViewModel.getInfoLiveData();

    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ERROR_GAME_BOARD")
    static MutableLiveData<String> provideGameBoardErrorLiveData(GameBoardViewModel gameBoardViewModel) {
        return gameBoardViewModel.getErrorLiveData();
    }



}