package at.aau.anti_mon.client.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.databinding.ItemRecyclerViewBinding;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;
import lombok.Getter;

@Getter
public class LobbyUserAdapter extends RecyclerView.Adapter<LobbyUserAdapter.UserViewHolder> {

    private final LobbyViewModel viewModel;
    private final AsyncListDiffer<User> differ;

    public LobbyUserAdapter(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
        differ = new AsyncListDiffer<>(this, new UserDiffCallback());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //RecyclerViewItemBinding binding = RecyclerViewItemBinding.inflate(inflater, parent, false);
        ItemRecyclerViewBinding binding = DataBindingUtil.inflate(inflater,R.layout.item_recycler_view, parent, false);
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

    public void updateUser(User user) {
        List<User> currentList = new ArrayList<>(differ.getCurrentList());
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getUsername().equals(user.getUsername())) {
                currentList.set(i, user);
                differ.submitList(currentList);
                Log.d("LobbyUserAdapter", "Updated user: " + user.getUsername() + " isReady: " + user.isReady());
                return;
            }
        }
        Log.d("LobbyUserAdapter", "User not found: " + user.getUsername());
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecyclerViewBinding binding;

        public UserViewHolder(ItemRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user, LobbyViewModel viewModel) {
            binding.setUser(user);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }

    public static class UserDiffCallback extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUsername().equals(newItem.getUsername());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.isReady() == newItem.isReady() && oldItem.isOwner() == newItem.isOwner();
        }
    }
}