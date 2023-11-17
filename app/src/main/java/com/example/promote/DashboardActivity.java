package com.example.promote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopListAdapter adapter;
    private List<Shop> shopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set up RecyclerView for displaying shop list
        recyclerView = findViewById(R.id.shop_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize shop list and adapter, then set the adapter for the RecyclerView
        shopList = new ArrayList<>();
        adapter = new ShopListAdapter(this, shopList);
        recyclerView.setAdapter(adapter);

        // Set up click listener for shop items in the list
        adapter.setOnItemClickListener(position -> {
            Shop shop = shopList.get(position);
            showShopDetails(shop);
        });

        // Fetch and display the list of shops
        fetchShops();
        // Set up the bottom navigation menu
        setupBottomNavigationMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch and display the list of shops when the activity is resumed
        fetchShops();
    }

    private void fetchShops() {
        // Get a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Query the "shops" collection in the database.
        db.collection("shops")
                .get()
                .addOnCompleteListener(task -> {
                    // Check if the query was successful.
                    if (task.isSuccessful()) {
                        // If the query was successful, clear the current shopList and retrieve data for each document.
                        shopList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert the document to a Shop object and set its ID.
                            Shop shop = document.toObject(Shop.class);
                            shop.setId(document.getId());
                            // Add the shop to the list of shops.
                            shopList.add(shop);
                        }
                        // Notify the adapter that the data set has changed.
                        adapter.notifyDataSetChanged();
                    } else {
                        // If the query was not successful, log the error.
                        Log.e("DashboardActivity", "Error getting shops: ", task.getException());
                    }
                });


    }


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
            } else if (itemId == R.id.action_team) {
                startActivity(new Intent(this, TeamActivity.class));
                return true;
            } else if (itemId == R.id.action_favorites) {
                startActivity(new Intent(this, FavoriteShopsActivity.class)); // Add this line
                return true;
            }
            return false;
        });
    }



    private void showShopDetails(Shop shop) {
        Intent intent = new Intent(DashboardActivity.this, ShopDetailsActivity.class);
        intent.putExtra("shop", shop);
        startActivity(intent);
    }
}
