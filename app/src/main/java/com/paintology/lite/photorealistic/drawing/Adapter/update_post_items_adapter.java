package com.paintology.lite.photorealistic.drawing.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.paintology.lite.photorealistic.drawing.Community.ShowPostFromNotification;
import com.paintology.lite.photorealistic.drawing.Model.AlbumImage;
import com.paintology.lite.photorealistic.drawing.Model.Spinner_Dialog_Item;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Youtube.utils.Utils;
import com.paintology.lite.photorealistic.drawing.util.KGlobal;
import com.paintology.lite.photorealistic.drawing.util.PostInterface;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.model.TagModel;

import java.util.ArrayList;
import java.util.List;

public class update_post_items_adapter extends RecyclerView.Adapter<update_post_items_adapter.MyViewHolder> {
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
    CustomeSpinnerAdapter _adapter_art_fav;
    CustomeSpinnerAdapter _adapter_art_medi;
    String _username = "";
    int lastpos = 0;
    StringConstants constants = new StringConstants();

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

    public update_post_items_adapter(List<AlbumImage> fileList, Context context, PostInterface objPostInterface) {
        this.fileList = fileList;
        this.context = context;
        this.objPostInterface = objPostInterface;
        lst_art_fav = context.getResources().getStringArray(R.array.arr_art_fav);
        lst_art_medium = context.getResources().getStringArray(R.array.art_medium);
        lst_art_ability = context.getResources().getStringArray(R.array.arr_art_ability);
        _username = constants.getString(constants.Username, context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_post, parent, false);
        return new MyViewHolder(itemView, new MyCustomEditTextListener(), new listener_for_desription(), listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        lastpos = position;
        if (fileList.get(position).isLocalPath()) {
            Glide.with(context)
                    .load("file://" + fileList.get(position).getFilePath())
                    .apply(new RequestOptions().placeholder(R.drawable.blue_circle).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.iv_selected_image);
        } else {
            String imageUrl = fileList.get(position).getFilePath();
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(context)
                        .load(imageUrl)
                        .error(R.drawable.image_placeholder)
                        .into(holder.iv_selected_image);
            }
        }
        holder.myCustomEditTextListener.updatePosition(position);
        holder.description_listener.updatePosition(position);
        holder.edt_caption.setText(fileList.get(position).getIv_caption());
        holder.edt_description.setText(fileList.get(position).getIv_description());
        holder.edt_youtube_link.setText(fileList.get(position).getYoutube_url());

        holder.tv_art_fav.setText(fileList.get(position).getStr_art_fav());
        holder.tv_art_medium.setText(fileList.get(position).getStr_art_med());
        holder.tv_art_ability.setText(fileList.get(position).getArt_ability());

        if (fileList.get(position).getMode() == 1) {
            holder.pbar.setVisibility(View.VISIBLE);
            holder.iv_tick_done.setVisibility(View.GONE);
            holder.btn_update_post.setTextColor(context.getResources().getColor(R.color.white));
            holder.btn_update_post.setText("Updating");
        } else if (fileList.get(position).getMode() == 2) {
            holder.pbar.setVisibility(View.GONE);
            holder.iv_tick_done.setVisibility(View.VISIBLE);
            holder.btn_update_post.setTextColor(context.getResources().getColor(R.color.dull_white));
            holder.btn_update_post.setText("Success");
        } else {
            holder.btn_update_post.setTextColor(context.getResources().getColor(R.color.white));
            holder.btn_update_post.setText("Update");
            holder.pbar.setVisibility(View.GONE);
            holder.iv_tick_done.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    String[] artAbility;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_selected_image, iv_tick_done;
        public TextView btn_update_post, btn_delete_post;
        EditText edt_description, edt_youtube_link;
        EditText edt_caption;
        TagView edt_hashTags;
        public MyCustomEditTextListener myCustomEditTextListener;
        public listener_for_desription description_listener;
        public ProgressBar pbar;
        TextView tv_art_fav, tv_art_medium;
        TextView tv_art_ability;
        TextView tv_open_post;

        public MyViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener, listener_for_desription description_listener, View.OnClickListener listener) {
            super(view);

            artAbility = context.getResources().getStringArray(R.array.arr_art_ability);
            iv_tick_done = (ImageView) view.findViewById(R.id.iv_tick_done);
            iv_selected_image = (ImageView) view.findViewById(R.id.iv_selected_image);
            btn_update_post = view.findViewById(R.id.btn_save);
            btn_update_post.setText("Update");
            edt_caption = (EditText) view.findViewById(R.id.edt_image_caption);
            edt_description = (EditText) view.findViewById(R.id.edt_image_description);
            edt_youtube_link = (EditText) view.findViewById(R.id.edt_youtube_link);
            pbar = (ProgressBar) view.findViewById(R.id.pbar);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.description_listener = description_listener;
            edt_description.addTextChangedListener(description_listener);
            edt_caption.addTextChangedListener(myCustomEditTextListener);
            mspanable = edt_caption.getText();

            edt_hashTags = (TagView) view.findViewById(R.id.et_tags);
            edt_hashTags.setHint("Add HashTag1#, HashTag2#");
            String[] tagList = new String[]{"paintology"};
            edt_hashTags.setTagList(tagList);

            int[] default_selection = new int[1];
            default_selection[0] = 0;

            tv_art_ability = (TextView) view.findViewById(R.id.spn_art_ability);
            tv_art_fav = (TextView) view.findViewById(R.id.spn_art_fav);
            ArrayList<String> _lst = new ArrayList<>();
            for (String s : lst_art_ability)
                _lst.add(s);

            tv_open_post = (TextView) view.findViewById(R.id.tv_open_post);
            tv_open_post.setVisibility(View.VISIBLE);
            tv_open_post.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("post_id", fileList.get(getAdapterPosition()).getPost_id());

                Intent intent = new Intent(
                        context,
                        ShowPostFromNotification.class
                );
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            tv_art_medium = (TextView) view.findViewById(R.id.spn_art_generes);

            btn_delete_post = view.findViewById(R.id.btn_cancel);

            btn_delete_post.setText("Delete");
            btn_delete_post.setOnClickListener(view1 -> {

                if (Utils.isOnline(context)) {
                    confirmDialog(getAdapterPosition(), fileList.get(getAdapterPosition()).getIv_caption());
                } else
                    Toast.makeText(context, context.getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
            });
            btn_update_post.setOnClickListener(view12 -> {
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
                    } else if (!edt_youtube_link.getText().toString().isEmpty() && !Patterns.WEB_URL.matcher(edt_youtube_link.getText().toString().trim()).matches()) {
                        edt_youtube_link.setError("Invalid URL");
                        edt_youtube_link.requestFocus();
                        return;
                    }

                    List<TagModel> tagList1 = edt_hashTags.getSelectedTags();

                    StringBuilder _final_builder = new StringBuilder();
                    for (TagModel model : tagList1) {
                        String tag = model.getTagText();
                        _final_builder.append("#" + tag.replace(" ", "") + "|");
                    }

                    String finalHashTag = "";
                    if (_final_builder.toString() != null && _final_builder.toString().endsWith("|"))
                        finalHashTag = _final_builder.toString().substring(0, _final_builder.toString().length() - 1);
                    else
                        finalHashTag = _final_builder.toString();
                    Log.e("TAGGG", "Data while post >> After Remove " + finalHashTag);

                    if (finalHashTag.isEmpty()) {
                        objPostInterface.postImage(
                                getAdapterPosition(),
                                edt_caption.getText().toString().trim(),
                                edt_description.getText().toString().trim(),
                                finalHashTag + "",
                                edt_youtube_link.getText().toString().trim());
                    } else {
                        objPostInterface.postImage(
                                getAdapterPosition(),
                                edt_caption.getText().toString().trim(),
                                edt_description.getText().toString().trim(),
                                finalHashTag + "",
                                edt_youtube_link.getText().toString().trim());
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at post image " + e.getMessage(), e);
                }
            });

            tv_art_fav.setOnClickListener(view13 -> showDialog(context.getResources().getString(R.string.pick_art_fav), true, getAdapterPosition()));

            tv_art_medium.setOnClickListener(view14 -> showDialog(context.getResources().getString(R.string.pick_art_med), false, getAdapterPosition()));

            tv_art_ability.setOnClickListener(view15 -> showDialog(tv_art_ability, getAdapterPosition()));

            iv_selected_image.setOnClickListener(view16 -> objPostInterface.pickPhotos(getAdapterPosition()));
        }
    }

    public void notifyList(int pos, boolean isFromNotify) {
        if (isFromNotify)
            notifyItemChanged(pos);
        else
            notifyItemRemoved(pos);
    }

    public void updateStatus(int pos, int mode) {
        fileList.get(pos).setMode(mode);
        notifyItemChanged(pos);
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

    public void addNewData(List<AlbumImage> _list) {
        Log.e("TAGGG", "Size Before " + fileList.size());
        for (int i = 0; i < _list.size(); i++) {
            fileList.add(_list.get(i));
            notifyItemInserted(i);
        }
        notifyDataSetChanged();
        Log.e("TAGGG", "Size After Add " + fileList.size());
    }

    public int getLastPosition() {
        return lastpos;
    }

    private void confirmDialog(int position, String title) {
        AlertDialog.Builder lBuilder1 = new AlertDialog.Builder(context);
        lBuilder1.setTitle(context.getResources().getString(R.string.delete_post));

        String _msg = "Do you want to delete <b>" + title + "</b>  post now ?";
        lBuilder1.setMessage(Html.fromHtml(_msg)).setCancelable(true);

        lBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                objPostInterface.deletePost(position);
                dialog.dismiss();
            }
        });

        lBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        lBuilder1.create().show();
    }
}

