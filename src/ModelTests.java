package Tests;

import static org.junit.Assert.*;

import guiDelegate.Delegate;
import model.Model;
import model.Rectangle;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;

public class ModelTests {

    private Model m;
    private Delegate d;
    private Shape Rectangle;

    @Before
    public void setup() {
        m = new Model();
        d = new Delegate(m);
    }

    @Test
    public void createRectangle() {
        d.Rectangle();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void createCircle()  {
        d.Circle();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void createHexagon()  {
        d.Hexagon();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void createLine()  {
        d.Line();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void createEllipse()  {
        d.Ellipse();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void createTriangle()  {
        d.Triangle();
        assertEquals(m.getList().size(), 1);
    }


    @Test
    public void createSquare()  {
        d.Square();
        assertEquals(m.getList().size(), 1);
    }

    @Test
    public void undoTest()  {
        d.Circle();
        d.Rectangle();
        d.Ellipse();
        d.Undo();
        assertEquals(m.getUndo().size(), 2);
        assertEquals(m.getRedo().size(), 1);
        assertEquals(m.getList().size(), 2);
    }

    @Test
    public void redoTest()  {
        d.Ellipse();
        d.Rectangle();
        d.Undo();
        d.Redo();
        assertEquals(m.getUndo().size(), 2);
        assertEquals(m.getRedo().size(), 0);
        assertEquals(m.getList().size(), 2);
    }

    @Test
    public void reDraw()  {
        d.Rectangle();
        d.Ellipse();
        d.Circle();
        d.reDraw();
        assertEquals(m.getList().size(), 3);
    }

}