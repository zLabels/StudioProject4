package com.spook.defenseofelements;

import android.graphics.Bitmap;

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
    private Bitmap Image;
    private GRID_TYPE type;

    public GridNode() {
        BoundingBox = new AABB2D(new Vector2(0.0f,10.0f), 10.0f,10.0f);
        Image = null;
        type = GRID_TYPE.GT_FREE;
    }

    public GridNode(AABB2D boundingBox, Bitmap image, GRID_TYPE type) {
        BoundingBox = boundingBox;
        Image = image;
        //Bitmap.createScaledBitmap(Image,1,1,false);
        this.type = type;
    }

    public GRID_TYPE getType() {
        return type;
    }

    public void setType(GRID_TYPE type) {
        this.type = type;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public AABB2D getBoundingBox() {
        return BoundingBox;
    }

    public void setBoundingBox(AABB2D boundingBox) {
        BoundingBox = boundingBox;
    }

}
