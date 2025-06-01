package com.paintology.lite.photorealistic.drawing.Chat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Token;
import com.paintology.lite.photorealistic.drawing.bus.UserLoginUpdateEvent;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RealTimeDBUtils {

    public Context context;
    public FirebaseAuth mFirebaseAuth;
    public DatabaseReference dbReferenceUserList;
    public DatabaseReference dbReference;
    StringConstants _constant = new StringConstants();
    public FirebaseUser _current_user;

    String email = "";
    String username = "";

    public RealTimeDBUtils(Context context) {
        this.context = context;
    }


    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    public DatabaseReference getDbReferenceUserList() {
        if (dbReferenceUserList == null) {
            dbReferenceUserList = FirebaseDatabase.getInstance().getReference(_constant.firebase_user_list);
        }
        return dbReferenceUserList;
    }


    public FirebaseUser getCurrentUser() {
        if (_current_user == null)
            _current_user = getFirebaseAuth().getCurrentUser();

        return _current_user;
    }

    public void setCurrentUser() {
        _current_user = getFirebaseAuth().getCurrentUser();
    }

    public DatabaseReference getDbReference() {
        if (dbReference == null) {
            dbReference = FirebaseDatabase.getInstance().getReference();
        }
        return dbReference;
    }

    public void setOnline(String logged_user_id) {
        if (logged_user_id != null && !logged_user_id.isEmpty() && getCurrentUser() != null) {
            Log.e("TAGG", "setOnline called");
            getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_online").setValue("true")
                    .addOnSuccessListener(unused -> Log.e("onlineStatus", "Success"))
                    .addOnFailureListener(e -> Log.e("onlineStatus", e.getMessage()));
        }
    }

    public void setOffline(String logged_user_id) {
        Log.e("TAGG", "setOffline called " + (getCurrentUser() != null) + " (getFirebaseAuth() != null) " + (getFirebaseAuth() != null));
        if (logged_user_id != null && !logged_user_id.isEmpty() && getCurrentUser() != null) {
            getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_online").setValue("false");
        }
    }

    public void setOnlineForChat(String logged_user_id) {
        if (logged_user_id != null && !logged_user_id.isEmpty() && getCurrentUser() != null) {
            Log.e("TAGG", "setOnline called");
            getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_online_chat").setValue("true");
        }
    }

    public void setOfflineForChat(String logged_user_id) {
        Log.e("TAGG", "setOffline called " + (getCurrentUser() != null) + " (getFirebaseAuth() != null) " + (getFirebaseAuth() != null));
        if (logged_user_id != null && !logged_user_id.isEmpty() && getCurrentUser() != null) {
            getDbReference().child(_constant.firebase_user_list).child(logged_user_id).child("is_online_chat").setValue("false");
        }
    }

    ValueEventListener _listener;

    public void autoLoginRegister(String status) {
        try {
            email = _constant.getString(_constant.Email, context);
            username = _constant.getString(_constant.Username, context);

            Log.e("TAGG", "autoLoginRegister email " + email + " username " + username);
            _listener = getDbReferenceUserList().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Firebase_User> _user_list = new ArrayList<Firebase_User>();
                    int i = 0;
                    boolean isExist = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Firebase_User _user = postSnapshot.getValue(Firebase_User.class);
                        _user_list.add(_user);
                        if (_user.getUser_email().equalsIgnoreCase(email)) {
                            isExist = true;
                            break;
                        }
                        i++;
                    }

                    getDbReferenceUserList().removeEventListener(_listener);

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        boolean isAnonymous = currentUser.isAnonymous();
                        if (isAnonymous) {
                            AuthCredential credential = EmailAuthProvider.getCredential(email, email);
                            currentUser.linkWithCredential(credential)
                                    .addOnCompleteListener(task -> {
                                       // giveSignupBonusRewardPoint();
                                        saveUserDataIntoDb(task);
                                    });
                        }
                    } else {
                        if (!isExist && status.equalsIgnoreCase("User already exists.")) {
                            createFirebaseAccount();
                        } else if (isExist) {
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            getFirebaseAuth().signInWithEmailAndPassword(email, email).addOnCompleteListener(task -> {
                              //  giveSignupBonusRewardPoint();
                                saveUserDataIntoDb(task);
                            });
                        } else {
                            if (TextUtils.isEmpty(email))
                                return;

                            createFirebaseAccount();
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            if (getCurrentUser() != null) {
                Log.e("TAGGG", "setOnline called 1");
                setOnline(_constant.getString(_constant.UserId, context));
            }

        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage());
        }
    }

    private void createFirebaseAccount() {
        getFirebaseAuth().createUserWithEmailAndPassword(email, email).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              //  giveSignupBonusRewardPoint();
                saveUserDataIntoDb(task);
            }
        });
    }

    public void saveUserDataIntoFirebaseDb(Task<AuthResult> task) {
        try {
            if (task.isSuccessful()) {
                FirebaseUser _user = task.getResult().getUser();

                String _id = _user.getUid();

                if (_constant.getString(_constant.UserId, context).isEmpty()) {
                    Log.e("TAGG", "Authenticate empty");
                    return;
                } else
                    Log.e("TAGG", "Authenticate goto else");

                Log.e("TAGGG", "Authenticate setOnline called 2 ");
                updateToken(FirebaseInstanceId.getInstance().getToken());
                HashMap<String, String> _map = new HashMap<>();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    _map.put("create_date", currentDateandTime);
                } catch (Exception e) {
                    Log.e("TAG", "saveUserDataIntoDb: " + e.getMessage());
                }
                _map.put("login_type", getLoginType());
                _map.put("user_id", _constant.getString(_constant.UserId, context));
                _map.put("key", _id);
                _map.put("is_online", "true");
                _map.put("is_typing", "false");
                FirebaseDatabase.getInstance().getReference(_constant.firebase_user_list).child(_constant.getString(_constant.UserId, context)).setValue(_map).addOnCompleteListener(task1 -> {
                });
             //   AppUtils.setLoggedIn(true);
            } else {
                Log.e("TAGG", "Exception in else " + task.getException());
                Toast.makeText(context, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at autoLogin " + e.getMessage(), e);
        }


    }

    private void saveUserDataIntoDb(Task<AuthResult> task) {
        try {
            if (task.isSuccessful()) {
                FirebaseUser _user = task.getResult().getUser();

                String _id = _user.getUid();

                if (_constant.getString(_constant.UserId, context).isEmpty()) {
                    Log.e("TAGG", "Authenticate empty");
                    return;
                } else
                    Log.e("TAGG", "Authenticate goto else");

                HashMap<String, String> _map = new HashMap<>();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    _map.put("create_date", currentDateandTime);
                } catch (Exception e) {
                    Log.e("TAG", "saveUserDataIntoDb: " + e.getMessage());
                }

                Log.e("TAGGG", "Authenticate setOnline called 2 ");
                updateToken(FirebaseInstanceId.getInstance().getToken());
                _map.put("login_type", getLoginType());
                _map.put("user_id", _constant.getString(_constant.UserId, context));
                _map.put("key", _id);
                _map.put("user_email", email);
                _map.put("user_name", username);
                _map.put("is_online", "true");
                _map.put("is_typing", "false");
                _map.put("profile_pic", _constant.getString(_constant.ProfilePicsUrl, context));
                FirebaseDatabase.getInstance().getReference(_constant.firebase_user_list).child(_constant.getString(_constant.UserId, context)).setValue(_map).addOnCompleteListener(task1 -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, email).addOnCompleteListener(task11 -> {
                        if (task11.isSuccessful()) {
                            //Toast.makeText(ChatUserList.this, "Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed " + task11.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
             //   AppUtils.setLoggedIn(true);
                EventBus.getDefault().post(new UserLoginUpdateEvent());
            } else {
                Log.e("TAGG", "Exception in else " + task.getException());
                Toast.makeText(context, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at autoLogin " + e.getMessage(), e);
        }
    }

    public String getLoginType() {
        String logged_user_id = _constant.getString(_constant.UserId, context);
        String _login_type = "";
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (isLoggedIn) {
            _login_type = "Facebook";
        } else if (account != null) {
            _login_type = "Google";
        } else if (_constant.getBoolean(_constant.IsGuestUser, context)) {
            _login_type = "Guest";
        } else {
            _login_type = "Paintology";
        }
        return _login_type;
    }

    private void updateToken(String token) {
        try {
            Log.e("TAG", "Authenticate Update Token Called");
            if (getCurrentUser() != null) {
                DatabaseReference reference = getDbReference().child("Tokens");
                Token token1 = new Token(token);
                reference.child(getCurrentUser().getUid()).setValue(token1);
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at update token " + e.getMessage());
        }
    }

}
