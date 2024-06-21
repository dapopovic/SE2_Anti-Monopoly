package at.aau.anti_mon.client.ui.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import at.aau.anti_mon.client.dependencyinjection.ViewModelFactory;
import lombok.Getter;

@Getter
public abstract class BaseFrame<VIEWDATABINDING extends ViewDataBinding, VIEWMODEL extends BaseViewModel> extends DialogFragment {

    protected VIEWMODEL viewModel;
    protected VIEWDATABINDING viewDataBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        performDataBinding(inflater, container);
        return viewDataBinding.getRoot();
    }

    private void performDataBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(getViewModelClass());
        viewDataBinding.setVariable(getBindingVariable(), viewModel);
        viewDataBinding.setLifecycleOwner(this);
    }

    public abstract int getBindingVariable();
    protected abstract int getLayoutId();
    protected abstract Class<VIEWMODEL> getViewModelClass();
    protected abstract void injectDependencies();

}