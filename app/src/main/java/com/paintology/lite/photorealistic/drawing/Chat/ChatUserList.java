package com.paintology.lite.photorealistic.drawing.Chat;

import static com.paintology.lite.photorealistic.drawing.util.ContextKt.getLastUpdatedTime;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Interfaces.user_list_page_interface;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.Community.CommunityDetail;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.helpers.SwipeToDeleteCallback;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChatUserList extends AppCompatActivity implements user_list_page_interface {


    RecyclerView rv_all_user;
    FirebaseUserListAdapter _adapter;
    List<Firebase_User> _user_list = new ArrayList<Firebase_User>();

    String email = "";
    String username = "";
    StringConstants _constant = new StringConstants();

    Gson _gson = new Gson();
    ProgressDialog mProgressDialog;
    String logged_user_id;

    RealTimeDBUtils realTimeDBUtils;

    RelativeLayout rl_instruction;
    ArrayList<DeletedUser> _lst_deleted = new ArrayList<>();
    ArrayList<BlockedUsersModel> _lst_blocked = new ArrayList<>();
    user_list_page_interface _interface;

    private ConstraintLayout loginMsgContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.paintology_chat);

        email = _constant.getString(_constant.Email, ChatUserList.this);
        username = _constant.getString(_constant.Username, ChatUserList.this);

        _interface = this;

        rv_all_user = (RecyclerView) findViewById(R.id.rv_all_user);
        rv_all_user.setLayoutManager(new LinearLayoutManager(this));
        rv_all_user.addItemDecoration(new DividerItemDecoration(rv_all_user.getContext(), DividerItemDecoration.VERTICAL));

        realTimeDBUtils = MyApplication.get_realTimeDbUtils(this);

        rl_instruction = (RelativeLayout) findViewById(R.id.rl_instruction);

        logged_user_id = _constant.getString(_constant.UserId, ChatUserList.this);

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) {
                Toast.makeText(ChatUserList.this, StringConstants.slidepop_chat_login, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(ChatUserList.this, StringConstants.slidepop_chat_login);

            Intent intent = new Intent(ChatUserList.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        loginMsgContainer = findViewById(R.id.login_msg_container);
        if (AppUtils.isLoggedIn()) {
            loginMsgContainer.setVisibility(View.GONE);
            getData();
        } else {
            loginMsgContainer.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        if (realTimeDBUtils.getCurrentUser() != null) {
            showProgressDialog(getResources().getString(R.string.please_wait));
            realTimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(_constant.firebase_deleted_user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    _lst_deleted.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DeletedUser _deleted = postSnapshot.getValue(DeletedUser.class);
                        _lst_deleted.add(_deleted);
                    }
                    blockedUser();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    hideDialog();
                }
            });
        }
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    void blockedUser() {
        realTimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(_constant.firebase_blocked_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _lst_blocked.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BlockedUsersModel _deleted = postSnapshot.getValue(BlockedUsersModel.class);
                    _lst_blocked.add(_deleted);
                }
                getStartData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideDialog();
            }
        });
    }

    public void getStartData() {
        FirebaseDatabase.getInstance().getReference(_constant.firebase_chat_module)
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (postSnapshot.getKey() != null) {
                                if (postSnapshot.getKey().startsWith(logged_user_id)) {
                                    String key = postSnapshot.getKey().replace(logged_user_id + "_", "");
                                    lst_my_user_timestamp.put(key, getLastUpdatedTime(postSnapshot));
                                    lst_my_user.put(key, postSnapshot.getKey());
                                } else if (postSnapshot.getKey().endsWith(logged_user_id)) {
                                    String key = postSnapshot.getKey().replace("_" + logged_user_id, "");
                                    lst_my_user_timestamp.put(key, getLastUpdatedTime(postSnapshot));
                                    lst_my_user.put(key, postSnapshot.getKey());
                                }

                            }
                        }
                        getAllUser(lst_my_user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        getAllUser(lst_my_user);
                    }
                });
    }

    private void updateToken(String token) {
        try {
            DatabaseReference reference = realTimeDBUtils.getDbReference().child("Tokens");
            Token token1 = new Token(token);
            reference.child(realTimeDBUtils.getCurrentUser().getUid()).setValue(token1);
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

    DatabaseReference _reference;
    ValueEventListener _ref_listener;

    private void getAllUser(HashMap<String, String> my_user_list) {
        _reference = realTimeDBUtils.getDbReferenceUserList();
        _ref_listener = _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _user_list.clear();
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Firebase_User _user = null;
                        try {
                            _user = postSnapshot.getValue(Firebase_User.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;

                        }
                        if (_user != null && !_user.getKey().equalsIgnoreCase("")) {
                            setPending(_user);
                            //  Log.e("TAGRR", "User status " + _user.isPending() + " _id " + _user.getKey());
                            if (!isallreadyAdded(_user.getKey()) && !isFoundInDeleted(_user.getKey()) && !_user.getKey().equalsIgnoreCase(logged_user_id)) {
                                if (my_user_list.containsKey(_user.getKey())) {
                                    //   Log.e("TAGRR", "getAllUser User Added " + _user.getKey());
                                    if (isFoundInBlocked(_user.getKey())) {
                                        _user.setBlocked(true);
                                    } else
                                        _user.setBlocked(false);

                                    try {
                                        if (lst_my_user_timestamp.containsKey(_user.getKey())) {
                                            _user.setUpdatedTime(lst_my_user_timestamp.get(_user.getKey()));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    _user_list.add(_user);
                                }
                            }
                        }

                    }

                    try {
                        Collections.sort(_user_list,
                                Comparator.comparingLong(Firebase_User::getUpdatedTime).reversed());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* try {
                        Collections.sort(_user_list, (o1, o2) -> {
                            try {
                                boolean b1 = Boolean.parseBoolean(o1.getIs_online());
                                boolean b2 = Boolean.parseBoolean(o2.getIs_online());
                                return Boolean.compare(b2, b1);
                            } catch (Exception e) {
                                Log.e("TAGGG", "Exception at sort 1 " + e.getMessage());
                            }
                            return 0;
                        });
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at sort " + e.getMessage());
                    }

                    try {
                        Collections.sort(_user_list, (o1, o2) -> {
                            try {
                                boolean b1 = o1.isBlocked();
                                boolean b2 = o2.isBlocked();
                                return Boolean.compare(b1, b2);
                            } catch (Exception e) {
                                Log.e("TAGGG", "Exception at sort 1 " + e.getMessage());
                            }
                            return 0;
                        });
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at sort " + e.getMessage());
                    }*/

                    if (_user_list != null && _user_list.isEmpty()) {
                        hideDialog();
                        rl_instruction.setVisibility(View.VISIBLE);
                    }
                    _adapter = new FirebaseUserListAdapter(ChatUserList.this, _listener, _user_list, lst_my_user, _interface);
                    rv_all_user.setAdapter(_adapter);
                    enableSwipeToDeleteAndUndo();
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage(), e);
                }
                hideDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideDialog();
            }
        });
    }

    public boolean isFoundInDeleted(String _id) {
        for (int i = 0; i < _lst_deleted.size(); i++) {
            if (_id.equalsIgnoreCase(_lst_deleted.get(i).getUser_id()))
                return true;
        }
        return false;
    }

    public boolean isFoundInBlocked(String _id) {
        for (int i = 0; i < _lst_blocked.size(); i++) {
            if (_id.equalsIgnoreCase(_lst_blocked.get(i).getUser_id()))
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Log.e("TAG", "OnBackPress Called");
            if (_reference != null && _ref_listener != null) {
                _reference.removeEventListener(_ref_listener);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at backpress " + e.getMessage());
        }
    }


    View.OnClickListener _listener = view -> {
        if (BuildConfig.DEBUG) {
            Toast.makeText(ChatUserList.this, _constant.chat_open_user_click, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(ChatUserList.this, _constant.chat_open_user_click);
        Intent _intent = new Intent(ChatUserList.this, ChatActivity.class);
        String userData = _gson.toJson(_user_list.get((int) view.getTag()));
        _intent.putExtra("selected_user", userData);
        startActivity(_intent);
    };

    private void showProgressDialog(String msg) {
        try {
            if (mProgressDialog == null)
                mProgressDialog = new ProgressDialog(ChatUserList.this);

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.setMessage(msg);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAGG", "ProgressDialog showProgressDialog called");
    }

    public void hideDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing() && !ChatUserList.this.isDestroyed()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            realTimeDBUtils.setOffline(logged_user_id);
            _reference.removeEventListener(_ref_listener);
        } catch (Exception e) {
            Log.e("TAG", "Exception onPause " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            realTimeDBUtils.setOnline(logged_user_id);
            if (_reference != null && _ref_listener != null) {
                _reference.addValueEventListener(_ref_listener);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception onResume " + e.getMessage());
            hideDialog();
        }
    }

    HashMap<String, String> lst_my_user = new HashMap<>();
    HashMap<String, Long> lst_my_user_timestamp = new HashMap<>();

    @Override
    public void openPostList(Firebase_User _object) {
        Intent _intent = new Intent(ChatUserList.this, CommunityDetail.class);
        _intent.setAction("isFromProfile");
        _intent.putExtra("user_id", _object.getKey());
        _intent.putExtra("user_name", _object.getUser_name());
        String _data = _gson.toJson(lst_my_user);
        _intent.putExtra("_data", _data);
        startActivity(_intent);
    }


    void setPending(Firebase_User user) {

        if (user.getKey().isEmpty() || TextUtils.isEmpty(user.getKey()))
            return;

        if (!lst_my_user.containsKey(user.getKey()))
            return;

        String _node_name = lst_my_user.get(user.getKey());
        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference(_constant.firebase_chat_module).child(_node_name).child("Msg");
        _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("TAGG", "Chat Found in node " + _node_name);
                boolean isreceived_msg = false;
                boolean issended_msg = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equalsIgnoreCase(user.getKey())) {
                        isreceived_msg = true;
                    } else if (chat.getSender().equalsIgnoreCase(logged_user_id)) {
                        issended_msg = true;
                    }
                }
                Log.e("TAGG", "Both flas isreceived_msg " + isreceived_msg + " issended_msg " + issended_msg);
                if (isreceived_msg && !issended_msg && !isallreadyAdded(user.getKey()) && !isFoundInDeleted(user.getKey())) {
                    Log.e("TAGG", "UserAdded in list " + user.getKey());
                    if (isFoundInBlocked(user.getKey())) {
                        user.setBlocked(true);
                    } else {
                        user.setBlocked(false);
                        user.setPending(true);
                    }
                    if (rl_instruction.getVisibility() == View.VISIBLE) {
                        rl_instruction.setVisibility(View.GONE);
                    }
                    _user_list.add(user);
                    if (_adapter != null) {
                        _adapter.notifyItemInserted(_user_list.size() - 1);
                    }
                } else if (isreceived_msg && issended_msg && !isallreadyAdded(user.getKey()) && !isFoundInDeleted(user.getKey())) {
                    if (isFoundInBlocked(user.getKey())) {
                        user.setBlocked(true);
                    } else
                        user.setBlocked(false);
                    if (rl_instruction.getVisibility() == View.VISIBLE) {
                        rl_instruction.setVisibility(View.GONE);
                    }
                    _user_list.add(user);
                    if (_adapter != null) {
                        _adapter.notifyItemInserted(_user_list.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean isallreadyAdded(String user_id) {
        for (int i = 0; i < _user_list.size(); i++) {
            if (_user_list.get(i).getKey().equalsIgnoreCase(user_id))
                return true;
        }
        return false;

    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                deleteChat(position);

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rv_all_user);
    }

    private void deleteChat(int position) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(ChatUserList.this, _constant.chat_menu_delete_user, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(ChatUserList.this, _constant.chat_menu_delete_user);
        String _str = "Delete<b> " + _user_list.get(position).getUser_name() + "</b> ?";
        new AlertDialog.Builder(ChatUserList.this).setMessage(Html.fromHtml(_str))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ChatUserList.this, _constant.chat_menu_delete_user_sucess, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ChatUserList.this, _constant.chat_menu_delete_user_sucess);
                        HashMap<String, String> _map = new HashMap<>();
                        _map.put("user_id", _user_list.get(position).getKey());
                        MyApplication.get_realTimeDbUtils(ChatUserList.this).getDbReference().child(_constant.firebase_user_list)
                                .child(logged_user_id).child(_constant.firebase_deleted_user)
                                .push().setValue(_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(ChatUserList.this, "success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChatUserList.this, "Failed", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
