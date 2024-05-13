package at.aau.anti_mon.client.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import at.aau.anti_mon.client.R;
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

        if (user.equals(currentUser)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ADD8E6"));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView playerInfo;

        UserViewHolder(View itemView) {
            super(itemView);
            playerInfo = itemView.findViewById(R.id.player_name);
        }
    }
}
