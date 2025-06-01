package com.paintology.lite.photorealistic.drawing.CustomePicker;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.Adapter.CustomeSpinnerAdapter;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.Model.Spinner_Dialog_Item;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.PostInterface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.interfaces.TagItemListener;
import com.skyhope.materialtagview.model.TagModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class show_selected_items_adapter extends RecyclerView.Adapter<show_selected_items_adapter.MyViewHolder> {
    private List<AlbumImage> fileList;
    private Context context;
    View.OnClickListener listener;
    Spannable mspanable;
    PostInterface objPostInterface;

    ArrayList<Spinner_Dialog_Item> _list_art_fav;
    ArrayList<Spinner_Dialog_Item> _list_art_medium;

    String[] lst_art_fav;
    String[] lst_art_medium;
    String[] lst_art_ability;

    String isPostGallery;

    CustomeSpinnerAdapter _adapter_art_fav;
    CustomeSpinnerAdapter _adapter_art_medi;
    boolean isFromCanvas = false;
    StringConstants constants = new StringConstants();
    String _username = "";
    ArrayList<String> reservedHashTags;

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                fileList.get(position).setIv_caption(s.toString());
            } catch (Exception e) {
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private class listener_for_desription implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                fileList.get(position).setIv_description(charSequence.toString());
            } catch (Exception e) {

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    public show_selected_items_adapter(String isPostGallery, List<AlbumImage> fileList, Context context, PostInterface objPostInterface, boolean isFromCanvas, ArrayList<String> reservedTags) {
        this.isPostGallery = isPostGallery;
        this.fileList = fileList;
        this.context = context;
        this.objPostInterface = objPostInterface;
        lst_art_fav = context.getResources().getStringArray(R.array.arr_art_fav);
        lst_art_medium = context.getResources().getStringArray(R.array.art_medium);
        lst_art_ability = context.getResources().getStringArray(R.array.arr_art_ability);
        this.isFromCanvas = isFromCanvas;
        _username = constants.getString(constants.Username, context);
        this.reservedHashTags = reservedTags;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        try {
            if (isPostGallery.equals("post_gallery"))
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post_gallery, parent, false);
            else
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post, parent, false);

        } catch (Exception e) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post, parent, false);

        }

       /* if (isFromCanvas)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_canvas, parent, false);
        else*/
//        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post, parent, false);
//        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post_gallery, parent, false);
        return new MyViewHolder(itemView, new MyCustomEditTextListener(), new listener_for_desription(), listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {

            Glide.with(context)
                    .load("file://" + fileList.get(position).getFilePath())
                    .apply(new RequestOptions().placeholder(R.drawable.blue_circle).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.iv_selected_image);

            holder.myCustomEditTextListener.updatePosition(position);
            holder.description_listener.updatePosition(position);
            holder.edt_caption.setText(fileList.get(position).getIv_caption());
            holder.edt_description.setText(fileList.get(position).getIv_description());

            holder.tv_art_fav.setText(fileList.get(position).getStr_art_fav());
            holder.tv_art_medium.setText(fileList.get(position).getStr_art_med());
            holder.tv_art_ability.setText(fileList.get(position).getArt_ability());

            holder.tv_tags_message.setText(context.getResources().getString(R.string.tag_tips_msg));

            if (fileList.get(position).getMode() == 1) {
                holder.pbar.setVisibility(View.VISIBLE);
                holder.iv_tick_done.setVisibility(View.GONE);
                holder.btn_do_post.setTextColor(context.getResources().getColor(R.color.white));
                holder.btn_do_post.setText("Posting");
            } else if (fileList.get(position).getMode() == 2) {
                holder.pbar.setVisibility(View.GONE);
                holder.iv_tick_done.setVisibility(View.VISIBLE);
                holder.btn_do_post.setTextColor(context.getResources().getColor(R.color.dull_white));
                holder.btn_do_post.setText("Success");
            } else {
                holder.btn_do_post.setTextColor(context.getResources().getColor(R.color.white));
                if (Objects.equals(isPostGallery, "post_gallery")) {
                    holder.btn_do_post.setText("Post");
                } else {
                    holder.btn_do_post.setText("Publish");
                }
                holder.pbar.setVisibility(View.GONE);
                holder.iv_tick_done.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    String[] artAbility;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_selected_image, iv_tick_done;
        public TextView btn_do_post, btn_remove, txt_youtube_link;
        EditText edt_description, edt_youtube_link;
        EditText edt_caption;
        TagView edt_hashTags;
        TextView tv_tags_message, tv_yt_url;
        public MyCustomEditTextListener myCustomEditTextListener;
        public listener_for_desription description_listener;
        public ProgressBar pbar;
        TextView tv_art_fav, tv_art_medium;
        TextView tv_art_ability;

        public MyViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener, listener_for_desription description_listener, View.OnClickListener listener) {
            super(view);

            txt_youtube_link = view.findViewById(R.id.txt_youtube_link);
            artAbility = context.getResources().getStringArray(R.array.arr_art_ability);
            iv_tick_done = (ImageView) view.findViewById(R.id.iv_tick_done);
            iv_selected_image = (ImageView) view.findViewById(R.id.iv_selected_image);
            btn_do_post = view.findViewById(R.id.btn_save);
            tv_tags_message = (TextView) view.findViewById(R.id.tv_tags_message);
            tv_yt_url = (TextView) view.findViewById(R.id.tv_yt_url);
            edt_caption = (EditText) view.findViewById(R.id.edt_image_caption);
            edt_hashTags = (TagView) view.findViewById(R.id.et_tags);
            edt_hashTags.setHint("Add HashTag1#, HashTag2#");


            String username = constants.getString(constants.Username, context);
            String[] tagList = new String[]{username};
            edt_hashTags.setTagList(tagList);

            edt_hashTags.initTagListener(new TagItemListener() {
                @Override
                public void onGetAddedItem(TagModel tagModel) {
                    if (reservedHashTags != null) {
                        for (String hashTag : reservedHashTags) {
                            if (hashTag.toLowerCase().contains(tagModel.getTagText().toLowerCase())) {
                                showDialog("#" + tagModel.getTagText());
                                List<TagModel> currentTags = edt_hashTags.getSelectedTags();
                                int index = currentTags.indexOf(tagModel);
                                edt_hashTags.removeViewAt(index);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onGetRemovedItem(TagModel model) {
                    Toast.makeText(context, "Removed " + model.getTagText(), Toast.LENGTH_SHORT).show();
                }
            });

            edt_hashTags.onGetSelectTag(0, username);

            edt_caption.clearFocus();
            edt_description = (EditText) view.findViewById(R.id.edt_image_description);
            edt_youtube_link = (EditText) view.findViewById(R.id.edt_youtube_link);
            pbar = (ProgressBar) view.findViewById(R.id.pbar);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.description_listener = description_listener;
            edt_description.addTextChangedListener(description_listener);
            edt_caption.addTextChangedListener(myCustomEditTextListener);
            mspanable = edt_caption.getText();

        /*    if (Objects.equals(isPostGallery, "post_gallery")) {
//                tv_yt_url.setVisibility(View.GONE);
//                edt_youtube_link.setVisibility(View.GONE);
                btn_do_post.setText("Post");
            } else {
                tv_yt_url.setVisibility(View.VISIBLE);
                edt_youtube_link.setVisibility(View.VISIBLE);
            }*/

           /* if (!AppUtils.getStoreProducts().containsKey("youtube_link")) {
                edt_youtube_link.setVisibility(View.GONE);
                txt_youtube_link.setVisibility(View.VISIBLE);
                tv_yt_url.setAlpha(0.5f);
                edt_youtube_link.setAlpha(0.5f);
            }*/

            if (!AppUtils.getPurchasedProducts().contains("youtube_link")) {
                edt_youtube_link.setVisibility(View.GONE);
                txt_youtube_link.setVisibility(View.VISIBLE);
                tv_yt_url.setAlpha(0.5f);
                edt_youtube_link.setAlpha(0.5f);

                txt_youtube_link.setOnClickListener(v -> {

                    FireUtils.getStoreDetails(context, "youtube_link", (productId, productName) -> {
                        FireUtils.showProgressDialog(context, context.getResources().getString(R.string.please_wait));
                        FirebaseFirestoreApi.redeemProduct(productId)
                                .addOnCompleteListener(task -> {
                                    FireUtils.hideProgressDialog();
                                    if (task.isSuccessful()) {
                                        if (!productName.equalsIgnoreCase("")) {
                                            AppUtils.getPurchasedProducts().add(productName);
                                        } else {
                                            AppUtils.getPurchasedProducts().add("youtube_link");
                                        }
                                        edt_youtube_link.setVisibility(View.VISIBLE);
                                        txt_youtube_link.setVisibility(View.GONE);
                                        tv_yt_url.setTextColor(ContextCompat.getColor(context, R.color.white));
                                        tv_yt_url.setAlpha(1f);
                                        edt_youtube_link.setAlpha(1f);
                                        ContextKt.showToast(context, "Redeem Success");
                                    } else {
                                        try {
                                            if (task.getException() != null) {
                                                if (task.getException().toString().contains("Insufficient points")) {
                                                    FireUtils.showStoreError(context, "feature");
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
                });
            } else {
                tv_yt_url.setAlpha(1f);
                edt_youtube_link.setAlpha(1f);
                edt_youtube_link.setVisibility(View.VISIBLE);
                txt_youtube_link.setVisibility(View.GONE);
                tv_yt_url.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

            int[] default_selection = new int[1];
            default_selection[0] = 0;

            tv_art_ability = (TextView) view.findViewById(R.id.spn_art_ability);
            tv_art_fav = (TextView) view.findViewById(R.id.spn_art_fav);
            ArrayList<String> _lst = new ArrayList<>();
            for (String s : lst_art_ability)
                _lst.add(s);

            tv_art_medium = (TextView) view.findViewById(R.id.spn_art_generes);

            btn_remove = view.findViewById(R.id.btn_cancel);
            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!isFromCanvas) {
                            fileList.remove(getAdapterPosition());
                            notifyList(getAdapterPosition(), false);
                            objPostInterface.cancelClick();
                        } else {
                            objPostInterface.cancelClick();
                        }
                    } catch (Exception e) {

                    }
                }
            });
            btn_do_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (fileList.get(getAdapterPosition()).getMode() != 0)
                            return;

                        if (!KGlobal.isInternetAvailable(context)) {
                            Toast.makeText(context, context.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (edt_caption.getText().toString().trim().isEmpty()) {
                            edt_caption.setError(context.getResources().getString(R.string.required));
                            edt_caption.requestFocus();
                            return;
                        } else if (edt_description.getText().toString().trim().isEmpty()) {
                            edt_description.setError(context.getResources().getString(R.string.required));
                            edt_description.requestFocus();
                            return;
                        } else if (!Objects.equals(isPostGallery, "isPostGallery")) {
                            if (!edt_youtube_link.getText().toString().isEmpty() && !Patterns.WEB_URL.matcher(edt_youtube_link.getText().toString().trim()).matches()) {
                                edt_youtube_link.setError("Invalid URL");
                                edt_youtube_link.requestFocus();
                                return;
                            }
                        }

//                        String _nameHashtag = "";
//                        try {
//                            if (_username.contains(" ")) {
//                                String[] split = _username.split(" ");
//                                if (split != null && split.length > 0) {
//                                    _nameHashtag = "#" + split[0];
//                                }
//                            } else {
//                                _nameHashtag = "#" + _username;
//                            }
//                        } catch (Exception e) {
//
//                        }
//
//                        StringTokenizer tokenizer = new StringTokenizer(_nameHashtag + " " + edt_caption.getText().toString(), " ");
//                        StringBuilder builder = new StringBuilder();
//                        try {
//                            if (tokenizer.countTokens() != 0)
//                                do {
//                                    String nextElem = tokenizer.nextToken().toString();
//                                    if (nextElem.startsWith("#") && nextElem.length() > 1) {
//                                        String[] separated = nextElem.trim().toString().split("#");
//                                        if (separated.length == 1)
//                                            builder.append(nextElem + "|");
//                                        else {
//                                            for (int i = 0; i < separated.length; i++) {
//                                                if (separated[i] != null && !separated[i].isEmpty())
//                                                    builder.append("#" + separated[i] + "|");
//                                            }
//                                        }
//                                    }
//                                } while (tokenizer.hasMoreTokens());
//                        } catch (Exception e) {
//
//                        }
//
//                        tokenizer = new StringTokenizer(edt_description.getText().toString(), " ");
//                        try {
//                            if (tokenizer.countTokens() != 0)
//                                do {
//                                    String nextElem = tokenizer.nextToken().toString();
//                                    if (nextElem.startsWith("#") && nextElem.length() > 1) {
//                                        String[] separated = nextElem.trim().toString().split("#");
//                                        if (separated.length == 1)
//                                            builder.append(nextElem + "|");
//                                        else {
//                                            for (int i = 0; i < separated.length; i++) {
//                                                if (separated[i] != null && !separated[i].isEmpty())
//                                                    builder.append("#" + separated[i] + "|");
//                                            }
//                                        }
//                                    }
//                                } while (tokenizer.hasMoreTokens());
//                        } catch (Exception e) {
//
//                        }
//
//                        builder.append("#" + fileList.get(getAdapterPosition()).getArt_ability().replace(" ", ""));
//
//                        if (fileList.get(getAdapterPosition()).getArtFavList() != null && fileList.get(getAdapterPosition()).getArtFavList().size() != 0)
//                            builder.append("|");
//
//                        if (fileList.get(getAdapterPosition()).getArtFavList() != null && fileList.get(getAdapterPosition()).getArtFavList().size() == 1)
//                            builder.append("#" + fileList.get(getAdapterPosition()).getArtFavList().get(0).replace(" ", ""));
//                        else if (fileList.get(getAdapterPosition()).getArtFavList() != null)
//                            for (int i = 0; i < fileList.get(getAdapterPosition()).getArtFavList().size(); i++) {
//                                if (i == fileList.get(getAdapterPosition()).getArtFavList().size() - 1)
//                                    builder.append("#" + fileList.get(getAdapterPosition()).getArtFavList().get(i).replace(" ", ""));
//                                else
//                                    builder.append("#" + fileList.get(getAdapterPosition()).getArtFavList().get(i).replace(" ", "") + "|");
//                            }
//
//
//                        if (fileList.get(getAdapterPosition()).getArtMediumList() != null && fileList.get(getAdapterPosition()).getArtMediumList().size() != 0)
//                            builder.append("|");
//
//                        if (fileList.get(getAdapterPosition()).getArtMediumList() != null && fileList.get(getAdapterPosition()).getArtMediumList().size() == 1)
//                            builder.append("#" + fileList.get(getAdapterPosition()).getArtMediumList().get(0).replace(" ", ""));
//                        else if (fileList.get(getAdapterPosition()).getArtMediumList() != null)
//                            for (int i = 0; i < fileList.get(getAdapterPosition()).getArtMediumList().size(); i++) {
//                                if (i == fileList.get(getAdapterPosition()).getArtMediumList().size() - 1)
//                                    builder.append("#" + fileList.get(getAdapterPosition()).getArtMediumList().get(i).replace(" ", ""));
//                                else
//                                    builder.append("#" + fileList.get(getAdapterPosition()).getArtMediumList().get(i).replace(" ", "") + "|");
//                            }
//                        Log.e("TAGGG", "Data while post >> Total Hash Tag " + builder.toString());
//
//                        String hashTag = builder.toString();
//
//                        StringTokenizer newToknizer = new StringTokenizer(hashTag, "#");
//                        StringBuilder _final_builder = new StringBuilder();
//                        do {
//                            String nextToken = newToknizer.nextToken();
//                            if (!nextToken.equalsIgnoreCase("Other|") && !nextToken.equalsIgnoreCase("Other")) {
//                                _final_builder.append("#" + nextToken);
//                            }
//                        } while (newToknizer.hasMoreTokens());
//                        Log.e("TAGGG", "Data while post >> Final tokenizer " + _final_builder.toString());

                        List<TagModel> tagList = edt_hashTags.getSelectedTags();//edt_hashTags.getText().toString().trim();

                        StringBuilder _final_builder = new StringBuilder();
                        for (TagModel model :
                                tagList) {
                            String tag = model.getTagText();
                            _final_builder.append("#" + tag.replace(" ", "") + "|");
                        }

                        String finalHashTag = "";
                        if (_final_builder.toString() != null && _final_builder.toString().endsWith("|"))
                            finalHashTag = _final_builder.substring(0, _final_builder.toString().length() - 1);
                        else
                            finalHashTag = _final_builder.toString();
                        Log.e("TAGGG", "Data while post >> After Remove " + finalHashTag);

                        if (finalHashTag.isEmpty()) {
                            objPostInterface.postImage(getAdapterPosition(), edt_caption.getText().toString().trim(), edt_description.getText().toString().trim(), finalHashTag + "", edt_youtube_link.getText().toString().trim());
                        } else {
                            objPostInterface.postImage(getAdapterPosition(), edt_caption.getText().toString().trim(), edt_description.getText().toString().trim(), finalHashTag + "", edt_youtube_link.getText().toString().trim());
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at post image " + e.getMessage(), e);
                    }
                }
            });

            tv_art_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(context.getResources().getString(R.string.pick_art_fav), true, getAdapterPosition());
                }
            });

            tv_art_medium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(context.getResources().getString(R.string.pick_art_med), false, getAdapterPosition());
                }
            });

            tv_art_ability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(tv_art_ability, getAdapterPosition());
                }
            });

//            edt_caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus)
//                        edt_caption.setHint("");
//                    else
//                        edt_caption.setHint(context.getResources().getString(R.string.enter_title_hashtag));
//                }
//            });
//
//            edt_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus)
//                        edt_description.setHint("");
//                    else
//                        edt_description.setHint(context.getResources().getString(R.string.enter_description_hashtag));
//                }
//            });
//
//            edt_youtube_link.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus)
//                        edt_youtube_link.setHint("");
//                    else
//                        edt_youtube_link.setHint(context.getResources().getString(R.string.enter_youtube_link));
//                }
//            });


        }
    }

    public void notifyList(int pos, boolean isFromNotify) {
        if (isFromNotify)
            notifyItemChanged(pos);
        else
            notifyItemRemoved(pos);
    }

    public void updateStatus(int pos, int mode) {
        try {
            fileList.get(pos).setMode(mode);
            notifyItemChanged(pos);
        } catch (Exception e) {

        }
    }

    public void showDialog(String title, Boolean isFromArtFav, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_with_recyclerview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        RecyclerView rv = dialog.findViewById(R.id.rv_dialod_items);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        Spinner_Dialog_Item obj;


        if (isFromArtFav) {
            _list_art_fav = new ArrayList<>();
            for (String a : lst_art_fav) {
                obj = new Spinner_Dialog_Item();
                obj.setItem_name(a);
                obj.setChecked(false);
                _list_art_fav.add(obj);
            }
            if (fileList.get(position).getArtFavList() != null)
                for (int i = 0; i < fileList.get(position).getArtFavList().size(); i++) {
                    for (int j = 0; j < _list_art_fav.size(); j++) {
                        if (fileList.get(position).getArtFavList().get(i).equalsIgnoreCase(_list_art_fav.get(j).getItem_name())) {
                            _list_art_fav.get(j).setChecked(true);
                            Log.e("TAGGG", "Selected Items " + _list_art_fav.get(j).getItem_name());
                            break;
                        }
                    }
                }
            _adapter_art_fav = new CustomeSpinnerAdapter(_list_art_fav, context);
            rv.setAdapter(_adapter_art_fav);
        } else {
            _list_art_medium = new ArrayList<>();
            for (String a : lst_art_medium) {
                obj = new Spinner_Dialog_Item();
                obj.setItem_name(a);
                obj.setChecked(false);
                _list_art_medium.add(obj);
            }
            if (fileList.get(position).getArtMediumList() != null)
                for (int i = 0; i < fileList.get(position).getArtMediumList().size(); i++) {
                    for (int j = 0; j < _list_art_medium.size(); j++) {
                        if (fileList.get(position).getArtMediumList().get(i).equalsIgnoreCase(_list_art_medium.get(j).getItem_name())) {
                            _list_art_medium.get(j).setChecked(true);
                            Log.e("TAGGG", "Selected Items " + _list_art_medium.get(j).getItem_name());
                            break;
                        }
                    }
                }
            _adapter_art_medi = new CustomeSpinnerAdapter(_list_art_medium, context);
            rv.setAdapter(_adapter_art_medi);
        }
        TextView cancelBTN = dialog.findViewById(R.id.tv_cancel);
        TextView acceptBTN = dialog.findViewById(R.id.tv_done);
        TextView tvTitle = dialog.findViewById(R.id.tv_head);
        tvTitle.setText(title);

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                StringBuilder stringBuilder;
                if (isFromArtFav) {
                    stringBuilder = new StringBuilder();
                    ArrayList<String> _lst = new ArrayList<>();
                    for (Spinner_Dialog_Item _obj : _list_art_fav) {
                        if (_obj.getChecked()) {
                            _lst.add(_obj.getItem_name());
                            stringBuilder.append(" " + _obj.getItem_name());
                        }
                    }
                    if (stringBuilder.toString().isEmpty()) {
                        stringBuilder.append(_list_art_fav.get(_list_art_fav.size() - 1).getItem_name());
                        fileList.get(position).setStr_art_fav(stringBuilder + "");
                        _lst.add(_list_art_fav.get(_list_art_fav.size() - 1).getItem_name());
                        fileList.get(position).setArtFavList(_lst);
                    } else {
                        fileList.get(position).setStr_art_fav(stringBuilder + "");
                        fileList.get(position).setArtFavList(_lst);
                    }
                    Log.e("TAGGG", "Selected Items " + stringBuilder);
                } else {
                    stringBuilder = new StringBuilder();
                    ArrayList<String> _lst = new ArrayList<>();
                    for (Spinner_Dialog_Item _obj : _list_art_medium) {
                        if (_obj.getChecked()) {
                            _lst.add(_obj.getItem_name());
                            stringBuilder.append(" " + _obj.getItem_name());
                        }
                    }

                    if (stringBuilder.toString().isEmpty()) {
                        stringBuilder.append(_list_art_medium.get(_list_art_medium.size() - 1).getItem_name());
                        fileList.get(position).setStr_art_med(stringBuilder + "");
                        _lst.add(_list_art_medium.get(_list_art_medium.size() - 1).getItem_name());
                        fileList.get(position).setArtMediumList(_lst);
                    } else {
                        fileList.get(position).setStr_art_med(stringBuilder + "");
                        fileList.get(position).setArtMediumList(_lst);
                    }
                }
                notifyItemChanged(position);

            }
        });
        dialog.show();
    }


    void showDialog(TextView textView, int position) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Ability");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

        arrayAdapter.addAll(context.getResources().getStringArray(R.array.arr_art_ability));

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                textView.setText(strName);
                fileList.get(position).setArt_ability(strName);
            }
        });
        builderSingle.show();
    }

    void showDialog(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

        builder1.setTitle(context.getResources().getString(R.string.reserved_hashtag));
//        String _name = "<b>" + msg + "</b>" + " You cannot use reserved HashTag";
//        builder1.setMessage(Html.fromHtml(_name));
        builder1.setMessage(context.getResources().getString(R.string.reserved_hashtag_msg));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

