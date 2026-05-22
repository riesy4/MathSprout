package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ChildViewHolder> {

    private List<Child> childrenList;

    public ChildrenAdapter(List<Child> childrenList) {
        this.childrenList = childrenList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_select, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childrenList.get(position);

        holder.emailText.setText(child.getEmail());
        holder.checkBox.setChecked(child.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            child.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }

    public List<Child> getSelectedChildren() {
        List<Child> selected = new ArrayList<>();
        for (Child c : childrenList) {
            if (c.isSelected()) {
                selected.add(c);
            }
        }
        return selected;
    }

    public void selectAll(boolean select) {
        for (Child c : childrenList) {
            c.setSelected(select);
        }
        notifyDataSetChanged();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView emailText;
        CheckBox checkBox;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            emailText = itemView.findViewById(R.id.childEmailText);
            checkBox = itemView.findViewById(R.id.childCheckBox);
        }
    }
}
