package at.aau.anti_mon.client.adapters;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class VisibilityBindingAdapter {

    @BindingAdapter("visibility")
    public static void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}