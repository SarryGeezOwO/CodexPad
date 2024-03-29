import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Note extends JPanel {

    private Font interFont;
    private File noteFile;
    public JLabel title;

    String lastModified;
    String importanceLevel;
    String isPinned;
    String content;
    MainClass holder;

    private final ImageIcon calendarIcon = FrameTemplate.resizeImageIcon(
            new ImageIcon("./res/icon/calendar.png"), 15, 15
    );
    private final ImageIcon starIcon = FrameTemplate.resizeImageIcon(
            new ImageIcon("./res/icon/star.png"), 15, 15
    );
    private final ImageIcon starCloseIcon = FrameTemplate.resizeImageIcon(
            new ImageIcon("./res/icon/star_close.png"), 15, 15
    );
    private final ImageIcon trashIcon = FrameTemplate.resizeImageIcon(
            new ImageIcon("./res/icon/trash.png"), 15, 15
    );

    public String getLastModified() {
        return lastModified;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public String getIsPinned() {
        return isPinned;
    }

    public String getContent() {
        return content;
    }
    public File getNoteFile() {
        return noteFile;
    }
    public void setNoteFile(File newFile) {
        noteFile = newFile;
    }
    public void setImportanceLevel(String imp) { importanceLevel = imp; }

    Note(File noteFile, MainClass holder) {
        this.holder = holder;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        this.noteFile = noteFile;
        try {
            File inter_file = new File("./res/font/inter.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, inter_file);
            interFont = font.deriveFont(14f);

            // Get description content
            FileReader fileReader = new FileReader(noteFile);
            BufferedReader reader = new BufferedReader(fileReader);
            StringBuilder builder = new StringBuilder();

            String line;
            int lineRead = 0;
            while ((line = reader.readLine()) != null) {
                switch (lineRead) {
                    case 0:
                        lastModified = line;
                        break;
                    case 1:
                        importanceLevel = line;
                        break;
                    case 2:
                        isPinned = line;
                        break;
                    default:
                        builder.append(line).append("\n"); // Modify this later on I think IDK...
                        break;
                }
                lineRead++;
            }
            reader.close();
            fileReader.close();
            content = builder.toString();
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        String name = noteFile.getName().substring(0, noteFile.getName().length()-4);
        title = new JLabel(name);
        title.setFont(interFont);
        title.setForeground(new Color(0xDEDEDE));

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        bottom.setOpaque(false);

        JLabel importance = new JLabel(importanceLevel) {
            @Override
            protected void paintComponent(Graphics g) {
                if(!isOpaque()) {
                    Graphics2D g2D = (Graphics2D) g.create();
                    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2D.setColor(getBackground());
                    g2D.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                super.paintComponent(g);
            }
        };
        importance.setFont(interFont);
        importance.setVerticalAlignment(JLabel.CENTER);
        importance.setOpaque(false);
        importance.setForeground(Color.white);
        importance.setBorder(new EmptyBorder(2, 5, 2, 10));
        importance.setPreferredSize(new Dimension(50, 20));
        importance.setHorizontalAlignment(JLabel.CENTER);
        if(importanceLevel.equalsIgnoreCase("high")) {
            importance.setBackground(new Color(0x942222));
        }else if(importanceLevel.equalsIgnoreCase("mid")) {
            importance.setBackground(new Color(0x1C9C2E));
        }else if(importanceLevel.equalsIgnoreCase("low")) {
            importance.setBackground(new Color(0x1E449D));
        }

        JLabel date = new JLabel(lastModified);
        date.setForeground(new Color(0xDEDEDE));
        date.setBorder(new EmptyBorder(0, 5, 0, 60));
        date.setIcon(calendarIcon);
        date.setHorizontalTextPosition(JLabel.TRAILING);

        JButton favoriteBtn = new JButton();
        favoriteBtn.setPreferredSize(new Dimension(20, 20));

        boolean check = Boolean.parseBoolean(isPinned);
        favoriteBtn.setIcon((check) ? starCloseIcon : starIcon);
        favoriteBtn.setContentAreaFilled(false);
        favoriteBtn.setOpaque(false);
        favoriteBtn.setBorder(new EmptyBorder(0, 0, 0, 5));
        favoriteBtn.setFocusable(false);
        favoriteBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                try {
                    FileReader fileReader = new FileReader(noteFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;
                    ArrayList<String> contents = new ArrayList<>();

                    while ((line = bufferedReader.readLine()) != null) {
                        contents.add(line);
                    }

                    FileWriter writer = new FileWriter(noteFile);
                    int counter = 0;
                    for(String str : contents) {
                        if(counter < 2)
                            writer.write(str+"\n");
                        counter++;
                    }
                    boolean check = Boolean.parseBoolean(isPinned);
                    writer.write((check) ? "false\n" : "true\n");
                    for(int i = 3; i < contents.size(); i++) {
                        writer.write(contents.get(i)+"\n");
                    }

                    bufferedReader.close();
                    fileReader.close();
                    writer.close();

                    holder.refresh();
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton trashBtn = new JButton();
        trashBtn.setPreferredSize(new Dimension(20, 20));
        trashBtn.setIcon(trashIcon);
        trashBtn.setContentAreaFilled(false);
        trashBtn.setOpaque(false);
        trashBtn.setBorder(null);
        trashBtn.setFocusable(false);
        trashBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                noteFile.delete();
                holder.selectedNote = null;
                holder.refresh();
            }
        });

        bottom.add(importance);
        bottom.add(date);
        bottom.add(favoriteBtn);
        bottom.add(trashBtn);

        add(title, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
    }

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
        g2D.setColor(getForeground());
        g2D.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
    }
}
