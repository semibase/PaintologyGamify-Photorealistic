package com.paintology.lite.photorealistic.drawing.api

import android.content.Context
import com.core.ext.showLongToast
import com.paintology.lite.photorealistic.drawing.Model.PostDetailModel
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.challenge.view.PostModelConverter
import org.json.JSONArray

open class ApiCallback constructor(private val context: Context) : BaseApiCallBack(context) {

    suspend fun getChallengeDetails(
        catId: String,
        posId: String,
        callback: (PostDetailModel) -> Unit
    ) {
        val api = apiInterface.getPostDetail(ApiClient.SECRET_KEY, catId, posId)
        makeNetworkCall(true, api) {
            when (it) {
                is Event.OnFailure -> {
                    context.showLongToast(it.e.message ?: "")
                }

                is Event.OnSuccess -> {
                    val mainArray = JSONArray(it.data)
                    if (mainArray.length() > 0) {
                        val objectFirst = PostModelConverter.getPostDetailsResponse(it.data)
                        callback.invoke(objectFirst)

                    }
                }
            }

        }
    }
}