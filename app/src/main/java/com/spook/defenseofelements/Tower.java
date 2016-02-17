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
    }

    int level;
    float damage;
    float firerate;
    float elaspedtime;
    float rotation;
    boolean fire;
    Vector2 Position;
    Vector2 Direction;
    private Bitmap image;    //Image of button
    TOWER_TYPE type;

    public Tower(Vector2 Pos, Bitmap mesh, TOWER_TYPE tower_type) {
        this.Position = Pos;
        this.Direction.Set(0, 1);
        this.image = mesh;
        this.type = tower_type;
        this.fire = false;
        this.level = 1;
        this.elaspedtime = 0;
        this.rotation = 0;

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

}
