package at.aau.anti_mon.client.ui.adapter;

import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;

public class CheckBoxBindingAdapter {
    @BindingAdapter("jumpToState")
    public static void setJumpToState(CheckBox checkBox, boolean enabled) {
        checkBox.setClickable(enabled);
        checkBox.jumpDrawablesToCurrentState();
    }
}