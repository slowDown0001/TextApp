package TextApp;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSearchWorker extends SwingWorker<LinkedHashMap<Integer, String>,Void>
{
    private JTextField textField;
    private JTextArea textArea;

    private String text, string;
    private LinkedHashMap<Integer, String> indicesMap = new LinkedHashMap<>();
    private Pattern regexPattern = null;

    public RegexSearchWorker(JTextField textField, JTextArea textArea) {
        this.textField = textField;
        this.textArea = textArea;
    }

    protected LinkedHashMap<Integer, String> doInBackground() throws Exception {

        text = textArea.getText();
        string = textField.getText();

        Matcher rm = regexPattern.compile(string).matcher(text);
        while (rm.find())
        {
            indicesMap.put(rm.start(), rm.group());
        }
        System.out.println((indicesMap));
        return indicesMap;
    }

    protected void done()
    {
        try
        {
            if(indicesMap.isEmpty())
                return;
            Map.Entry<Integer, String> firstEntry = indicesMap.entrySet().stream().findFirst().get();
            int firstIndex = firstEntry.getKey();
            String firstMatch = firstEntry.getValue();

            System.out.println(firstIndex + " = " + firstMatch);

            textArea.setCaretPosition(firstIndex + firstMatch.length());
            textArea.select(firstIndex, firstIndex + firstMatch.length());
            textArea.grabFocus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
