package com.paintology.lite.photorealistic.drawing.Retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class Options {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Object mData;
    @SerializedName("response")
    private String response;
    @SerializedName("error")
    private String error;

    private String message;
    private String firstname;
    private String lastname;
    private String mobilenumber;
    private String emailid;
    private String timezone;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getmData() {
        return mData;
    }

    public String getResponse() {
        return response;
    }

    public String getError() {
        return error;
    }

    public static class DataStateDeserializer implements JsonDeserializer<Options> {

        @Override
        public Options deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Options userResponse = new Gson().fromJson(json, Options.class);
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("data")) {
                JsonElement elem = jsonObject.get("data");
                if (elem != null && !elem.isJsonNull()) {
                    if (elem.isJsonPrimitive()) {
                        userResponse.setMessage(elem.getAsString());
                    } else {
                        userResponse.setFirstname(elem.getAsJsonObject().get("firstname").getAsString());
                        userResponse.setLastname(elem.getAsJsonObject().get("lastname").getAsString());
                        userResponse.setMobilenumber(elem.getAsJsonObject().get("mobilenumber").getAsString());
                        userResponse.setEmailid(elem.getAsJsonObject().get("emailid").getAsString());
                        userResponse.setTimezone(elem.getAsJsonObject().get("timezone").getAsString());
                    }
                }
            }
            return userResponse;
        }
    }
}
