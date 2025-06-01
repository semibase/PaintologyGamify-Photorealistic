package com.paintology.lite.photorealistic.drawing.Model;

public class LanguageModel {

    public int flag_img = 0;

    public boolean isSelected = false;

    public String lang_name = "";

    public String lang_pref = "";

    public int getFlag_img() {
        return flag_img;
    }

    public void setFlag_img(int flag_img) {
        this.flag_img = flag_img;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLang_name() {
        return lang_name;
    }

    public void setLang_name(String lang_name) {
        this.lang_name = lang_name;
    }

    public String getLang_pref() {
        return lang_pref;
    }

    public void setLang_pref(String lang_pref) {
        this.lang_pref = lang_pref;
    }
}
