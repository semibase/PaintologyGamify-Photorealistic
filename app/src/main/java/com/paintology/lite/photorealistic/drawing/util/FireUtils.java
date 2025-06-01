package com.paintology.lite.photorealistic.drawing.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paintology.lite.photorealistic.drawing.Activity.profile.MyProfileActivity;
import com.paintology.lite.photorealistic.drawing.Activity.profile.UserProfileActivity;
import com.paintology.lite.photorealistic.drawing.Activity.video_intro.IntroVideoActivity;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.databinding.DialogCommonBinding;
import com.paintology.lite.photorealistic.drawing.databinding.DialogLoadingBinding;
import com.paintology.lite.photorealistic.drawing.databinding.DialogLoginBinding;
import com.paintology.lite.photorealistic.drawing.databinding.DialogStoreBinding;
import com.paintology.lite.photorealistic.drawing.databinding.DialogStoreErrorBinding;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class FireUtils {

    public static Dialog mProgressDialog = null;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void updateToken(Context context, String refreshtoken) {
        try {
            FirebaseUser uId = FirebaseAuth.getInstance().getCurrentUser();
            if (uId != null) {
                FirebaseFirestore.getInstance().collection("fcm_tokens").document(uId.getUid())
                        .set(refreshtoken)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("fcm_tokens", "FCM token successfully written! ");
                        })
                        .addOnFailureListener(e -> {
                            Log.d("fcm_tokens", "getFcmToken: " + e);
                        });
            } else {
                // Handle the case where the user is not signed in
                Log.e("Error", "User not signed in");
                // You can redirect to a sign-in screen or show an error message
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPoints(Context context, TextView textView, TextView textView2) {
        StringConstants constants = StringConstants.getInstance();
        if (!constants.getString(constants.UserId, context).equalsIgnoreCase("") && (textView != null || textView2 != null)) {
            try {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(constants.getString(constants.UserId, context));
                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.contains("points")) {
                                if (textView != null) {
                                    textView.setText(value.get("points") + "");
                                }
                                if (textView2 != null) {
                                    textView2.setText(value.get("points") + "");
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openProfileScreen(Context context, String userId) {
        StringConstants constants = StringConstants.getInstance();
        if (userId == null || userId.equalsIgnoreCase(constants.getString(constants.UserId, context))) {
            Intent intent = new Intent(context, MyProfileActivity.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra(StringConstants.SelectedUserId, userId);
            context.startActivity(intent);
        }

    }

    public static void openIntroVideoScreen(Context context, String video_key, String event_name) {
        Intent intent = new Intent(context, IntroVideoActivity.class);
        intent.putExtra("video_id", video_key.replaceAll("_", ""));
        intent.putExtra("event_name", event_name);
        context.startActivity(intent);
    }

    public static void openLoginScreen(Context context, boolean isShowDialog) {
        if (isShowDialog) {
            try {
                Dialog dialog = new Dialog(context);
                DialogLoginBinding binding = DialogLoginBinding.inflate(LayoutInflater.from(context));
                dialog.setContentView(binding.getRoot());
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                binding.imgCross.setOnClickListener(v -> dialog.dismiss());
                binding.btnClose.setOnClickListener(v -> dialog.dismiss());
                binding.btnOpen.setOnClickListener(v -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("FromProfile", true);
                    context.startActivity(intent);
                });
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("FromProfile", true);
                context.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("FromProfile", true);
            context.startActivity(intent);
        }
    }

    public static void showStoreError(Context context, String message) {
        try {
            Dialog dialog = new Dialog(context);
            DialogStoreErrorBinding binding = DialogStoreErrorBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            binding.tvMessage.setText("You currently don’t have sufficient points to redeem this " + message);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            binding.btnClosee.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface onCloseListener {
        public void onDismiss();
    }

    public static void showCustomDialog(Context context, String title, String message, onCloseListener listener) {
        try {
            Dialog dialog = new Dialog(context);
            DialogCommonBinding binding = DialogCommonBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            binding.tvTitle.setText(title);
            binding.tvMessage.setText(message);
            binding.btnClosee.setText(context.getResources().getString(R.string.ok_label));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            binding.btnClosee.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog1 -> listener.onDismiss());
                dialog.dismiss();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openDashboardScreen(Activity activity) {
        Intent intent = new Intent(activity, GalleryDashboard.class);
        activity.startActivity(intent);
        activity.finish();
    }


    public interface onStoreClickListener {
        public void onRedeem(String productId, String productName);
    }

    @SuppressLint("SetTextI18n")
    public static void getStoreDetails(Context context, String key, onStoreClickListener onStoreClickListener) {
        if (AppUtils.getStoreProducts().containsKey(key)) {
            showStoreDialog(context, AppUtils.getStoreProducts().get(key), onStoreClickListener);
        } else if (AppUtils.getStoreProductsCommon().containsKey(key)) {
            showStoreDialog(context, AppUtils.getStoreProductsCommon().get(key), onStoreClickListener);
        } else {
            FireUtils.showProgressDialog(context, context.getResources().getString(R.string.ss_loading_please_wait));
            ContextKt.getStoreProduct(key, product -> {
                FireUtils.hideProgressDialog();
                if (product.equalsIgnoreCase("")) {
                    ContextKt.showToast(context, context.getResources().getString(R.string.unknown_error));
                } else {
                    showStoreDialog(context, product, onStoreClickListener);
                }
            });
        }
    }


    @SuppressLint("SetTextI18n")
    public static void showStoreDialog(Context context, String product, onStoreClickListener onStoreClickListener) {
        try {
            Dialog dialog = new Dialog(context);
            DialogStoreBinding binding = DialogStoreBinding.inflate(LayoutInflater.from(context), null, false);
            dialog.setContentView(binding.getRoot());
            binding.ivClose.setVisibility(View.VISIBLE);
            binding.ivClose.setOnClickListener(v -> {
                dialog.dismiss();
            });
            String productId = "", productName = "";
            try {
                JSONObject object = new JSONObject(product);
                productId = object.getString("id");

                if (object.has("data")) {
                    JSONObject data = object.getJSONObject("data");
                    if (data.has("id")) {
                        productName = data.getString("id");
                    }
                }
                if (object.has("images")) {
                    JSONObject images = object.getJSONObject("images");
                    if (images.has("thumbnail") && !images.getString("thumbnail").equalsIgnoreCase("")) {
                        Picasso.get().load(Uri.parse(images.getString("thumbnail"))).into(binding.ivThumbnail);
                    } else {
                        binding.ivThumbnail.setVisibility(View.GONE);
                    }
                } else {
                    binding.ivThumbnail.setVisibility(View.GONE);
                }

                binding.tvDialogTitle.setText(object.getString("name") + " ");
                binding.tvDialogContent.setText(object.getString("description") + " ");

                if (object.has("prices")) {
                    JSONObject prices = object.getJSONObject("prices");
                    if (prices.has("points")) {
                        binding.btnUnlock.setText(context.getResources().getString(R.string.redeem) + " - " + prices.get("points") + " Points");
                    } else {
                        binding.btnUnlock.setText(context.getResources().getString(R.string.redeem));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.show();
            String finalProductId = productId;
            String finalProductName = productName;
            binding.btnUnlock.setOnClickListener(v -> {
                dialog.setOnDismissListener(dialog1 -> onStoreClickListener.onRedeem(finalProductId, finalProductName));
                dialog.dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressDialog(Context context, String text) {
        try {
            mProgressDialog = new Dialog(context);
            DialogLoadingBinding binding = DialogLoadingBinding.inflate(LayoutInflater.from(context), null, false);
            binding.DescriptionTxt.setText(text);
            mProgressDialog.setContentView(binding.getRoot());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            if (mProgressDialog.getWindow() != null) {
                mProgressDialog.getWindow().setGravity(Gravity.CENTER);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface onClickListener {
        public void onSuccess();
    }

    public static void showCustomDialog(Context context, String message, onClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            listener.onSuccess();
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.create().show();
    }

    public static StringConstants _constant = new StringConstants();


    public static void showFeedbackDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setContentView(R.layout.feedback_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        Spinner _spn = (Spinner) dialog.findViewById(R.id.spn_feedback);

        String[] array = context.getResources().getStringArray(R.array.feedback_type);

        ImageView iv_icon_close = (ImageView) dialog.findViewById(R.id.iv_icon_close);
        iv_icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tv_exit = (TextView) dialog.findViewById(R.id.tv_exit);
        tv_exit.setText(context.getResources().getString(R.string.close));
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtils.logEvents(context, _constant.feedback_close_community);
                dialog.dismiss();
            }
        });

        ImageView play_logo = (ImageView) dialog.findViewById(R.id.play_logo);
        play_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseUtils.logEvents(context, _constant.feedback_google_play_click_community);
                    String url = "https://play.google.com/store/apps/details?id=com.paintology.lite";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_name_user);
        EditText edt_feedback = (EditText) dialog.findViewById(R.id.edt_feedback);
        TextView tv_send_feedback = (TextView) dialog.findViewById(R.id.tv_submit);
        RatingBar _rating = dialog.findViewById(R.id.ratingBar);
        _rating.setRating(0);
        tv_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_name.getText().toString().isEmpty()) {
                    edt_name.setError(context.getResources().getString(R.string.required));
                } else if (edt_feedback.getText().toString().isEmpty()) {
                    edt_feedback.setError(context.getResources().getString(R.string.required));
                } else if (_rating.getRating() == 0) {
                    Toast.makeText(context, "Please apply rating!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!KGlobal.isInternetAvailable(context)) {
                        Toast.makeText(context, context.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        sendFeedback(edt_name.getText().toString().trim(), edt_feedback.getText().toString().trim(), _rating.getRating() + "", context);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    public static void sendFeedback(String _name, String _feedback, String _rating, Context context) {
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
            String country = _constant.getString(_constant.UserCountry, context);
            String city = _constant.getString(_constant.UserCity, context);
            if (country != null && !country.isEmpty()) {
                _feedbackMap.put("location", country + "/" + city);
            } else {
                String country_code = _constant.getString(_constant.UserCountry, context);
                _feedbackMap.put("location", country_code + "/" + city);
            }
        } catch (Exception e) {

        }
        try {
            FireUtils.showProgressDialog(context, context.getResources().getString(R.string.ss_sending_feedback_please_wait));
            FirebaseFirestore.getInstance().collection("feedback")
                    .add(_feedbackMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Log.e("TAGG", "DocumentSnapshot added with ID: " + documentReference.getId());

                            FireUtils.hideProgressDialog();
                            FirebaseUtils.logEvents(context, _constant.feedback_sent_community);


                            Dialog dialog1 = new Dialog(context);

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

//                            AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
//                            builderInner.setMessage("Thanks for your feedback!");
////                            builderInner.setMessage("This will help us to bring the best experience to Paintology. Thanks again.");
//                            builderInner.setCancelable(false);
//
//                            builderInner.setPositiveButton("Okay", (dialog, which) -> dialog.dismiss());
//                            builderInner.show();
                        }
                    })
                    .addOnFailureListener((OnFailureListener) e -> {
                        FireUtils.hideProgressDialog();
                        Log.w("TAGG", "Error adding document", e);
                    });
        } catch (Exception e) {
            FireUtils.hideProgressDialog();
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }


}
