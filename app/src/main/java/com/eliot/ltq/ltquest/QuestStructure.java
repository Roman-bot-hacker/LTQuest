package com.eliot.ltq.ltquest;

public class QuestStructure {
    private int level;
    private int questID;
    private String questName;
    private String description;
    private int parentCategoryID;
    private String imageUrl;
    private double distance;

    public int getLevel() {
        return level;
    }

    public double getDistance() {
        return distance;
    }
    public String getQuestName() {
        return questName;
    }

}
