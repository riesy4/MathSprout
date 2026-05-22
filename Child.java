package com.example.mathsprout;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Child model
 * - Firebase compatible (empty constructor)
 * - Parcelable (for Intent passing)
 * - Supports 2-arg and 3-arg constructors
 */
public class Child implements Parcelable {

    private String uid;
    private String name;
    private String email;
    private boolean selected;

    // Required empty constructor for Firebase
    public Child() {
        this.selected = false;
    }

    // Used when only uid + email are known
    public Child(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.name = "";
        this.selected = false;
    }

    // Used when uid + name + email are known
    public Child(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.selected = false;
    }

    // Parcelable constructor
    protected Child(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    // -------- Getters --------

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return selected;
    }

    // -------- Setters --------

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // -------- Parcelable --------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
