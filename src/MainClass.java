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
    public JTextArea textArea;

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

        contentPanel.add(createHeader(), BorderLayout.NORTH);
        contentPanel.add(createSideBar(), BorderLayout.WEST);
        contentPanel.add(createNoteEditor(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void refresh() {
        contentPanel.removeAll();
        contentPanel.add(createHeader(), BorderLayout.NORTH);
        contentPanel.add(createSideBar(), BorderLayout.WEST);
        contentPanel.add(createNoteEditor(), BorderLayout.CENTER);
        revalidate();
        repaint();
        scrollBar.setValue(currentScroll);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        panel.setPreferredSize(new Dimension(999, 50));
        panel.setBackground(contentPanel.getBackground());
        panel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,  new Color(36, 36, 36)));

        JButton addNote = new RoundedButton("Add note", 15);
        addNote.setPreferredSize(new Dimension(100, 30));
        addNote.setBackground(new Color(0x1789A0));
        addNote.setForeground(contentPanel.getBackground());
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
                addNote.setBackground(new Color(0x0A9EBF));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                addNote.setBackground(new Color(0x1789A0));
            }
        });
        panel.add(addNote);
        return panel;
    }

    private JScrollPane createSideBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        panel.setPreferredSize(new Dimension(250, scrollHeight));
        panel.setBackground(contentPanel.getBackground());
        panel.setBorder(BorderFactory.createMatteBorder(0,0,0,1,  new Color(36, 36, 36)));

        if(noteStorage.isDirectory()) {

            scrollHeight = noteStorage.listFiles().length * 65;
            for(File n : noteStorage.listFiles()) {
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.addMouseWheelListener(e -> {
            //capturing previous value
            int previousValue = scrollBar.getValue();

            int addAmount;

            //decide where the wheel scrolled
            //depending on how fast you want to scroll
            //you can chane the addAmount to something greater or lesser

            if(e.getWheelRotation()>0) {
                addAmount = 2;
            }else {
                addAmount = -2;
            }

            //set the new value
            scrollBar.setValue(previousValue + addAmount);
        });
        return scrollPane;
    }

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
            textArea.setMargin(new Insets(20, 5, 5, 5));
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

            panel.add(titleField, BorderLayout.NORTH);
            panel.add(textArea, BorderLayout.CENTER);
        }else {
            JLabel reminder = new JLabel("No note is currently selected...");
            reminder.setHorizontalAlignment(JLabel.CENTER);
            reminder.setFont(interFont);
            reminder.setForeground(new Color(0xC4C4C4));
            panel.add(reminder, BorderLayout.CENTER);
        }

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClass());
    }

}
