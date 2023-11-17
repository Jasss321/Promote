package com.example.promote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class ShopDetailsActivity extends AppCompatActivity {
    // Declare UI components and a FavoritesManager instance

    private ImageView shopImageView;
    private TextView shopNameTextView;
    private TextView shopDetailsTextView;
    private TextView shopAddressTextView;
    private Button donateButton;

    private Button favoriteButton;

    private FavoritesManager favoritesManager;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_item_layout);
        // Initialize UI components and FavoritesManager

        shopImageView = findViewById(R.id.shop_image_view);
        shopNameTextView = findViewById(R.id.shop_name_text_view);
        shopDetailsTextView = findViewById(R.id.shop_details_text_view);
        shopAddressTextView = findViewById(R.id.shop_address_text_view);
        donateButton = findViewById(R.id.donate_button);
        favoriteButton = findViewById(R.id.favorite_button);
        favoritesManager = new FavoritesManager();
        // Get the shop object passed from the calling activity

        Shop shop = (Shop) getIntent().getSerializableExtra("shop");
        // If the shop object is not null, populate the UI components with the shop data

        if (shop != null) {
            shopNameTextView.setText(shop.getName());
            shopDetailsTextView.setText(shop.getDetails());
            shopAddressTextView.setText(shop.getAddress());

            Glide.with(this)
                    .load(shop.getImageUrl())
                    .into(shopImageView);

            // Check if the shop is already in the favorites list and update the favorite status
            checkFavoriteStatus(shop.getId());
        }

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent donationIntent = new Intent(ShopDetailsActivity.this, DonationActivity.class);
                startActivity(donationIntent);
            }
        });
        // Set up a click listener for the favorite button

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (shop != null && shop.getId() != null) {
                    Log.d("ShopDetailsActivity", "Favorite button clicked. UserID: " + userId + ", ShopID: " + shop.getId());

                    if (isFavorite) {
                        favoritesManager.removeFromFavorites(userId, shop.getId());
                        isFavorite = false;
                        favoriteButton.setText("Add to Favorites");
                    } else {
                        favoritesManager.addToFavorites(userId, shop);
                        isFavorite = true;
                        favoriteButton.setText("Remove from Favorites");
                    }
                } else {
                    Log.d("ShopDetailsActivity", "ShopID is null.");
                }
            }
        });

    }
    // Check if the shop is in the user's favorites list and update the favorite button accordingly

    private void checkFavoriteStatus(String shopId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        favoritesManager.isShopInFavorites(userId, shopId, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        isFavorite = true;
                        favoriteButton.setText("Remove from Favorites");
                    } else {
                        isFavorite = false;
                        favoriteButton.setText("Add to Favorites");
                    }
                } else {
                    // Handle any errors
                }
            }
        });
    }
}
