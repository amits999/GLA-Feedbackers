package com.amitsharma.action.glafeedbacker;

public class Model {
    String name,feedText,feedType,feedRating;

    public Model() {
    }

    public Model(String name, String feedText, String feedType, String feedRating) {
        this.name = name;
        this.feedText = feedText;
        this.feedType = feedType;
        this.feedRating = feedRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedText() {
        return feedText;
    }

    public void setFeedText(String feedText) {
        this.feedText = feedText;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedRating() {
        return feedRating;
    }

    public void setFeedRating(String feedRating) {
        this.feedRating = feedRating;
    }
}
