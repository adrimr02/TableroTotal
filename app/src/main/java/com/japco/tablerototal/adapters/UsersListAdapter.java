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
import com.japco.tablerototal.model.User;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(User item);
    }
    private List<User> usersList;
    private final UsersListAdapter.OnItemClickListener listener;

    public UsersListAdapter(List<User> usersList, UsersListAdapter.OnItemClickListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_recycler_view_users_connected, parent, false);
        return new UserViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersListAdapter.UserViewHolder holder, int position) {
        User user = usersList.get(position);
        Log.i("Lista","Visualiza elemento: "+ user);
        holder.bindUser(user, listener);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView nickname;
        private TextView state;
        private ImageView userPicture;
        public UserViewHolder(View itemView) {
            super(itemView);
            nickname= (TextView)itemView.findViewById(R.id.nickname);
            state= (TextView)itemView.findViewById(R.id.state);
            userPicture = (ImageView)itemView.findViewById(R.id.userPicture);
        }

        public void bindUser(final User user, final UsersListAdapter.OnItemClickListener listener) {
            nickname.setText(user.getNickname());
            state.setText(user.getState());
            userPicture.setImageResource(R.drawable.contacto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}
