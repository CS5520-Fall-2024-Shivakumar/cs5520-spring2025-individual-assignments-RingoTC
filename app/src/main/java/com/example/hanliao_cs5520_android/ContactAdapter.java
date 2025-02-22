package com.example.hanliao_cs5520_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contacts = new ArrayList<>();
    private OnContactClickListener clickListener;

    public interface OnContactClickListener {
        void onContactClick(String phoneNumber);
        void onEditContact(Contact contact);
        void onDeleteContact(Contact contact);
    }

    public void setOnContactClickListener(OnContactClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhone());
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onContactClick(contact.getPhone());
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onEditContact(contact);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDeleteContact(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        notifyItemInserted(contacts.size() - 1);
    }

    public void updateContact(Contact updatedContact, int position) {
        contacts.set(position, updatedContact);
        notifyItemChanged(position);
    }

    public void removeContact(int position) {
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public Contact getContact(int position) {
        return contacts.get(position);
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
} 