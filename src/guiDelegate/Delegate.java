package guiDelegate;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import model.*;
import model.Rectangle;
import model.Shape;


/**
 * This class makes changes to the model reflecting on user events through mouse-listener
 * It  listens for user input events translates these to appropriate calls to methods to build shapes using a case switch
 * The class implements PropertyChangeListener in order to permit it to be added as an observer of the model class.
 * This class also allows users to load and save files into the GUI*
 */
public class Delegate extends JFrame implements PropertyChangeListener {

    private static final int FRAME_HEIGHT = 900;
    private static final int FRAME_WIDTH = 900;
    private static final int TEXT_HEIGHT = 10;
    private static final int TEXT_WIDTH = 10;


    private JToolBar toolbar;
    private JTextField inputField;
    private JPanel panel;
    private JLabel label;

    //Buttons
    private JButton move;
    private JButton rectangle;
    private JButton lines;
    private JButton ellipse;
    private JButton square;
    private JButton circle;
    private JButton triangle;
    private JButton hexagon;
    private JButton colourButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton clear;
    private JCheckBox fillCheck;

    private Color colour;

    //co-ordinates
    private int xPressed;
    private int yPressed;
    private int xReleased;
    private int yReleased;

    private String currButton;
    private Shape currShapeObject;
    private Color currColor;
    private boolean fill;

    private JScrollPane outputPane;
    private JTextArea outputField;
    private JMenuBar menu;
    private Model model;

    Point midPoint, endDrag, startDrag;

    public Delegate(Model model) {
        this.model = model;
        menu = new JMenuBar();
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        inputField = new JTextField(TEXT_WIDTH);
        outputField = new JTextArea(TEXT_WIDTH, TEXT_HEIGHT);

        outputField.setEditable(false);
        outputPane = new JScrollPane(outputField);

        panel = new JPanel();
        getContentPane().add(panel);
        setupComponents();
        setupToolbar();
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);

        currButton = "null";

        currColor = colour;
        fill = false;

        label = new JLabel("");
        draw();

        //This allows users to resize their screen without losing drawings on the screen
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                paintAll(getGraphics());
                reDraw();
            }
        });

    }


    public void draw() {

        panel.setFocusable(true);
        panel.grabFocus();
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //first x,y co-ordinates
                xPressed = e.getX();
                yPressed = e.getY();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //first x,y co-ordinates
                xPressed = e.getX();
                yPressed = e.getY();
                startDrag = new Point(e.getX(), e.getY());
                endDrag = startDrag;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //co-ordinates when released
                xReleased = e.getX();
                yReleased = e.getY();

                //Switch functions to draw shape depending on which button is clicked
                switch (currButton) {
                    case "Line":
                        Line();
                        break;
                    case "Rectangle":
                        Rectangle();
                        break;
                    case "Circle":
                        Circle();
                        break;
                    case "Square":
                        Square();
                        break;
                    case "Hexagon":
                        Hexagon();
                        break;
                    case "Triangle":
                        Triangle();
                        break;
                    case "Ellipse":
                        Ellipse();
                        break;
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                xPressed = e.getX();
                yPressed = e.getY();
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endDrag = new Point(e.getX(), e.getY());
                xReleased = e.getX();
                yReleased = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        model.addObserver(this);
    }

    /**
     * Method to a line using co-ordinates from mouse listener (press(x,y) and released(x,y)
     * This uses the built-in class drawLine()
     */
    public void Line() {
        Graphics2D line = (Graphics2D) panel.getGraphics();
        currShapeObject = new Line(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        line.setColor(colour);
        line.drawLine(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getHeight());
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
    }

    /**
     * Method to re-paint a line using stored co-ordinates in list
     * @param line
     * @param storedLine
     */
    public void lineRepaint(Graphics2D line, Line storedLine) {
        currShapeObject = new Line(storedLine.firstX(), storedLine.firstY(), storedLine.lastX(), storedLine.lastY(), storedLine.getColour(), storedLine.getFill());
        line.setColor(storedLine.getColour());
        line.drawLine(storedLine.firstX(), storedLine.firstY(), storedLine.getWidth(), storedLine.getHeight());
    }

    /**
     * Method to a rectangle using co-ordinates from mouse listener (press(x,y) and released(x,y)
     * This uses the built-in class drawRect()
     */
    public void Rectangle() {
        Graphics2D rect = (Graphics2D) panel.getGraphics();
        currShapeObject = new Rectangle(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
        if (currShapeObject.getFill()) {
            rect.setColor(colour);
            rect.fillRect(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getHeight());
        } else {
            panel.getGraphics().drawRect(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getHeight());
        }
    }

    /**
     * Method to re-paint a rectangle using stored co-ordinates in list
     * @param rect
     * @param storedRect
     */
    public void rectangleRepaint(Graphics2D rect, Rectangle storedRect) {
        currShapeObject = new Rectangle(storedRect.firstX(), storedRect.firstY(), storedRect.lastX(), storedRect.lastY(), storedRect.getColour(), storedRect.getFill());
        if (currShapeObject.getFill()) {
            rect.fillRect(storedRect.firstX(), storedRect.firstY(), storedRect.getWidth(), storedRect.getHeight());
        } else {
            panel.getGraphics().drawRect(storedRect.firstX(), storedRect.firstY(), storedRect.getWidth(), storedRect.getHeight());
        }
    }

    /**
     * Method to a circle using co-ordinates from mouse listener (press(x,y) and released(x,y)
     * This uses the built-in class drawOval()
     * This method uses pressed events and same height and width of drag so that the shape retains a perfect circle
     */
    public void Circle() {
        Graphics2D circle = (Graphics2D) panel.getGraphics();
        currShapeObject = new Circle(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
        if (currShapeObject.getFill()) {
            circle.setColor(colour);
            circle.fillOval(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getWidth());
        } else {
            getGraphics().drawOval(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getWidth());
        }
    }

    /**
     * Method to re-paint a rectangle using stored co-ordinates in list
     * @param circle
     * @param storedCircle
     */
    public void circleRepaint(Graphics2D circle, Circle storedCircle) {
        currShapeObject = new Circle(storedCircle.firstX(), storedCircle.firstY(), storedCircle.lastX(), storedCircle.lastY(), storedCircle.getColour(), storedCircle.getFill());
        if (currShapeObject.getFill()) {
            circle.fillOval(storedCircle.firstX(), storedCircle.firstY(), storedCircle.getWidth(), storedCircle.getWidth());
        } else {
            getGraphics().drawOval(storedCircle.firstX(), storedCircle.firstY(), storedCircle.getWidth(), storedCircle.getWidth());
        }
    }

    /**
     * Method to draw an ellipse using co-ordinates from mouse listener (press(x,y) and released(x,y)
     * This uses the built-in class drawOval() which takes in press(x,y) and height and width of drag
     */
    public void Ellipse() {
        Graphics2D ellipse = (Graphics2D) panel.getGraphics();
        currShapeObject = new Ellipse(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
        if (currShapeObject.getFill()) {
            ellipse.setColor(colour);
            ellipse.fillOval(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getHeight());
        } else {
            getGraphics().drawOval(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getHeight());
        }
    }

    /**
     * Method to re-paint an ellipse using stored co-ordinates in list
     * @param ellipse
     * @param storedEllipse
     */
    public void ellipseRepaint(Graphics2D ellipse, Ellipse storedEllipse) {
        currShapeObject = new Ellipse(storedEllipse.firstX(), storedEllipse.firstY(), storedEllipse.lastX(), storedEllipse.lastY(), storedEllipse.getColour(), storedEllipse.getFill());
        if (currShapeObject.getFill()) {
            ellipse.fillOval(storedEllipse.firstX(), storedEllipse.firstY(), storedEllipse.getWidth(), storedEllipse.getHeight());
        } else {
            getGraphics().drawOval(storedEllipse.firstX(), storedEllipse.firstY(), storedEllipse.getWidth(), storedEllipse.getHeight());
        }
    }

    /**
     * Method to draw a square using co-ordinates from mouse listener (press(x,y) and released(x,y)
     * This method uses pressed events and same height and width of drag so that the shape retains a perfect square
     */
    public void Square() {
        Graphics2D square = (Graphics2D) panel.getGraphics();
        currShapeObject = new Square(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
        if (currShapeObject.getFill()) {
            square.setColor(colour);
            square.fillRect(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getWidth());
        } else {
            panel.getGraphics().drawRect(currShapeObject.firstX(), currShapeObject.firstY(), currShapeObject.getWidth(), currShapeObject.getWidth());
        }
    }

    /**
     * Method to re-paint a square using stored co-ordinates in list
     * @param square
     * @param storedSquare
     */
    public void squareRepaint(Graphics2D square, Square storedSquare) {
        currShapeObject = new Square(storedSquare.firstX(), storedSquare.firstY(), storedSquare.lastX(), storedSquare.lastY(), storedSquare.getColour(), storedSquare.getFill());
        if (currShapeObject.getFill()) {
            square.fillRect(storedSquare.firstX(), storedSquare.firstY(), storedSquare.getWidth(), storedSquare.getWidth());
        } else {
            panel.getGraphics().drawRect(storedSquare.firstX(), storedSquare.firstY(), storedSquare.getWidth(), storedSquare.getWidth());
        }
    }

    /**
     * This method draws a triangle using mouse pressed events and built-in class drawPolygon
     * source of math://https://www.daniweb.com/programming/software-development/threads/509447/draw-triangle-on-mousedragged-event
     */
    public void Triangle() {
        Graphics2D triangle = (Graphics2D) panel.getGraphics();
        currShapeObject = new Triangle(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        model.getUndo().push(currShapeObject);
        if (startDrag.x > endDrag.x) {
            midPoint = new Point((endDrag.x + (Math.abs(startDrag.x - endDrag.x) / 2)), xReleased);
        }
        else {
            midPoint = new Point((endDrag.x - (Math.abs(startDrag.x - endDrag.x) / 2)), yReleased);
        }
        int[] xs = {startDrag.x, endDrag.x, midPoint.x};
        int[] ys = {startDrag.y, startDrag.y, midPoint.y};
        if (currShapeObject.getFill()) {
            triangle.setColor(colour);
            triangle.fillPolygon(xs, ys, 3);
        } else {
            triangle.drawPolygon(xs, ys, 3);
        }
    }

    /**
     * Re-draws triangle based on stored-coordinates in list
     * @param triangle
     * @param storedTriangle
     */
    public void triangleRepaint(Graphics2D triangle, Triangle storedTriangle) {
        currShapeObject = new Triangle(storedTriangle.firstX(), storedTriangle.firstY(), storedTriangle.lastX(), storedTriangle.lastY(), storedTriangle.getColour(), storedTriangle.getFill());
        if (storedTriangle.firstX() > storedTriangle.lastX())
            midPoint = new Point((storedTriangle.lastX() + (Math.abs(storedTriangle.firstX() - storedTriangle.lastX()) / 2)), xReleased);
        else
            midPoint = new Point((storedTriangle.lastX() - (Math.abs(storedTriangle.firstX() - storedTriangle.lastX()) / 2)), yReleased);
        int[] xs = {storedTriangle.firstX(), storedTriangle.lastX(), midPoint.x};
        int[] ys = {storedTriangle.firstY(), storedTriangle.firstY(), midPoint.y};
        if (currShapeObject.getFill()) {
            triangle.fillPolygon(xs, ys, 3);
        } else {
            triangle.drawPolygon(xs, ys, 3);
        }
    }

    /**
     * This method draws a hexagon using mouse pressed events and built-in class drawPolygon
     * source of math: //https://stackoverflow.com/questions/35853902/drawing-hexagon-using-java-error
     */
    public void Hexagon() {
        Graphics2D hexagon = (Graphics2D) panel.getGraphics();
        currShapeObject = new Hexagon(xPressed, yPressed, xReleased, yReleased, colour, fillCheck.isSelected());
        Polygon polygon = new Polygon();
        model.getUndo().push(currShapeObject);
        model.getList().add(currShapeObject);
        for (int i = 0; i < 6; i++) {
            int xval = (int) (xPressed + ((xReleased-xPressed)/2) + (xReleased - xPressed)/2 * Math.cos(i * 2 * Math.PI / 6));
            int yval = (int) (yPressed + ((yReleased-yPressed)/2) + (yReleased - yPressed)/2 * Math.sin(i * 2 * Math.PI / 6));
            polygon.addPoint(xval, yval);
        }
        if (currShapeObject.getFill()) {
            hexagon.setColor(colour);
            hexagon.fillPolygon(polygon);
        }
        else {
            hexagon.drawPolygon(polygon);
        }
    }

    /**
     * Repaint hexagon based on stored co-ordinates in list
     * @param hexagon
     * @param storedHexagon
     */
    public void hexagonRepaint(Graphics2D hexagon, Hexagon storedHexagon) {
        currShapeObject = new Hexagon(storedHexagon.firstX(), storedHexagon.firstY(), storedHexagon.lastX(), storedHexagon.lastY(), storedHexagon.getColour(), storedHexagon.getFill());
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            int xval = (int) (storedHexagon.firstX() + ((storedHexagon.lastX()-storedHexagon.firstX())/2) + (storedHexagon.lastX() - storedHexagon.firstX())/2 * Math.cos(i * 2 * Math.PI / 6));
            int yval = (int) (storedHexagon.firstY()+ ((storedHexagon.lastY()-storedHexagon.firstY())/2) + (storedHexagon.lastY() - storedHexagon.firstY())/2 * Math.sin(i * 2 * Math.PI / 6));
            polygon.addPoint(xval, yval);
        }
        if (currShapeObject.getFill()) {
            hexagon.fillPolygon(polygon);
        }
        else {
            hexagon.drawPolygon(polygon);
        }
    }

    /**
     * This method redraws based on stored co-ordinates in list
     * It iterates in reverse so that it can match how values are stored in the undo stack
     */
    public void reDraw() {
        for (int i = model.getList().size() - 1; i >= 0; i--) {
            Graphics2D shape = (Graphics2D) panel.getGraphics();
            shape.setColor(model.getList().get(i).getColour());
            if (model.getList().get(i) instanceof Line) {
                lineRepaint(shape, (Line) model.getList().get((i)));
            } else if (model.getList().get(i) instanceof Square) {
                squareRepaint(shape, (Square) model.getList().get(i));
            } else if (model.getList().get(i) instanceof Rectangle) {
                rectangleRepaint(shape, (Rectangle) model.getList().get(i));
            } else if (model.getList().get(i) instanceof Ellipse) {
                ellipseRepaint(shape, (Ellipse) model.getList().get(i));
            } else if (model.getList().get(i) instanceof Circle) {
                circleRepaint(shape, (Circle) model.getList().get(i));
            } else if (model.getList().get(i) instanceof Triangle) {
                triangleRepaint(shape, (Triangle) model.getList().get(i));
            } else if (model.getList().get(i) instanceof Hexagon) {
                hexagonRepaint(shape, (Hexagon) model.getList().get(i));
            }
        }
    }

    /**
     * This method removes the last element from the stack and the list,
     * clears the panel, and then re-draws everything in the list to reproduce all
     * objects except for the last removed object
     * This method throws an exception if the Undo stack is empty
     */
    public void Undo() {
        if (model.getList().isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Nothing to Undo", "Empty Stack", JOptionPane.ERROR_MESSAGE);
        }
        else {
            model.getRedo().push(model.getUndo().lastElement());
            model.getList().remove(model.getUndo().lastElement());
            model.getUndo().pop();
            paintAll(getGraphics());
            reDraw();
        }
    }

    /**
     * This method removes the last element from the undo stack and puts it into the redo stack
     * The list is updated with the last element in the redo stack and then the panel is clears
     * and everything is re-drawn including the last removed item
     * This method throws an exception if the Redo stack is empty
     */
    public void Redo() {
        if (model.getRedo().isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Nothing to Redo", "Empty Stack", JOptionPane.ERROR_MESSAGE);
        } else {
            model.getUndo().push(model.getRedo().lastElement());
            model.getList().add(model.getRedo().lastElement());
            model.getRedo().pop();
            paintAll(getGraphics());
            reDraw();
        }
    }

    /**
     * This clears the entire panel and the undo and redo stack.
     * This throws an exception to confirm the user wants to clear as they cannot undo.
     */
    public void Clear() {
        int answer = JOptionPane.showConfirmDialog(panel, "You cannot Undo. Continue?");
        if (answer == JOptionPane.YES_OPTION) {
            model.getUndo().clear();
            model.getRedo().clear();
            model.getList().removeAll(model.getList());
            paintAll(getGraphics());
        } else if (answer == JOptionPane.NO_OPTION) {
            reDraw();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        super.paintComponents(g);
        g.setColor(colour);
    }

    /**
     * Initialises the toolbar to contain the buttons, label, input field, etc. and adds the toolbar to the main frame.
     * Listeners are created for the buttons and text field which translate user events to model object method calls (controller aspect of the delegate)
     */
    private void setupToolbar() {

        rectangle = new JButton("Rectangle");
        rectangle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Rectangle";
            }
        });

        ellipse = new JButton("Ellipse");
        ellipse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Ellipse";
            }
        });

        lines = new JButton("Line");
        lines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Line";
            }
        });

        square = new JButton("Square");
        square.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Square";
            }
        });

        circle = new JButton("Circle");
        circle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Circle";
            }
        });

        triangle = new JButton("Triangle");
        triangle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Triangle";
            }
        });


        hexagon = new JButton("Hexagon");
        hexagon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Hexagon";
            }
        });

        undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Undo();
            }
        });

        redoButton = new JButton("Redo");
        redoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Redo();
            }
        });

        move = new JButton("Move");
        move.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currButton = "Move";
            }
        });

        clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });

        fillCheck = new JCheckBox("Fill");
        fillCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (fillCheck.isSelected()) {
                    fill = true;
                } else {
                    fill = false;
                }
            }
        });

        colourButton = new JButton("Colour");
        colourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colour = JColorChooser.showDialog(null, "Choose Colour", panel.getBackground());
            }
        });

        toolbar.add(rectangle);
        toolbar.add(lines);
        toolbar.add(ellipse);
        toolbar.add(circle);
        toolbar.add(square);
        toolbar.add(triangle);
        toolbar.add(hexagon);
        toolbar.add(undoButton);
        toolbar.add(redoButton);
        toolbar.add(move);
        toolbar.add(clear);
        toolbar.add(colourButton);
        toolbar.add(fillCheck);
        getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    /**
     * This method saves and loads the file onto the panel
     * This uses the built in class JFileChooser
     * saves it as a serializable object with a .ser extension
     * When it loads the file it retains the undo stack, redo stack and list of stored objects.
     */
    private void saveFile() {

        JMenu file = new JMenu("File");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");
        file.add(load);
        file.add(save);
        menu.add(file);


        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File f = new File("");
                JFileChooser j = new JFileChooser(f, FileSystemView.getFileSystemView());
                int fileChoice = j.showSaveDialog(null);
                if(fileChoice == JFileChooser.APPROVE_OPTION) {
                    f = new File(j.getSelectedFile().toString()+".ser");
                    try {
                        FileOutputStream fOut = new FileOutputStream(f);
                        ObjectOutputStream oos = new ObjectOutputStream(fOut);
                        oos.writeObject(model);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = new File("C:\\Users\\pc\\Documents\\New folder\\");
                JFileChooser j = new JFileChooser(f, FileSystemView.getFileSystemView());
                int fileChoice = j.showOpenDialog(null);

                if (fileChoice == JFileChooser.APPROVE_OPTION) {
                    f = j.getSelectedFile();
                    try {
                        FileInputStream fIn = new FileInputStream(f);
                        ObjectInputStream oos = new ObjectInputStream(fIn);
                         Model loadedModel = (Model) oos.readObject();
                         model.setList(loadedModel.getList());
                         model.setUndo(loadedModel.getUndo());
                         model.setRedo(loadedModel.getRedo());
                         paintAll(getGraphics());
                         reDraw();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        setJMenuBar(menu);
    }

    private void setupComponents() {
        saveFile();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void propertyChange(final PropertyChangeEvent event) {

        if (event.getSource() == model && event.getPropertyName().equals("theText")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    outputField.setText((String) event.getNewValue());
                }
            });
        }
    }

}



