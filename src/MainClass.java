import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainClass extends FrameTemplate {

    Font interFont;
    Font derivedFont;
    Font derivedFont2;
    File noteStorage;

    Note selectedNote;
    int scrollHeight;
    int currentScroll;
    JScrollBar scrollBar;

    public JTextField titleField;
    public JTextField searchField;
    public JTextArea textArea;

    JButton highBtn, midBtn, lowBtn;
    private ImageIcon emptyImg = resizeImageIcon(
            new ImageIcon("./res/icon/notebook.png"), 250, 250
    );
    private ImageIcon searchIcon = resizeImageIcon(
            new ImageIcon("./res/icon/search.png"), 20, 20
    );
    ArrayList<File> searchList = new ArrayList<>();
    String searchValue;

    // ================================================== Constructor ===========================================================//
    MainClass() {
        super("CodexPad", 1200, 720, true);
        try {
            noteStorage = new File("./NoteStorage"); // "Database" folder path
            noteStorage.mkdir(); // If somehow my Supreme Powerful Database didn't work I have to revive it then

            File inter_file = new File("./res/font/inter_SemiBold.ttf");
            File inter_file2 = new File("./res/font/inter.ttf");
            derivedFont = Font.createFont(Font.TRUETYPE_FONT, inter_file);
            derivedFont2 = Font.createFont(Font.TRUETYPE_FONT, inter_file2);
            interFont = derivedFont.deriveFont(14f);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, 404));
        sidePanel.setLayout(new BorderLayout());

        contentPanel.add(createHeader(), BorderLayout.NORTH);
        sidePanel.add(createSideBar(), BorderLayout.CENTER);
        sidePanel.add(createFilterTab(), BorderLayout.SOUTH);
        contentPanel.add(sidePanel, BorderLayout.WEST);
        contentPanel.add(createNoteEditor(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void refresh() {
        contentPanel.removeAll();
        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, 404));
        sidePanel.setLayout(new BorderLayout());

        contentPanel.add(createHeader(), BorderLayout.NORTH);
        sidePanel.add(createSideBar(), BorderLayout.CENTER);
        sidePanel.add(createFilterTab(), BorderLayout.SOUTH);
        contentPanel.add(sidePanel, BorderLayout.WEST);
        contentPanel.add(createNoteEditor(), BorderLayout.CENTER);
        revalidate();
        repaint();
        scrollBar.setValue(currentScroll);
    }

    // ================================================== Header ===============================================================//
    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
        panel.setPreferredSize(new Dimension(999, 50));
        panel.setBackground(contentPanel.getBackground());
        panel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,  new Color(36, 36, 36)));

        JLabel label = new JLabel("Notes");
        label.setPreferredSize(new Dimension(235, 30));
        label.setForeground(new Color(0xC4C4C4));

        JLabel searchLabel = new JLabel();
        searchLabel.setPreferredSize(new Dimension(20,20));
        searchLabel.setBorder(null);
        searchLabel.setIcon(searchIcon);

        searchField = new JTextField(searchValue);
        searchField.setOpaque(false);
        searchField.setForeground(new Color(0x9F9F9F));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,2,0, new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(0,5,0,5)
        ));
        searchField.setPreferredSize(new Dimension(getWidth()-405, 30));
        searchField.setCaretColor(new Color(0x4E4E4E));
        Font searchFont = derivedFont2.deriveFont(16f);
        searchField.setFont(searchFont);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchContent(); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchContent(); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchContent(); }

            private void searchContent() {
                searchList.clear();
                searchValue = searchField.getText();
                if(!searchField.getText().isEmpty()) {
                    for(File f : noteStorage.listFiles()) {
                        if(f.getName().toLowerCase().contains(searchField.getText().toLowerCase())) {
                            searchList.add(f);
                        }
                    }
                }
                refresh();
                searchField.requestFocus();
            }
        });

        Font f = derivedFont.deriveFont(18f);
        label.setFont(f);
        panel.add(label);
        panel.add(searchLabel);
        panel.add(searchField);

        JButton addNote = new RoundedButton("Add note", 15);
        addNote.setPreferredSize(new Dimension(100, 30));
        addNote.setBackground(new Color(0x0F6B7F));
        addNote.setForeground(new Color(0xC4C4C4));
        addNote.setFont(interFont);
        addNote.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(noteStorage.isDirectory()) {
                    try {

                        File[] list = noteStorage.listFiles();
                        int additional = 0;
                        for(int i = 0; i < list.length; i++) {
                            if(list[i].getName().contains("Untitled")) {
                                // Skip Files that actually have a name lmao
                                if(Integer.parseInt(String.valueOf(list[i].getName().charAt(9))) == additional) {
                                    additional++; // If it is somehow the same number then increment by one 🔥🔥🔥🔥
                                }else {
                                    break; // Found a suitable animatronic suit 💀💀💀
                                }
                            }
                        }

                        File newFile = new File(noteStorage.getPath()+"/Untitled("+additional+").txt");
                        if(newFile.createNewFile()) {
                            LocalDate now = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                            String nowStr = now.format(formatter);

                            FileWriter writer = new FileWriter(newFile);
                            writer.write(nowStr+"\n"); // Last modified
                            writer.write("High\n"); // Importance
                            writer.write("false\n"); // Pinned boolean

                            writer.close();
                            refresh();
                        }else {
                            // TODO : Show warning message that it failed lmao
                            new CustomDialog("Error...", 320, 110, "Unable to create new Note...");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                addNote.setBackground(new Color(0x0A90AE));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                addNote.setBackground(new Color(0x0F6B7F));
            }
        });
        panel.add(addNote);
        return panel;
    }

    // ================================================== Side Bar =============================================================//
    private JScrollPane createSideBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        panel.setPreferredSize(new Dimension(250, scrollHeight));
        panel.setBackground(new Color(23, 23, 27));
        panel.setBorder(BorderFactory.createMatteBorder(0,0,0,1,  new Color(36, 36, 36)));

        if(noteStorage.isDirectory()) {

            File[] filteredArray;
            if(!searchList.isEmpty()) {
                filteredArray = new File[searchList.size()];
                filteredArray = searchList.toArray(filteredArray);
            }else {
                filteredArray = noteStorage.listFiles();
            }

            if(!searchField.getText().isEmpty() && searchList.isEmpty()) {
                filteredArray = new File[0];
            }

            scrollHeight = filteredArray.length * 60;
            for(File n : filteredArray) {
                if(n.getName().contains(".txt")) {
                    Note note = new Note(n, this);
                    note.setPreferredSize(new Dimension(248, 60));
                    if(selectedNote != null) {
                        if(n.getName().equals(selectedNote.getNoteFile().getName())) {
                            note.setBackground(new Color(37, 37, 46));
                        }else {
                            note.setBackground(contentPanel.getBackground());
                        }
                    }else {
                        note.setBackground(contentPanel.getBackground());
                    }
                    note.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            super.mousePressed(e);
                            selectedNote = note;
                            currentScroll = scrollBar.getValue();
                            refresh();
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            super.mouseEntered(e);
                            note.setBackground(new Color(37, 37, 46));

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            super.mouseExited(e);
                            if(selectedNote != null) {
                                if(n.getName().equals(selectedNote.getNoteFile().getName())) {
                                    note.setBackground(new Color(37, 37, 46));
                                }else {
                                    note.setBackground(contentPanel.getBackground());
                                }
                            }else {
                                note.setBackground(contentPanel.getBackground());
                            }
                        }
                    });

                    panel.add(note);
                }
            }
        }
        CustomScroll scrollPane = new CustomScroll(panel);
        scrollBar =  scrollPane.getVerticalScrollBar();

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.addMouseWheelListener(e -> {
            int previousValue = scrollBar.getValue();
            int addAmount;

            if(e.getWheelRotation()>0) {
                addAmount = 2;
            }else {
                addAmount = -2;
            }

            scrollBar.setValue(previousValue + addAmount);
        });
        return scrollPane;
    }

    // ================================================== Filter Tab ===========================================================//
    private JPanel createFilterTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 0 , 0));
        panel.setPreferredSize(new Dimension(404, 260));
        panel.setBackground(contentPanel.getBackground());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,0,0,2, new Color(0x242424)),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        JLabel label = new JLabel("Filter");
        label.setPreferredSize(new Dimension(200, 50));
        label.setForeground(new Color(0xC4C4C4));
        Font f = derivedFont.deriveFont(18f);
        label.setFont(f);

        panel.add(label);
        return panel;
    }

    // ================================================== Note Editor ==========================================================//
    private JPanel createNoteEditor() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(contentPanel.getBackground());

        if(selectedNote != null) {
            File noteFile = selectedNote.getNoteFile();

            String name = noteFile.getName().substring(0, noteFile.getName().length()-4);
            titleField = new JTextField(name);
            titleField.setBackground(contentPanel.getBackground());
            titleField.setPreferredSize(new Dimension(440, 50));
            titleField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(36, 36, 36), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5))
            );
            Font f = derivedFont.deriveFont(30f);
            titleField.setForeground(new Color(0xC4C4C4));
            titleField.setFont(f);
            titleField.setCaretColor(titleField.getForeground());
            titleField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { changeContent(); }
                @Override
                public void removeUpdate(DocumentEvent e) { changeContent(); }
                @Override
                public void changedUpdate(DocumentEvent e) { changeContent(); }

                public void changeContent() {
                    File selectedFile = selectedNote.getNoteFile();
                    File newFileName = new File(noteStorage.getPath()+"/"+titleField.getText()+".txt");
                    if(selectedFile.renameTo(newFileName)) {
                        selectedNote.setNoteFile(newFileName);
                        selectedNote.title.setText(titleField.getText());
                        refresh();
                        titleField.requestFocus();
                    }
                }
            });

            textArea = new JTextArea(selectedNote.getContent());
            textArea.setBackground(contentPanel.getBackground());
            textArea.setMargin(new Insets(15, 5, 5, 5));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            Font f2 = derivedFont2.deriveFont(16f);
            textArea.setForeground(new Color(0xB9B9B9));
            textArea.setFont(f2);
            textArea.setCaretColor(titleField.getForeground());
            if(textArea.getText().isEmpty()) {
                textArea.setText(". . .");
            }
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { changeContent(); }
                @Override
                public void removeUpdate(DocumentEvent e) { changeContent(); }
                @Override
                public void changedUpdate(DocumentEvent e) { changeContent(); }

                public void changeContent() {
                    // TODO : Update the .txt file accordingly while also changing the preview note on the sidebar
                    File currentFile = selectedNote.getNoteFile();
                    try {
                        FileReader fileReader = new FileReader(currentFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);

                        String line;
                        ArrayList<String> contents = new ArrayList<>();
                        int count = 0;

                        while ((line = bufferedReader.readLine()) != null) {
                            if(count < 3) {
                                contents.add(line);
                            }
                            count++;
                        }

                        FileWriter writer = new FileWriter(currentFile);

                        LocalDate now = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                        String nowStr = now.format(formatter);

                        writer.write(nowStr+"\n");
                        int counter = 0;
                        for(String str : contents) {
                            if(counter != 0)
                                writer.write(str+"\n");
                            counter++;
                        }
                        writer.write(textArea.getText());

                        bufferedReader.close();
                        fileReader.close();
                        writer.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            JPanel toolbar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Dimension arcs = new Dimension(10, 10);
                    int width = getWidth();
                    int height = getHeight();
                    Graphics2D g2D = (Graphics2D) g;
                    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2D.setColor(getBackground());
                    g2D.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
                }
            };
            toolbar.setOpaque(false);
            toolbar.setPreferredSize(new Dimension(404, 30));
            toolbar.setBackground(contentPanel.getBackground());
            toolbar.setBorder(BorderFactory.createMatteBorder(2,0,0,0, new Color(36,36,36)));
            toolbar.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));

            highBtn = importanceButton("High", new Color(0x942222));
            midBtn = importanceButton("Mid", new Color(0x1C9C2E));
            lowBtn = importanceButton("Low", new Color(0x1E449D));

            JLabel impLabel = new JLabel("Importance Level :   ");
            impLabel.setForeground(new Color(0xC4C4C4));

            toolbar.add(impLabel);
            toolbar.add(highBtn); toolbar.add(midBtn); toolbar.add(lowBtn);

            panel.add(titleField, BorderLayout.NORTH);
            panel.add(textArea, BorderLayout.CENTER);
            panel.add(toolbar, BorderLayout.SOUTH);
        }else {
            JLabel reminder = new JLabel("No note is currently selected...");
            reminder.setHorizontalAlignment(JLabel.CENTER);

            Font f = derivedFont.deriveFont(20f);
            reminder.setFont(f);
            reminder.setForeground(new Color(0x585867));
            reminder.setIcon(emptyImg);
            reminder.setIconTextGap(15);
            reminder.setHorizontalTextPosition(JLabel.CENTER);
            reminder.setVerticalTextPosition(JLabel.BOTTOM);
            panel.add(reminder, BorderLayout.CENTER);
        }

        return panel;
    }

    private JButton importanceButton(String msg, Color color) {
        JButton btn = new RoundedButton(msg, 20);
        btn.setPreferredSize(new Dimension(80, 28));
        btn.setOpaque(false);
        btn.setForeground(new Color(0xC4C4C4));
        btn.setBackground(new Color(0x4E4E4E));
        if(selectedNote.getImportanceLevel().equalsIgnoreCase("high") && msg.equalsIgnoreCase("high")) {
            btn.setBackground(new Color(0x942222));
        }else if(selectedNote.getImportanceLevel().equalsIgnoreCase("mid") && msg.equalsIgnoreCase("mid")) {
            btn.setBackground(new Color(0x1C9C2E));
        }else if(selectedNote.getImportanceLevel().equalsIgnoreCase("low") && msg.equalsIgnoreCase("low")) {
            btn.setBackground(new Color(0x1E449D));
        }
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); // Handle Importance level change
                File currentFile = selectedNote.getNoteFile();
                selectedNote.setImportanceLevel(msg);
                try {
                    FileReader fileReader = new FileReader(currentFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;
                    ArrayList<String> contents = new ArrayList<>();
                    int count = 0;

                    while ((line = bufferedReader.readLine()) != null) {
                        if(count < 3) {
                            contents.add(line);
                        }
                        count++;
                    }

                    FileWriter writer = new FileWriter(currentFile);

                    LocalDate now = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    String nowStr = now.format(formatter);

                    writer.write(nowStr+"\n"); // Last modified
                    writer.write(msg+"\n"); // Change Importance
                    writer.write(contents.get(2)+"\n"); // Rewrite Pin
                    writer.write(textArea.getText()); // Rewrite TextArea

                    bufferedReader.close();
                    fileReader.close();
                    writer.close();

                    refresh();
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                btn.setBackground(color);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                btn.setBackground(new Color(0x4E4E4E));
                if(selectedNote.getImportanceLevel().equalsIgnoreCase("high") && msg.equalsIgnoreCase("high")) {
                    btn.setBackground(new Color(0x942222));
                }else if(selectedNote.getImportanceLevel().equalsIgnoreCase("mid") && msg.equalsIgnoreCase("mid")) {
                    btn.setBackground(new Color(0x1C9C2E));
                }else if(selectedNote.getImportanceLevel().equalsIgnoreCase("low") && msg.equalsIgnoreCase("low")) {
                    btn.setBackground(new Color(0x1E449D));
                }
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainClass::new);
    }
}
