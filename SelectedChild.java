package com.example.mathsprout;

public class SelectedChild {
    private String uid;
    private String email;
    private boolean isSelected;

    public SelectedChild() {
        // Default constructor required for Firebase
    }

    public SelectedChild(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.isSelected = false; // default not selected
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
