import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FrameTemplate extends JFrame implements MouseListener, MouseMotionListener {

    private ImageIcon closeIcon = resizeImageIcon(
            new ImageIcon("./res/icon/close.png"), 15, 15
    );
    private ImageIcon minIcon = resizeImageIcon(
            new ImageIcon("./res/icon/minimize.png"), 15, 15
    );
    private ImageIcon frameIcon = resizeImageIcon(
            new ImageIcon("./res/icon/java.png"), 15, 15
    );

    JPanel titleBar, contentPanel;
    JButton closeBtn, minBtn;
    int xMouse, yMouse;

    FrameTemplate(String title, int width, int height, boolean operation) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(25, 25, 30));

        titleBar = new JPanel();
        titleBar.addMouseListener(this);
        titleBar.addMouseMotionListener(this);
        titleBar.setBackground(new Color(15, 14, 15));
        titleBar.setPreferredSize(new Dimension(69, 20));
        titleBar.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        contentPanel.setBorder(BorderFactory.createLineBorder(titleBar.getBackground(), 2));


        JLabel titleLabel = new JLabel(title);
        titleLabel.setPreferredSize(new Dimension(this.getWidth()-40, 20));
        titleLabel.setIcon(frameIcon);
        titleLabel.setForeground(Color.white);

        closeBtn = new JButton();
        closeBtn.addMouseListener(this);
        closeBtn.setBackground(titleBar.getBackground());
        closeBtn.setPreferredSize(new Dimension(20, 20));
        closeBtn.setFocusable(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setIcon(closeIcon);
        closeBtn.addActionListener(event -> {
            System.exit(0);
        });

        minBtn = new JButton();
        minBtn.addMouseListener(this);
        minBtn.setBackground(titleBar.getBackground());
        minBtn.setPreferredSize(new Dimension(20, 20));
        minBtn.setFocusable(false);
        minBtn.setBorderPainted(false);
        minBtn.setIcon(minIcon);
        minBtn.addActionListener(event -> {
            this.setExtendedState(JFrame.ICONIFIED);
        });

        titleBar.add(titleLabel);
        titleBar.add(minBtn);
        titleBar.add(closeBtn);

        this.add(titleBar, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
        // Call SetVisible to the inheritor
    }

    public static ImageIcon resizeImageIcon(ImageIcon img, int sizeX, int sizeY) {
        Image i = img.getImage();
        Image scaledImg = i.getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getSource()==titleBar) {
            xMouse = e.getX();
            yMouse = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource()==closeBtn) {
            closeBtn.setBackground(new Color(205, 33, 33));
        }else if(e.getSource()==minBtn) {
            minBtn.setBackground(new Color(94, 101, 110));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getSource()==closeBtn) {
            closeBtn.setBackground(titleBar.getBackground());
        }else if(e.getSource()==minBtn) {
            minBtn.setBackground(titleBar.getBackground());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(e.getSource()==titleBar) {
            int x = e.getXOnScreen();
            int y = e.getYOnScreen();
            this.setLocation(x-xMouse, y-yMouse);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
