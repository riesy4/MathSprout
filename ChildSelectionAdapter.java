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

public class ChildSelectionAdapter
        extends RecyclerView.Adapter<ChildSelectionAdapter.ChildViewHolder> {

    private final List<Child> childList;
    private final List<Boolean> selectedStates;

    public ChildSelectionAdapter(List<Child> childList) {
        this.childList = childList;
        this.selectedStates = new ArrayList<>();

        for (int i = 0; i < childList.size(); i++) {
            selectedStates.add(false);
        }
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
        Child child = childList.get(position);

        holder.nameText.setText(child.getName() != null ? child.getName() : "Unnamed");
        holder.emailText.setText(child.getEmail() != null ? child.getEmail() : "-");

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedStates.get(position));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectedStates.set(position, isChecked));
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public List<Child> getSelectedChildren() {
        List<Child> selectedChildren = new ArrayList<>();
        for (int i = 0; i < childList.size(); i++) {
            if (selectedStates.get(i)) {
                selectedChildren.add(childList.get(i));
            }
        }
        return selectedChildren;
    }

    public void selectAll(boolean select) {
        for (int i = 0; i < selectedStates.size(); i++) {
            selectedStates.set(i, select);
        }
        notifyDataSetChanged();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;
        CheckBox checkBox;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.childNameText);
            emailText = itemView.findViewById(R.id.childEmailText);
            checkBox = itemView.findViewById(R.id.childCheckBox);
        }
    }
}
