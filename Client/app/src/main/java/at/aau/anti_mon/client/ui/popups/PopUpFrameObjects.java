package at.aau.anti_mon.client.ui.popups;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


import at.aau.anti_mon.client.BR;
import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ActivityPopObjectsBinding;
import at.aau.anti_mon.client.game.Player;
import at.aau.anti_mon.client.ui.base.BaseFrame;
import at.aau.anti_mon.client.ui.gamefield.GameFieldViewModel;

public class PopUpFrameObjects extends BaseFrame<ActivityPopObjectsBinding, GameFieldViewModel> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pop_objects;
    }

    @Override
    protected Class<GameFieldViewModel> getViewModelClass() {
        return GameFieldViewModel.class;
    }

    @Override
    protected void injectDependencies() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Observer für den aktuellen Spieler
        viewModel.getCurrentPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                if (player != null) {
                    //viewDataBinding textViewPlayerName.setText(player.getUserName());
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Frame-spezifische Einstellungen
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getDialog().getWindow().setLayout((int) (width * .8), (int) (height * .8));
    }

    public void onX(View view) {
        dismiss();
    }
}