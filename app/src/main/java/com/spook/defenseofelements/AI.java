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
    }

    float health;
    float movespeed;
    float rotation;
    Vector2 Position;
    Vector2 Direction;
    Vector2 NextPosition;
    Vector<Vector2> Waypoints;
    int waypointIndex;
    Bitmap image;
    AI_TYPE type;

    public AI(Vector2 Pos, Vector<Vector2> waypoints, Bitmap mesh, AI_TYPE ai_type)
    {
        this.Position = Pos;
        this.Direction.Set(0, 1);
        this.Waypoints = waypoints;
        this.NextPosition = this.Waypoints.get(0);
        this.image = mesh;
        this.type = ai_type;
        this.rotation = 0;
        this.waypointIndex = 0;

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
        if(NextPosition.operatorMinus(Position).Length() <= 5)
        {
            Position = NextPosition;
            NextPosition = Waypoints.get(++waypointIndex);
        }

        // Update AI direction
        Direction = (NextPosition.operatorMinus(Position).Normailzed());
        double theta = Math.atan2(Direction.y, Direction.x);
        rotation = (float) Math.toDegrees(theta);

        //Position += movespeed * Direction
    }

    public void Draw(Canvas canvas)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation, image.getWidth() / 2, image.getHeight() / 2);
        canvas.drawBitmap(image, matrix, null);
    }
}
