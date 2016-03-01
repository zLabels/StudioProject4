package com.spook.defenseofelements;

import android.graphics.Bitmap;

/**
 * Created by Princeton on 8/12/2015.
 */
public class InGameButton {

    public enum BUTTON_TYPE
    {
        UI_NORMAL_TOWER,
        UI_FAST_TOWER,
        UI_WORKER,
        UI_SLOW_TOWER,
        UI_OP_TOWER,
        UI_RETRY,
        UI_REPLAY,
        UI_MAINMENU,
        UI_UPGRADE,
        UI_DEFAULT,
    }
    float posX = 0, posY = 0;   //Coordinates of button
    private Bitmap image;    //Image of button
    int imgWidth;   //Width of image
    int imgHeight;  //Height of image
    boolean active; //active status of button
    private AABB2D BoundingBox = new AABB2D(new Vector2(0,0),10.0f,10.0f);
    public BUTTON_TYPE buttonID;

    public InGameButton() {
        this.posX = 0;
        this.posY = 0;
        this.image = null;
        this.imgWidth = 0;
        this.imgHeight = 0;
        this.active = false;
        this.buttonID = BUTTON_TYPE.UI_DEFAULT;
    }

    public InGameButton(float posX, float posY, Bitmap image,boolean active, BUTTON_TYPE ID) {
        this.posX = posX;
        this.posY = posY;
        this.image = image;
        this.imgWidth = image.getWidth();
        this.imgHeight = image.getHeight();
        this.active = active;
        this.buttonID = ID;
        BoundingBox.SetAllData(new Vector2(posX ,posY),imgWidth,imgHeight);
    }

    public void SetAllData(float posX, float posY, Bitmap image,boolean active, BUTTON_TYPE ID)
    {
        this.posX = posX;
        this.posY = posY;
        this.image = image;
        this.imgWidth = image.getWidth();
        this.imgHeight = image.getHeight();
        this.active = active;
        this.buttonID = ID;
        BoundingBox.SetAllData(new Vector2(posX ,posY),imgWidth,imgHeight);
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

    public void setPosition( float x, float y)
    {
        this.posX = x;
        this.posY = y;
        BoundingBox.setCenterPoint(new Vector2(posX, posY));
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
