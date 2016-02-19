package com.spook.defenseofelements;

import android.graphics.Bitmap;

/**
 * Created by Princeton on 8/12/2015.
 */
public class InGameButton {
    float posX = 0, posY = 0;   //Coordinates of button
    private Bitmap image;    //Image of button
    int imgWidth;   //Width of image
    int imgHeight;  //Height of image
    boolean active; //active status of button
    private AABB2D BoundingBox = new AABB2D(new Vector2(0,0),10.0f,10.0f);
    public String buttonID;

    public InGameButton(float posX, float posY, Bitmap image,boolean active, String ID) {
        this.posX = posX;
        this.posY = posY;
        this.image = image;
        this.imgWidth = image.getWidth();
        this.imgHeight = image.getHeight();
        this.active = active;
        this.buttonID = ID;
        BoundingBox.SetAllData(new Vector2(posX + (imgWidth * 0.5f),posY + (imgHeight * 0.5f)),imgWidth,imgHeight);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public AABB2D getBoundingBox()
    {
        return this.BoundingBox;
    }
}
