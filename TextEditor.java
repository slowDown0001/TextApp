package TextApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class TextEditor extends JFrame {
    private final ImageIcon iconOpen, iconSave, iconLens, arrowLeft, arrowRight;
    private final JFileChooser jfc = new JFileChooser();
    JCheckBox regexCheckBox = new JCheckBox("Use regex");
    private int counter, regexCounter;
    private boolean isSimpleSearch, isRegexSearch;
    private ArrayList<Integer> indices;
    private LinkedHashMap<Integer, String> indicesMap;
    private String string;
    private JTextArea textArea;


    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setTitle("Text Editor");
        iconOpen = new ImageIcon("iconOpen.png");
        iconSave = new ImageIcon("iconSave.png");
        iconLens = new ImageIcon("iconLens.png");
        arrowLeft = new ImageIcon("arrowLeft.png");
        arrowRight = new ImageIcon("arrowRight.png");


        getContentPane().setLayout(new BorderLayout(10, 10));
        initComponents();

        setVisible(true);
    }

    public void initComponents() {

        //Scrollable textArea
        textArea = new JTextArea();
        textArea.setName("TextArea");
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");

        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setMargin(scrollableTextArea, 2, 50, 50, 50);
        add(scrollableTextArea);

        //text field Search
        JTextField searchField = new JTextField();
        searchField.setName("SearchField");
        forceSize(searchField, 200, 27);
        //code from https://mkyong.com/swing/java-swing-jfilechooser-example/

        regexCheckBox.setName("UseRegExCheckbox");
        jfc.setName("FileChooser");


        /*--------------------------------------Search------------------------------------------------------------------------*/
        /*--------------------------------------Initial-Search----------------------------------------------------------------*/
        JButton search = new JButton(iconLens);
        search.setName("StartSearchButton");
        forceSize(search, 32, 32);
        search.addActionListener(actionEvent -> searchWithWorkers(searchField));
        /*--------------------------------------Left-button-------------------------------------------------------------------*/
        JButton left = new JButton(arrowLeft);
        left.setName("PreviousMatchButton");
        forceSize(left, 32, 32);
        left.addActionListener(actionEvent -> {
            if (regexCheckBox.isSelected())
                doRegexLeftSearch();
            else
                doSimpleLeftSearch(searchField);
        });
        /*--------------------------------------Right-button------------------------------------------------------------------*/
        JButton right = new JButton(arrowRight);
        right.setName("NextMatchButton");
        forceSize(right, 32, 32);
        right.addActionListener(actionEvent -> {
            if (regexCheckBox.isSelected())
                doRegexRightSearch();
            else
                doSimpleRightSearch(searchField);
        });


        /*--------------------------------------File-system-------------------------------------------------------------------*/
        /*--------------------------------------Load-button-------------------------------------------------------------------*/

        //Load button loads text from the file. Filename is obtained from the textArea
        JButton load = new JButton(iconOpen);
        load.setName("OpenButton");
        forceSize(load, 32, 32);
        load.addActionListener(actionEvent -> loadFile());
        /*--------------------------------------Load-button-------------------------------------------------------------------*/
        /*--------------------------------------Save-button-------------------------------------------------------------------*/
        //Save button
        JButton save = new JButton(iconSave);
        save.setName("SaveButton");
        forceSize(save, 32, 32);
        save.addActionListener(actionEvent -> saveFile());

        /*String filePath = searchField.getText();
            File fileName = new File(filePath);
            BufferedWriter outFile = null;
            try {
                outFile = new BufferedWriter(new FileWriter(fileName));

                textArea.write(outFile);

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (outFile != null) {
                    try {
                        outFile.close();
                    } catch (IOException e) {
                    }
                }
            }*/




        /*--------------------------------------Top-Panel---------------------------------------------------------------------*/
        //top panel with searchField and buttons
        JPanel panel = new JPanel();
        setMargin(panel, 10, 50, 10, 45);
        add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel.add(load);
        panel.add(save);
        panel.add(searchField);
        panel.add(search);
        panel.add(left);
        panel.add(right);
        panel.add(regexCheckBox);
        /*--------------------------------------Panel-------------------------------------------------------------------------*/


        /*--------------------------------------Menus-------------------------------------------------------------------------*/
        //building a menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);


        //openMenuItem
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        openMenuItem.addActionListener(actionEvent -> loadFile());

        //saveMenuItem
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(actionEvent -> saveFile());

        //exitMenuItem
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(actionEvent -> dispose());

        //adding items to a menu
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        //Search menu

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem startSearch = new JMenuItem("Start search");
        startSearch.setName("MenuStartSearch");
        startSearch.addActionListener(actionEvent -> searchWithWorkers(searchField));

        //Previous search
        JMenuItem previousSearch = new JMenuItem("Previous search");
        previousSearch.setName("MenuPreviousMatch");
        previousSearch.addActionListener(actionEvent -> {
            if (regexCheckBox.isSelected())
                doRegexLeftSearch();
            else
                doSimpleLeftSearch(searchField);
        });


        //next match
        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("MenuNextMatch");
        nextMatch.addActionListener(actionEvent -> {
            if (regexCheckBox.isSelected())
                doRegexRightSearch();
            else
                doSimpleRightSearch(searchField);
        });

        //use REGEX
        JMenuItem useRegex = new JMenuItem("Use Regex");
        useRegex.setName("MenuUseRegExp");
        useRegex.addActionListener(actionEvent -> regexCheckBox.setSelected(!regexCheckBox.isSelected()));


        searchMenu.add(startSearch);
        searchMenu.add(previousSearch);
        searchMenu.add(nextMatch);
        searchMenu.add(useRegex);

        //adding menu to a menu bar
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);

    }

    /*------------------------------------------Methods-------------------------------------------------------------------*/
    //To force frame change its components size
    public void forceSize(JComponent component, int width, int height) {
        Dimension d = new Dimension(width, height);
        component.setMinimumSize(d);/*from  ww w .j ava  2 s .  c  o  m*/
        component.setMaximumSize(d);
        component.setPreferredSize(d);
    }


    //set margins works well with JScrollPan objects
    public void setMargin(JComponent aComponent, int aTop,
                          int aRight, int aBottom, int aLeft) {

        Border border = aComponent.getBorder();

        Border marginBorder = new EmptyBorder(new Insets(aTop, aLeft,
                aBottom, aRight));//from   w ww. j  a va2s.  c  o m
        aComponent.setBorder(border == null ? marginBorder
                : new CompoundBorder(marginBorder, border));
    }


    //not used, could be an option for writing files
    public void writeInFile(File file, String text) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(text);
        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }

    }

    //not used, checks all filenames in the project directory
    public void fileNamesChecker() {
        File file = new File(".");
        for (String fileNames : file.list()) System.out.println(fileNames);
    }

    /*-------------------------------------Highlighting-------------------------------------------------------------------*/
    public void highlightText(int start, int length) {

        textArea.setCaretPosition(start + length);
        textArea.select(start, start + length);
        textArea.grabFocus();

    }

    /*-------------------------------------Searching------------------------------------------------------------------*/
    public void searchWithWorkers(JTextField searchField) {
        if (!regexCheckBox.isSelected()) {
            isSimpleSearch = true;
            isRegexSearch = false;
            SimpleSearchWorker ssw = new SimpleSearchWorker(searchField, textArea);
            ssw.execute();
            try {
                indices = ssw.get();
                counter = 0;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        } else if (regexCheckBox.isSelected()) {
            isSimpleSearch = false;
            isRegexSearch = true;
            RegexSearchWorker rsw = new RegexSearchWorker(searchField, textArea);
            rsw.execute();
            try {
                indicesMap = rsw.get();
                regexCounter = 0;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void doSimpleRightSearch(JTextField searchField) {
        string = searchField.getText();

        //simple search performing here
        if (isSimpleSearch && indices != null) {
            if (!indices.isEmpty() && (counter + 1) < indices.size()) {
                counter++;
                highlightText(indices.get(counter), string.length());

            } else if ((counter + 1) == indices.size()) {
                counter = 0;
                highlightText(indices.get(counter), string.length());
            }
        }
    }

    public void doRegexRightSearch() {
        //REGEX search performing here
        System.out.println(regexCheckBox.isSelected());
        if (isRegexSearch && indicesMap != null) {
            if (!indicesMap.isEmpty()) {
                regexCounter++;
                Map.Entry<Integer, String> currentElement = indicesMap.entrySet().stream().skip(regexCounter).findFirst().orElse(null);
                if (currentElement != null) {
                    int currentIndex = currentElement.getKey();
                    String currentMatch = currentElement.getValue();
                    highlightText(currentIndex, currentMatch.length());
                } else {
                    currentElement = indicesMap.entrySet().stream().findFirst().orElse(null);
                    int currentIndex = currentElement.getKey();
                    String currentMatch = currentElement.getValue();
                    highlightText(currentIndex, currentMatch.length());
                    regexCounter = 0;
                }
            }
        }
    }


    public void doSimpleLeftSearch(JTextField searchField) {
        string = searchField.getText();

        //simple search performing here
        if (isSimpleSearch && indices != null) {
            if (!indices.isEmpty() && counter > 0) {
                counter--;
                highlightText(indices.get(counter), string.length());
            } else if (counter == 0) {
                counter = indices.size() - 1;
                highlightText(indices.get(counter), string.length());
            }
        }
    }

    public void doRegexLeftSearch() {
        //REGEX search performing here
        if (isRegexSearch && indicesMap != null) {
            if (!indicesMap.isEmpty()) {
                regexCounter--;
                System.out.println(regexCounter);
                Map.Entry<Integer, String> currentElement;
                if (regexCounter < 0) {
                    currentElement = indicesMap.entrySet().stream().skip(indicesMap.size() - 1).findFirst().orElse(null);
                    int currentIndex = currentElement.getKey();
                    String currentMatch = currentElement.getValue();
                    highlightText(currentIndex, currentMatch.length());
                    regexCounter = indicesMap.size() - 1;
                    System.out.println(regexCounter);
                } else {
                    currentElement = indicesMap.entrySet().stream().skip(regexCounter).findFirst().orElse(null);
                    int currentIndex = currentElement.getKey();
                    String currentMatch = currentElement.getValue();
                    highlightText(currentIndex, currentMatch.length());
                }
            }
        }
    }

    /*-------------------------------------Save/Load----------------------------------------------------------------------*/
    public void loadFile() {

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            textArea.setText("");
            if (selectedFile.exists()) {
                textArea.setText(null);
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    textArea.read(reader, "File");
                } catch (IOException exp) {
                    exp.printStackTrace();
                }
            }
        }
    }

    public void saveFile() {
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            try (FileWriter fw = new FileWriter(jfc.getSelectedFile())) {
                fw.write(textArea.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
