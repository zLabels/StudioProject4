package com.spook.defenseofelements;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Princeton on 18/2/2016.
 */
public class GridNode
{
    public enum GRID_TYPE
    {
        GT_PATH,
        GT_FREE,
        GT_OCCUPIED,
    };
    public AABB2D BoundingBox;
    private Rect sourceRect;
    private GRID_TYPE type;
    int ID;

    public GridNode() {
        BoundingBox = new AABB2D(new Vector2(0.0f,10.0f), 10.0f,10.0f);
        sourceRect = null;
        type = GRID_TYPE.GT_FREE;
    }

    public GridNode(AABB2D boundingBox, Bitmap image, int col, int tileID, GRID_TYPE type) {
        BoundingBox = boundingBox;

        ID = tileID;

        int spriteWidth = image.getWidth() / col;
        int spriteHeight = image.getHeight();

        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);

        sourceRect.left = ID * spriteWidth;
        sourceRect.right = sourceRect.left + spriteWidth;

        this.type = type;
    }

    public GRID_TYPE getType() {
        return type;
    }

    public void setType(GRID_TYPE type) {
        this.type = type;
    }

    public AABB2D getBoundingBox() {
        return BoundingBox;
    }

    public void setBoundingBox(AABB2D boundingBox) {
        BoundingBox = boundingBox;
    }

    public Rect getSourceRect() {
        return sourceRect;
    }

    public void setSourceRect(Rect sourceRect) {
        this.sourceRect = sourceRect;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
