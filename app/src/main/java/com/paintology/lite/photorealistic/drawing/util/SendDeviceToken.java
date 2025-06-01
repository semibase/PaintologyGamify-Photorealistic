package com.paintology.lite.photorealistic.drawing.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;

public class SendDeviceToken extends IntentService {

    public Context _context;
    ApiInterface apiInterface;
    StringConstants _constant = new StringConstants();

    public SendDeviceToken() {
        super("");
    }

    public SendDeviceToken(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.e("TAGG", "SendDeviceToken Service called");
        _context = getApplicationContext();
        String token = _constant.getString(_constant.DeviceToken, _context);
        sendLocationToBackend(token);
    }

    void sendLocationToBackend(String _token) {
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);
        String user_id = _constant.getString(_constant.UserId, _context);

        Log.e("TAGGG", "SendToken Token " + _token + " user_id " + user_id);
        /*Observable<ResponseModel> _observable = apiInterface.sendDeviceToken(ApiClient.SECRET_KEY, _token, user_id);
        _observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseModel responseModel) {
                try {
                    if (responseModel != null) {
                        Log.e("TAGG", "status " + responseModel.getStatus() + " msg " + responseModel.getResponse());
                    }
                } catch (Exception e) {
                    Log.e("TAGG", "Exception");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "Exception at sendtoken ");
            }

            @Override
            public void onComplete() {

            }
        });*/

    }
}
