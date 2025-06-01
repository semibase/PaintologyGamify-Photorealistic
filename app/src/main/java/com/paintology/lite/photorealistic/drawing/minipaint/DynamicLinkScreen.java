package com.paintology.lite.photorealistic.drawing.minipaint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.paintology.lite.photorealistic.drawing.Activity.user_pogress.helper.TutorialUtils;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

public class DynamicLinkScreen extends AppCompatActivity {

    boolean isOpen = false, isStop = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isOpen && isStop) {
            startActivity(new Intent(this, FirstScreen.class));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("TAGG", "DynamicLinkScreen Called");
        try {
            if (StringConstants.isLoggedIn()) {
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                String browserlink = getIntent().getDataString();
                                Log.e("TAG", "browserlink in DynamicLink " + browserlink);
                                Intent intent_test = getIntent();
                                Uri data = intent_test.getData();
                                if (data != null) {
                                    try {
                                        String _cat_id = data.getQueryParameter("cat_id");
                                        String _post_id = data.getQueryParameter("post_id");
                                        FireUtils.showProgressDialog(
                                                DynamicLinkScreen.this,
                                                getResources().getString(R.string.please_wait)
                                        );
                                        isOpen = true;
                                        new TutorialUtils(DynamicLinkScreen.this).parseTutorial(_post_id);
                                       /* Log.e("TAGG", "DynamicLinkScreen PushMessage  _cat_id " + _cat_id + " _post_id " + _post_id + " browserlink " + browserlink);
                                        Intent intent = new Intent(DynamicLinkScreen.this, TutorialDetail_Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("catID", _cat_id);
                                        intent.putExtra("postID", _post_id);
                                        intent.putExtra("isFromDynamicLink", "true");
                                        startActivity(intent);
                                        finish();*/
                                    } catch (Exception e) {
                                        Log.e("TAGGG", "Exception at redirectLink " + e.getMessage());
                                    }
                                }  else {
                                    if (URLUtil.isValidUrl(browserlink)) {
                                        KGlobal.openInBrowser(DynamicLinkScreen.this, browserlink);
                                    }
                                    Log.e("TAGG", "DynamicLinkScreen extras null");
                                }
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAGG", "DynamicLinkScreen Called On onFailure " + e.getMessage());
                                Log.e("", "getDynamicLink:onFailure", e);
                            }
                        });
            } else {
                startActivity(new Intent(this, FirstScreen.class));
                finish();
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at getlink " + e.getMessage());
        }
    }
}
