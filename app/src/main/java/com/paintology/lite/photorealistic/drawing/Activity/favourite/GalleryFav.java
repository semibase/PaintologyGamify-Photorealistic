package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paintology.lite.photorealistic.drawing.Adapter.SubCategoryAdapterAll;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.DashboardScreen.NewSubCategoryActivity;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.TutorialModel.Tutorialdatum;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.interfaces.SubCategoryItemClickListener;
import com.paintology.lite.photorealistic.drawing.ui.login.LoginActivity;
import com.paintology.lite.photorealistic.drawing.util.AppUtils;
import com.paintology.lite.photorealistic.drawing.util.ContextKt;
import com.paintology.lite.photorealistic.drawing.util.StringConstants;
import com.paintology.lite.photorealistic.drawing.util.events.RefreshFavoriteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class GalleryFav extends Fragment implements SubCategoryItemClickListener {


    String FavScreenType = "";

    private SubCategoryAdapterAll mSubCategoryAdapterAll;

    SubCategoryItemClickListener _obj_interface;


    StringConstants constants = new StringConstants();

    public GalleryFav() {
    }

    public GalleryFav(String favScreenType) {
        FavScreenType = favScreenType;
    }

    private DrawingRepository repository;
    private DrawingAdapter adapter;
    private UserFavAdapter UserAdapter;

    private RecyclerView rvBlogPosts;
    private TextView HeadingTxt;
    private TextView DescriptionTxt;
    private ConstraintLayout layoutNoData;
    private ImageView EmptyDataImg;

    ArrayList<Tutorialdatum> mTutorialdata = new ArrayList<>();
    RecyclerView recyclerView;
    TutorialDbHelper helper;

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFavoriteEvent event) {
        if (event.getPage() == 0 && FavScreenType.equals("user")) {
            checkUsers();
        } else if (event.getPage() == 1 && FavScreenType.equals("gallery")) {
            checkGallery();
        } else if (event.getPage() == 2 && FavScreenType.equals("tutorial")) {
            checkTutorials();
        }
    }

    public void checkGallery() {
        if (!repository.getAllDrawingsList().isEmpty()) {
            FavActivity.favActivity.binding.tabLayout.getTabAt(1).setText("Gallery" + " (" + repository.getAllDrawingsList().size() + ")");
        } else {
            FavActivity.favActivity.binding.tabLayout.getTabAt(1).setText("Gallery");
            recyclerView.setVisibility(View.INVISIBLE);
            layoutNoData.setVisibility(View.VISIBLE);
            HeadingTxt.setText(getString(R.string.no_favorites_added));
            DescriptionTxt.setText(getString(R.string.your_favorite_galleries_list_is_empty_discover_and_mark_your_favorite_ones));
        }
    }

    public void checkTutorials() {
        if (!helper.getAllTutorials().isEmpty()) {
            FavActivity.favActivity.binding.tabLayout.getTabAt(2).setText("Tutorials" + " (" + mTutorialdata.size() + ")");
            recyclerView.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.INVISIBLE);
        } else {
            FavActivity.favActivity.binding.tabLayout.getTabAt(2).setText("Tutorials");
            EmptyDataImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_tut_fav));
            recyclerView.setVisibility(View.INVISIBLE);
            layoutNoData.setVisibility(View.VISIBLE);
            HeadingTxt.setText(getString(R.string.no_favorites_added));
            DescriptionTxt.setText(getResources().getString(R.string.there_are_no_tutorials_in_your_favorites_browse_and_select_the_best_tutorials_to_add_them_here));
        }
    }

    public void checkUsers() {
        if (!repository.getUserProfiles().isEmpty()) {
            FavActivity.favActivity.binding.tabLayout.getTabAt(0).setText("Users" + " (" + repository.getUserProfiles().size() + ")");
        } else {
            FavActivity.favActivity.binding.tabLayout.getTabAt(0).setText("Users");
            EmptyDataImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_user_fav));
            recyclerView.setVisibility(View.INVISIBLE);
            layoutNoData.setVisibility(View.VISIBLE);
            HeadingTxt.setText(getString(R.string.no_favorites_added));
            DescriptionTxt.setText(getString(R.string.you_haven_t_added_any_users_to_your_favorites_explore_the_community_and_save_your_favorite_artists));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery_fav, container, false);

        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        _obj_interface = this;


        repository = new DrawingRepository(requireContext());

        recyclerView = view.findViewById(R.id.rvBlogPosts);
        HeadingTxt = view.findViewById(R.id.HeadingTxt);
        DescriptionTxt = view.findViewById(R.id.DescriptionTxt);
        layoutNoData = view.findViewById(R.id.layoutNoData);
        EmptyDataImg = view.findViewById(R.id.EmptyDataImg);


        if (FavScreenType.equals("gallery")) {
            adapter = new DrawingAdapter(requireContext(), repository.getAllDrawingsList());
            recyclerView.setAdapter(adapter);
            checkGallery();
        } else if (FavScreenType.equals("user")) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(layoutManager);
            UserAdapter = new UserFavAdapter(requireContext(), repository.getUserProfiles());
            recyclerView.setAdapter(UserAdapter);
            checkUsers();
        } else if (FavScreenType.equals("community")) {

         /*   DatabaseHelperForCommunity helper = new DatabaseHelperForCommunity(requireContext());

            CommunityFavAdapter adapter1 = new CommunityFavAdapter(requireContext(), helper.getAllCommunityPosts());
            List<CommunityPost> arrayList = helper.getAllCommunityPosts();

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter1);*/

        } else if (FavScreenType.equals("tutorial")) {
            helper = new TutorialDbHelper(requireContext());
            mTutorialdata = helper.getAllTutorials();
            mSubCategoryAdapterAll = new SubCategoryAdapterAll(mTutorialdata, requireContext(), this, true, "");
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(mSubCategoryAdapterAll);
            checkTutorials();
        }
        return view;

    }

    @Override
    public void onSubMenuClick(@Nullable View view, @Nullable GetCategoryPostModel.postData item, int position) {

//        if (BuildConfig.DEBUG) {
////            Toast.makeText(this, "Child Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
//        }
//
//        NewSubCategoryActivity activity = new NewSubCategoryActivity();
//
//
//        StringConstants constants = new StringConstants();
//        // Initializing the popup menu and giving the reference as current context
//        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
//
//        // Inflating popup menu from popup_menu.xml file
//        popupMenu.getMenuInflater().inflate(R.menu.sub_cat_menu_remove_fav, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(menuItem -> {
//            int id = menuItem.getItemId();
//            switch (id) {
//                case R.id.action_share:
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(requireContext(), constants.TUTORIAL_MENU_SHARE, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(requireContext(), constants.TUTORIAL_MENU_SHARE);
//
//                    activity.onShareClickAll(item,position,requireActivity());
//
//                    break;
//                case R.id.action_open_tutorial:
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(requireContext(), constants.TUTORIAL_MENU_OPEN, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(requireContext(), constants.TUTORIAL_MENU_OPEN);
//                    activity.SelectItemsAll(requireActivity(),item,item.getCateName());
//                    break;
//                case R.id.action_rating:
//                    if (BuildConfig.DEBUG) {
//                        Toast.makeText(requireContext(), constants.TUTORIAL_MENU_RATING, Toast.LENGTH_SHORT).show();
//                    }
//                    FirebaseUtils.logEvents(requireContext(), constants.TUTORIAL_MENU_RATING);
//                    if (AppUtils.isLoggedIn()) {
//                        activity.openRatingDialogAll(item,requireActivity());
//                    } else {
//                        Intent intent = new Intent(requireContext(), LoginActivity.class);
//                        startActivity(intent);
//                    }
//
//                    break;
//
////                case R.id.removeFav:
////                    TutorialDbHelper dbHelper = new TutorialDbHelper(requireContext());
////                    dbHelper.RemoveTut(item.childs.get(position).getChilds().get(position).);
////                    mSubCategoryAdapterAll.RemoveItem(position);
////                    mSubCategoryAdapterAll.notifyDataSetChanged();
////
////                    break;
//            }
//            return true;
//        });
//        // Showing the popup menu
//        popupMenu.show();

        Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void selectItem(int pos, boolean isFromRelatedPost) {

    }

    @Override
    public void selectChildItem(@Nullable GetCategoryPostModel.postData item, @Nullable String subCategoryName) {
        NewSubCategoryActivity activity = new NewSubCategoryActivity();
        activity.selectChild(requireActivity(), item, subCategoryName);

    }

    @Override
    public void onSubMenuClickAll(@Nullable View view, @Nullable Tutorialdatum item, int position) {
        if (BuildConfig.DEBUG) {
//            Toast.makeText(this, "Child Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        NewSubCategoryActivity activity = new NewSubCategoryActivity();


        StringConstants constants = new StringConstants();
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.sub_cat_menu_remove_fav, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.action_open_link).setVisible(false);
        if (item != null && item.getExternal() != null) {
            if (!item.getExternal().equalsIgnoreCase("")) {
                popupMenu.getMenu().findItem(R.id.action_open_link).setVisible(true);
            }
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {

                case R.id.action_share:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(requireContext(), StringConstants.tutorials_share, bundle);
                    }
                    activity.onShareClickAll(item, position, requireActivity());

                    break;
                case R.id.action_open_link:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(requireContext(), StringConstants.tutorials_open_link, bundle);
                    }
                    gotoUrl(item.getExternal().toString());
                    break;
                case R.id.action_open_tutorial:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("post_id", item.getId());
                        ContextKt.sendUserEventWithParam(requireContext(), StringConstants.favorites_tutorials_open, bundle);
                    }
                    activity.SelectItemsAll(requireActivity(), item, item.getCateName());
                    break;
                case R.id.action_rating:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(requireContext(), StringConstants.tutorials_rating, bundle);
                    }
                    if (AppUtils.isLoggedIn()) {
                        activity.openRatingDialogAll(item, requireActivity());
                    } else {
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                    break;

                case R.id.removeFav:
                    if (item != null && item.getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tutorial_id", item.getId());
                        ContextKt.sendUserEventWithParam(requireContext(), StringConstants.tutorials_remove_favorite, bundle);
                    }
                    TutorialDbHelper dbHelper = new TutorialDbHelper(requireContext());
                    dbHelper.RemoveTut(item.getId());
                    mSubCategoryAdapterAll.RemoveItem(position);
                    mSubCategoryAdapterAll.notifyDataSetChanged();
                    EventBus.getDefault().post(new RefreshFavoriteEvent(2));
                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();
    }

    public void gotoUrl(String url) {
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(url));
            startActivity(viewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void selectChildItemAll(@Nullable Tutorialdatum item, @Nullable String subCategoryName) {
        if (item != null && item.getId() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("post_id", item.getId());
            ContextKt.sendUserEventWithParam(requireContext(), StringConstants.favorites_tutorials_open, bundle);
        }
        NewSubCategoryActivity activity = new NewSubCategoryActivity();
        activity.SelectItemsAll(requireActivity(), item, subCategoryName);
    }
}
