package com.eliot.ltq.ltquest;

import android.support.annotation.NonNull;

public class QuestStructure {
    private int level;
    private int questID;
    private String questName;
    private String description;
    private int parentCategoryID;
    private String imageUrl;
    private double distance;
    private int imageViewId;

    public int getImageViewId() {
        return imageViewId;
    }

    public void setImageViewId(int imageViewId) {
        this.imageViewId = imageViewId;
    }

    public int getLevel() {
        return level;
    }

    public double getDistance() {
        return distance;
    }
    public String getQuestName() {
        return questName;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    public int getParentCategoryID() {
        return parentCategoryID;
    }
}
