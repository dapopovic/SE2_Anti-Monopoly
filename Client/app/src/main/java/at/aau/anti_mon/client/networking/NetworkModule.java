package at.aau.anti_mon.client.networking;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * This is a Dagger module. We use this to pass in the View
 */
@Module
public class NetworkModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    WebSocketClient provideWebSocketClient(OkHttpClient okHttpClient) {
        return new WebSocketClient(okHttpClient);
    }
}
