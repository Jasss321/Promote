package com.example.promote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.promote.User;




public class AccountActivity extends AppCompatActivity {

    private TextView userEmail;
    private EditText nameInput, surnameInput, ageInput, phoneInput;
    private Button updateButton, signOutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize FirebaseAuth and FirebaseFirestore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get UI elements references
        userEmail = findViewById(R.id.userEmail);
        nameInput = findViewById(R.id.nameInput);
        surnameInput = findViewById(R.id.surnameInput);
        ageInput = findViewById(R.id.ageInput);
        phoneInput = findViewById(R.id.phoneInput);
        updateButton = findViewById(R.id.updateButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Set user email if the user is logged in
        if (mAuth.getCurrentUser() != null) {
            userEmail.setText(mAuth.getCurrentUser().getEmail());
        }

        // Fetch and display user data
        fetchUserData();
// Set up click listeners for the update and sign out buttons
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    // This function retrieves the user data from Firestore and displays it in the appropriate fields
    private void fetchUserData() {
        // Access the "users" collection in Firestore and get the document with the current user's ID
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid()) // The ID of the current user is used to get their document
                .get() // Retrieve the document
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) { // If the task was successful
                            // Get the document snapshot
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) { // If the document exists
                                // Retrieve the user object from the document
                                User user = document.toObject(User.class);
                                // Set the appropriate fields in the user interface with the user's data
                                nameInput.setText(user.getName()); // Display the user's name
                                surnameInput.setText(user.getSurname()); // Display the user's surname
                                ageInput.setText(String.valueOf(user.getAge())); // Display the user's age
                                phoneInput.setText(user.getPhone()); // Display the user's phone number
                            } else { // If the document doesn't exist
                                // Display a message indicating that the user data was not found
                                Toast.makeText(AccountActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else { // If the task was not successful
                            // Display an error message with the exception message
                            Toast.makeText(AccountActivity.this, "Error fetching user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Updates user data in Firestore with the input from the UI
    private void updateUserData() {
        String name = nameInput.getText().toString().trim();
        String surname = surnameInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        User updatedUser = new User(name, surname, age, phone, mAuth.getCurrentUser().getEmail());

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(updatedUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountActivity.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountActivity.this, "Error updating user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    // Set up the bottom navigation menu
    private void setupBottomNavigationMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.action_upload_shop) {
                startActivity(new Intent(this, UploadShopActivity.class));
                return true;
            } else if (itemId == R.id.action_account) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            return false;
        });
    }

}



