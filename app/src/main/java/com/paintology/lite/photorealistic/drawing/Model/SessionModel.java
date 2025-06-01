package com.paintology.lite.photorealistic.drawing.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionModel {


    public String doc_name = "";
    public int _index = 0;

    public long interval_time = 60000;

    public String _start_time = "";

    HashMap<String, ArrayList<String>> _map = new HashMap<>();

    public String get_start_time() {
        return _start_time;
    }

    public void set_start_time(String _start_time) {
        this._start_time = _start_time;
    }

    public long getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(long interval_time) {
        this.interval_time = interval_time;
    }

    public ArrayList<String> _event_list = new ArrayList<>();

    public ArrayList<String> get_event_list() {
        return _event_list;
    }

    public void set_event_list(ArrayList<String> _event_list) {
        this._event_list = _event_list;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public int get_index() {
        return _index;
    }

    public void set_index(int _index) {
        this._index = _index;
    }

    public HashMap<String, ArrayList<String>> get_map() {
        return _map;
    }

    public void set_map(HashMap<String, ArrayList<String>> _map) {
        this._map = _map;
    }
}
