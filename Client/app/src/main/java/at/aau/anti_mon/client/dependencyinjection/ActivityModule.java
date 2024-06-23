package at.aau.anti_mon.client.dependencyinjection;

import android.app.Activity;

import at.aau.anti_mon.client.ui.base.BaseActivity;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final BaseActivity<?,?> activity;

    public ActivityModule(BaseActivity<?,?> activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return activity;
    }

}
