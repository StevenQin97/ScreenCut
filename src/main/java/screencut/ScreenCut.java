package screencut;

import common.Constant;
import home.CustomTreeNode;
import home.HomePage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenCut extends JDialog implements MouseListener, MouseMotionListener, ActionListener {


    /**
     * 是否确定开始点
     */
    private boolean isStart;
    /**
     * 是否确定结束点
     */
    private boolean isEnd;
    /**
     * 是否可以开始绘制
     */
    private boolean isDraw;
    /**
     * 是否开始拖曳
     */
    private boolean isDrag;
    /**
     * 是否整个框移动
     */
    private boolean isMove;
    /**
     * 是否显示工具栏
     */
    private boolean isShow;
    /**
     * 是否正在绘制
     */
    private boolean isDrawing;
    /**
     * 鼠标所有边框位置
     */
    private int dir;
    /**
     * 拖曳终止点
     */
    private Point end = new Point();
    /**
     * 拖曳开始点
     */
    private Point begin = new Point();
    /**
     * 绘图起始点
     */
    private Point drawStart = new Point();
    /**
     * 绘图终止点
     */
    private Point drawEnd = new Point();
    /**
     * 用于检测的矩形框
     */
    private Rectangle smallRect = new Rectangle();
    /**
     * 调整边框起始点与边框起始点相对距离
     */
    private int dx, dy, dx2, dy2;
    private List<Object[]> draws = new ArrayList<Object[]>();
    private Cursor cur;
    private static File file;
    /**
     * 当前关联节点
     */
    DefaultMutableTreeNode node;


    /**
     * 画方形的粗细
     */
    private int thick = 1;
    private int clickCount;
    private Rectangle screen;
    private JToggleButton temp;
    private BufferedImage bgimg;
    private BufferedImage image;
    private Toolkit tool = Toolkit.getDefaultToolkit();
    private final int range = 5;
    private final int UP = 4;
    private final int LEFT = 2;
    private final int DOWN = 3;
    private final int RIGHT = 1;
    private final int LEFT_UP = 5;
    private final int RIGHT_UP = 7;
    private final int LEFT_DOWN = 6;
    private final int RIGHT_DOWN = 8;



    private JPanel panel;
    private JToolBar toolbar;
    private JToggleButton rectbtn;
    private JToggleButton circlebtn;
    private JButton jButton5;
    private JButton jButton7;
    private JButton jButton8;
    private JPanel panel1;
    private JButton jButton11;
    private JButton jButton16;
    private JButton jButton9;
    private JButton jButton10;
    private JButton jButton14;
    private JButton jButton15;
    private JButton jButton13;
    private JButton jButton12;
    private JButton displaySelected;
    private JToolBar.Separator jSeparator1;


    private Robot robot;
    private HomePage parentPage;

    public static void main(String[] args) {
        ShowScreenCut(null,null);
    }

    public static void ShowScreenCut(HomePage parent, DefaultMutableTreeNode node) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ex) {
            Logger.getLogger(ScreenCut.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDialog.setDefaultLookAndFeelDecorated(false);
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ScreenCut dialog = new ScreenCut(new JFrame(), parent, node);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public ScreenCut(java.awt.Frame parent, HomePage parentPage,DefaultMutableTreeNode node) {
        parent.setResizable(false);
        initComponents();
        addMouseListener(this);
        addMouseMotionListener(this);
        toolbar.setVisible(false);
        panel.setVisible(false);
        if(parentPage!= null){
            this.parentPage = parentPage;
            this.node = node;
        }
        try {
            robot = new Robot();
            screen = new Rectangle(tool.getScreenSize());
            setSize(screen.width, screen.height);
            bgimg = robot.createScreenCapture(screen);
            cur = tool.createCustomCursor(tool.createImage(getClass().getResource("/icon/cur.png")), new Point(0, 0), "cur");
            setCursor(cur);
            initActionListener(panel.getComponents());
            initActionListener(toolbar.getComponents());
        } catch (AWTException ex) {
            Logger.getLogger(ScreenCut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;
        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        panel.setPreferredSize(new java.awt.Dimension(320, 100));
        java.awt.GridBagLayout panelLayout = new java.awt.GridBagLayout();
        panelLayout.columnWidths = new int[] {0, 0, 0, 0, 0};
        panelLayout.rowHeights = new int[] {0};
        panel.setLayout(panelLayout);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;

        jButton9.setBackground(new java.awt.Color(102, 102, 102));
        jButton9.setForeground(new java.awt.Color(102, 102, 102));
        jButton9.setActionCommand("color");
        jButton9.setPreferredSize(new java.awt.Dimension(20, 20));
        panel.add(jButton9, gridBagConstraints);

        jButton10.setBackground(new java.awt.Color(255, 255, 255));
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setActionCommand("color");
        jButton10.setPreferredSize(new java.awt.Dimension(20, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        panel.add(jButton10, gridBagConstraints);

        jButton11.setBackground(new java.awt.Color(0, 0, 0));
        jButton11.setActionCommand("color");
        jButton11.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        panel.add(jButton11, gridBagConstraints);

        jButton12.setBackground(new java.awt.Color(255, 51, 255));
        jButton12.setForeground(new java.awt.Color(255, 51, 255));
        jButton12.setActionCommand("color");
        jButton12.setPreferredSize(new java.awt.Dimension(20, 20));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        panel.add(jButton12, gridBagConstraints);

        jButton13.setBackground(new java.awt.Color(0, 153, 153));
        jButton13.setForeground(new java.awt.Color(0, 153, 153));
        jButton13.setActionCommand("color");
        jButton13.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        panel.add(jButton13, gridBagConstraints);

        jButton14.setBackground(new java.awt.Color(255, 0, 0));
        jButton14.setForeground(new java.awt.Color(255, 0, 0));
        jButton14.setActionCommand("color");
        jButton14.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        panel.add(jButton14, gridBagConstraints);

        jButton15.setBackground(new java.awt.Color(255, 255, 51));
        jButton15.setForeground(new java.awt.Color(255, 255, 51));
        jButton15.setActionCommand("color");
        jButton15.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        panel.add(jButton15, gridBagConstraints);

        jButton16.setBackground(new java.awt.Color(0, 153, 0));
        jButton16.setForeground(new java.awt.Color(0, 153, 0));
        jButton16.setActionCommand("color");
        jButton16.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        panel.add(jButton16, gridBagConstraints);


        displaySelected.setBackground(new java.awt.Color(255, 0, 0));
        displaySelected.setFocusable(false);
        displaySelected.setPreferredSize(new java.awt.Dimension(40, 40));
        displaySelected.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displaySelectedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        panel.add(displaySelected, gridBagConstraints);


        getContentPane().add(panel);
        panel.setBounds(80, 170, 150, 50);
        toolbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 255)));
        toolbar.setFloatable(false);
        toolbar.setDoubleBuffered(true);

        rectbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/rect.png"))); // NOI18N
        rectbtn.setToolTipText("添加矩形");
        rectbtn.setFocusable(false);
        rectbtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectbtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(rectbtn);

        circlebtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/circle.png"))); // NOI18N
        circlebtn.setToolTipText("添加圆形");
        circlebtn.setFocusable(false);
        circlebtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        circlebtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(circlebtn);

        jSeparator1.setPreferredSize(new java.awt.Dimension(1, 0));
        toolbar.add(jSeparator1);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/undo.png"))); // NOI18N
        jButton5.setToolTipText("撤销编辑");
        jButton5.setActionCommand("undo");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(jButton5);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/exit.png"))); // NOI18N
        jButton7.setToolTipText("退出截图");
        jButton7.setActionCommand("exit");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(jButton7);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/ok.png"))); // NOI18N
        jButton8.setToolTipText("完成截图");
        //save 为保存 ok为放置到剪贴板
        jButton8.setActionCommand("save");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(jButton8);

        getContentPane().add(toolbar);
        toolbar.setBounds(81, 138, 150, 25);

        pack();
    }

    private void initActionListener(Component[] cps) {
        for (Component cp : cps) {
            if (cp instanceof AbstractButton) {
                AbstractButton ccp = (AbstractButton) cp;
                ccp.addActionListener(this);
            }
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("color".equals(cmd)) {
            JButton jb = (JButton) e.getSource();
            displaySelected.setBackground(jb.getBackground());
        } else {
            if ("exit".equals(cmd)) {
                dispose();
                if(parentPage!= null){
                    parentPage.actionForPicChange(node);
                }
            } else if ("save".equals(cmd)) {
                File imgDir = new File(Constant.IMG_PATH);
                if(!imgDir.exists()){
                    imgDir.mkdirs();
                }
                CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
                if(userObject.getImgName() == null || "".equals(userObject.getImgName()) ){
                    userObject.setImgName(System.currentTimeMillis()+"");
                }
                ScreenCut.file = new File(Constant.IMG_PATH + userObject.getImgName() + Constant.IMG_TYPE);
                if (ScreenCut.file.exists()) {
                    int opt = JOptionPane.showConfirmDialog(rootPane, "文件" + ScreenCut.file.getName() + "已经存在,是否覆盖？");
                    if (opt == JOptionPane.OK_OPTION) {
                        saveFile(ScreenCut.file);
                    }
                } else {
                    saveFile(ScreenCut.file);
                }
                requestFocus();
            } else if ("undo".equals(cmd)) {
                if (!draws.isEmpty()) {
                    draws.remove(draws.size() - 1);
                }
            } else {
                boolean eq = temp != null && temp.equals(e.getSource());
                clickCount = eq ? ++clickCount : 0;
                temp = (JToggleButton) e.getSource();
                initButton(temp);
                temp.setSelected(clickCount % 2 == 0);
            }
        }
        toolbar.repaint();
        panel.repaint();
        repaint();
    }

    private void saveFile(final File file) {
        isDraw = false;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    image = robot.createScreenCapture(getNormalRect());
                    String name = file.getName();
                    ImageIO.write(image, name.substring(name.indexOf(".") + 1), file);
                } catch (IOException ex) {
                    Logger.getLogger(ScreenCut.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if(parentPage!= null){
                        parentPage.actionForPicChange(node);
                    }
                    dispose();
                }
            }
        }, 50);
    }

    private Rectangle getNormalRect() {
        Rectangle nor = (Rectangle) smallRect.clone();
        nor.x -= range;
        nor.y -= range;
        nor.height += 2 * range;
        nor.width += 2 * range;
        return nor;
    }

    /**
     * 使工具按钮处于单选
     *
     * @param bt
     */
    private void initButton(JToggleButton bt) {
        rectbtn.setSelected(bt.equals(rectbtn));
        circlebtn.setSelected(bt.equals(circlebtn));
    }

    private boolean isSelection() {
        return rectbtn.isSelected() || circlebtn.isSelected();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2) {//鼠标右键双击退出截图
            dispose();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isStart) {//初始化边框起始点
            isStart = true;
            begin.x = e.getX();
            begin.y = e.getY();
        } else if (isSelection() && isInRect(e.getX(), e.getY())) {//在截图边框内绘制其他图形起始点初始化
            drawStart.x = e.getX();
            drawStart.y = e.getY();

        } else {
            if (isInRect(e.getX(), e.getY())) {
                isMove = true;
            } else {
                checkDIR(e.getX(), e.getY());
                if (dir > 0) {
                    isDrag = true;
                }
            }//初始化调整边框起始点相对位置
            dx = e.getX() - begin.x;
            dy = e.getY() - begin.y;
            dx2 = e.getX() - end.x;
            dy2 = e.getY() - end.y;
        }
        isShow = false;
        repaint();
    }

    /**
     * 判断鼠标方向
     *
     * @param x
     * @param y
     */
    private void checkDIR(int x, int y) {
        Point st = new Point();
        st.x = Math.min(begin.x, end.x);
        st.y = Math.min(begin.y, end.y);
        if (x - range < st.x && x + range > st.x) {
            if (y > st.y + range && y < st.y + smallRect.height + range) {
                dir = LEFT;
            } else if (y >= st.y - range && y <= st.y + range) {
                dir = LEFT_UP;
            } else if (y >= st.y + smallRect.height + range && y <= st.y + smallRect.height + 3 * range) {
                dir = LEFT_DOWN;
            } else {
                dir = -1;
            }
        } else if (x - range < st.x + smallRect.width + 2 * range && x + range > st.x + smallRect.width + 2 * range) {
            if (y > st.y + range && y < st.y + smallRect.height + range) {
                dir = RIGHT;
            } else if (y >= st.y - range && y <= st.y + range) {
                dir = RIGHT_UP;
            } else if (y >= st.y + smallRect.height + range && y <= st.y + smallRect.height + 3 * range) {
                dir = RIGHT_DOWN;
            } else {
                dir = -1;
            }
        } else if (y - range < st.y + smallRect.height + 2 * range && y + range > st.y + smallRect.height + 2 * range) {
            if (x > st.x + range && x < st.x + smallRect.width + range) {
                dir = DOWN;
            } else {
                dir = -1;
            }
        } else if (y - range < st.y && y + range > st.y) {
            if (x > st.x + range && x < st.x + smallRect.width + range) {
                dir = UP;
            } else {
                dir = -1;
            }
        } else {
            dir = -1;
        }
        dir = draws.isEmpty() ? dir : -1;
    }


    /**
     * 判断鼠标是否在矩形内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInRect(int x, int y) {
        smallRect = new Rectangle();
        smallRect.x = Math.min(begin.x, end.x) + range;
        smallRect.y = Math.min(begin.y, end.y) + range;
        smallRect.height = Math.abs(begin.y - end.y) - 2 * range;
        smallRect.width = Math.abs(begin.x - end.x) - 2 * range;
        return smallRect.contains(x, y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isStart && !isEnd) {//首次绘制边框结束点
            end.x = e.getX();
            end.y = e.getY();
            isEnd = true;
        } else if (isDrawing && isInRect(e.getX(), e.getY())) {
            drawEnd.x = e.getX();
            drawEnd.y = e.getY();
            String type = rectbtn.isSelected() ? "rect" : circlebtn.isSelected() ? "circle" : "";
            if (!type.isEmpty()) {
                draws.add(new Object[]{
                        drawStart.clone(), drawEnd.clone(), displaySelected.getBackground(), type, thick
                });
            }
            isDrawing = false;
        }
        int x = end.x, y = end.y;//交换起始点结束点坐标，使起始点保持在左上角位置
        end.x = Math.max(end.x, begin.x);
        end.y = Math.max(end.y, begin.y);
        begin.x = Math.min(x, begin.x);
        begin.y = Math.min(y, begin.y);
        isDrag = false;
        isMove = false;
        isShow = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isEnd) {//首次拖曳未完成时动态绘制边框
            end.x = e.getX();
            end.y = e.getY();
            isDraw = true;
        } else if (isSelection() && isInRect(e.getX(), e.getY())) {//绘制图形
            drawEnd.x = e.getX();
            drawEnd.y = e.getY();
            isDrawing = true;
        } else {//首次绘制边框完成后
            if (isMove && draws.isEmpty()) {//整个边框移动
                int sx = e.getX() - dx, sy = e.getY() - dy, ex = e.getX() - dx2, ey = e.getY() - dy2;
                int sxo = begin.x, syo = begin.y, exo = end.x, eyo = end.y;
                begin.x = sx;
                begin.y = sy;
                end.x = ex;
                end.y = ey;
                if (Math.min(sx, ex) < 0 || Math.min(sy, ey) < 0 || Math.max(sx, ex) > screen.width || Math.max(sy, ey) > screen.height) {
                    begin.x = sxo;
                    begin.y = syo;
                    end.x = exo;
                    end.y = eyo;
                }
            } else if (isDrag) {//大小调整
                switch (dir) {
                    case LEFT:
                        begin.x = e.getX();
                        break;
                    case RIGHT:
                        end.x = e.getX();
                        break;
                    case LEFT_UP:
                        begin.y = e.getY();
                        begin.x = e.getX();
                        break;
                    case RIGHT_DOWN:
                        end.y = e.getY();
                        end.x = e.getX();
                        break;
                    case RIGHT_UP:
                        begin.y = e.getY();
                        end.x = e.getX();
                        break;
                    case LEFT_DOWN:
                        begin.x = e.getX();
                        end.y = e.getY();
                        break;
                    case DOWN:
                        end.y = e.getY();
                        break;
                    case UP:
                        begin.y = e.getY();
                        break;
                }
            }
        }
        isInRect(dx, dy);
        isShow = false;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isEnd) {
            int x = e.getX(), y = e.getY();
            if (isInRect(x, y)) {
                if (isSelection()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (draws.isEmpty()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    setCursor(cur);
                }
            } else {
                checkDIR(x, y);
                switch (dir) {
                    case LEFT:
                    case RIGHT:
                        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                        break;
                    case LEFT_UP:
                    case RIGHT_DOWN:
                        setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                        break;
                    case RIGHT_UP:
                    case LEFT_DOWN:
                        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                        break;
                    case DOWN:
                    case UP:
                        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                        break;
                    default:
                        setCursor(cur);
                }
            }
        }
    }

    private void displaySelectedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    @Override
    public void dispose() {
        JDialog.setDefaultLookAndFeelDecorated(true);
        super.dispose();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bgimg, 0, 0, null);
        drawLines(g);
        if (isDraw) {
            drawRect(begin, end, Color.red, g, 1);
            //四个顶点
            g.fillRect(begin.x - 2, begin.y - 2, range, range);
            g.fillRect(end.x - 2, begin.y - 2, range, range);
            g.fillRect(begin.x - 2, end.y - 2, range, range);
            g.fillRect(end.x - 2, end.y - 2, range, range);
            //四个中点
            g.fillRect(smallRect.x + smallRect.width / 2 - 2, begin.y - 2, range, range);
            g.fillRect(smallRect.x + smallRect.width / 2 - 2, end.y - 2, range, range);
            g.fillRect(begin.x - 2, smallRect.y + smallRect.height / 2 - 2, range, range);
            g.fillRect(end.x - 2, smallRect.y + smallRect.height / 2 - 2, range, range);
            //边框大小
            g.setColor(Color.BLACK);
            int x = smallRect.x - 5, y = smallRect.y - 28;
            y = y < -5 ? 2 : y;
            g.fillRect(x, y, 80, 20);
            g.setColor(Color.white);
            g.drawString(Math.abs(begin.x - end.x) + "×" + Math.abs(begin.y - end.y), x + 5, y + 18);
            //工具栏
            int totalHeight = toolbar.getHeight() + panel.getHeight();
            x = smallRect.x + smallRect.width + range - toolbar.getWidth();
            x = x < 0 ? 2 : x;
            y = smallRect.y + smallRect.height + 3 * range;
            if (y > screen.height - totalHeight) {
                y = smallRect.y - toolbar.getHeight() - 3 * range;
                toolbar.setLocation(x, y);
                panel.setLocation(toolbar.getX(), y - totalHeight + 3 * range);
            } else {
                toolbar.setLocation(x, y);
                panel.setLocation(toolbar.getX(), toolbar.getY() + toolbar.getHeight() + 2);
            }
            toolbar.setVisible(isShow);
            panel.setVisible(isShow && isSelection());
        }
        if (isDrawing) {
            if (rectbtn.isSelected()) {
                drawRect(drawStart, drawEnd, displaySelected.getBackground(), g, thick);
            } else if (circlebtn.isSelected()) {
                drawRoundRect(drawStart, drawEnd, displaySelected.getBackground(), g);
            }
        }
        //绘制图形
        for (Iterator<Object[]> it = draws.iterator(); it.hasNext();) {
            Object[] pts = it.next();
            if (pts[3].toString().equals("rect")) {
                drawRect((Point) pts[0], (Point) pts[1], (Color) pts[2], g, Integer.valueOf(pts[4].toString()));
            } else if (pts[3].toString().equals("circle")) {
                drawRoundRect((Point) pts[0], (Point) pts[1], (Color) pts[2], g);
            }  else {
                drawString((Point) pts[0], pts[3].toString(), (Color) pts[2], g, Integer.valueOf(pts[4].toString()));
            }
        }
        toolbar.repaint();
        panel.repaint();
    }

    /**
     * 绘制方形
     */
    private void drawRect(Point st, Point ed, Color c, Graphics g, int thick) {
        g.setColor(c);
        g.fillRect(Math.min(st.x, ed.x), st.y, Math.abs(ed.x - st.x), thick);//横线1
        g.fillRect(Math.min(st.x, ed.x), ed.y, Math.abs(ed.x - st.x) + thick, thick);//横线2
        g.fillRect(st.x, Math.min(st.y, ed.y), thick, Math.abs(ed.y - st.y));//竖线1
        g.fillRect(ed.x, Math.min(st.y, ed.y), thick, Math.abs(ed.y - st.y));//坚线2
    }

    /**
     * 绘制圆形
     */
    private void drawRoundRect(Point st, Point ed, Color c, Graphics g) {
        g.setColor(c);
        int w = Math.abs(st.x - ed.x);
        int h = Math.abs(st.y - ed.y);
        g.drawRoundRect(Math.min(st.x, ed.x), Math.min(st.y, ed.y), w, h, w, h);
    }
    /**
     * 绘制文字
     */
    private void drawString(Point st, String str, Color c, Graphics g, int size) {
        g.setFont(new Font("宋体", Font.PLAIN, size));
        g.setColor(c);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r2d = fm.getStringBounds(str, g);
        g.drawString(str, st.x, st.y + (int) r2d.getHeight());
    }

    /**
     * 绘制阴影
     *
     * @param g
     */
    private void drawLines(Graphics g) {
        g.setColor(Color.gray);
        for (int i = 0; i < screen.height; i += 8) {
            g.drawLine(0, i, Math.min(begin.x, end.x) - 2, i);
            g.drawLine(Math.max(begin.x, end.x) + 2, i, screen.width, i);
            if (i < Math.min(begin.y, end.y) || i > Math.max(begin.y, end.y)) {
                g.drawLine(Math.min(begin.x, end.x) - 2, i, Math.max(begin.x, end.x) + 2, i);
            }
        }
    }

}
