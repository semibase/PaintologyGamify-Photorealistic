package com.paintology.lite.photorealistic.drawing.Retrofit;


import com.paintology.lite.photorealistic.drawing.Chat.MyUsersModel;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.MyResponse;
import com.paintology.lite.photorealistic.drawing.Chat.Notification.Sender;
import com.paintology.lite.photorealistic.drawing.Model.AllCommentModel;
import com.paintology.lite.photorealistic.drawing.Model.CategoryModel;
import com.paintology.lite.photorealistic.drawing.Model.GetCategoryPostModel;
import com.paintology.lite.photorealistic.drawing.Model.GetUserProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.LocalityData;
import com.paintology.lite.photorealistic.drawing.Model.LoginResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.ReserveHashTag;
import com.paintology.lite.photorealistic.drawing.Model.ResponseBase;
import com.paintology.lite.photorealistic.drawing.Model.ResponseDeletePost;
import com.paintology.lite.photorealistic.drawing.Model.ResponseIncreaseCounter;
import com.paintology.lite.photorealistic.drawing.Model.ResponseModel;
import com.paintology.lite.photorealistic.drawing.Model.ResponseUserStatus;
import com.paintology.lite.photorealistic.drawing.Model.UpdateProfileResponse;
import com.paintology.lite.photorealistic.drawing.Model.UploadZipResponse;
import com.paintology.lite.photorealistic.drawing.Model.UserPostFromApi;
import com.paintology.lite.photorealistic.drawing.Model.UserSinglePostFromApi;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {

    @GET("wp-json/wcra/v1/getTutorialCategories/")
    Call<CategoryModel> getCategoryList(
            @Query("secret_key") String secretKey
    );

    @GET("wp-json/wcra/v1/getTutorialCatPosts/")
    Call<GetCategoryPostModel> getCategoryPostList(
            @Query("secret_key") String secretKey,
            @Query("cat_id") String cateId
    );


    @GET("wp-json/wcra/v1/getTutorialPostsData/")
    Call<String> getPostDetail(
            @Query("secret_key") String secretKey,
            @Query("cat_id") String caID,
            @Query("post_id") String postID
    );

    @GET("wp-json/wcra/v1/searchContent/")
    Call<String> searchContent(
            @Query("secret_key") String secretKey,
            @Query("search") String search
    );


//    @POST("wp-json/getTutorialsPost/v2")
//    Call<ArrayList<PostDetailModel>> getTutorialData(@Body RequestModel model);

    @Multipart
    @POST("wp-json/wcra/v1/socialMediaLogin/")
    Call<LoginResponseModel> addUserData(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );


    @GET
    Call<LocalityData> getLocalityData(@Url String url);


    @Multipart
    @POST("wp-json/wcra/v1/s3Bucket/")
    Call<UploadZipResponse> uploadZip(
            @Query("secret_key") String secretKey,
            @Part MultipartBody.Part file,
            @Part("user_id") RequestBody user_id
    );

    @Multipart
    @POST("wp-json/wcra/v1/update_profile/")
    Observable<UpdateProfileResponse> updateProfileData(
            @Query("secret_key") String secretKey,
            @Part MultipartBody.Part file,
            @PartMap() Map<String, RequestBody> partMap
    );

    @FormUrlEncoded
    @POST("wp-json/wcra/v1/get_profile/")
    Observable<GetUserProfileResponse> getUserProfileData(
            @Query("secret_key") String secretKey,
            @Field("user_id") String userID
    );


    @FormUrlEncoded
    @POST("wp-json/wcra/v1/update_followers/")
    Observable<ResponseModel> followToAPI(
            @Query("secret_key") String secretKey,
            @Field("followed_by") String followed_by,
            @Field("followed_to") String followed_to,
            @Field("operation_type") int Type
    );

    @Multipart
    @POST("wp-json/wcra/v1/community_feature_upload/")
    Observable<ResponseModel> postNewImage(
            @Query("secret_key") String secretKey,
            @Part MultipartBody.Part file,
            @PartMap() Map<String, RequestBody> partMap
    );


    @Multipart
    @POST("wp-json/wcra/v1/get_community_feature/")
    Observable<UserPostFromApi> getMyPost(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );


    @Multipart
    @POST("wp-json/wcra/v1/get_all_posts/")
    Observable<UserPostFromApi> getAllUsersPost(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );


    @Multipart
    @POST("wp-json/wcra/v1/update_posts_like/")
    Observable<ResponseBase> doLikeUnlike(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );

    @Multipart
    @POST("wp-json/wcra/v1/update_posts_views/")
    Observable<ResponseBase> updateViewCount(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );


    @Multipart
    @POST("wp-json/wcra/v1/add_post_comment/")
    Observable<ResponseBase> add_post(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );

    @Multipart
    @POST("wp-json/wcra/v1/viewPostByMe/")
    Observable<ResponseIncreaseCounter> increaseViewCounter(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );

    @Multipart
    @POST("wp-json/wcra/v1/add_post_reports/")
    Observable<ResponseBase> reportToPost(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );

    @Multipart
    @POST("wp-json/wcra/v1/get_post_comments/")
    Observable<AllCommentModel> getAllComment(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );


    @Multipart
    @POST("wp-json/wcra/v1/search_post_hashtag/")
    Observable<UserPostFromApi> getAllPostByHashTag(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );
//    Observable<UserPostFromApi> getAllPostByHashTag(@Field("hashtag") String tag, @Field("pagenumber") String pagenumber, @Field("size") String size);


    @GET("wp-json/wcra/v1/get_reserved_hashtag/")
    Observable<ReserveHashTag> getReservedHashTag(
            @Query("secret_key") String secretKey
    );

    @Multipart
    @POST("wp-json/wcra/v1/getCommunityPostDelete/")
    Observable<ResponseDeletePost> deletePost(
            @Query("secret_key") String secretKey,
            @PartMap() Map<String, RequestBody> partMap
    );


    @Multipart
    @POST("wp-json/wcra/v1/getCommunityPostUpdate/")
    Observable<ResponseModel> updatePost(
            @Query("secret_key") String secretKey,
            @Part MultipartBody.Part file,
            @PartMap() Map<String, RequestBody> partMap
    );

    @FormUrlEncoded
    @POST("wp-json/wcra/v1/device_token/")
    Observable<ResponseModel> sendDeviceToken(
            @Query("secret_key") String secretKey,
            @Field("device_token") String device_token,
            @Field("user_id") String user_id
    );


    @Multipart
    @POST("wp-json/wcra/v1/getCommunityPost/")
    Observable<UserSinglePostFromApi> getPostDetail(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );


    // https://fcm.googleapis.com/fcm/send
    @POST("fcm/send")
    Call<MyResponse> sendNotification(
            @Body Sender body
    );

    @Multipart
    @POST("wp-json/wcra/v1/my_users/")
    Observable<MyUsersModel> getMyUser(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );

    @Multipart
    @POST("wp-json/wcra/v1/user_status/")
    Call<ResponseUserStatus> changeStaus(
            @Query("secret_key") String secretKey,
            @PartMap HashMap<String, RequestBody> map
    );


}
