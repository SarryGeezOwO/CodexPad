import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    int radius;
    RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        if(!isOpaque()) {
            Graphics2D g2D = (Graphics2D) g.create();
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(getBackground());
            g2D.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        // Nothing...
    }
}
