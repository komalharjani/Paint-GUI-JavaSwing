package model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class Model implements Serializable {

    private PropertyChangeSupport notifier;

    ArrayList<Shape> list = new ArrayList<>();
    Stack<Shape> undo = new Stack();
    Stack<Shape> redo = new Stack();

    public Model(){
        notifier = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    public void setList(ArrayList<Shape> list) {
        this.list = list;
    }

    public void setUndo(Stack<Shape> undo) {
        this.undo = undo;
    }

    public void setRedo(Stack<Shape> redo) {
        this.redo = redo;
    }

    public ArrayList<Shape> getList() {
        return list;
    }

    public Stack<Shape> getUndo() {
        return undo;
    }

    public Stack<Shape> getRedo() {
        return redo;
    }
}