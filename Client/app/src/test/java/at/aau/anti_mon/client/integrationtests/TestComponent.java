package at.aau.anti_mon.client.integrationtests;

import at.aau.anti_mon.client.AppComponent;
import at.aau.anti_mon.client.networking.NetworkModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {NetworkModule.class})
public interface TestComponent {
    void inject(WebSocketClientTest webSocketClientTest);
}
