package com.skyler.android.familymap.model;

import android.graphics.Color;

import static com.skyler.android.familymap.model.Settings.MapType.HYBRID;
import static com.skyler.android.familymap.model.Settings.MapType.NORMAL;
import static com.skyler.android.familymap.model.Settings.MapType.SATELLITE;
import static com.skyler.android.familymap.model.Settings.MapType.TERRAIN;

/**
 * Created by Skyler on 4/6/2016.
 */
public class Settings {
    boolean lifeStoryLinesOn = false;
    int lifeStoryLinesColor;
    int lifeStoryLinesColorIndex = 0;
    boolean familyTreeLinesOn = false;
    int familyTreeLinesColor;
    int familyTreeLinesColorIndex = 1;
    boolean spouseLinesOn = false;
    int spouseLinesColor;
    int spouseLinesColorIndex = 2;
    int mapTypeIndex = 0;
    private MapType mapType = NORMAL;

    public void resetSettings() {
        lifeStoryLinesOn = false;
        lifeStoryLinesColorIndex = 0;
        familyTreeLinesOn = false;
        familyTreeLinesColorIndex = 1;
        spouseLinesOn = false;
        spouseLinesColorIndex = 2;
        mapType = NORMAL;
        mapTypeIndex = 0;
    }

    public int getMapTypeIndex() {
        return mapTypeIndex;
    }

    public void setMapTypeIndex(int mapTypeIndex) {
        this.mapTypeIndex = mapTypeIndex;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        switch (mapType) {
            case "Normal":
                this.mapType = NORMAL;
                break;
            case "Hybrid":
                this.mapType = HYBRID;
                break;
            case "Satellite":
                this.mapType = SATELLITE;
                break;
            case "Terrain":
                this.mapType = TERRAIN;
                break;
            default:
                this.mapType = NORMAL;
        }
    }

    public int getFamilyTreeLinesColor() {
        return familyTreeLinesColor;
    }

    public void setFamilyTreeLinesColor(String colorString) {
        this.familyTreeLinesColor = Color.parseColor(colorString);
    }

    public void setFamilyTreeLinesColor(int familyTreeLinesColor) {
        this.familyTreeLinesColor = familyTreeLinesColor;
    }

    public boolean isFamilyTreeLinesOn() {
        return familyTreeLinesOn;
    }

    public void setFamilyTreeLinesOn(boolean familyTreeLinesOn) {
        this.familyTreeLinesOn = familyTreeLinesOn;
    }

    public int getLifeStoryLinesColor() {
        return lifeStoryLinesColor;
    }

    public void setLifeStoryLinesColor(String spinnerColor) {
        lifeStoryLinesColor = Color.parseColor(spinnerColor);
    }

    public void setLifeStoryLinesColor(int lifeStoryLinesColor) {
        this.lifeStoryLinesColor = lifeStoryLinesColor;
    }

    public boolean isLifeStoryLinesOn() {
        return lifeStoryLinesOn;
    }

    public void setLifeStoryLinesOn(boolean lifeStoryLinesOn) {
        this.lifeStoryLinesOn = lifeStoryLinesOn;
    }

    public int getSpouseLinesColor() {
        return spouseLinesColor;
    }

    public void setSpouseLinesColor(String colorString) {
        this.spouseLinesColor = Color.parseColor(colorString);
    }

    public void setSpouseLinesColor(int spouseLinesColor) {
        this.spouseLinesColor = spouseLinesColor;
    }

    public boolean isSpouseLinesOn() {
        return spouseLinesOn;
    }

    public void setSpouseLinesOn(boolean spouseLinesOn) {
        this.spouseLinesOn = spouseLinesOn;
    }

    public int getSpouseLinesColorIndex() {
        return spouseLinesColorIndex;
    }

    public void setSpouseLinesColorIndex(int index) {
        spouseLinesColorIndex = index;
    }

    public int getLifeStoryLinesColorIndex() {
        return lifeStoryLinesColorIndex;
    }

    public void setLifeStoryLinesColorIndex(int index) {
        lifeStoryLinesColorIndex = index;
    }

    public int getFamilyTreeLinesColorIndex() {
        return familyTreeLinesColorIndex;
    }

    public void setFamilyTreeLinesColorIndex(int index) {
        familyTreeLinesColorIndex = index;
    }

    public enum MapType {NORMAL, HYBRID, SATELLITE, TERRAIN}

}
