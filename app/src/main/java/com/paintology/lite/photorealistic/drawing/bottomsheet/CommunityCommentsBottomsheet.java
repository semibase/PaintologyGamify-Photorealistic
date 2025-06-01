package com.paintology.lite.photorealistic.drawing.bottomsheet;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paintology.lite.photorealistic.drawing.Adapter.CommunityCommentsAdapter;
import com.paintology.lite.photorealistic.drawing.Model.CommunityComment;
import com.paintology.lite.photorealistic.drawing.R;

import java.util.ArrayList;

public class CommunityCommentsBottomsheet extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "post_id";
    private String postId;
    private FirebaseFirestore db;
    RecyclerView rvComments;
    LinearLayout llEmptyState;
    AppCompatImageView closeAppCompatImageView;
    private CommunityCommentsAdapter communityCommentsAdapter;
    private ArrayList<CommunityComment> communityComments = new ArrayList<>();

    public static CommunityCommentsBottomsheet newInstance(String postId) {
        CommunityCommentsBottomsheet fragment = new CommunityCommentsBottomsheet();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BSDialogThemeRegular);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_comemnts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View bottomSheet = (View) view.getParent();
        bottomSheet.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);

        if (getDialog() != null) {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
            FrameLayout bottomSheetView = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetView != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheetView);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
                behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
        }


        rvComments = view.findViewById(R.id.rv_comments);
        closeAppCompatImageView = view.findViewById(R.id.closeAppCompatImageView);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false));

        fetchCommunityComments(postId);

        closeAppCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // Do nothing
        }
    };

    private void fetchCommunityComments(String postId) {
        db.collection("community_posts")
                .document(postId)
                .collection("comments")
                .orderBy("created_at", Query.Direction.DESCENDING) // Order by created_at in descending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();



                        for (QueryDocumentSnapshot document : querySnapshot) {
                            CommunityComment communityComment = document.toObject(CommunityComment.class);
                            communityComments.add(communityComment);
                        }
                        if (communityComments.isEmpty()) {
                            llEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            communityCommentsAdapter = new CommunityCommentsAdapter(communityComments,getActivity());
                            rvComments.setAdapter(communityCommentsAdapter);
                            llEmptyState.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("WelcomeBottomSheet", "Error getting documents.", task.getException());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }
}
