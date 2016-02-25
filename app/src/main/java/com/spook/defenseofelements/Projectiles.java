package com.spook.defenseofelements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Projectiles {

    public enum PROJECTILE_TYPE
    {
        PROJECTILE_NORMAL,
        PROJECTILE_SLOW,
    }

    Vector2 Position = new Vector2(0.0f,0.0f);
    Vector2 TopLeft = new Vector2(0.0f,0.0f);
    Vector2 Direction = new Vector2(0.0f,0.0f);
    private Bitmap image;

    float DecreaseSpeed;
    float rotation;
    float movespeed;
    boolean active;
    float LifeTime;
    float Damage;
    PROJECTILE_TYPE type;

    AABB2D bounding_box = new AABB2D();

    public Projectiles()
    {
        this.Position = new Vector2(0.0f,0.0f);
        this.Direction.Set(0, 1);

        this.type = PROJECTILE_TYPE.PROJECTILE_NORMAL;
        this.image = null;
        this.movespeed = 0.0f;
        this.active = false;
        this.LifeTime = 5.0f;
        this.Damage = 10;

    }

    public void SetAllData(Vector2 Pos, Bitmap mesh, float speed,float damage,Vector2 targetPosition, boolean Active, PROJECTILE_TYPE projectile_type)
    {
        this.Position = Pos;
        this.Direction = (targetPosition.operatorMinus(this.Position).Normailzed());

        this.type = projectile_type;
        this.image = mesh;
        this.movespeed = speed;
        this.active = Active;
        this.LifeTime = 5.0f;
        this.Damage = damage;

        this.bounding_box.SetAllData(new Vector2(Position.x, Position.y), image.getWidth(), image.getHeight());

        AssignProjectileType(type);
    }
    public void AssignProjectileType(PROJECTILE_TYPE type)
    {
        switch(type)
        {
            case PROJECTILE_NORMAL:
            {
                this.DecreaseSpeed = 0;
            }
            break;
            case PROJECTILE_SLOW:
            {
                this.DecreaseSpeed = 50;
            }
            break;
        }
    }
    public void Update(float dt)
    {
        //Getting the direction towards the target AI
        double theta = Math.atan2(Direction.y, Direction.x);

        //Update rotation for use in Render
        rotation = (float) Math.toDegrees(theta);

        //Update Velocity based on the direction set at the start
        Vector2 velocity = Direction.operatorTimes(movespeed);

        velocity = velocity.operatorTimes(0.015f);

        Position.operatorPlusEqual(velocity);

        //Update Bounding Box
        bounding_box.setCenterPoint(Position);

        this.LifeTime -= dt;

        if (LifeTime < 0) {active = false;}
    }

    public void Draw(Canvas canvas)
    {
        Matrix matrix = new Matrix();
        matrix.postTranslate(bounding_box.getTopLeft().x, bounding_box.getTopLeft().y);
        //matrix.postRotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
        canvas.drawBitmap(image, matrix, null);
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

    public AABB2D getBounding_box() {
        return bounding_box;
    }

    public void setBounding_box(AABB2D bounding_box) {
        this.bounding_box = bounding_box;
    }

    public float getDamage() {return Damage;}

    public void setDamage(float damage) {
        this.Damage = damage;
    }

    public float getDecreaseSpeed() {return DecreaseSpeed;}

    public void setDecreaseSpeed(float decreaseSpeed) {this.DecreaseSpeed = decreaseSpeed;}

    public PROJECTILE_TYPE getType() {
        return type;
    }

    public void setType(PROJECTILE_TYPE type) {
        this.type = type;
    }
}
