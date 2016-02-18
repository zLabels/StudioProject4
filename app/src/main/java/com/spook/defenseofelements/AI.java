package com.spook.defenseofelements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.Vector;

/**
 * Created by Malcolm on 17/2/2016.
 */
public class AI {

    public enum AI_TYPE {
        AI_NORMAL,
        AI_FAST,
        AI_SLOWBUTTANKY,
    };

    public enum AI_STATE{
        IDLE_STATE,
        WALK_STATE,
    };

    float health;
    float movespeed;
    float rotation;
    Vector2 Position = new Vector2(0,0);
    Vector2 Direction = new Vector2(0,0);
    Vector2 NextPosition = new Vector2(0, 0);
    Vector<Vector2> Waypoints = new Vector<Vector2>();
    int waypointIndex;
    Bitmap image;
    AI_TYPE type;
    AI_STATE currentstate;
    boolean active;
    boolean endofwaypoint;

    public AI(Vector2 Pos, Vector<Vector2> waypoints, Bitmap mesh, AI_TYPE ai_type)
    {
        this.Position = Pos;
        this.Direction.Set(0, 1);
        this.Waypoints = waypoints;
        this.NextPosition = this.Waypoints.get(0);
        this.image = mesh;
        this.type = ai_type;
        this.currentstate = AI_STATE.WALK_STATE;
        this.rotation = 0;
        this.waypointIndex = 0;
        this.active = true;
        this.endofwaypoint = false;

        AssignAIType(type);
    }

    public void Init(Vector2 Pos, Vector<Vector2> waypoints, Bitmap mesh, AI_TYPE ai_type)
    {
        this.Position = Pos;
        this.Direction.Set(0, 1);
        this.Waypoints = waypoints;
        this.NextPosition = this.Waypoints.get(0);
        this.image = mesh;
        this.type = ai_type;
        this.currentstate = AI_STATE.WALK_STATE;
        this.rotation = 0;
        this.waypointIndex = 0;
        this.active = true;
        this.endofwaypoint = false;

        AssignAIType(type);
    }

    public void AssignAIType(AI_TYPE type)
    {
        switch(type) {
            case AI_NORMAL:
            {
                health = 100;
                movespeed = 50;
            }
            break;
            case AI_FAST:
            {
                health = 75;
                movespeed = 75;
            }
            break;
            case AI_SLOWBUTTANKY:
            {
                health = 125;
                movespeed = 25;
            }
            break;
        }
    }

    public void Update(float dt)
    {
        if(active) {

            if(currentstate == AI_STATE.IDLE_STATE) {
                Position = NextPosition;

                if(waypointIndex + 1 != Waypoints.size()) {
                    NextPosition = Waypoints.get(++waypointIndex);
                }
                else {
                    active = false;
                    endofwaypoint = true;
                }
            }

            else if(currentstate == AI_STATE.WALK_STATE) {
                // Update AI direction
                Direction = (NextPosition.operatorMinus(Position).Normailzed());
                double theta = Math.atan2(Direction.y, Direction.x);
                rotation = (float) Math.toDegrees(theta);

                Position.operatorPlusEqual(Direction.operatorTimes(movespeed).operatorTimes(dt));
            }

            UpdateFSM();
        }
    }

    public void UpdateFSM()
    {
        if (NextPosition.operatorMinus(Position).Length() <= 5) {
            currentstate = AI_STATE.IDLE_STATE;
        }

        else {
            currentstate = AI_STATE.WALK_STATE;
        }
    }

    public void Draw(Canvas canvas)
    {
        if(active) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
            canvas.drawBitmap(image, matrix, null);
        }
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
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

    public Vector2 getNextPosition() {
        return NextPosition;
    }

    public void setNextPosition(Vector2 nextPosition) {
        NextPosition = nextPosition;
    }

    public Vector<Vector2> getWaypoints() {
        return Waypoints;
    }

    public void setWaypoints(Vector<Vector2> waypoints) {
        Waypoints = waypoints;
    }

    public int getWaypointIndex() {
        return waypointIndex;
    }

    public void setWaypointIndex(int waypointIndex) {
        this.waypointIndex = waypointIndex;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public AI_TYPE getType() {
        return type;
    }

    public void setType(AI_TYPE type) {
        this.type = type;
    }

    public AI_STATE getCurrentstate() {
        return currentstate;
    }

    public void setCurrentstate(AI_STATE currentstate) {
        this.currentstate = currentstate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEndofwaypoint() {
        return endofwaypoint;
    }

    public void setEndofwaypoint(boolean endofwaypoint) {
        this.endofwaypoint = endofwaypoint;
    }

}
