package com.spook.defenseofelements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by Malcolm on 16/2/2016.
 */
public class Tower {

    public enum TOWER_TYPE {
        TOWER_NORMAL,
        TOWER_HIGHFIRERATE,
        TOWER_SLOW,
    };

    int level;
    float damage;
    float firerate;
    float elaspedtime;
    float rotation;
    boolean fire;
    Vector2 Position = new Vector2(0.0f,0.0f);
    Vector2 TopLeft = new Vector2(0.0f,0.0f);
    Vector2 Direction = new Vector2(0.0f,0.0f);
    private Bitmap image;
    TOWER_TYPE type;

    public Tower() {
        this.Position = new Vector2(0.f,0.0f);
        this.Direction.Set(0, 1);
        this.image = null;
        this.type = TOWER_TYPE.TOWER_NORMAL;
        this.fire = false;
        this.level = 1;
        this.elaspedtime = 0;
        this.rotation = 0;

        AssignTowerType(type);
    }

    public Tower(Vector2 Pos, Bitmap mesh, TOWER_TYPE tower_type) {
        this.Position = Pos;
        this.Direction.Set(0, 1);
        this.image = mesh;
        this.type = tower_type;
        this.fire = false;
        this.level = 1;
        this.elaspedtime = 0;
        this.rotation = 0;

        float halfWidth = image.getWidth() * 0.5f;
        float halfHeight = image.getHeight() * 0.5f;

        TopLeft.Set(Pos.x - halfWidth, Pos.y + halfHeight);

        AssignTowerType(type);
    }

    public void AssignTowerType(TOWER_TYPE type)
    {
        switch(type) {
            case TOWER_NORMAL:
            {
                damage = 1;
                firerate = 1;
            }
                break;
            case TOWER_HIGHFIRERATE:
            {
                damage = 0.5f;
                firerate = 1.5f;
            }
                break;
            case TOWER_SLOW:
            {
                damage = 0.5f;
                firerate = 1;
            }
                break;
        }
    }

    public boolean Update(Vector2 target, float dt)
    {
        // Update Tower direction
        Direction = (target.operatorMinus(Position).Normailzed());
        double theta = Math.atan2(Direction.y, Direction.x);
        rotation = (float) Math.toDegrees(theta);

        return Fire(dt);
    }

    public void Draw(Canvas canvas)
    {
        Matrix matrix = new Matrix();
        matrix.postTranslate(TopLeft.x,TopLeft.y);
        matrix.postRotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
        canvas.drawBitmap(image, matrix, null);
    }

    public boolean Fire(float dt)
    {
        elaspedtime += dt;

        if(elaspedtime >= firerate)
        {
            fire = true;
            elaspedtime = 0;
        }
        else
        {
            fire = false;
        }

        return fire;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getFirerate() {
        return firerate;
    }

    public void setFirerate(float firerate) {
        this.firerate = firerate;
    }

    public float getElaspedtime() {
        return elaspedtime;
    }

    public void setElaspedtime(float elaspedtime) {
        this.elaspedtime = elaspedtime;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public TOWER_TYPE getType() {
        return type;
    }

    public void setType(TOWER_TYPE type) {
        this.type = type;
    }

    public Vector2 getTopLeft() {
        return TopLeft;
    }

    public void setTopLeft(Vector2 topLeft) {
        TopLeft = topLeft;
    }
}
