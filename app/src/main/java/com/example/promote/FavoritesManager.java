package com.example.promote;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FavoritesManager {
    private final FirebaseFirestore firestore;

    public FavoritesManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addToFavorites(String userId, Shop shop) {

        // Checking if both the user ID and shop object are not null
        if (userId != null && shop != null && shop.getId() != null) {

            // Creating a reference to the shop document within the user's favorites collection
            DocumentReference userFavoriteShopsRef = firestore
                    .collection("favorites")
                    .document(userId)
                    .collection("shops")
                    .document(shop.getId());

            // Creating a map of the shop's data
            Map<String, Object> shopData = new HashMap<>();
            shopData.put("id", shop.getId());
            shopData.put("name", shop.getName());
            shopData.put("imageUrl", shop.getImageUrl());

            // Adds the shop data to the Firestore document reference
            userFavoriteShopsRef.set(shopData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // If the operation was successful, log a message
                            Log.d("FavoritesManager", "Shop successfully added to favorites!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // If the operation failed, log an error message with the exception
                            Log.w("FavoritesManager", "Error adding shop to favorites", e);
                        }
                    });
        } else {
            // If either the user ID or shop object is null, log a message
            Log.d("FavoritesManager", "User ID or Shop ID is null in addToFavorites");
        }
    }


    public void fetchFavoriteShops(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        if (userId == null) {
            return;
        }

        firestore.collection("favorites")
                .document(userId)
                .collection("shops")
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void removeFromFavorites(String userId, String shopId) {
        if (userId == null || shopId == null) {
            Log.d("FavoritesManager", "User ID or Shop ID is null in removeFromFavorites");
            return;
        }

        firestore.collection("favorites")
                .document(userId)
                .collection("shops")
                .document(shopId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("FavoritesManager", "Shop removed from favorites"))
                .addOnFailureListener(e -> Log.e("FavoritesManager", "Error removing shop from favorites", e));
    }

    public void isShopInFavorites(String userId, String shopId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        if (userId == null || shopId == null) {
            return;
        }

        firestore.collection("favorites")
                .document(userId)
                .collection("shops")
                .document(shopId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
}
