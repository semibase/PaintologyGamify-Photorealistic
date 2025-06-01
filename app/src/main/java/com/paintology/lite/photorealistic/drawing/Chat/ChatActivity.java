package com.paintology.lite.photorealistic.drawing.Chat;

import static com.paintology.lite.photorealistic.drawing.util.MyApplication._realTimeDbUtils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.protobuf.Any;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Data;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.MyResponse;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Sender;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.CircleProgress.CircleProgressBar;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.FireUtils;
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils;
import com.paintology.lite.photorealistic.drawing.util.MyApplication;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements MainInterface {

    Gson _gson = new Gson();


    ImageView iv_send, iv_close;
    EditText edt_msg;
    RecyclerView rv_chat_msg;

    FirebaseUser myUser;
    Firebase_User _user_data_receiver;

    MessageAdapter _adapter;
    ArrayList<Chat> _chat_list;

    DatabaseReference _reference;
    String logged_user_id;
    StringConstants _constant = new StringConstants();

    TextView tv_user_status;
    Boolean receiverOnline = false;
    public int MSG_TYPE_LEFT = 0;
    public int MSG_TYPE_RIGHT = 1;
    public int MSG_TYPE_DATE = 2;
    public int MSG_TYPE_TYPING = 3;
    ArrayList<Chat> _list;
    RealTimeDBUtils _realtimeDBUtils;
    boolean isAlreadyTyping = false;
    LinearLayoutManager manager;

    ApiInterface apiService;

    boolean notify = false;

    public static MainInterface _objInterface;
    String uIdNotificaiton = "";
    String userPaintologyId = "";

    CircleProgressBar _pbar;
    String message = "";


    ValueEventListener _seenListerner;


    boolean isRemovedFromDelete = false;
    private ImageView btn_more;
    String chatNode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

       /* SharedPreferences sf = getSharedPreferences("shareprefrence", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        try {
            editor.putString("target_type", "");
            editor.apply();
        } catch (Exception e) {

        }*/

        _pbar = (CircleProgressBar) findViewById(R.id.item_progress_bar);
        logged_user_id = _constant.getString(_constant.UserId, ChatActivity.this);
        _realtimeDBUtils = MyApplication.get_realTimeDbUtils(this);
        myUser = _realtimeDBUtils.getCurrentUser();

        apiService = ApiClient.getClientNotification().create(ApiInterface.class);

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, _constant.chat_conversation_window, Toast.LENGTH_SHORT).show();
        }
        FirebaseUtils.logEvents(ChatActivity.this, _constant.chat_conversation_window);

        tv_user_status = (TextView) findViewById(R.id.tv_user_status);
        tv_user_status.setText(R.string.ss_online);
        tv_user_status.setVisibility(View.GONE);
        tv_user_status.setTextColor(getResources().getColor(R.color.white));
        receiverOnline = true;
        edt_msg = (EditText) findViewById(R.id.edt_msg);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        rv_chat_msg = (RecyclerView) findViewById(R.id.rv_chat_msg);
        btn_more = findViewById(R.id.btn_more);

        manager = new LinearLayoutManager(ChatActivity.this);
        manager.setStackFromEnd(true);
        rv_chat_msg.setLayoutManager(manager);
        _pbar.setVisibility(View.VISIBLE);

        if (getIntent() != null && getIntent().hasExtra("room_id")) {
            chatNode = getIntent().getStringExtra("room_id");
            try {
                String[] strings = chatNode.split("_");
                if (strings[0].equalsIgnoreCase(logged_user_id)) {
                    uIdNotificaiton = strings[1];
                } else {
                    uIdNotificaiton = strings[0];
                }
                getUserInfo();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } else if (getIntent() != null && getIntent().hasExtra("selected_user")) {
            Log.e("TAGG", "GO TO IF");
            _user_data_receiver = _gson.fromJson(getIntent().getStringExtra("selected_user"), Firebase_User.class);
            TextView tv_name = (TextView) findViewById(R.id.tv_user_name);
            ImageView profile_icon = (ImageView) findViewById(R.id.profile_icon);
            tv_name.setText(_user_data_receiver.getUser_name());
            if (!_user_data_receiver.getUser_profile_pic().isEmpty()) {
                try {
                    String url = _user_data_receiver.getUser_profile_pic();
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(profile_icon);
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }

            getNewChatNode(_user_data_receiver.getKey(), node -> {
                chatNode = node;
                Log.e("TAG", chatNode);
                checkBlockStatus();
                getReceiveStatus();
                getAllMsg(logged_user_id, _user_data_receiver.getKey());
                setAllMsgSeen();
            });

        } else if (getIntent() != null && getIntent().hasExtra("userUid")) {
            Log.e("TAGG", "GO TO ELSE IF");
            uIdNotificaiton = getIntent().getStringExtra("userUid");
            Log.w("getAllMsg", "uIdNotificaiton: " + uIdNotificaiton);
            getUserInfo();
        }

        iv_close.setOnClickListener(view -> finish());
        btn_more.setOnClickListener(this::openMorePopupMenu);

        edt_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                try {
                    if (s.toString().trim().length() == 0) {
                        isAlreadyTyping = false;
                        _realtimeDBUtils.getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_typing").setValue("false");
                    } else {
                        if (!isAlreadyTyping) {
                            isAlreadyTyping = true;
                            _realtimeDBUtils.getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_typing").setValue("true");
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isAlreadyTyping = false;
                }
            }
        });

        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notify = true;
                if (edt_msg.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Can't send empty message!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isRemovedFromDelete)
                        removeUserFromDelete();
                    message = edt_msg.getText().toString().trim();
                    isAlreadyTyping = false;

                    Bundle bundle = new Bundle();
                    bundle.putString("room_id", getChatNode());
                    bundle.putString("sender_id", myUser.getUid());
                    bundle.putString("recipient_id", _user_data_receiver.getKey());
                    ContextKt.sendUserEventWithParam(ChatActivity.this, StringConstants.chat_send, bundle);
                    sendMessage(myUser.getUid(), _user_data_receiver.getKey(), edt_msg.getText().toString().trim());
                    edt_msg.setText("");
                }
            }
        });
//        updateToken(FirebaseInstanceId.getInstance().getToken());
    }


    public void checkBlockStatus() {
        Query applesQuery = _realtimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(_constant.firebase_blocked_user).orderByChild("user_id").equalTo(_user_data_receiver.getKey());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    _user_data_receiver.setBlocked(true);
                } else {
                    _user_data_receiver.setBlocked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                _user_data_receiver.setBlocked(false);
                Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
            }
        });
       /* FirebaseFirestoreApi.getUserFromBlocked(logged_user_id, _user_data_receiver.user_id).
                addOnSuccessListener(documentSnapshot -> {
                    _user_data_receiver.isBlocked = documentSnapshot.getData() != null;
                });*/
    }

    public void blockUnblockUser(boolean isBlock) {
        if (isBlock) {

            FireUtils.showProgressDialog(ChatActivity.this, getString(R.string.ss_blocking)+" " + _user_data_receiver.getUser_name());
            if (BuildConfig.DEBUG) {
                Toast.makeText(ChatActivity.this, _constant.chat_menu_unblock_user_success, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(ChatActivity.this, _constant.chat_menu_unblock_user_success);
            HashMap<String, String> _map = new HashMap<>();
            _map.put("user_id", _user_data_receiver.getKey());
            _realtimeDBUtils.getDbReferenceUserList().child(myUser.getUid()).child(_constant.firebase_blocked_user).push().setValue(_map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FireUtils.hideProgressDialog();
                            if (task.isSuccessful()) {
                                Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                                _user_data_receiver.setBlocked(true);
                            } else {
                                Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

          /*  FirebaseFirestoreApi.blockUser(_user_data_receiver.getKey())
                    .addOnCompleteListener(task -> {
                        FireUtils.hideProgressDialog();
                        if (task.isSuccessful()) {
                            _user_data_receiver.setBlocked(true);
                            Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });*/
        } else {

            FireUtils.showProgressDialog(ChatActivity.this, getString(R.string.ss_unblocking)+" " + _user_data_receiver.getUser_name());
            if (BuildConfig.DEBUG) {
                Toast.makeText(ChatActivity.this, _constant.chat_menu_unblock_user_success, Toast.LENGTH_SHORT).show();
            }
            FirebaseUtils.logEvents(ChatActivity.this, _constant.chat_menu_unblock_user_success);
            Query applesQuery = _realtimeDBUtils.getDbReferenceUserList().child(logged_user_id).child(_constant.firebase_blocked_user).orderByChild("user_id").equalTo(_user_data_receiver.getKey());
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FireUtils.hideProgressDialog();
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                    _user_data_receiver.setBlocked(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FireUtils.hideProgressDialog();
                    Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
                }
            });

           /* FirebaseFirestoreApi.unBlockUser(_user_data_receiver.getKey())
                    .addOnCompleteListener(task -> {
                        FireUtils.hideProgressDialog();
                        if (task.isSuccessful()) {
                            _user_data_receiver.setBlocked(false);
                            Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });*/
        }
    }

    private void openMorePopupMenu(View view) {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_chat_more_menu, popupMenu.getMenu());
        if (_user_data_receiver.isBlocked) {
            popupMenu.getMenu().getItem(0).setTitle("Unblock");
        } else {
            popupMenu.getMenu().getItem(0).setTitle("Block");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_block:
                        String _str = "";

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(ChatActivity.this, _constant.chat_menu_block_user, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(ChatActivity.this, _constant.chat_menu_block_user);

                        if (_user_data_receiver.isBlocked()) {
                            _str = "Unblock <b> " + _user_data_receiver.getUser_name() + "</b> ?";
                            try {
                                FireUtils.showCustomDialog(ChatActivity.this, _str, () -> blockUnblockUser(false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            _str = "Block <b> " + _user_data_receiver.getUser_name() + "</b> ?";
                            try {
                                FireUtils.showCustomDialog(ChatActivity.this, _str, () -> blockUnblockUser(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.action_view_profile:
                        try {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(ChatActivity.this, _constant.chat_menu_see_user_profile, Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUtils.logEvents(ChatActivity.this, _constant.chat_menu_see_user_profile);
                            if (_user_data_receiver.getKey() != null) {
                                FireUtils.openProfileScreen(ChatActivity.this, _user_data_receiver.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("FirebaseUserListAdapter", e.getMessage());
                        }
                        break;
                    case R.id.action_delete_chat:

                        break;

                }
                return false;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }


    public void removeUserFromDelete() {
        try {
            isRemovedFromDelete = true;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query applesQuery = reference.child(_constant.firebase_user_list).child(logged_user_id).child(_constant.firebase_deleted_user).orderByChild("user_id").equalTo(_user_data_receiver.getKey());
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        Log.e("TAGG", "applesQuery onRemove");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
                }
            });
            Query query_2 = reference.child(_constant.firebase_user_list).child(logged_user_id).child(_constant.firebase_blocked_user).orderByChild("user_id").equalTo(_user_data_receiver.getKey());
            query_2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        Log.e("TAGG", "applesQuery onRemove");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAGG", "applesQuery onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
        }
    }


    void setAllMsgSeen() {
        _reference = FirebaseDatabase.getInstance().getReference(_constant.firebase_chat_module).child(getChatNode()).child("Msg");
        _seenListerner = _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equalsIgnoreCase(_user_data_receiver.getKey()) && chat.getIsMsgseen().equalsIgnoreCase("false")) {
                        HashMap<String, Object> _map = new HashMap<>();
                        _map.put("isMsgseen", "true");
                        snapshot.getRef().updateChildren(_map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*private void updateToken(String token) {
        DatabaseReference reference = _realtimeDBUtils.getDbReference().child("Tokens");
        Token token1 = new Token(token);
        reference.child(_realtimeDBUtils.getCurrentUser().getUid()).setValue(token1);
    }
*/


    public interface onResultListener {
        public void onSuccess(String chatNode);
    }

    private void getNewChatNode(String uid, onResultListener listener) {
        if (chatNode != null && !chatNode.equalsIgnoreCase("")) {
            listener.onSuccess(chatNode);
        }else {
            FirebaseFirestoreApi.getChatNode(uid)
                    .addOnSuccessListener(httpsCallableResult -> {
                        HashMap<String, Any> anyHashMap = (HashMap<String, Any>) httpsCallableResult.getData();
                        if (anyHashMap != null && anyHashMap.containsKey("room_id") && anyHashMap.get("room_id") != null) {
                            listener.onSuccess(String.valueOf(anyHashMap.get("room_id")));
                        }
                    });
        }
    }

    /**
     * Function setsup endpoint for one to one chat
     **/
    private String getChatNode() {
        return chatNode;
    }

    void sendMessage(String sender, String receiver, String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        String _date_time = sdf.format(new Date());
        Log.e("TAGG", "Date And Time " + _date_time);

        HashMap<String, String> _map = new HashMap<>();
        _map.put("sender", sender);
        _map.put("receiver", receiver);
        _map.put("message", msg);
        _map.put("date", _date_time);
        _map.put("isMsgseen", "false");
        _realtimeDBUtils.getDbReference().child(_constant.firebase_chat_module).child(getChatNode()).child("Msg").push().setValue(_map);

        Log.e("TAGGG", "Notification Msg Before " + message);
        _realTimeDbUtils.getDbReferenceUserList().child(logged_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAlreadyTyping) {
                    Firebase_User user = dataSnapshot.getValue(Firebase_User.class);
                    Log.e("TAGGG", "Notification Msg After " + message + " notify " + notify);
                    if (notify) {
                        if (!receiverOnline) {
                           // sendNotification(sender, receiver, user.getUser_name(), message);
                            message = "";
                        }
                    }
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String senderUid, String receiverUid, String user_name, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverUid);

        Log.e("TAG", "sendNotification called msg " + msg);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(senderUid, R.mipmap.ic_launcher, msg, user_name, receiverUid, "false", "0");

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Log.e("TAGG", "onResponse called " + response.toString());
                                    if (BuildConfig.DEBUG) {
                                        if (response.code() == 200) {
                                            if (response.body().success != 1) {
                                                Toast.makeText(ChatActivity.this, "Notification failed", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(ChatActivity.this, "Notification success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "Notification failed code " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("TAG", "Notification onFailure " + t.getMessage(), t);
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ValueEventListener getMsgValueListener;

    void getAllMsg(String myUid, String userUid) {
        if (_user_data_receiver == null) {
            if (_pbar.getVisibility() == View.VISIBLE) {
                _pbar.setVisibility(View.GONE);
            }
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.w("getAllMsg", "logged_user_id: " + logged_user_id);
            Log.w("getAllMsg", "_user_data_receiver: " + _user_data_receiver.getKey());
            Log.w("getAllMsg", "myUid: " + myUid);
            Log.w("getAllMsg", "userUid: " + userUid);

            Toast.makeText(this, "logged_user_id: " + logged_user_id + " _user_data_receiver: " + _user_data_receiver.getKey(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "myUid: " + myUid + " \nuserUid: " + userUid, Toast.LENGTH_SHORT).show();
        }

        _chat_list = new ArrayList<>();
        _reference = FirebaseDatabase.getInstance().getReference(_constant.firebase_chat_module).child(getChatNode()).child("Msg");
        getMsgValueListener = _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _chat_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equalsIgnoreCase(myUid) && chat.getSender().equalsIgnoreCase(userUid) || chat.getSender().equalsIgnoreCase(myUid) && chat.getReceiver().equalsIgnoreCase(userUid)) {
                        _chat_list.add(setType(chat));
                    }
                }

                boolean isTypingFound = false;
                if (_list != null && _list.size() > 0) {
                    for (int i = 0; i < _list.size(); i++) {
                        if (_list.get(i).getMsg_type() == MSG_TYPE_TYPING) {
                            isTypingFound = true;
                            break;
                        }
                    }
                }
                _list = new ArrayList<>();
                try {
                    Log.e("TAGG", "List size Before " + _chat_list.size());
                    String lastDate = "";
                    for (int i = 0; i < _chat_list.size(); i++) {
                        if (!getDate(_chat_list.get(i).getDate()).equalsIgnoreCase(lastDate)) {
                            Chat obj_ = new Chat();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String _date_time = sdf.format(new Date());
                            if (_date_time.trim().equalsIgnoreCase(getDate(_chat_list.get(i).getDate()))) {
                                obj_.setDate("Today");
                            } else
                                obj_.setDate(getDate(_chat_list.get(i).getDate()));
                            obj_.setMsg_type(MSG_TYPE_DATE);
                            _list.add(obj_);
                            lastDate = getDate(_chat_list.get(i).getDate());
                            _chat_list.get(i).setTime(getTime(_chat_list.get(i).getDate()));
                            _list.add(_chat_list.get(i));
                        } else {
                            _chat_list.get(i).setTime(getTime(_chat_list.get(i).getDate()));
                            _list.add(_chat_list.get(i));
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception " + e.getMessage());
                }
                if (isTypingFound) {
                    isTypingFound = false;
                    Chat _chat = new Chat();
                    _chat.setMsg_type(MSG_TYPE_TYPING);
                    _list.add(_chat);
                }

                if (_pbar.getVisibility() == View.VISIBLE) {
                    _pbar.setVisibility(View.GONE);
                }
                Log.e("TAGG", "List size After " + _list.size());
                _adapter = new MessageAdapter(ChatActivity.this, _list, _user_data_receiver.getUser_profile_pic());
                rv_chat_msg.setAdapter(_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error " + databaseError.getMessage());
                if (_pbar.getVisibility() == View.VISIBLE) {
                    _pbar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            _reference.removeEventListener(getMsgValueListener);
        } catch (Exception e) {

        }
    }

    public String getDate(String chatdate) {
        String dateOnly = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            Date d = sdf.parse(chatdate);
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat time = new SimpleDateFormat("hh:mm a");
            dateOnly = date.format(d);
            System.out.println("Date: " + date.format(d));
            System.out.println("Time: " + time.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateOnly;
    }

    public String getTime(String chatdate) {
        String timeOnly = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            Date d = sdf.parse(chatdate);
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat time = new SimpleDateFormat("hh:mm a");
            timeOnly = time.format(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOnly;
    }


    Chat setType(Chat chat) {
        if (myUser.getUid().equalsIgnoreCase(chat.getSender()))
            chat.setMsg_type(MSG_TYPE_RIGHT);
        else
            chat.setMsg_type(MSG_TYPE_LEFT);
        return chat;
    }

//    void setupToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow_white);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//    }

    private void getReceiveStatus() {
        DatabaseReference _reference = FirebaseDatabase.getInstance().getReference(_constant.firebase_user_list).child(_user_data_receiver.getKey());
        _reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Firebase_User _user = dataSnapshot.getValue(Firebase_User.class);
                    if (_user.getIs_online().equalsIgnoreCase("true"))
                        tv_user_status.setVisibility(View.VISIBLE);
                    else
                        tv_user_status.setVisibility(View.GONE);

                    if (_user.getIs_online_chat().equalsIgnoreCase("true")) {
                        receiverOnline = true;
                    } else {
                        receiverOnline = false;
                    }

                    if (_list == null) {
                        _list = new ArrayList<>();
                    }
                    Log.e("TAGG", "_user.getIs_typing() " + _user.getIs_typing());
                    if (_user.getIs_typing().equalsIgnoreCase(logged_user_id)) {

                        boolean isTypingFound = false;
                        for (int i = 0; i < _list.size(); i++) {
                            if (_list.get(i).getMsg_type() == MSG_TYPE_TYPING) {
                                isTypingFound = true;
                                break;
                            }
                        }
                        if (!isTypingFound) {
                            Chat _chat = new Chat();
                            _chat.setMsg_type(MSG_TYPE_TYPING);
                            _list.add(_chat);
                            _adapter.notifyItemInserted(_list.size());
                            _adapter.notifyDataSetChanged();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    manager.scrollToPosition(_list.size() - 1);
                                }
                            });
                        }
                    } else {
                        for (int i = 0; i < _list.size(); i++) {
                            if (_list.get(i).getMsg_type() == MSG_TYPE_TYPING) {
                                _list.remove(i);
                                _adapter.notifyItemRemoved(i);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            _objInterface = null;
            if (isAlreadyTyping) {
                _realtimeDBUtils.getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_typing").setValue("false");
            }
            _realtimeDBUtils.setOffline(logged_user_id);
            _realtimeDBUtils.setOfflineForChat(logged_user_id);
        } catch (Exception e) {

        }
        try {
            _reference.removeEventListener(_seenListerner);
        } catch (Exception e) {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            _objInterface = this;
            _realtimeDBUtils.setOnline(logged_user_id);
            _realtimeDBUtils.setOnlineForChat(logged_user_id);
            if (isAlreadyTyping) {
                _realtimeDBUtils.getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_typing").setValue("true");
            }

        } catch (Exception e) {

        }
    }

    @Override
    public boolean isScreenVisible() {
        return true;
    }

    private void getUserInfo() {
        Log.e("TAG", uIdNotificaiton);
        _realtimeDBUtils.getDbReferenceUserList().child(uIdNotificaiton).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (uIdNotificaiton != null && !uIdNotificaiton.isEmpty()) {
                        Firebase_User _user = dataSnapshot.getValue(Firebase_User.class);
                        _user.setKey(dataSnapshot.getKey());
                        _user.setUser_id(dataSnapshot.getKey());
                        if (_user.getKey().equalsIgnoreCase(uIdNotificaiton)) {
                            _user_data_receiver = _user;
                            Log.e("TAGG", "GO TO ELSE IF Loop " + _user_data_receiver.getKey());
                            TextView tv_name = (TextView) findViewById(R.id.tv_user_name);
                            ImageView profile_icon = (ImageView) findViewById(R.id.profile_icon);
                            tv_name.setText(_user_data_receiver.getUser_name());
                            if (!_user_data_receiver.getUser_profile_pic().isEmpty()) {
                                try {
                                    String url = _user_data_receiver.getUser_profile_pic();
                                    Glide.with(ChatActivity.this)
                                            .load(url)
                                            .apply(new RequestOptions().placeholder(R.drawable.profile_icon).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                            .into(profile_icon);
                                } catch (Exception e) {
                                    Log.e("TAGG", "Exception " + e.getMessage());
                                }
                            }

                            getNewChatNode(_user.getKey(), node -> {
                                Log.e("TAG", "success");
                                chatNode = node;
                                checkBlockStatus();
                                getReceiveStatus();
                                getAllMsg(myUser.getUid(), _user_data_receiver.getKey());
                                setAllMsgSeen();
                                uIdNotificaiton = "";
                            });

                        }
                    }
                    if (_pbar.getVisibility() == View.VISIBLE) {
                        _pbar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception " + e.getMessage());
                    if (_pbar.getVisibility() == View.VISIBLE) {
                        _pbar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error " + databaseError.getMessage());
                if (_pbar.getVisibility() == View.VISIBLE) {
                    _pbar.setVisibility(View.GONE);
                }
            }
        });
    }

}
