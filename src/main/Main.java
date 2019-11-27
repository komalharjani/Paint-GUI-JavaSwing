package main;

import guiDelegate.Delegate;
import model.Model;

import javax.swing.*;

/**
 * Main creates a new Model and Delegate
 */
public class Main {

    public static void main(String[] args){
        Model model = new Model();
        new Delegate(model);
    }
}
