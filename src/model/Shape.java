package model;
import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements Serializable {

    private int firstX, firstY, lastX, lastY;
    private Color colour;
    private boolean fill;

    /**
     * Constructor for a Shape --> which includes pressed(x,y) and released(x,y) co-ordinates
     * The colour from JColourChooser and whether fill is selected
     * All shapes implement extent this class
     * @param firstX
     * @param firstY
     * @param lastX
     * @param lastY
     * @param colour
     * @param fill
     */
    public Shape(int firstX, int firstY, int lastX, int lastY, Color colour, boolean fill) {
        this.firstX = firstX;
        this.firstY = firstY;
        this.lastX = lastX;
        this.lastY = lastY;
        this.colour = colour;
        this.fill = fill;
    }

    public int firstX() {
        return firstX;
    }

    public int firstY() {
        return firstY;
    }

    public int lastX() {
        return lastX;
    }

    public int lastY() {
        return lastY;
    }

    public int getWidth() {
        return Math.abs(firstX() - lastX());
    }

    public int getHeight() {
        return Math.abs(firstY() - lastY());
    }

    public boolean getFill() {
        return fill;
    }

    public Color getColour() {
        return colour;
    }
}




