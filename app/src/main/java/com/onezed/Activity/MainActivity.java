package com.onezed.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.R;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    private FirebaseAnalytics mFirebaseAnalytics;
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.web_view);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Set up WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Enable DOM storage for modern websites
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Get the URL from the Intent
        String url = getIntent().getStringExtra("URL");
        if (url != null) {
            webView.loadUrl(url);
        } else {
            // Load a default page or show an error
            CustomAlertDialog dialog = new CustomAlertDialog(MainActivity.this, R.style.WideDialog, getString(R.string.app_name), "Link Not Found");
            dialog.show();
            dialog.onPositiveButton(v ->{
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this,StartActivity.class));

            } );
        }


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
