package com.spook.defenseofelements;

/**
 * Created by Princeton on 17/2/2016.
 */
public class AABB2D {
    public Vector2 CenterPoint;    //Center point of the box
    float size; //Size of box
    public Vector2 Minimum;
    public Vector2 Maximum;

    public AABB2D(Vector2 centerPoint, float size) {
        CenterPoint = centerPoint;
        this.size = size;
        float dividedSize = size * 0.5f;
        Minimum.Set(centerPoint.x - dividedSize, centerPoint.y - dividedSize);
        Maximum.Set(centerPoint.x + dividedSize, centerPoint.y + dividedSize);
    }

    public Vector2 getCenterPoint() {
        return CenterPoint;
    }

    public void setCenterPoint(Vector2 centerPoint) {
        CenterPoint = centerPoint;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean CheckIntersect(AABB2D box)
    {
        if (this.Maximum.x > box.Minimum.x ||
                this.Maximum.y > box.Minimum.y ||
                this.Minimum.x < box.Maximum.x ||
                this.Minimum.y < box.Maximum.y)
        {
            return true;
        }
        return false;
    }

    public boolean CheckIntersect(Vector2 pos)
    {
        if(pos.x > this.Minimum.x && pos.x < this.Maximum.x ||
                pos.y > this.Minimum.y && pos.y < this.Minimum.y)
        {
            return true;
        }
        return false;
    }
}
