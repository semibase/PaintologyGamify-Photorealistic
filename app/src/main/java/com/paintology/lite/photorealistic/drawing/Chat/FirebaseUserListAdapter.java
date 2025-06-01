package com.paintology.lite.photorealistic.drawing.Chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Interfaces.user_list_page_interface;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.util.ChatUtils;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.HashMap;
import java.util.List;

public class FirebaseUserListAdapter extends RecyclerView.Adapter<FirebaseUserListAdapter.MyViewHolder> {


    Activity _context;
    View.OnClickListener _listener;
    List<Firebase_User> _list;
    StringConstants constants = new StringConstants();
    String _user_id = "";
    user_list_page_interface _interface;
    String myKey = "";
    Gson _gson = new Gson();
    HashMap<String, String> my_user_list;

    public FirebaseUserListAdapter(Activity _context, View.OnClickListener _listener, List<Firebase_User> user_list, HashMap<String, String> my_user_list, user_list_page_interface _interface) {
        this._context = _context;
        this._list = user_list;
        this._listener = _listener;
        _user_id = constants.getString(constants.UserId, _context);
        this._interface = _interface;
        this.my_user_list = my_user_list;
        myKey = MyApplication.get_realTimeDbUtils(_context).getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_user_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Firebase_User _user = _list.get(i);
        if (_user != null) {
            Log.e("TAGG", "Username At Adapter " + _user.getUser_name() + " Profile " + _user.getUser_profile_pic());
            myViewHolder.tv_username.setText(_user.getUser_name());
            try {
                String url = _user.getUser_profile_pic();
                Glide.with(_context)
                        .load(url)
                        .placeholder(R.drawable.img_default_avatar)
                        .error(R.drawable.img_default_avatar)
                        .into(myViewHolder.iv_profile_icon);
            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage());
            }

            try {
                if (_user.isBlocked()) {
                    myViewHolder.tv_blocked.setVisibility(View.VISIBLE);
                    myViewHolder.tv_blocked.setText(_context.getResources().getString(R.string.blocked));
                } else if (_user.isPending()) {
                    myViewHolder.tv_blocked.setVisibility(View.VISIBLE);
                    myViewHolder.tv_blocked.setText(_context.getResources().getString(R.string.pending));
                } else
                    myViewHolder.tv_blocked.setVisibility(View.GONE);
            } catch (Exception e) {
            }

            try {
                if (_user.isBlocked()) {
                    myViewHolder.view_online.setVisibility(View.GONE);
                    myViewHolder.view_online.setVisibility(View.GONE);
                } else if (_user.getIs_online().equalsIgnoreCase("true")) {
                    myViewHolder.view_online.setVisibility(View.VISIBLE);
                    myViewHolder.view_offline.setVisibility(View.GONE);
                } else {
                    myViewHolder.view_online.setVisibility(View.GONE);
                    myViewHolder.view_offline.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
            if (!_user.isPending())
                getUnreadCounter(_user.getKey(), _user.getUser_id(), myViewHolder.tv_unread_counter, myViewHolder.tv_last_msg);
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    boolean is_found_in_delete = false;
    boolean is_found_in_block = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_profile_icon, iv_chat_user_menu;
        TextView tv_username, tv_blocked, tv_unread_counter, tv_last_msg;
        RelativeLayout rl_main;
        View view_online, view_offline;
        LinearLayout ll_dots;

        public MyViewHolder(View view) {
            super(view);
            iv_profile_icon = (ImageView) view.findViewById(R.id.iv_profile_pic);
            iv_chat_user_menu = (ImageView) view.findViewById(R.id.iv_chat_user_menu);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_blocked = (TextView) view.findViewById(R.id.tv_block);
            tv_last_msg = (TextView) view.findViewById(R.id.tv_last_msg);
            rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
            tv_blocked.setVisibility(View.GONE);

            tv_unread_counter = view.findViewById(R.id.tv_unread_counter);
            tv_unread_counter.setVisibility(View.GONE);

            view_online = view.findViewById(R.id.view_online);
            view_offline = view.findViewById(R.id.view_offline);

            view_offline.setVisibility(View.GONE);
            view_online.setVisibility(View.GONE);
            ll_dots = view.findViewById(R.id.ll_dots);
            rl_main.setOnClickListener(v -> {
                new ChatUtils(_context).openChatScreen(_list.get(getAdapterPosition()).getKey(), _list.get(getAdapterPosition()).getUser_name());
            });
            ll_dots.setOnClickListener(v -> {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(_context, constants.chat_user_menu_click, Toast.LENGTH_SHORT).show();
                }
                FirebaseUtils.logEvents(_context, constants.chat_user_menu_click);
                showDialog(v, _list.get(getAdapterPosition()));
            });
        }
    }

    void showDialog(View view, Firebase_User _object) {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(_context, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_chat_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_view_profile:
                        try {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(_context, constants.chat_menu_see_user_profile, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(_context, constants.chat_menu_see_user_profile);
                            if (_object.getUser_id() != null) {
                                FireUtils.openProfileScreen(_context, _object.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("FirebaseUserListAdapter", e.getMessage());
                        }
                        break;
                    case R.id.action_view_post:
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(_context, constants.chat_menu_see_user_posts, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(_context, constants.chat_menu_see_user_posts);
                        _interface.openPostList(_object);
                        break;
                    case R.id.action_block:
                        String _str = "";

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(_context, constants.chat_menu_block_user, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(_context, constants.chat_menu_block_user);

                        if (_object.isBlocked()) {
                            _str = "Unblock <b> " + _object.getUser_name() + "</b> ?";
                            try {
                                new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    if (BuildConfig.DEBUG) {
                                                        Toast.makeText(_context, constants.chat_menu_unblock_user_success, Toast.LENGTH_SHORT).show();
                                                    }
                                                    FirebaseUtils.logEvents(_context, constants.chat_menu_unblock_user_success);
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                    Query applesQuery = reference.child(constants.firebase_user_list).child(_user_id).child(constants.firebase_blocked_user).orderByChild("user_id").equalTo(_object.getUser_id());
                                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                                                appleSnapshot.getRef().removeValue();
                                                                Log.e("TAGG", "applesQuery onRemove");
//                                                        Toast.makeText(_context, _list.get(getAdapterPosition()).getUser_name() + " Unblocked ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                }
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } catch (Exception e) {

                            }
                        } else {
                            _str = "Block <b> " + _object.getUser_name() + "</b> ?";
                            new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (BuildConfig.DEBUG) {
                                                Toast.makeText(_context, constants.chat_menu_block_user_success, Toast.LENGTH_SHORT).show();
                                            }
                                            FirebaseUtils.logEvents(_context, constants.chat_menu_block_user_success);
                                            HashMap<String, String> _map = new HashMap<>();
                                            _map.put("user_id", _object.getUser_id());
                                            MyApplication.get_realTimeDbUtils(_context).getDbReference().child(constants.firebase_user_list).child(_user_id).child(constants.firebase_blocked_user).push().setValue(_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(_context, "success", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                        break;
                }
                return false;
            }
        });
        // Showing the popup menu
        popupMenu.show();

//        final Dialog dialog = new Dialog(_context);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        dialog.setContentView(R.layout.chat_menu_dialog_item);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//
//
//        TextView tv_profile = dialog.findViewById(R.id.tv_see_profile);
//        TextView tv_see_post = dialog.findViewById(R.id.tv_see_posts);
//        TextView tv_block = dialog.findViewById(R.id.tv_block);
//        TextView tv_delete_post = dialog.findViewById(R.id.tv_delete_post);
//        TextView tv_chat_with = dialog.findViewById(R.id.tv_chat_with);
//
//
//        String _str = "See <b> " + _object.getUser_name() + "'s</b>" + " profile";
//        tv_profile.setText(Html.fromHtml(_str));
//
//        _str = "See posts by<b> " + _object.getUser_name() + "</b>";
//        tv_see_post.setText(Html.fromHtml(_str));
//
//        _str = "Chat with<b> " + _object.getUser_name() + "</b>";
//        tv_chat_with.setText(Html.fromHtml(_str));
//
//
//        if (_object.isBlocked())
//            _str = "Unblock<b> " + _object.getUser_name() + "</b>";
//        else
//            _str = "Block<b> " + _object.getUser_name() + "</b>";
//        tv_block.setText(Html.fromHtml(_str));
//
//        _str = "Delete<b> " + _object.getUser_name() + "</b>";
//        tv_delete_post.setText(Html.fromHtml(_str));
//
//        tv_block.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String _str = "";
//
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(_context, constants.chat_menu_block_user, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(_context, constants.chat_menu_block_user);
//
//                if (_object.isBlocked()) {
//                    _str = "Unblock <b> " + _object.getUser_name() + "</b> ?";
//                    try {
//                        new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        try {
//                                            if (BuildConfig.DEBUG){
//                                                Toast.makeText(_context, constants.chat_menu_unblock_user_success, Toast.LENGTH_SHORT).show();
//                                            }
//                                            FirebaseUtils.logEvents(_context, constants.chat_menu_unblock_user_success);
//                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                                            Query applesQuery = reference.child(constants.firebase_user_list).child(_user_id).child(constants.firebase_blocked_user).orderByChild("user_id").equalTo(_object.getUser_id());
//                                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
//                                                        appleSnapshot.getRef().removeValue();
//                                                        Log.e("TAGG", "applesQuery onRemove");
////                                                        Toast.makeText(_context, _list.get(getAdapterPosition()).getUser_name() + " Unblocked ", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//                                                    Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
//                                                }
//                                            });
//                                        } catch (Exception e) {
//                                        }
//                                        dialog.dismiss();
//                                    }
//                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
//                    } catch (Exception e) {
//
//                    }
//                } else {
//                    _str = "Block <b> " + _object.getUser_name() + "</b> ?";
//                    new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (BuildConfig.DEBUG){
//                                        Toast.makeText(_context, constants.chat_menu_block_user_success, Toast.LENGTH_SHORT).show();
//                                    }
//                                    FirebaseUtils.logEvents(_context, constants.chat_menu_block_user_success);
//                                    HashMap<String, String> _map = new HashMap<>();
//                                    _map.put("user_id", _object.getUser_id());
//                                    MyApplication.get_realTimeDbUtils(this).getDbReference().child(constants.firebase_user_list).child(_user_id).child(constants.firebase_blocked_user).push().setValue(_map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(_context, "success", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                    dialog.dismiss();
//                                }
//                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//                }
//                dialog.dismiss();
//            }
//        });
//
//        tv_delete_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(_context, constants.chat_menu_delete_user, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(_context, constants.chat_menu_delete_user);
//                String _str = "Delete<b> " + _object.getUser_name() + "</b> ?";
//                new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (BuildConfig.DEBUG){
//                                    Toast.makeText(_context, constants.chat_menu_delete_user_sucess, Toast.LENGTH_SHORT).show();
//                                }
//                                FirebaseUtils.logEvents(_context, constants.chat_menu_delete_user_sucess);
//                                HashMap<String, String> _map = new HashMap<>();
//                                _map.put("user_id", _object.getUser_id());
//                                MyApplication.get_realTimeDbUtils(this).getDbReference().child(constants.firebase_user_list).child(_user_id).child(constants.firebase_deleted_user).push().setValue(_map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
////                                        Toast.makeText(_context, "success", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                dialog.dismiss();
//                            }
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//                dialog.dismiss();
//            }
//        });
//
//        tv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (BuildConfig.DEBUG){
//                        Toast.makeText(_context, constants.chat_menu_see_user_profile, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(_context, constants.chat_menu_see_user_profile);
//                    if (_object.getUser_id() != null) {
//                        Intent _intent = new Intent(_context, SelectedUserProfile.class);
//                        _intent.putExtra("_id", Integer.parseInt(_object.getUser_id()));
//                        _context.startActivity(_intent);
//                        dialog.dismiss();
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        });
//
//        tv_see_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               /* Intent _intent = new Intent(_context, CommunityDetail.class);
//                _intent.setAction("isFromProfile");
//                _intent.putExtra("user_id", _object.getUser_id());
//                _intent.putExtra("user_name", _object.getUser_name());
//                _context.startActivity(_intent);*/
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(_context, constants.chat_menu_see_user_posts, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(_context, constants.chat_menu_see_user_posts);
//                _interface.openPostList(_object);
//                dialog.dismiss();
//            }
//        });
//
//        tv_chat_with.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (BuildConfig.DEBUG){
//                    Toast.makeText(_context, constants.chat_menu_chat_click, Toast.LENGTH_SHORT).show();
//                }
//                FirebaseUtils.logEvents(_context, constants.chat_menu_chat_click);
//                dialog.dismiss();
//                //Same code of rl_main click
//                is_found_in_delete = false;
//                is_found_in_block = false;
//                if (_object.isBlocked()) {
//                    String _str = "Unblock <b> " + _object.getUser_name() + "</b> ?";
//                    new AlertDialog.Builder(_context).setMessage(Html.fromHtml(_str))
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    try {
//                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                                        Query applesQuery = reference.child(constants.firebase_user_list).child(_user_id).child(constants.firebase_blocked_user).orderByChild("user_id").equalTo(_object.getUser_id());
//                                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
//                                                    appleSnapshot.getRef().removeValue();
//                                                    Log.e("TAGG", "applesQuery onRemove");
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//                                                Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
//                                            }
//                                        });
//                                    } catch (Exception e) {
//                                    }
//                                    dialog.dismiss();
//                                }
//                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//                } else {
//
//                    try {
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                        Query applesQuery = reference.child(constants.firebase_user_list).child(_object.getUser_id()).child(constants.firebase_blocked_user).orderByChild("user_id").equalTo(_user_id);
//                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
////                                        appleSnapshot.getRef().removeValue();
//                                    Log.e("TAGG", "User Found in list blocked");
//                                    Toast.makeText(_context, _object.getUser_name() + " is not available for chat!", Toast.LENGTH_SHORT).show();
////                                    break;
//                                    is_found_in_block = true;
//                                    return;
//                                }
//
//                                try {
//                                    Query applesQuery = reference.child(constants.firebase_user_list).child(_object.getUser_id()).child(constants.firebase_deleted_user).orderByChild("user_id").equalTo(_user_id);
//                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
////                                        appleSnapshot.getRef().removeValue();
//                                                Log.e("TAGG", "User Found in list deleted");
//                                                Toast.makeText(_context, _object.getUser_name() + " is not available for chat!", Toast.LENGTH_SHORT).show();
//                                                is_found_in_delete = true;
//                                                return;
//                                            }
//                                            if (!is_found_in_delete && !is_found_in_block) {
////                                                view.setTag(getAdapterPosition());
////                                                _listener.onClick(view);
//                                                dialog.dismiss();
//                                                if (BuildConfig.DEBUG){
//                                                    Toast.makeText(_context, constants.chat_open_user_click, Toast.LENGTH_SHORT).show();
//                                                }
//                                                FirebaseUtils.logEvents(_context, constants.chat_open_user_click);
//                                                Intent _intent = new Intent(_context, ChatActivity.class);
//                                                String userData = _gson.toJson(_object);
//                                                Log.e("TAG", "Converted Data " + userData);
//                                                _intent.putExtra("selected_user", userData);
//                                                _context.startActivity(_intent);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                            Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
//                                        }
//                                    });
//                                } catch (Exception e) {
//
//                                }
//                                Log.e("TAGG", "User Found in list is_found ");
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
//                            }
//                        });
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//        });
//        dialog.show();
    }

    public int getUnreadCounter(String senderKey, String senderID, TextView tv_counter, TextView tv_last_msg) {


        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference(constants.firebase_chat_module).child(my_user_list.get(senderKey)).child("Msg");
        _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                String _lst_msg = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equalsIgnoreCase(senderKey) && chat.getIsMsgseen().equalsIgnoreCase("false")) {
                        counter = counter + 1;
                    }
                    _lst_msg = chat.getMessage();
                }

                tv_last_msg.setText(_lst_msg);
                if (counter == 0) {
                    tv_counter.setVisibility(View.GONE);
                } else {
                    tv_counter.setText(counter + "");
                    tv_counter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return 0;
    }

    private String getChatNode(int uid1, int uid2) {
        if (uid1 < uid2) {
            return uid1 + "_" + uid2;
        } else {
            return uid2 + "_" + uid1;
        }
    }


}
