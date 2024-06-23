package at.aau.anti_mon.client.networking;

import java.util.List;

import at.aau.anti_mon.client.game.PropertyGameCard;
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
    Call<PropertyGameCard> createGameStreet(@Body PropertyGameCard propertyGameCard);

    @POST("/api/gamestreets/batch")
    Call<List<PropertyGameCard>> createProperties(@Body List<PropertyGameCard> properties);

    @POST("/api/database/sync")
    Call<String> syncDatabase(@Body String databaseJson);

    @GET("/api/players/{id}/gamestreets")
    Call<List<PropertyGameCard>> getPlayerGameStreets(@Path("id") Long playerId);
}

