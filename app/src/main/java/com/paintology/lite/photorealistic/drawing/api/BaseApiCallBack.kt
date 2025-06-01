package com.paintology.lite.photorealistic.drawing.api

import android.app.ProgressDialog
import android.content.Context
import com.core.ext.showLongToast
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface
import com.paintology.lite.photorealistic.drawing.Activity.utils.isNetworkConnected
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseApiCallBack constructor(private val context: Context) {


    private lateinit var progressDialog: ProgressDialog
    val  defaultLink = ApiClient.BASE_URL + "Tutorials/left-hand-drawing-challenge-for-30-days/";

    val apiInterface: ApiInterface by lazy {
        ApiClient.getRetroClient().create(ApiInterface::class.java);
    }


    fun showProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait))
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (::progressDialog.isInitialized) {
            progressDialog.dismiss()
        }
    }


    suspend fun makeNetworkCall(
        showLoader: Boolean = true,
        categoryPostList: Call<String>,
        callback: (Event) -> Unit
    ) {
        if (context.isNetworkConnected()) {
            if (showLoader) {
                showProgressDialog()
            }
            categoryPostList.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        callback.invoke(Event.OnSuccess(response.body()))
                    } else {
                        callback.invoke(Event.OnFailure(Exception(response.message())))
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    hideProgressDialog()
                    callback.invoke(Event.OnFailure(Exception(t.message)))
                }
            })
        } else {
            callback.invoke(Event.OnFailure(NoInternetException()))
        }
    }

    fun showGenericErrorToast(message:String = "Something Went wrong please try again"){
        context.showLongToast(message)
    }

    sealed class Event {
        data class OnFailure(val e: Exception) : Event()

        data class OnSuccess(val data: String?) : Event()
    }

    class NoInternetException(val ex: String = "No Internet Available") : Exception(ex)
}