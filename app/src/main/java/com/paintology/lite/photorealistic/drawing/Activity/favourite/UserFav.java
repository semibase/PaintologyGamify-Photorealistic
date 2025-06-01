package com.paintology.lite.photorealistic.drawing.Activity.favourite;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.R;

public class UserFav extends Fragment {

    private DrawingRepository repository;
    private UserFavAdapter adapter;
    private RecyclerView rvBlogPosts;

    public UserFav() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery_fav, container, false);


        repository = new DrawingRepository(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.rvBlogPosts);

        DrawingRepository repository1 = new DrawingRepository(requireContext());




        return view;

    }

}




