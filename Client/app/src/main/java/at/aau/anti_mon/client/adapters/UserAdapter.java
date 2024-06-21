package at.aau.anti_mon.client.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import at.aau.anti_mon.client.databinding.CardviewLayoutBinding;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.ui.gamefield.GameFieldViewModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private  ArrayList<User> users;

    private final GameFieldViewModel viewModel;
    private final AsyncListDiffer<User> differ;

    public UserAdapter(GameFieldViewModel viewModel) {
        this.viewModel = viewModel;
        differ = new AsyncListDiffer<>(this, new UserAdapter.UserDiffCallback());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardviewLayoutBinding binding = CardviewLayoutBinding.inflate(inflater, parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = differ.getCurrentList().get(position);
        holder.bind(user, viewModel);
        Log.d("LobbyUserAdapter", "Binding user: " + user.getUsername() + " isReady: " + user.isReady());
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void updateUsers(List<User> newUsers) {
        differ.submitList(newUsers);
        Log.d("LobbyUserAdapter", "Updated user list with AsyncListDiffer");
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

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final CardviewLayoutBinding binding;

        public UserViewHolder(CardviewLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user, GameFieldViewModel viewModel) {
            binding.setUser(user);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }

    public static class UserDiffCallback extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@androidx.annotation.NonNull User oldItem, @androidx.annotation.NonNull User newItem) {
            return oldItem.getUsername().equals(newItem.getUsername());
        }

        @Override
        public boolean areContentsTheSame(@androidx.annotation.NonNull User oldItem, @androidx.annotation.NonNull User newItem) {
            return oldItem.isReady() == newItem.isReady() && oldItem.isOwner() == newItem.isOwner();
        }
    }
}
