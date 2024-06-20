package at.aau.anti_mon.client.manager;

import androidx.collection.SparseArrayCompat;

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

// Bottom ResourceMap
        resourceMap.put(R.id.startField, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(0, R.id.startField);

        resourceMap.put(R.id.rom1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(1, R.id.rom1);

        resourceMap.put(R.id.rom2, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(2, R.id.rom2);

        resourceMap.put(R.id.rom3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        fieldPositions.put(3, R.id.rom3);

        resourceMap.put(R.id.rom4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(4, R.id.rom4);

        resourceMap.put(R.id.rom5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(5, R.id.rom5);

        resourceMap.put(R.id.rom6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        fieldPositions.put(6, R.id.rom6);

        resourceMap.put(R.id.rom7, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(7, R.id.rom7);

        resourceMap.put(R.id.rom8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(8, R.id.rom8);

        resourceMap.put(R.id.rom9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(9, R.id.rom9);

        resourceMap.put(R.id.sittingField, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(10, R.id.sittingField);

// Left ResourceMap
        resourceMap.put(R.id.left1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(11, R.id.left1);

        resourceMap.put(R.id.left2, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(12, R.id.left2);

        resourceMap.put(R.id.left_3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        fieldPositions.put(13, R.id.left_3);

        resourceMap.put(R.id.left_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(14, R.id.left_4);

        resourceMap.put(R.id.left_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(15, R.id.left_5);

        resourceMap.put(R.id.left_7, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(16, R.id.left_7);

        resourceMap.put(R.id.left_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(17, R.id.left_8);

        resourceMap.put(R.id.left_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(18, R.id.left_9);

        resourceMap.put(R.id.unluckyField, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(19, R.id.unluckyField);

// Top ResourceMap
        resourceMap.put(R.id.top_1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(20, R.id.top_1);

        resourceMap.put(R.id.top_2, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(21, R.id.top_2);

        resourceMap.put(R.id.top_3, new int[]{R.drawable.rom_2, R.string.prison_field_description});
        fieldPositions.put(22, R.id.top_3);

        resourceMap.put(R.id.top_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(23, R.id.top_4);

        resourceMap.put(R.id.top_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(24, R.id.top_5);

        resourceMap.put(R.id.top_7, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(25, R.id.top_7);

        resourceMap.put(R.id.top_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(26, R.id.top_8);

        resourceMap.put(R.id.top_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(27, R.id.top_9);

        resourceMap.put(R.id.prison, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(28, R.id.prison);

// Right ResourceMap
        resourceMap.put(R.id.right_1, new int[]{R.drawable.rom_1, R.string.field_square_description});
        fieldPositions.put(29, R.id.right_1);

        resourceMap.put(R.id.right_2, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(30, R.id.right_2);

        resourceMap.put(R.id.right_3, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(31, R.id.right_3);

        resourceMap.put(R.id.right_4, new int[]{R.drawable.income_tax, R.string.income_tax_field_description});
        fieldPositions.put(32, R.id.right_4);

        resourceMap.put(R.id.right_5, new int[]{R.drawable.flughafen, R.string.airport_field_description});
        fieldPositions.put(33, R.id.right_5);

        resourceMap.put(R.id.right_6, new int[]{R.drawable.berlin_1, R.string.berlin1_field_description});
        fieldPositions.put(34, R.id.right_6);

        resourceMap.put(R.id.right_7, new int[]{R.drawable.take, R.string.take_field_description});
        fieldPositions.put(35, R.id.right_7);

        resourceMap.put(R.id.right_8, new int[]{R.drawable.berlin_2, R.string.berlin2_field_description});
        fieldPositions.put(36, R.id.right_8);

        resourceMap.put(R.id.right_9, new int[]{R.drawable.berlin_3, R.string.berlin3_field_description});
        fieldPositions.put(37, R.id.right_9);

    }



}
