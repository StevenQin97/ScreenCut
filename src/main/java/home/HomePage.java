package home;

import screencut.ScreenCut;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HomePage extends JDialog{
    private JTree functionTree;
    private JPanel treePanel;
    private JPanel imgPanel;
    private JPanel panel;
    private JLabel imgLabel;
    private String IMG_PATH = "E:\\1.jpg";
    private static HomePage homePage;

    public static HomePage getInstance() {
        if(homePage == null){
            homePage = new HomePage();
        }
        initComponents(homePage);
        return homePage;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("HomePage");
        frame.setSize(1024,800);
        frame.setResizable(false);
        getInstance();
        frame.setContentPane(homePage.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void initComponents(HomePage homePage){
        //            BufferedImage img = ImageIO.read(new File(homePage.IMG_PATH));
        ImageIcon icon = new ImageIcon("E:\\1.jpg");
        icon.setImage(icon.getImage().getScaledInstance(650, 500, Image.SCALE_DEFAULT));
        homePage.imgLabel.setIcon(icon);
        homePage.functionTree.setEditable(true);
        homePage.getTree();
    }


    public void getTree(){

    }
}
