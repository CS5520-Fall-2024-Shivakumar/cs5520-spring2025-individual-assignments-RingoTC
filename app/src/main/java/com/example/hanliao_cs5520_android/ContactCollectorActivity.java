package com.example.hanliao_cs5520_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactCollectorActivity extends AppCompatActivity {
    private static final int PERMISSION_CALL_PHONE = 1;
    private ContactAdapter adapter;
    private String pendingCallNumber;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_collector);

        dbHelper = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter();
        recyclerView.setAdapter(adapter);

        // 从数据库加载联系人
        adapter.setContacts(dbHelper.getAllContacts());

        adapter.setOnContactClickListener(new ContactAdapter.OnContactClickListener() {
            @Override
            public void onContactClick(String phoneNumber) {
                initiateCall(phoneNumber);
            }

            @Override
            public void onEditContact(Contact contact) {
                showEditContactDialog(contact);
            }

            @Override
            public void onDeleteContact(Contact contact) {
                showDeleteConfirmationDialog(contact);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_contact);
        fab.setOnClickListener(v -> showAddContactDialog());
    }

    private void initiateCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            pendingCallNumber = phoneNumber;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSION_CALL_PHONE);
        } else {
            makePhoneCall(phoneNumber);
        }
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Failed to make call: Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingCallNumber != null) {
                    makePhoneCall(pendingCallNumber);
                    pendingCallNumber = null;
                }
            } else {
                Toast.makeText(this, "Permission denied to make phone calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() == 10 && phone.matches("\\d+");
    }

    private void showAddContactDialog() {
        showContactDialog(null, false);
    }

    private void showEditContactDialog(Contact contact) {
        showContactDialog(contact, true);
    }

    private void showContactDialog(Contact existingContact, boolean isEdit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        EditText nameEdit = dialogView.findViewById(R.id.edit_name);
        EditText phoneEdit = dialogView.findViewById(R.id.edit_phone);

        if (isEdit && existingContact != null) {
            nameEdit.setText(existingContact.getName());
            phoneEdit.setText(existingContact.getPhone());
        }

        String title = isEdit ? "Edit Contact" : "Add Contact";
        String positiveButton = isEdit ? "Save" : "Add";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    String name = nameEdit.getText().toString().trim();
                    String phone = phoneEdit.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isValidPhoneNumber(phone)) {
                        Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Contact newContact = new Contact(name, phone);
                    if (isEdit) {
                        int position = -1;
                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            if (adapter.getContact(i).getPhone().equals(existingContact.getPhone())) {
                                position = i;
                                break;
                            }
                        }
                        if (position != -1) {
                            // 更新数据库和适配器
                            dbHelper.deleteAllContacts();
                            adapter.updateContact(newContact, position);
                            for (int i = 0; i < adapter.getItemCount(); i++) {
                                dbHelper.addContact(adapter.getContact(i));
                            }
                            Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 添加到数据库和适配器
                        dbHelper.addContact(newContact);
                        adapter.addContact(newContact);
                        Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int position = -1;
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (adapter.getContact(i).getPhone().equals(contact.getPhone())) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        // 从数据库和适配器中删除
                        dbHelper.deleteAllContacts();
                        adapter.removeContact(position);
                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            dbHelper.addContact(adapter.getContact(i));
                        }
                        Toast.makeText(this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 