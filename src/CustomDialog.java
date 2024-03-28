import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class CustomDialog extends FrameTemplate{

    Font derivedFont;

    CustomDialog(String title, int width, int height, String message) {
        super(title, width, height, false);

        try {

            File inter_file = new File("./res/font/inter.ttf");
            derivedFont = Font.createFont(Font.TRUETYPE_FONT, inter_file);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        JLabel warning = new JLabel(message);
        warning.setHorizontalAlignment(JLabel.CENTER);
        warning.setVerticalAlignment(JLabel.BOTTOM);
        warning.setForeground(Color.white);
        Font f = derivedFont.deriveFont(14f);
        warning.setFont(f);
        warning.setBorder(new EmptyBorder(0, 0, 5, 0));

        JPanel holder = new JPanel();
        holder.setOpaque(false);
        holder.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setBorder(new EmptyBorder(10, 10, 10, 20));

        JButton close = new RoundedButton("Okay", 15);
        close.setPreferredSize(new Dimension(80, 25));
        close.setOpaque(false);
        close.setBackground(new Color(0x242424));
        close.setForeground(Color.white);
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                close.setBackground(new Color(0x3C3C3C));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                close.setBackground(new Color(0x242424));
            }
        });

        holder.add(close);
        contentPanel.add(warning, BorderLayout.CENTER);
        contentPanel.add(holder, BorderLayout.SOUTH);
        setAlwaysOnTop(true);
        setVisible(true);
    }
}
