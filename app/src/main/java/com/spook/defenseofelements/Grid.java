package com.spook.defenseofelements;

/**
 * Created by Princeton on 17/2/2016.
 */
public class Grid {
    AABB2D[] RowList;
    int ArrayNumber;

    public Grid(int RowNumber) {
        this.RowList = new AABB2D[RowNumber];
        for(int i = 0; i < RowNumber; ++i)
        {
            RowList[i] = new AABB2D(new Vector2(10.0f,10.0f),10.0f);
        }
        this.ArrayNumber = RowNumber;
    }

    public AABB2D[] getRowList() {
        return RowList;
    }

    public int getArrayNumber() {
        return ArrayNumber;
    }

    public void setArrayNumber(int arrayNumber) {
        ArrayNumber = arrayNumber;
    }

}
