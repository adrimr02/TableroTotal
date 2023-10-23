package com.japco.tablerototal;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Friend item);
    }
    private List<Friend> friendsList;
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
        private TextView friendName;
        private TextView lastConnection;
        private ImageView friendPicture;
        public FriendViewHolder(View itemView) {
            super(itemView);
            friendName= (TextView)itemView.findViewById(R.id.friendName);
            lastConnection= (TextView)itemView.findViewById(R.id.lastConnection);
            friendPicture = (ImageView)itemView.findViewById(R.id.friendPicture);
        }

        public void bindUser(final Friend friend, final FriendListAdapter.OnItemClickListener listener) {
            friendName.setText(friend.getName());
            lastConnection.setText("Última Conexión: " + friend.getLastConnection());
            friendPicture.setImageResource(R.drawable.contacto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("Hola", "Hola");
                    listener.onItemClick(friend);
                }
            });
        }
    }
}
