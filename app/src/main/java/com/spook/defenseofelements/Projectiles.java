package com.spook.defenseofelements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by Jessica on 23/2/2016.
 */
public class Projectiles {

    Vector2 Position = new Vector2(0.0f,0.0f);
    Vector2 TopLeft = new Vector2(0.0f,0.0f);
    Vector2 Direction = new Vector2(0.0f,0.0f);
    private Bitmap image;

    float rotation;
    float movespeed;
    boolean active;

    AABB2D bounding_box;

    public Projectiles()
    {
        this.Position = new Vector2(0.f,0.0f);
        this.Direction.Set(0, 1);

        this.image = null;
        this.movespeed = 0.0f;
        this.active = true;

        this.bounding_box = new AABB2D(new Vector2(Position.x, Position.y), image.getWidth(), image.getHeight());

    }
    public Projectiles(Vector2 Pos, Bitmap mesh, float speed)
    {
        this.Position = Pos;
        this.Direction.Set(0, 1);

        this.image = mesh;
        this.movespeed = speed;
        this.active = true;

        this.bounding_box.SetAllData(new Vector2(Position.x, Position.y), image.getWidth(), image.getHeight());
    }

    public void Update(Vector2 target, float dt)
    {
        if(active)
        {
            Direction = (target.operatorMinus(Position).Normailzed());
            double theta = Math.atan2(Direction.y, Direction.x);
            rotation = (float) Math.toDegrees(theta);

            Vector2 velocity = Direction.operatorTimes(movespeed);
            Position.operatorPlusEqual(velocity);

            bounding_box.setCenterPoint(Position);
            if(Position == target)
            {
                active = false;
            }

        }
        else
        {
            bounding_box.setCenterPoint(Position);
        }
    }

    public void Draw(Canvas canvas)
    {
        if(active) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(bounding_box.getTopLeft().x, bounding_box.getTopLeft().y);
            matrix.postRotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
            canvas.drawBitmap(image, matrix, null);
        }
    }

    public float getMovespeed() {
        return movespeed;
    }

    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
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

    public Vector2 getTopLeft() {
        return TopLeft;
    }

    public void setTopLeft(Vector2 topLeft) {
        TopLeft = topLeft;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
