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

    public UsersListAdapter(List<User> usersList) {
        this.usersList = usersList;
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
        holder.bindUser(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        private final TextView nickname;
        private final TextView state;
        private final ImageView userPicture;
        public UserViewHolder(View itemView) {
            super(itemView);
            nickname= itemView.findViewById(R.id.nickname);
            state= itemView.findViewById(R.id.state);
            userPicture = itemView.findViewById(R.id.userPicture);
        }

        public void bindUser(final User user) {
            nickname.setText(user.getNickname());
            state.setText(user.getState());
            userPicture.setImageResource(R.drawable.contacto);
        }
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}
