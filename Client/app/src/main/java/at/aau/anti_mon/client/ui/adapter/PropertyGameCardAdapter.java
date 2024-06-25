package at.aau.anti_mon.client.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.game.PropertyGameCardDTO;

public class PropertyGameCardAdapter extends RecyclerView.Adapter<PropertyGameCardAdapter.ViewHolder> {
    private final List<PropertyGameCardDTO> propertyGameCards;

    public PropertyGameCardAdapter(Set<PropertyGameCardDTO> propertyGameCards) {
        this.propertyGameCards = new ArrayList<>(propertyGameCards);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_gamecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PropertyGameCardDTO card = propertyGameCards.get(position);
        holder.bind(card);
    }

    @Override
    public int getItemCount() {
        return propertyGameCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //private final ImageView itemImage;
        private final TextView itemStreetName;
        private final TextView itemPriceNumber;
        private final TextView itemHousePriceNumber;
        private final TextView itemHotelPriceNumber;
        //private final TextView itemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemImage = itemView.findViewById(R.id.item_image);
            itemStreetName = itemView.findViewById(R.id.item_streetname);
            itemPriceNumber = itemView.findViewById(R.id.item_price_number);
            itemHousePriceNumber = itemView.findViewById(R.id.item_houseprice_number);
            itemHotelPriceNumber = itemView.findViewById(R.id.item_hotelprice_number);
            //itemDescription = itemView.findViewById(R.id.item_description);
        }

        public void bind(PropertyGameCardDTO card) {
            //itemImage.setImageResource(card.getImageResId());
            itemStreetName.setText(card.getStreetName());
            itemPriceNumber.setText(String.valueOf(card.getPrice()));
            itemHousePriceNumber.setText(String.valueOf(card.getHousePrice()));
            itemHotelPriceNumber.setText(String.valueOf(card.getHotelPrice()));
            //itemDescription.setText(card.getDescriptionResId());
        }
    }
}