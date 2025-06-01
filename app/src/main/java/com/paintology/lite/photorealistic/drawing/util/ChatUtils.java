package com.paintology.lite.photorealistic.drawing.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Any;
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity;
import com.paintology.lite.photorealistic.drawing.Chat.RealTimeDBUtils;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.data.db.FirebaseFirestoreApi;

import java.util.HashMap;
import java.util.Objects;

public class ChatUtils {

    HashMap<String, String> lst_my_user = new HashMap<>();

    Context context;
    String user_Id = "", user_name = "";
    String logged_user_id = "";
    RealTimeDBUtils realTimeDBUtils;

    StringConstants constants = new StringConstants();

    boolean is_found_in_delete = false;
    boolean is_found_in_block = false;

    public ChatUtils(Context context) {
        this.context = context;
        logged_user_id = constants.getString(constants.UserId, context);
        realTimeDBUtils = MyApplication.get_realTimeDbUtils(context);
    }

    public void openChatScreen(String user_Id, String user_name) {
        this.user_Id = user_Id;
        this.user_name = user_name;
        FireUtils.showProgressDialog(context, context.getResources().getString(R.string.please_wait));
        FirebaseFirestoreApi.getChatNode(user_Id)
                .addOnSuccessListener(httpsCallableResult -> {
                    HashMap<String, Any> anyHashMap = (HashMap<String, Any>) httpsCallableResult.getData();
                    if (anyHashMap != null && anyHashMap.containsKey("room_id") && anyHashMap.get("room_id") != null) {
                        checkBlockStatus();
                    }
                }).addOnFailureListener(e -> {

                    FireUtils.hideProgressDialog();

                    if (Objects.requireNonNull(e.getMessage()).startsWith("Unable to initiate the chat. You must follow")){
                        showMsgDialog("You must follow " + user_name + " and be followed back to start chatting. Make sure both of you follow each other to unlock chat.");

                    }else {
                        showMsgDialog(e.getMessage());

                    }
                });

    }

    boolean isFoundInMyUser(String u_id) {
        return lst_my_user.containsKey(u_id);
    }

    private void checkBlockStatus() {
        if (realTimeDBUtils.getCurrentUser() != null) {
            Query applesQuery = realTimeDBUtils.getDbReferenceUserList().child(user_Id).child(constants.firebase_blocked_user).orderByChild("user_id").equalTo(logged_user_id);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                        is_found_in_block = true;
                        Toast.makeText(context, user_name + " is not available for chat !", Toast.LENGTH_SHORT).show();
                        FireUtils.hideProgressDialog();
                        return;
                    }
                    checkDeleteStatus();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    checkDeleteStatus();
                }
            });
        } else {
            FireUtils.hideProgressDialog();
        }
    }

    private void checkDeleteStatus() {
        Query applesQuery = realTimeDBUtils.getDbReferenceUserList().child(user_Id).child(constants.firebase_deleted_user).orderByChild("user_id").equalTo(logged_user_id);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                    is_found_in_delete = true;
                    Toast.makeText(context, user_name + " is not available for chat !", Toast.LENGTH_SHORT).show();
                    FireUtils.hideProgressDialog();
                    return;
                }
                openChatScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                openChatScreen();
            }
        });
    }

    void openChatScreen() {
        FireUtils.hideProgressDialog();
        if (!is_found_in_delete && !is_found_in_block) {
            Intent _intent = new Intent(context, ChatActivity.class);
            _intent.putExtra("userUid", user_Id);
            context.startActivity(_intent);
        } else {
            showMsgDialog(null);
        }
    }

    void showMsgDialog(String msg) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_chat_unavailable);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView message = dialog.findViewById(R.id.tvMessage);
        if (msg != null){
            message.setText(msg);

        }else {
            context.getResources().getString(R.string.chat_click_hint);
        }
        dialog.findViewById(R.id.btnClosee).setOnClickListener(v -> dialog.dismiss());
        dialog.show();



//        new AlertDialog.Builder(context).setMessage(msg != null ? msg : context.getResources().getString(R.string.chat_click_hint)).setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
    }


    public static void showCustomDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(msg != null ? msg : context.getResources().getString(R.string.unknown_error))
                .setPositiveButton("Ok", listener).show();
    }
}
