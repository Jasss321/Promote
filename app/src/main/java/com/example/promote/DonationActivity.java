package com.example.promote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class DonationActivity extends AppCompatActivity {

    private Button donateSubmitButton;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // Initialize the donate button and WebView
        donateSubmitButton = findViewById(R.id.donate_submit_button);
        webView = findViewById(R.id.webview);

        // Enable JavaScript for the WebView
        webView.getSettings().setJavaScriptEnabled(true);
        // Set a WebViewClient to handle URL loading in the WebView itself
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Set up a click listener for the donate button
        donateSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the WebView visible and load the PayPal donation URL
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl("https://www.sandbox.paypal.com/donate/?hosted_button_id=JKAD2NLGB5XV2");
                Toast.makeText(DonationActivity.this, "Redirecting to payment gateway...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press for WebView navigation
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
