package at.aau.anti_mon.client.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.enums.Roles;
import at.aau.anti_mon.client.game.User;
import lombok.NonNull;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;
    private User currentUser;

    public UserAdapter() {
        this.users = new ArrayList<>();
        this.currentUser = null;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gameboard_cardview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Bind the User data to the views in the layout
        User user = users.get(position);
        String username = user.getUserName().length() <= 10 ? user.getUserName() : user.getUserName().substring(0, 10) + "...";
        String userInfo = holder.itemView.getContext().getString(R.string.player_info, username, user.getPlayerMoney() + " €");
        holder.playerInfo.setText(userInfo);
        holder.playerIcon.setImageResource(user.getPlayerRole() == Roles.MONOPOLIST ? R.drawable.monopolist : R.drawable.competititor);
        switch(user.getPlayerFigure()) {
            case BLUE_CIRCLE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_wheelbarrow);
            case BLUE_SQUARE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_hat);
            case BLUE_TRIANGLE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_ship);
            case GREEN_CIRCLE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_cat);
            case GREEN_SQUARE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_car);
            case GREEN_TRIANGLE ->
                holder.playerFigure.setImageResource(R.drawable.gameboard_monopoly_boot);
        }

        if (user.equals(currentUser)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ADD8E6"));
        }
        if (user.isCurrentPlayer()){
            holder.playerInfo.setTextColor(Color.parseColor("#FF0000"));
        }
        if (!user.isCurrentPlayer()){
            holder.playerInfo.setTextColor(Color.parseColor("#000000"));
        }
        if(user.getHasLostGame()){
            holder.itemView.setBackgroundColor(Color.parseColor("#6C757D"));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Method to update money and notify the adapter
    public void updatePlayerMoney(String username, int newMoney) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserName().equals(username)) {
                users.get(i).setPlayerMoney(newMoney);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }

    public void updateCurrentPlayersTurn(String username){
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).isCurrentPlayer()){
                users.get(i).setCurrentPlayer(false);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserName().equals(username)) {
                users.get(i).setCurrentPlayer(true);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }
    public void updatePlayerLostTheGame(String username){
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserName().equals(username)) {
                users.get(i).setHasLostGame(true);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<User> users, User currentUser) {
        this.users = users != null ? users : new ArrayList<>();
        this.currentUser = currentUser;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView playerInfo;
        ImageView playerIcon;
        ImageView playerFigure;

        UserViewHolder(View itemView) {
            super(itemView);
            playerInfo = itemView.findViewById(R.id.player_name);
            playerIcon = itemView.findViewById(R.id.player_icon);
            playerFigure = itemView.findViewById(R.id.player_figure);
        }
    }
}
