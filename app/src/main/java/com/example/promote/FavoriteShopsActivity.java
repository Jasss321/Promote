package com.example.promote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FavoriteShopsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopListAdapter adapter;
    private List<Shop> favoriteShopList;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Set up the RecyclerView for displaying favorite shops
        recyclerView = findViewById(R.id.favorite_shop_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the favoriteShopList and adapter
        favoriteShopList = new ArrayList<>();
        adapter = new ShopListAdapter(this, favoriteShopList);
        recyclerView.setAdapter(adapter);

        // Set up the click listener for RecyclerView items
        adapter.setOnItemClickListener(position -> {
            Shop shop = favoriteShopList.get(position);
            showShopDetails(shop);
        });

        // Initialize the FavoritesManager and fetch the user's favorite shops
        favoritesManager = new FavoritesManager();
        fetchFavoriteShops();

        // Set up the bottom navigation menu
        setupBottomNavigationMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch favorite shops again when the activity is resumed
        fetchFavoriteShops();
    }

    private void fetchFavoriteShops() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the favorite shops from the database and update the favoriteShopList
        favoritesManager.fetchFavoriteShops(userId, task -> {
            if (task.isSuccessful()) {
                favoriteShopList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Shop shop = document.toObject(Shop.class);
                    shop.setId(document.getId());
                    favoriteShopList.add(shop);
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            } else {
                Log.e("FavoriteShopsActivity", "Error getting favorite shops: ", task.getException());
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
                startActivity(new Intent(this, FavoriteShopsActivity.class));
                return true;
            }
            return false;
        });
    }

    // Launch the ShopDetailsActivity to show the selected shop's details
    private void showShopDetails(Shop shop) {
        Intent intent = new Intent(FavoriteShopsActivity.this, ShopDetailsActivity.class);
        intent.putExtra("shop", shop);
        startActivity(intent);
    }
}
