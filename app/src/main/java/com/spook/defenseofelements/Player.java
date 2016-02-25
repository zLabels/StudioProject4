package com.spook.defenseofelements;

/**
 * Created by Malcolm on 24/2/2016.
 */
public class Player {

    public enum ELEMENT_TYPE{
        FIRE_TYPE,
        WATER_TYPE,
        WIND_TYPE,
        EARTH_TYPE,
    };

    int fireElement;
    int waterElement;
    int windElement;
    int earthElement;

    public Player()
    {
        fireElement = 1;
        waterElement = 0;
        windElement = 0;
        earthElement = 0;
    }

    public void AddtoElement(Element.ELEMENT_TYPE type, int value)
    {
        switch (type)
        {
            case FIRE_TYPE: fireElement += value;
                break;
            case WATER_TYPE: waterElement += value;
                break;
            case WIND_TYPE: windElement += value;
                break;
            case EARTH_TYPE: earthElement += value;
                break;
        }
    }

    public void MinusfromElement(Element.ELEMENT_TYPE type, int value)
    {
        switch (type)
        {
            case FIRE_TYPE: fireElement -= value;
                break;
            case WATER_TYPE: waterElement -= value;
                break;
            case WIND_TYPE: windElement -= value;
                break;
            case EARTH_TYPE: earthElement -= value;
                break;
        }
    }

    public void AddtoElements(int fire, int water, int wind, int earth)
    {
        fireElement += fire;
        waterElement += water;
        windElement += wind;
        earthElement += earth;
    }

    public void MinusfromElements(int firecost, int watercost, int windcost, int earthcost)
    {
        fireElement -= firecost;
        waterElement -= watercost;
        windElement -= windcost;
        earthElement -= earthcost;
    }

    public boolean CheckCanBuild(int firecost, int watercost, int windcost, int earthcost)
    {
        if(fireElement - firecost < 0)
        {
            return false;
        }

        else if(waterElement - watercost < 0)
        {
            return false;
        }

        else if(windElement - windcost < 0)
        {
            return false;
        }

        else if(earthElement - earthcost < 0)
        {
            return false;
        }

        return true;
    }

    public int getFireElement() {
        return fireElement;
    }

    public void setFireElement(int fireElement) {
        this.fireElement = fireElement;
    }

    public int getWaterElement() {
        return waterElement;
    }

    public void setWaterElement(int waterElement) {
        this.waterElement = waterElement;
    }

    public int getWindElement() {
        return windElement;
    }

    public void setWindElement(int windElement) {
        this.windElement = windElement;
    }

    public int getEarthElement() {
        return earthElement;
    }

    public void setEarthElement(int earthElement) {
        this.earthElement = earthElement;
    }

}
