package TextApp;

import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SimpleSearchWorker extends SwingWorker<ArrayList<Integer>,Void>
{
    private JTextField textField;
    private JTextArea textArea;

    private String string;
    private ArrayList<Integer> indices = new ArrayList<>();

    public SimpleSearchWorker(JTextField textField, JTextArea textArea) {
        this.textField = textField;
        this.textArea = textArea;

    }

    protected ArrayList<Integer> doInBackground() throws Exception {

        String text = textArea.getText();
        string = textField.getText();

        if(text.contains(string)){
            Matcher m = Pattern.compile("(?=(" + string + "))").matcher(text);
            while (m.find())
            {
                indices.add(m.start());
            }
            return indices;
        }
        return null;
    }

    protected void done()
    {
        try
        {
            if(indices.isEmpty())
                return;
            textArea.setCaretPosition(indices.get(0) + string.length());
            textArea.select(indices.get(0), indices.get(0) + string.length());
            textArea.grabFocus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
