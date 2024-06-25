package at.aau.anti_mon.client.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.collection.SparseArrayCompat;
import androidx.lifecycle.MutableLiveData;

import at.aau.anti_mon.client.R;
import lombok.Getter;

@Getter
public class ResourceManager {


    // SparseArrayCompat für das Mapping von View-IDs zu Bild- und Textressourcen
    private SparseArrayCompat<int[]> resourceMap;
    private SparseArrayCompat<Integer> fieldPositions;


    public ResourceManager() {
        initResourceMap();
    }

    private void initResourceMap() {
        resourceMap = new SparseArrayCompat<>();
        fieldPositions = new SparseArrayCompat<>();

        resourceMap.put(R.id.field1, new int[]{R.drawable.start, R.string.start_field});
        fieldPositions.put(0, R.id.field1);

        resourceMap.put(R.id.field2, new int[]{R.drawable.rom_1, R.string.rom1});
        fieldPositions.put(1, R.id.field2);

        resourceMap.put(R.id.field3, new int[]{R.drawable.take, R.string.rom2});
        fieldPositions.put(2, R.id.field3);

        resourceMap.put(R.id.field3, new int[]{R.drawable.take, R.string.take});
        fieldPositions.put(3, R.id.field3);

        resourceMap.put(R.id.field4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(4, R.id.field4);

        resourceMap.put(R.id.field5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(5, R.id.field5);

        resourceMap.put(R.id.field6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        fieldPositions.put(6, R.id.field6);

        resourceMap.put(R.id.field6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        fieldPositions.put(6, R.id.field6);

        resourceMap.put(R.id.field7, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(7, R.id.field7);

        resourceMap.put(R.id.field8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(8, R.id.field8);

        resourceMap.put(R.id.field9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(9, R.id.field9);

        resourceMap.put(R.id.field10, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(10, R.id.field10);

// Left ResourceMap
        resourceMap.put(R.id.field11, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(11, R.id.field11);

        resourceMap.put(R.id.field12, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(12, R.id.field12);

        resourceMap.put(R.id.field13, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        fieldPositions.put(13, R.id.field13);

        resourceMap.put(R.id.field14, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(14, R.id.field14);

        resourceMap.put(R.id.field15, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(15, R.id.field15);

        resourceMap.put(R.id.field16, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(16, R.id.field16);

        resourceMap.put(R.id.field17, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(17, R.id.field17);

        resourceMap.put(R.id.field18, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(18, R.id.field18);

        resourceMap.put(R.id.field19, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(19, R.id.field19);

// Top ResourceMap
        resourceMap.put(R.id.field20, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(20, R.id.field20);

        resourceMap.put(R.id.field21, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(21, R.id.field21);

        resourceMap.put(R.id.field22, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        fieldPositions.put(22, R.id.field22);

        resourceMap.put(R.id.field23, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(23, R.id.field23);

        resourceMap.put(R.id.field24, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(24, R.id.field24);

        resourceMap.put(R.id.field25, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(25, R.id.field25);

        resourceMap.put(R.id.field26, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(26, R.id.field26);

        resourceMap.put(R.id.field27, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(27, R.id.field27);

        resourceMap.put(R.id.field28, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(28, R.id.field28);

// Right ResourceMap
        resourceMap.put(R.id.field29, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(29, R.id.field29);

        resourceMap.put(R.id.field30, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(30, R.id.field30);

        resourceMap.put(R.id.field31, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(31, R.id.field31);

        resourceMap.put(R.id.field32, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(32, R.id.field32);

        resourceMap.put(R.id.field33, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(33, R.id.field33);

        resourceMap.put(R.id.field34, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        fieldPositions.put(34, R.id.field34);

        resourceMap.put(R.id.field35, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(35, R.id.field35);

        resourceMap.put(R.id.field36, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(36, R.id.field36);

        resourceMap.put(R.id.field37, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(37, R.id.field37);

        resourceMap.put(R.id.field38, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(38, R.id.field38);

        resourceMap.put(R.id.field39, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(39, R.id.field39);

        resourceMap.put(R.id.field40, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(40, R.id.field40);

    }

    // Methode zum Abrufen der Ressourcen basierend auf der View-ID
    public int[] getResourceForViewId(int viewId) {
        return resourceMap.get(viewId);
    }

    // Methode zum Abrufen der View-ID basierend auf der Feldposition
    public Integer getViewIdForPosition(int position) {
        return fieldPositions.get(position);
    }

    public void openCustomDialogForViewId(Context context, int viewId) {
        int[] resources = getResourceForViewId(viewId);
        if (resources != null) {
            int imageResId = resources[0];
            int textResId = resources[1];

            // Erstelle den benutzerdefinierten Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_custom_layout, null);
            builder.setView(dialogView);

            // Setze die Bild- und Textressourcen
            ImageView dialogImage = dialogView.findViewById(R.id.dialog_image);
            TextView dialogStreetName = dialogView.findViewById(R.id.dialog_streetname);
            TextView dialogDescription = dialogView.findViewById(R.id.dialog_description);

            dialogImage.setImageResource(imageResId);
            dialogStreetName.setText(context.getString(textResId));
            dialogDescription.setText(context.getString(textResId)); // Falls Beschreibung gleich der Streetname ist, sonst entsprechend anpassen

            AlertDialog dialog = builder.create();

            // Schließe den Dialog, wenn der Schließen-Button geklickt wird
            Button closeButton = dialogView.findViewById(R.id.closeButton);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

}
