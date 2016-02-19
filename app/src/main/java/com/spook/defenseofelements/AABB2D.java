package com.spook.defenseofelements;

/**
 * Created by Princeton on 17/2/2016.
 */
public class AABB2D {
    public Vector2 CenterPoint;    //Center point of the box
    float width; //width of box
    float height; // height of box
    public Vector2 Minimum = new Vector2(0.0f,0.0f);
    public Vector2 Maximum = new Vector2(0.0f,0.0f);
    public Vector2 TopLeft = new Vector2(0.0f,0.0f);

    public AABB2D(Vector2 centerPoint, float width, float height) {
        CenterPoint = centerPoint;
        this.width = width;
        this.height = height;
        float dividedSizeX = width * 0.5f;
        float dividedSizeY = height * 0.5f;
        Minimum.Set(centerPoint.x - dividedSizeX, centerPoint.y - dividedSizeY);
        Maximum.Set(centerPoint.x + dividedSizeX, centerPoint.y + dividedSizeY);
        TopLeft.Set(centerPoint.x - dividedSizeX,centerPoint.y - dividedSizeY);
    }

    public void SetAllData(Vector2 centerPoint, float width,float height)
    {
        CenterPoint = centerPoint;
        this.width = width;
        this.height = height;
        float dividedSizeX = width * 0.5f;
        float dividedSizeY = height * 0.5f;
        Minimum.Set(centerPoint.x - dividedSizeX, centerPoint.y - dividedSizeY);
        Maximum.Set(centerPoint.x + dividedSizeX, centerPoint.y + dividedSizeY);
        TopLeft.Set(centerPoint.x - dividedSizeX,centerPoint.y - dividedSizeY);
    }

    public Vector2 getTopLeft() {
        return TopLeft;
    }

    public void setTopLeft(Vector2 topLeft) {
        TopLeft = topLeft;
    }

    public Vector2 getCenterPoint() {
        return CenterPoint;
    }

    public void setCenterPoint(Vector2 centerPoint) {
        CenterPoint = centerPoint;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setSize(float width,float height) {
        this.width = width;
        this.height = height;
    }

    public boolean CheckIntersect(AABB2D box)
    {
        if (this.Maximum.x > box.Minimum.x &&
                this.Maximum.y > box.Minimum.y &&
                this.Minimum.x < box.Maximum.x &&
                this.Minimum.y < box.Maximum.y)
        {
            return true;
        }
        return false;
    }

    public boolean CheckIntersect(Vector2 pos)
    {
        if(pos.x > this.Minimum.x && pos.x < this.Maximum.x &&
                pos.y > this.Minimum.y && pos.y < this.Maximum.y)
        {
            return true;
        }
        return false;
    }
}
