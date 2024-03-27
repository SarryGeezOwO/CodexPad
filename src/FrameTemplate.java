import javax.swing.*;
import java.awt.*;

public class FrameTemplate extends JFrame {
    FrameTemplate(String title, int width, int height, boolean operation) {
        this.setTitle(title);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);


        // Call SetVisible to the inheritor
    }
}
