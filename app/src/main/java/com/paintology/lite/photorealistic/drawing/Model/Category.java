package com.paintology.lite.photorealistic.drawing.Model;

import java.util.List;


public class Category {
    private String id;
    private String level;
    private List<String> levels;
    private String name;
    private String parent_id;
    private String sorting_number;
    private Statistic statistic;
    private String thumbnail;
    private String total_tutorials;
    private List<Category> childs;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSorting_number() {
        return sorting_number;
    }

    public void setSorting_number(String sorting_number) {
        this.sorting_number = sorting_number;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTotal_tutorials() {
        return total_tutorials;
    }

    public void setTotal_tutorials(String total_tutorials) {
        this.total_tutorials = total_tutorials;
    }

    public List<Category> getChilds() {
        return childs;
    }

    public void setChilds(List<Category> childs) {
        this.childs = childs;
    }

    // Nested Statistic class
    public static class Statistic {
        private String views;

        // Getters and setters
        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }
    }
}
