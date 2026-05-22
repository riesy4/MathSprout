package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private List<Child> childrenList;

    public ChildAdapter(List<Child> childrenList) {
        this.childrenList = childrenList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childrenList.get(position);
        holder.emailText.setText(child.getEmail());
        holder.checkbox.setChecked(child.isSelected());
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> child.setSelected(isChecked));
    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView emailText;
        CheckBox checkbox;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            emailText = itemView.findViewById(R.id.childEmail);
            checkbox = itemView.findViewById(R.id.childCheckbox);
        }
    }
}
