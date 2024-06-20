package at.aau.anti_mon.client.ui.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import at.aau.anti_mon.client.R;

public class ObjectsDialogFragment extends DialogFragment {

    public ObjectsDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_pop_roles, container, false);

        // Schließen des Dialogs bei Klick auf "X"
        view.findViewById(R.id.popup_close).setOnClickListener(v -> dismiss());

        // Weitere Initialisierungen
        processIntent(view);

        return view;
    }

    private void processIntent(View view) {
        // Logik zur Verarbeitung von Intents
        Log.d(this.getClass().getName(), "in processIntent");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set Dialog dimensions
        if (getDialog() != null) {
            DisplayMetrics dm = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = (int) (dm.widthPixels * 0.8);
            int height = (int) (dm.heightPixels * 0.8);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    public void closeDialog(View view) {
        dismiss();
    }
}