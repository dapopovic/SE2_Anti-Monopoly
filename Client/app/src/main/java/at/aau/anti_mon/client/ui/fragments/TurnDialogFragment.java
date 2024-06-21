package at.aau.anti_mon.client.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class TurnDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_IS_CURRENT_PLAYER = "isCurrentPlayer";

    public static TurnDialogFragment newInstance(String message, boolean isCurrentPlayer) {
        TurnDialogFragment fragment = new TurnDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_IS_CURRENT_PLAYER, isCurrentPlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ARG_MESSAGE);
        boolean isCurrentPlayer = getArguments().getBoolean(ARG_IS_CURRENT_PLAYER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);

        if (isCurrentPlayer) {
            builder.setPositiveButton("OK", (dialog, id) -> {
                // Continue the game
            });
        } else {
            builder.setPositiveButton("OK", null); // Disable the button
        }

        return builder.create();
    }
}
