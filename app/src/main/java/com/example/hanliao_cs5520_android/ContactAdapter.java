package com.example.hanliao_cs5520_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contacts = new ArrayList<>();
    private OnContactClickListener listener;

    public void setContacts(List<Contact> contacts) {
        this.contacts = new ArrayList<>(contacts);
        notifyDataSetChanged();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        notifyItemInserted(contacts.size() - 1);
    }

    public void updateContact(Contact contact, int position) {
        contacts.set(position, contact);
        notifyItemChanged(position);
    }

    public void removeContact(int position) {
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public Contact getContact(int position) {
        return contacts.get(position);
    }

    public interface OnContactClickListener {
        void onContactClick(String phoneNumber);
        void onEditContact(Contact contact);
        void onDeleteContact(Contact contact);
    }

    public void setOnContactClickListener(OnContactClickListener listener) {
        this.listener = listener;
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
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView phoneText;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contact_name);
            phoneText = itemView.findViewById(R.id.contact_phone);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onContactClick(contacts.get(position).getPhone());
                }
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditContact(contacts.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteContact(contacts.get(position));
                }
            });
        }

        public void bind(Contact contact) {
            nameText.setText(contact.getName());
            phoneText.setText(contact.getPhone());
        }
    }
} 