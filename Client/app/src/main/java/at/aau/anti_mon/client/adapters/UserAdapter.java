package at.aau.anti_mon.client.adapters;

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
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> users;
    private User currentUser;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Bind the User data to the views in the layout
        User user = users.get(position);
        String username = user.getUsername().length() <= 10 ? user.getUsername() : user.getUsername().substring(0, 10) + "...";
        String userInfo = holder.itemView.getContext().getString(R.string.player_info, username, user.getMoney() + " €");
        holder.playerInfo.setText(userInfo);
        holder.playerIcon.setImageResource(user.getRole() == Roles.MONOPOLIST ? R.drawable.monopolist : R.drawable.competititor);
        switch(user.getFigure()) {
            case BLUE_CIRCLE ->
                holder.playerFigure.setImageResource(R.drawable.bluecircle);
            case BLUE_SQUARE ->
                holder.playerFigure.setImageResource(R.drawable.bluesquare);
            case BLUE_TRIANGLE ->
                holder.playerFigure.setImageResource(R.drawable.bluetriangle);
            case GREEN_CIRCLE ->
                holder.playerFigure.setImageResource(R.drawable.greencircle);
            case GREEN_SQUARE ->
                holder.playerFigure.setImageResource(R.drawable.greensquare);
            case GREEN_TRIANGLE ->
                holder.playerFigure.setImageResource(R.drawable.greentriangle);
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
        if(user.isLostGame()){
            holder.itemView.setBackgroundColor(Color.parseColor("#6C757D"));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Method to update money and notify the adapter
    public void updateUserMoney(String username, int newMoney) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.get(i).setMoney(newMoney);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }

    public void currentPlayer(String username){
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).isCurrentPlayer()){
                users.get(i).setCurrentPlayer(false);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.get(i).setCurrentPlayer(true);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }
    public void lostthegame(String username){
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.get(i).setLostGame(true);
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
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
