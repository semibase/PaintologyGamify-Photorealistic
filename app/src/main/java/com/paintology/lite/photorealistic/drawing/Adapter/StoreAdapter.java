package com.paintology.lite.photorealistic.drawing.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.databinding.DialogStoreBinding;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.transforms.RoundedTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    Context context;
    List<String> mProductList = new ArrayList<>();

    public StoreAdapter(Context context, List<String> mProductList) {
        this.context = context;
        this.mProductList = mProductList;
    }

    public void setmProductList(List<String> mProductList) {
        this.mProductList = mProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogStoreBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId = "", productName = "", productType = "";

        try {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(holder.binding.llMain.getLayoutParams());
            layoutParams.setMarginStart(com.intuit.sdp.R.dimen._5sdp);
            layoutParams.setMarginEnd(com.intuit.sdp.R.dimen._5sdp);
            holder.binding.llMain.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject object = new JSONObject(mProductList.get(position));
            productId = object.getString("id");

            if (object.has("data")) {
                JSONObject data = object.getJSONObject("data");
                if (data.has("id")) {
                    productName = data.getString("id");
                    if (AppUtils.getPurchasedBrushes().contains(productName) || AppUtils.getPurchasedProducts().contains(productName)) {
                        holder.binding.btnUnlock.setText("Already Redeemed");
                        holder.binding.btnUnlock.setEnabled(false);
                        holder.binding.btnUnlock.setAlpha(0.75f);
                    }
                }
            }
            productType = object.getString("type");

            if (object.has("images")) {
                JSONObject images = object.getJSONObject("images");
                if (images.has("thumbnail") && !images.getString("thumbnail").equalsIgnoreCase("")) {
                    Picasso.get().load(Uri.parse(images.getString("thumbnail"))).transform(new RoundedTransformation(20, 0)).into(holder.binding.ivThumbnail);
                } else {
                    Picasso.get().load(R.drawable.feed_thumb_default).transform(new RoundedTransformation(20, 0)).into(holder.binding.ivThumbnail);
                }
            } else {
                Picasso.get().load(R.drawable.feed_thumb_default).transform(new RoundedTransformation(20, 0)).into(holder.binding.ivThumbnail);
            }

            holder.binding.tvDialogTitle.setText(object.getString("name") + " ");
            holder.binding.tvDialogContent.setText(object.getString("description") + " ");

            if (object.has("prices") && holder.binding.btnUnlock.isEnabled()) {
                JSONObject prices = object.getJSONObject("prices");
                if (prices.has("points")) {
                    holder.binding.btnUnlock.setText(context.getResources().getString(R.string.redeem) + " - " + prices.get("points") + " Points");
                } else {
                    holder.binding.btnUnlock.setText(context.getResources().getString(R.string.redeem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalProductId = productId;
        String finalProductName = productName;
        String finalProductType = productType;

        holder.binding.btnUnlock.setOnClickListener(v -> {
            FireUtils.showProgressDialog(context, context.getResources().getString(R.string.please_wait));
            FirebaseFirestoreApi.redeemProduct(finalProductId)
                    .addOnCompleteListener(task -> {
                        FireUtils.hideProgressDialog();
                        if (task.isSuccessful()) {
                            holder.binding.btnUnlock.setText("Already Redeemed");
                            holder.binding.btnUnlock.setEnabled(false);
                            holder.binding.btnUnlock.setAlpha(0.75f);
                            if (finalProductType.equalsIgnoreCase("brush")) {
                                AppUtils.getPurchasedBrushes().add(finalProductName);
                            } else {
                                AppUtils.getPurchasedProducts().add(finalProductName);
                            }
                            ContextKt.showToast(context, "Redeem Success");
                        } else {
                            try {
                                if (task.getException() != null) {
                                    if (task.getException().toString().contains("Insufficient points")) {
                                        if (finalProductType.equalsIgnoreCase("brush")) {
                                            FireUtils.showStoreError(context, "feature");
                                        }else{
                                            FireUtils.showStoreError(context, "brush");
                                        }
                                    } else {
                                        ContextKt.showToast(context, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                    }
                                    Log.e("TAGRR", task.getException().toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        DialogStoreBinding binding;

        public ViewHolder(@NonNull DialogStoreBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
