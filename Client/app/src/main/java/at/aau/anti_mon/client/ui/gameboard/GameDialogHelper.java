package at.aau.anti_mon.client.ui.gameboard;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModel;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.game.PropertyGameCard;
import at.aau.anti_mon.client.game.User;

public class GameDialogHelper {


    private Context context;
    private User appUser;
    private GameBoardViewModel viewModel;

    public GameDialogHelper(Context context, User appUser, GameBoardViewModel viewModel) {
        this.context = context;
        this.appUser = appUser;
        this.viewModel = viewModel;
    }


    private void addHouseImageView(ImageView propertyImageView) {
        // Neues ImageView für das Haus erstellen
        ImageView houseImageView = new ImageView(context);
        houseImageView.setId(View.generateViewId());
        houseImageView.setLayoutParams(new FrameLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics())
        ));
        houseImageView.setImageResource(R.drawable.gameboard_monopoly_house); // Dein Bild

        // HouseImageView zum PropertyImageView hinzufügen
        ((ViewGroup) propertyImageView.getParent()).addView(houseImageView);

        // Position des houseImageView relativ zum propertyImageView setzen
        houseImageView.setX(propertyImageView.getX() + (float) propertyImageView.getWidth() / 2 - (float) houseImageView.getLayoutParams().width / 2);
        houseImageView.setY(propertyImageView.getY() + (float) propertyImageView.getHeight() / 2 - (float) houseImageView.getLayoutParams().height / 2);
    }

    private void addHotelImageView(ImageView propertyImageView) {
        GameBoardLayout gameLayout = ((Activity) context).findViewById(R.id.game_board_layout);

        // Neues ImageView für das Haus erstellen
        ImageView houseImageView = new ImageView(context);
        houseImageView.setId(View.generateViewId());
        houseImageView.setLayoutParams(new ConstraintLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics())
        ));
        houseImageView.setImageResource(R.drawable.gameboard_monopoly_hotel); // Dein Bild

        // Hinzufügen des houseImageView zum Layout
        gameLayout.addView(houseImageView);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(gameLayout);

        constraintSet.connect(houseImageView.getId(), ConstraintSet.START, propertyImageView.getId(), ConstraintSet.START, 0);
        constraintSet.connect(houseImageView.getId(), ConstraintSet.TOP, propertyImageView.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(houseImageView.getId(), ConstraintSet.END, propertyImageView.getId(), ConstraintSet.END, 0);
        constraintSet.connect(houseImageView.getId(), ConstraintSet.BOTTOM, propertyImageView.getId(), ConstraintSet.BOTTOM, 0);

        constraintSet.applyTo(gameLayout);
    }


    private void showPurchaseDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Aktion, wenn der OK-Button gedrückt wird
                    dialog.dismiss();
                });

        // Den Dialog erstellen und anzeigen
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showCustomDialog(Context context, int imageResId, String description, PropertyGameCard card) {
        //Dialog dialog = new Dialog(context);
        //dialog.setContentView(R.layout.dialog_custom_layout);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom_layout, null);

        ImageView fieldImage = view.findViewById(R.id.dialog_image);
        Button buyHouse = view.findViewById(R.id.dialog_button_buy_house);
        Button buyButton = view.findViewById(R.id.dialog_button_buy);
        Button hypothek = view.findViewById(R.id.dialog_button_hypothek);

        TextView fieldDescription = view.findViewById(R.id.dialog_description);
        TextView streetName = view.findViewById(R.id.dialog_streetname);
        TextView price = view.findViewById(R.id.dialog_price_number);
        TextView housePrice = view.findViewById(R.id.dialog_houseprice_number);
        TextView hotelPrice = view.findViewById(R.id.dialog_hotelprice_number);

        streetName.setText(card.getStreetName());
        price.setText(String.valueOf(card.getPrice()));
        housePrice.setText(String.valueOf(card.getHousePrice()));
        hotelPrice.setText(String.valueOf(card.getHotelPrice()));

        fieldImage.setImageResource(imageResId);
        fieldDescription.setText(description);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Feldinformation")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        if(appUser.getPlayerLocation() == card.getPosition()) {
            if (viewModel.getAppUsersTurn().getValue()) {
                if (card.hasOwner()) {
                    buyButton.setEnabled(false);
                    buyButton.setText("Purchased");
                } else {
                    buyButton.setEnabled(true);
                    buyButton.setText("Buy");
                    buyButton.setOnClickListener(v -> {
                        // TODO Logic for buying
                        card.buyProperty(appUser);
                        buyButton.setEnabled(false);
                        buyButton.setText("Purchased");
                        // TODO: Money & ChangeBalanceEvent
                        dialog.dismiss();
                        showPurchaseDialog("Erfolg", "Grundstück gekauft. " + card.getStreetName());

                    });
                }

                if (card.hasOwner()) {
                    if (card.getOwner().equals(appUser)) {
                        hypothek.setEnabled(true);
                        buyHouse.setEnabled(true);

                        if (card.getHouses() <= 4) {
                            buyHouse.setText("Buy House");
                        } else {
                            buyHouse.setText("Buy Hotel");
                        }
                        hypothek.setText("Hypothek");

                        hypothek.setOnClickListener(v -> {
                            // TODO Logic for hypothek
                            card.sellProperty(appUser);
                            hypothek.setEnabled(false);
                            hypothek.setText("Sold");
                            // TODO: Money & ChangeBalanceEvent
                            dialog.dismiss();
                            showPurchaseDialog("Erfolg", "Grundstück verkauft. " + card.getStreetName());
                        });

                        buyHouse.setOnClickListener(v -> {
                            // TODO Logic for buying house
                            if (card.getHouses() <= 4) {
                                card.addHouse();
                                // TODO: Return true/false and  Money & ChangeBalanceEvent
                                addHouseImageView(((Activity) context).findViewById(card.getId()));
                                dialog.dismiss();
                                showPurchaseDialog("Erfolg", "Haus gekauft. " + card.getStreetName());
                            } else {
                                card.addHotel();
                                // TODO: Return true/false and  Money & ChangeBalanceEvent
                                addHotelImageView(((Activity) context).findViewById(card.getId()));
                                dialog.dismiss();
                                showPurchaseDialog("Erfolg", "Haus gekauft. " + card.getStreetName());
                            }
                        });
                    } else {
                        hypothek.setEnabled(false);
                        hypothek.setText("Not yours");
                        buyHouse.setEnabled(false);
                        buyHouse.setText("Not yours");
                    }
                } else {
                    hypothek.setEnabled(false);
                    hypothek.setText("Not yours");
                    buyHouse.setEnabled(false);
                    buyHouse.setText("Not yours");
                }

            } else {
                buyButton.setEnabled(false);
                buyButton.setText("Not your turn");
                hypothek.setEnabled(false);
                hypothek.setText("Not your turn");
                buyHouse.setEnabled(false);
                buyHouse.setText("Not your turn");
            }
        }else {
            buyButton.setEnabled(false);
            buyButton.setText("Not on this field");
            hypothek.setEnabled(false);
            hypothek.setText("Not on this field");
            buyHouse.setEnabled(false);
            buyHouse.setText("Not on this field");
        }

        dialog.show();
    }

}
