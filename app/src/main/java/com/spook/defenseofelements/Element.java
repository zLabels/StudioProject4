package com.spook.defenseofelements;

import android.graphics.Bitmap;

/**
 * Created by Malcolm on 18/2/2016.
 */
public class Element {

    public enum ELEMENT_TYPE{
        FIRE_TYPE,
        WATER_TYPE,
        WIND_TYPE,
        EARTH_TYPE,
    };

    Bitmap image;
    Vector2 Position = new Vector2(0,0);
    Vector2 Direction = new Vector2(0, 0);
    ELEMENT_TYPE type;
    boolean active;

    public Element(Vector2 Pos, Bitmap mesh, ELEMENT_TYPE element_type)
    {
        this.Position = Pos;
        this.Direction.Set(0, -1);
        this.image = mesh;
        this.type = element_type;
        this.active = true;
    }

    public void Init(Vector2 Pos, Bitmap mesh, ELEMENT_TYPE element_type)
    {
        this.Position = Pos;
        this.Direction.Set(0, -1);
        this.image = mesh;
        this.type = element_type;
        this.active = true;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Vector2 getPosition() {
        return Position;
    }

    public void setPosition(Vector2 position) {
        Position = position;
    }

    public Vector2 getDirection() {
        return Direction;
    }

    public void setDirection(Vector2 direction) {
        Direction = direction;
    }

    public ELEMENT_TYPE getType() {
        return type;
    }

    public void setType(ELEMENT_TYPE type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
