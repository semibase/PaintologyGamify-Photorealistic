package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 3/4/21.
 */
public class UserChoices {

    @SerializedName("art_abilities")
    @Expose
    private List<ArtAbility> artAbilities = null;
    @SerializedName("art_preferences")
    @Expose
    private List<ArtPreference> artPreferences = null;
    @SerializedName("art_mediums")
    @Expose
    private List<ArtMedium> artMediums = null;
    @SerializedName("device_types")
    @Expose
    private List<DeviceType> deviceTypes = null;
    @SerializedName("country_list")
    @Expose
    private List<Country> countryList = null;
    @SerializedName("age_options")
    @Expose
    private AgeOptions ageOptions;

    public List<ArtAbility> getArtAbilities() {
        return artAbilities;
    }

    public void setArtAbilities(List<ArtAbility> artAbilities) {
        this.artAbilities = artAbilities;
    }

    public List<ArtPreference> getArtPreferences() {
        return artPreferences;
    }

    public void setArtPreferences(List<ArtPreference> artPreferences) {
        this.artPreferences = artPreferences;
    }

    public List<ArtMedium> getArtMediums() {
        return artMediums;
    }

    public void setArtMediums(List<ArtMedium> artMediums) {
        this.artMediums = artMediums;
    }

    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public AgeOptions getAgeOptions() {
        return ageOptions;
    }

    public void setAgeOptions(AgeOptions ageOptions) {
        this.ageOptions = ageOptions;
    }
}
