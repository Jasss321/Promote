package com.example.promote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadShopActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private ImageView shopImage;
    private TextInputLayout shopNameInputLayout, shopDetailsInputLayout, shopAddressInputLayout;
    private EditText shopNameInput, shopDetailsInput, shopAddressInput;
    private Uri imageUri;
    private LatLng shopLocation;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ActivityResultLauncher<Intent> selectLocationActivityResultLauncher;
    private ActivityResultLauncher<Intent> pickImageActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        // Initialize Firebase Firestore and Storage references
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Bind views
        shopImage = findViewById(R.id.shop_image);
        Button uploadImageButton = findViewById(R.id.upload_image_button);
        shopNameInputLayout = findViewById(R.id.shop_name_input_layout);
        shopDetailsInputLayout = findViewById(R.id.shop_details_input_layout);
        shopAddressInputLayout = findViewById(R.id.shop_address_input_layout);
        shopNameInput = findViewById(R.id.shop_name_input);
        shopDetailsInput = findViewById(R.id.shop_details_input);
        shopAddressInput = findViewById(R.id.shop_address_input);
        Button selectLocationButton = findViewById(R.id.select_location_button);
        Button uploadShopButton = findViewById(R.id.upload_shop_button);

        // Register the activity result launcher for selecting a location
        selectLocationActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            double latitude = data.getDoubleExtra("latitude", 0);
                            double longitude = data.getDoubleExtra("longitude", 0);
                            shopLocation = new LatLng(latitude, longitude);
                        }
                    }
                });

        // Register the activity result launcher for picking an image
        pickImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                            shopImage.setImageURI(imageUri);
                        }
                    }
                });

        // Set click listeners for buttons
        uploadImageButton.setOnClickListener(view -> openFileChooser());
        selectLocationButton.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissions();
            } else {
                Intent intent = new Intent(UploadShopActivity.this, SelectLocationActivity.class);
                selectLocationActivityResultLauncher.launch(intent);
            }
        });

        uploadShopButton.setOnClickListener(view -> uploadShop());
    }

    // Request location permissions
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_LOCATION_PERMISSION);
    }

    // Open the file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    // Upload the shop information
    private void uploadShop() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String shopName = shopNameInput.getText().toString().trim();
        String shopDetails = shopDetailsInput.getText().toString().trim();
        String shopAddress = shopAddressInput.getText().toString().trim();

        // Validate user input
        if (TextUtils.isEmpty(shopName)) {
            shopNameInputLayout.setError("Shop name is required");
            return;
        } else {
            shopNameInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(shopDetails)) {
            shopDetailsInputLayout.setError("Shop details are required");
            return;
        } else {
            shopDetailsInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(shopAddress)) {
            shopAddressInputLayout.setError("Shop address is required");
            return;
        } else {
            shopAddressInputLayout.setError(null);
        }

        if (shopLocation == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the image to Firebase Storage
        StorageReference imageStorageReference = storageRef.child("shop_images/" + UUID.randomUUID().toString());
       // Upload the image to Firebase Storage
        imageStorageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // If the upload is successful, get the download URL for the image
            imageStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the shop details along with the image URL to Firestore
                Map<String, Object> shop = new HashMap<>();
                shop.put("name", shopName);
                shop.put("details", shopDetails);
                shop.put("address", shopAddress);
                shop.put("latitude", shopLocation.latitude);
                shop.put("longitude", shopLocation.longitude);
                shop.put("imageUrl", uri.toString());

                // Add the shop details to the shops collection in Firestore
                db.collection("shops")
                        .add(shop)
                        .addOnSuccessListener(documentReference -> {
                            // If the shop details are successfully added to Firestore, show a success message and finish the activity
                            Toast.makeText(this, "Shop uploaded successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // If there is an error adding the shop details to Firestore, show an error message and log the error
                            Toast.makeText(this, "Error uploading shop", Toast.LENGTH_SHORT).show();
                            Log.e("UploadShopActivity", "Error uploading shop", e);
                        });
            }).addOnFailureListener(e -> {
                // If there is an error getting the download URL for the image, show an error message and log the error
                Toast.makeText(this, "Error getting image download URL", Toast.LENGTH_SHORT).show();
                Log.e("UploadShopActivity", "Error getting image download URL", e);
            });
        }).addOnFailureListener(e -> {
                // If there is an error uploading the image, show an error message and log the error
            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
            Log.e("UploadShopActivity", "Error uploading image", e);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(UploadShopActivity.this, SelectLocationActivity.class);
                selectLocationActivityResultLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Location permission is required to select a shop location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

