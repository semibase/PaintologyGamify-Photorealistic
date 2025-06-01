package com.paintology.lite.photorealistic.drawing.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paintology.lite.photorealistic.drawing.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class LoadingDialog {


    Activity activity;
    Dialog dialog;

    ProgressDialog progressDialog = null;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);

    }

    public void watchYoutubeVideo(String id) {

        id = "V4nV6Q19Nls";

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            activity.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            activity.startActivity(webIntent);
        }
    }


    public void ShowPleaseWaitDialog(String Description){

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_chat_loading);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        com.google.android.material.textview.MaterialTextView description = dialog.findViewById(R.id.DescriptionTxt);
        description.setText(Description);

        dialog.show();

    }

    public void DismissDialog(){
        if (dialog != null){
            dialog.dismiss();
        }

    }

    public boolean IsDialogShowing(){

        return dialog.isShowing();

    }



    public void showFeedbackDialog() {

        StringConstants constants = new StringConstants();
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setContentView(R.layout.feedback_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Spinner _spn = (Spinner) dialog.findViewById(R.id.spn_feedback);

        String[] array = activity.getResources().getStringArray(R.array.feedback_type);

        ImageView iv_icon_close = (ImageView) dialog.findViewById(R.id.iv_icon_close);
        iv_icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tv_exit = (TextView) dialog.findViewById(R.id.tv_exit);
        tv_exit.setText(activity.getResources().getString(R.string.close));
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtils.logEvents(activity, constants.feedback_close_community);
                dialog.dismiss();
            }
        });

        ImageView play_logo = (ImageView) dialog.findViewById(R.id.play_logo);
        play_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseUtils.logEvents(activity, constants.feedback_google_play_click_community);
                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    activity.startActivity(browserIntent);
                } catch (ActivityNotFoundException nf) {

                } catch (Exception e) {

                }
            }
        });

        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_name_user);
        EditText edt_feedback = (EditText) dialog.findViewById(R.id.edt_feedback);
        TextView tv_send_feedback = (TextView) dialog.findViewById(R.id.tv_submit);
        me.zhanghai.android.materialratingbar.MaterialRatingBar _rating = dialog.findViewById(R.id.ratingBar);
        _rating.setRating(0);
        tv_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_name.getText().toString().isEmpty()) {
                    edt_name.setError(activity.getResources().getString(R.string.required));
                } else if (edt_feedback.getText().toString().isEmpty()) {
                    edt_feedback.setError(activity.getResources().getString(R.string.required));
                } else if (_rating.getRating() == 0) {
                    Toast.makeText(activity, "Please apply rating!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!KGlobal.isInternetAvailable(activity)) {
                        Toast.makeText(activity, activity.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        sendFeedback(edt_name.getText().toString().trim(), edt_feedback.getText().toString().trim(), _rating.getRating() + "", array[_spn.getSelectedItemPosition()]);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }


    void sendFeedback(String _name, String _feedback, String _rating, String app_behaviour) {

        StringConstants constants = new StringConstants();

        FirebaseFirestore db_firebase;

        db_firebase = FirebaseFirestore.getInstance();
        HashMap<String, Object> _feedbackMap = new HashMap<>();
        _feedbackMap.put("name", _name);
        _feedbackMap.put("feedback", _feedback);
        _feedbackMap.put("rating", _rating);
//        _feedbackMap.put("app_behaviour", app_behaviour);
        _feedbackMap.put("isFromCommunity", true);
        try {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateandTime = sdf.format(new Date());
                _feedbackMap.put("date", currentDateandTime);
            } catch (Exception e) {

            }
            String country = constants.getString(constants.UserCountry, activity);
            String city = constants.getString(constants.UserCity, activity);
            if (country != null && !country.isEmpty()) {
                _feedbackMap.put("location", country + "/" + city);
            } else {
                String country_code = constants.getString(constants.UserCountry, activity);
                _feedbackMap.put("location", country_code + "/" + city);
            }
        } catch (Exception e) {

        }
        try {
            showProgress();
            db_firebase.collection("feedback")
                    .add(_feedbackMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Log.e("TAGG", "DocumentSnapshot added with ID: " + documentReference.getId());

                            hideProgress();
                            FirebaseUtils.logEvents(activity, constants.feedback_sent_community);


                            Dialog dialog1 = new Dialog(activity);

                            dialog1.setContentView(R.layout.thank_feedback_dialog);
                            Objects.requireNonNull(dialog1.getWindow()).setGravity(Gravity.CENTER);
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog1.setCanceledOnTouchOutside(false);

                            dialog1.findViewById(R.id.btnClosee).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                }
                            });


                            dialog1.show();


//                            AlertDialog.Builder builderInner = new AlertDialog.Builder(activity);
//                            builderInner.setMessage("Thanks for your feedback!");
////                            builderInner.setMessage("This will help us to bring the best experience to Paintology. Thanks again.");
//                            builderInner.setCancelable(false);
//
//                            builderInner.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            builderInner.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgress();
                            Log.w("TAGG", "Error adding document", e);
                        }
                    });
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }

    void showProgress() {

        try {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
            progressDialog.show();
        } catch (Exception e) {

        }
    }


    void hideProgress() {
        try {
            if (activity.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

}
