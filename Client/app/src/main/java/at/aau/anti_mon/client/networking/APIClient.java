package at.aau.anti_mon.client.networking;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "http://se2-demo.aau.at:53215/game?userID="; // /game?userID=
    private static Retrofit retrofit = null;

    public static APIService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APIService.class);
    }
}
