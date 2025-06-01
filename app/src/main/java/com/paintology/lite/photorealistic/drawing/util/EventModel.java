package com.paintology.lite.photorealistic.drawing.util;

public class EventModel {

    public String BrushColor = "";

    public BrushChangeMetaData objChangeData = new BrushChangeMetaData();

    public String eventType;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String TimeStamp = "";

    String notes = "";

    String StrokeAxis = "";

    public boolean isPlayed = false;

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public String getStrokeAxis() {
        return StrokeAxis;
    }

    public void setStrokeAxis(String strokeAxis) {
        StrokeAxis = strokeAxis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBrushColor() {
        return BrushColor;
    }

    public void setBrushColor(String brushColor) {
        BrushColor = brushColor;
    }

    public BrushChangeMetaData getObjChangeData() {
        return objChangeData;
    }

    public void setObjChangeData(BrushChangeMetaData objChangeData) {
        this.objChangeData = objChangeData;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public class BrushChangeMetaData {

        public String BrushName = "", BrushHardness = "", BrushStyle = "", BrushSize = "", BrushFlow = "", BrushAlpha = "", BrushColor = "", BrushMode = "";

        public String getBrushHardness() {
            return BrushHardness;
        }

        public void setBrushHardness(String brushHardness) {
            BrushHardness = brushHardness;
        }

        public String getBrushAlpha() {
            return BrushAlpha;
        }

        public void setBrushAlpha(String brushAlpha) {
            BrushAlpha = brushAlpha;
        }

        public String getBrushMode() {
            return BrushMode;
        }

        public void setBrushMode(String brushMode) {
            BrushMode = brushMode;
        }

        public String getBrushName() {
            return BrushName;
        }

        public void setBrushName(String brushName) {
            BrushName = brushName;
        }


        public String getBrushStyle() {
            return BrushStyle;
        }

        public void setBrushStyle(String brushStyle) {
            BrushStyle = brushStyle;
        }

        public String getBrushSize() {
            return BrushSize;
        }

        public void setBrushSize(String brushSize) {
            BrushSize = brushSize;
        }

        public String getBrushFlow() {
            return BrushFlow;
        }

        public void setBrushFlow(String brushFlow) {
            BrushFlow = brushFlow;
        }

        public String getBrushColor() {
            return BrushColor;
        }

        public void setBrushColor(String brushColor) {
            BrushColor = brushColor;
        }
    }
}
