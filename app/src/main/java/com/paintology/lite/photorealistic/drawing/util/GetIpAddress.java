package com.paintology.lite.photorealistic.drawing.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.paintology.lite.photorealistic.drawing.BuildConfig;
import com.paintology.lite.photorealistic.drawing.Model.LocalityData;
import com.paintology.lite.photorealistic.drawing.Model.ReserveHashTag;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiClient;
import com.paintology.lite.photorealistic.drawing.Retrofit.ApiInterface;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;

public class GetIpAddress extends IntentService {


    public GetIpAddress() {
        super("");
    }

    StringConstants _constants = new StringConstants();
    public Context _context;
    ApiInterface apiInterface;
    Call<LocalityData> Localitycall;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("TAGG", "onHandleIntent called");
        Log.e("TAGG", "GetIpAddress OnHandleIntent");
        _context = getApplicationContext();
//        getIPAddress();
      //  getReservedHashTag();
    }


    public void getReservedHashTag() {
        Log.e("TAGG", "GetIpAddress getReservedHashTag()");
        apiInterface = ApiClient.getClientForRX().create(ApiInterface.class);

        Observable<ReserveHashTag> observable = apiInterface.getReservedHashTag(ApiClient.SECRET_KEY);

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ReserveHashTag>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReserveHashTag reserveHashTag) {

                try {
                    if (reserveHashTag != null) {
                        if (reserveHashTag.get_lst_hashTag() != null) {
                            Log.e("TAGG", "onNext Called " + reserveHashTag.get_lst_hashTag().size());
                            Gson gson = new Gson();
                            String arrayData = gson.toJson(reserveHashTag.get_lst_hashTag());

                            _constants.putString(_constants.hashTagList, arrayData, _context);
                        }
                    }
                } catch (Exception e) {

                }

                getLocalityDataAPI();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError Called " + e.getMessage(), e);
                getLocalityDataAPI();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    void getLocalityDataAPI() {
        Log.e("TAGG", "GetIpAddress getLocalityDataAPI()");
        if (Localitycall != null) {
            Localitycall.cancel();
            Log.e("TAGG", "getLocalityDataAPI onCancel");
        }
        Localitycall = apiInterface.getLocalityData("https://us-central1-even-scheduler-265110.cloudfunctions.net/geolocation");
        try {
            Localitycall.enqueue(new Callback<LocalityData>() {
                @Override
                public void onResponse(Call<LocalityData> call, retrofit2.Response<LocalityData> response) {
                    try {

                        if (response != null) {
                            if (response.body() != null) {
                                if (response.body().getCity() != null) {
                                    _constants.putString(_constants.UserCity, response.body().getCity(), _context);
                                }
                                if (response.body().getUserIP() != null) {
                                    _constants.putString(_constants.IpAddress, response.body().getUserIP(), _context);
                                }

                                if (response.body().getCountry() != null && !response.body().getCountry().isEmpty()) {
                                    _constants.putString(_constants.UserCountryCode, response.body().getCountry(), _context);
                                }

                              /*  if (response.body().getCityData() != null && response.body().getCityData().size() > 0) {
                                    if (response.body().getCityData().get(0).getCountry() != null)
                                        _constants.putString(_constants.UserCountry, response.body().getCityData().get(0).getCountry(), _context);
                                } else {
                                    if (response.body().getCountry() != null && !response.body().getCountry().isEmpty())
                                        _constants.putString(_constants.UserCountry, response.body().getCountry(), _context);
                                }*/
                            }
                        }

                        Log.e("TAGGG", "LocalityData response country " + response.body().getCountry());
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at set counter locality data " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(Call<LocalityData> call, Throwable t) {
                    try {
                        Log.e("TAGGG", "OnFailure on IpAddress");
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), new StringConstants().get_ip_failed, Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUtils.logEvents(getApplicationContext(), new StringConstants().get_ip_failed);
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at callAPI " + e.getMessage() + " " + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAGG", "OnDestroy IpService Called");
    }
}
