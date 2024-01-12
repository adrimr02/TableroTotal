package com.japco.tablerototal.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.japco.tablerototal.R;
import com.japco.tablerototal.model.Friend;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Friend item);
    }
    private final List<Friend> friendsList;
    private final FriendListAdapter.OnItemClickListener listener;

    public FriendListAdapter(List<Friend> friendsList, FriendListAdapter.OnItemClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendListAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_recycler_view_friends, parent, false);
        return new FriendListAdapter.FriendViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        Log.i("Lista","Visualiza elemento: "+ friend);
        holder.bindUser(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        private final TextView friendName;
        private final TextView lastConnection;
        private final ImageView friendPicture;
        public FriendViewHolder(View itemView) {
            super(itemView);
            friendName= itemView.findViewById(R.id.friendName);
            lastConnection= itemView.findViewById(R.id.lastConnection);
            friendPicture = itemView.findViewById(R.id.friendPicture);
        }

        public void bindUser(final Friend friend, final FriendListAdapter.OnItemClickListener listener) {
            friendName.setText(friend.getName());
            lastConnection.setText("Última Conexión: " + friend.getLastConnection());
            friendPicture.setImageResource(R.drawable.contacto);
            itemView.setOnClickListener(v -> {
                Log.i("Hola", "Hola");
                listener.onItemClick(friend);
            });
        }
    }
}
