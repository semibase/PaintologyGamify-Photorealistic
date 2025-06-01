package com.paintology.lite.photorealistic.drawing.policy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.paintology.lite.photorealistic.drawing.R;
import com.rey.material.widget.ProgressView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        TextView titleTv = findViewById(R.id.title_tv);
        ProgressView progressView = findViewById(R.id.circular_progress1);

        Intent intent = getIntent();
        if (intent != null) {
            String value = intent.getStringExtra("value");
            if (value.equalsIgnoreCase("terms")) {
                titleTv.setText("Terms and Conditions");
                url = "https://paintology.com/terms-of-service/";
            } else if (value.equalsIgnoreCase("privacy")) {
                titleTv.setText("Privacy Policy");
                url = "https://paintology.com/privacy-policy/";
            } else if (value.equalsIgnoreCase("Paintology")){
                titleTv.setText("Paintology");
                url = "https://paintology.com/";
            }
        }

        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView webview = findViewById(R.id.webview);


        if (haveNetworkConnection()) {
            webview.getSettings().setLoadsImagesAutomatically(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webview.loadUrl(url);


            webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(
                        WebView view, WebResourceRequest request) {

                    return true;
                }

                @Override
                public void onPageStarted(
                        WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressView.setVisibility(View.VISIBLE);
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressView.setVisibility(View.GONE);
                }
            });


        } else {
            Toast.makeText(this, "Please connect to Network!", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}