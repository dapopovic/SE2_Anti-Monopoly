package at.aau.anti_mon.client.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import at.aau.anti_mon.client.dependencyinjection.ViewModelFactory;
import dagger.android.AndroidInjection;
import lombok.Getter;

/**
 * Base activity for all activities in the app
 * general structure for creating and managing the viewmodel and viewbinding
 * @param <VIEWDATABINDING>
 * @param <VIEWMODEL>
 */
@Getter
public abstract class BaseActivity <VIEWDATABINDING extends ViewDataBinding, VIEWMODEL extends BaseViewModel> extends AppCompatActivity {

    protected VIEWMODEL viewModel;
    protected VIEWDATABINDING viewDataBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        performDataBinding();
    }

    private void performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(getViewModelClass());
        viewDataBinding.setVariable(getBindingVariable(), viewModel);
        viewDataBinding.setLifecycleOwner(this);
    }

    public abstract int getBindingVariable();
    protected abstract int getLayoutId();
    protected abstract Class<VIEWMODEL> getViewModelClass();


}
