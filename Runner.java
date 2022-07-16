package TextApp;

import AdvancedCapture.ScreenCapture;

import javax.swing.*;

public class Runner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TextEditor();
            }
        });
    }
}
