package at.aau.anti_mon.client.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.game.User;
import lombok.Getter;

public class LobbyUserAdapter extends RecyclerView.Adapter<LobbyUserAdapter.UserViewHolder> {

    private final ArrayList<User> userList;

    public LobbyUserAdapter(ArrayList<User> userList) {
        this.userList = userList;
    }

    @Getter
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImageView;
        public TextView usernameTextView;
        public TextView additionalInfoTextView;
        public CheckBox readyCheckbox;

        public UserViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.item_imageView);
            usernameTextView = itemView.findViewById(R.id.item_name_textView);
            additionalInfoTextView = itemView.findViewById(R.id.item_descr_textView);
            readyCheckbox = itemView.findViewById(R.id.item_readyCheckbox);
        }
    }

    public void setReady(String username, boolean ready) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equals(username)) {
                userList.get(i).setReady(ready);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public boolean areAllUsersReady() {
        for (User user : userList) {
            if (!user.isReady()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //final int index = holder.getAdapterPosition();
        User user = userList.get(position);

            holder.usernameTextView.setText(user.getUsername());
            holder.getAdditionalInfoTextView().setText("Additional info");
            holder.getReadyCheckbox().setChecked(user.isReady());

        // Avatar anzeigen
        // if (holder.avatar != null) {
        //     holder.avatar.setImageResource(user.getAvatarResource());
        // }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }
    public void removeUser(String username) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equals(username)) {
                userList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}