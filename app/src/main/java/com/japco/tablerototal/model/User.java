package com.japco.tablerototal.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String nickname;
    private String state;
    private String profileImgUrl;

    public User(String nickname, String state, String profileImgUrl) {
        this.nickname = nickname;
        this.state = state;
        this.profileImgUrl = profileImgUrl;
    }

    protected User(Parcel in) {
        nickname = in.readString();
        state = in.readString();
        profileImgUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeString(state);
        dest.writeString(profileImgUrl);
    }
}
