package at.aau.anti_mon.client.networking;

import java.util.List;

import at.aau.anti_mon.client.game.GameStreet;
import at.aau.anti_mon.client.game.Player;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @POST("/api/players")
    Call<Player> createPlayer(@Body Player player);

    @POST("/api/gamestreet")
    Call<GameStreet> createGameStreet(@Body GameStreet gameStreet);

    @POST("/api/gamestreets/batch")
    Call<List<GameStreet>> createProperties(@Body List<GameStreet> properties);

    @POST("/api/database/sync")
    Call<String> syncDatabase(@Body String databaseJson);

    @GET("/api/players/{id}/gamestreets")
    Call<List<GameStreet>> getPlayerGameStreets(@Path("id") Long playerId);
}

