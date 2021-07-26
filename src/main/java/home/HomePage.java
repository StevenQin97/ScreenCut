package home;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.Constant;
import screencut.ScreenCut;
import util.DataUtil;
import util.FileUtil;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Date;
import java.util.Map;

public class HomePage extends JDialog implements ActionListener, TreeModelListener {
    private JPanel treePanel;
    private JPanel imgPanel;
    private JPanel panel;
    private JFrame frame;
    private JLabel imgLabel;
    private JTree tree;
    private String nodeName = null;
    JLabel label = null;
    DefaultTreeModel treeModel = null;
    CustomTreeNode defaultRoot = new CustomTreeNode(System.currentTimeMillis(), "功能清单");

    public HomePage() {
        frame = new JFrame("功能截图管理工具");
        frame.setSize(1024, 800);
        frame.setResizable(false);

        initTree();
        initComponents();

        frame.requestFocus();
        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        });

    }

    public static void main(String[] args) {
        new HomePage();

    }

    public void initComponents() {

        JPanel buttonPanel = new JPanel();

        JButton btnDelPic = new JButton("删除截图");
        buttonPanel.add(btnDelPic);
        btnDelPic.setActionCommand("delPic");
        btnDelPic.addActionListener(this);

        JButton btnNewPic = new JButton("添加截图");
        buttonPanel.add(btnNewPic);
        btnNewPic.setActionCommand("newPic");
        btnNewPic.addActionListener(this);

        imgLabel = new JLabel();
        showPic("");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(imgLabel);

        imgPanel.add(buttonPanel, BorderLayout.NORTH);
        imgPanel.add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * @Description 设置展示截图的label的背景图片
     * @Date 11:53 2021/7/21
     **/
    private void showPic(String filepath) {
        if (filepath != null) {
            String path = Constant.IMG_PATH + filepath + Constant.IMG_TYPE;
            ImageIcon icon = new ImageIcon(path);
            icon.setImage(icon.getImage().getScaledInstance(685, 500, Image.SCALE_DEFAULT));
            imgLabel.setIcon(icon);
        }
    }

    /**
     * @Description 根据Json数据构建节点结构
     * @Date 11:37 2021/7/21
     **/
    private void generateTreeNode(JSONObject data, DefaultMutableTreeNode parent) {
        JSONObject userObject = (JSONObject) data.get("userObject");
        CustomTreeNode treeNode = new CustomTreeNode(userObject);
        DefaultMutableTreeNode current = new DefaultMutableTreeNode(treeNode);
        JSONArray children = data.getJSONArray("children");
        for (Object child : children) {
            generateTreeNode((JSONObject) child, current);
        }
        if (parent != null) {
            parent.add(current);
        } else {
            tree = new JTree(current);
        }
    }

    /**
     * @Description 初始化树组件
     * @Date 11:36 2021/7/21
     **/
    public void initTree() {
        getTreeData();
        tree.setEditable(true);
        tree.addMouseListener(new MouseHandle());
        treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.addTreeModelListener(this);

        tree.getCellEditor().addCellEditorListener(new TreeCellEditorAction());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);
        JPanel panel = new JPanel();
        JButton buttonClean = new JButton("清除所有节点");
        buttonClean.setActionCommand("clean");
        buttonClean.addActionListener(this);
        panel.add(buttonClean);

        JButton buttonExport = new JButton("导出文件");
        buttonExport.setActionCommand("export");
        buttonExport.addActionListener(this);
        panel.add(buttonExport);
        label = new JLabel("Action");
        treePanel.add(panel, BorderLayout.NORTH);
        treePanel.add(scrollPane, BorderLayout.CENTER);
        treePanel.add(label, BorderLayout.SOUTH);
        treePanel.setPreferredSize(new Dimension(300, -1));
        //右键菜单
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItemNew = new JMenuItem("新增子节点");
        JMenuItem menuItemDel = new JMenuItem("删除当前节点及子节点");
        menu.add(menuItemNew);
        menu.add(menuItemDel);
        //菜单项绑定监听
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                DefaultMutableTreeNode parentNode = null;
                CustomTreeNode customTreeNode = new CustomTreeNode(System.currentTimeMillis(), "新节点");
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(customTreeNode);
                newNode.setAllowsChildren(true);
                TreePath parentPath = tree.getSelectionPath();
                if (parentPath == null) {
                    return;
                }
                // 取得新节点的父节点
                parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                // 由DefaultTreeModel的insertNodeInto()方法增加新节点
                treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
                // tree的scrollPathToVisible()方法在使Tree会自动展开文件夹以便显示所加入的新节点。若没加这行则加入的新节点
                // 会被 包在文件夹中，你必须自行展开文件夹才看得到。
                tree.scrollPathToVisible(new TreePath(newNode.getPath()));
                label.setText("新增节点成功");
            }
        });

        menuItemDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                TreePath treepath = tree.getSelectionPath();
                int opt = JOptionPane.showConfirmDialog(rootPane, "是否删除当前节点及子节点？");
                if (opt == JOptionPane.OK_OPTION) {
                    if (treepath != null) {
                        // 下面两行取得选取节点的父节点.
                        DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) treepath.getLastPathComponent();
                        TreeNode parent = (TreeNode) selectionNode.getParent();
                        if (parent != null) {
                            // 由DefaultTreeModel的removeNodeFromParent()方法删除节点，包含它的子节点。
                            TreeNode[] path = selectionNode.getPath();
                            StringBuffer pathStr = new StringBuffer(Constant.IMG_PATH);
                            for (int i = 0; i < path.length - 1; i++) {
                                pathStr.append(path[i]);
                                pathStr.append("/");
                            }
                            pathStr.append(path[path.length - 1]);
                            if (selectionNode.isLeaf()) {
                                pathStr.append(Constant.IMG_TYPE);
                                File file = new File(pathStr.toString());
                                file.delete();
                            } else {
                                File file = new File(pathStr.toString());
                                FileUtil.deleteFileDir(file);
                            }

                            treeModel.removeNodeFromParent(selectionNode);
                            label.setText("删除节点成功");
                        }
                    }
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                super.mouseClicked(e);
                int x = e.getX();
                int y = e.getY();

                if (e.getButton() == MouseEvent.BUTTON3) {
                    TreePath pathForLocation = tree.getPathForLocation(x, y);//获取右键点击所在树节点路径
                    if (pathForLocation != null) {
                        tree.setSelectionPath(pathForLocation);
                        menu.show(tree, x, y);
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    TreePath pathForLocation = tree.getPathForLocation(x, y);

                    Object selectNode = tree.getLastSelectedPathComponent();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
                    CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
                    String path = Constant.IMG_PATH + userObject.getImgName() + Constant.IMG_TYPE;
                    if (pathForLocation != null) {
                        tree.setSelectionPath(pathForLocation);
                        showPic(path);
                    }
                    if (menu.isVisible()) {
                        menu.setVisible(false);
                    }
                }
            }
        });
    }

    /**
     * @Description 获取json数据信息并构建树
     * @Date 11:37 2021/7/21
     **/
    public void getTreeData() {
        File dir = new File(Constant.BASE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(Constant.BASE_PATH + "data.json");
        if (!file.exists()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(defaultRoot);
            tree = new JTree(root);
            return;
        }

        JSONObject data = DataUtil.getData();
        if (data == null) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(defaultRoot);
            tree = new JTree(root);
            return;
        }
        generateTreeNode(data, null);
    }


    /**
     * @Description 功能树存为Json
     * @Date 16:58 2021/7/21
     **/
    private String parseTreeNode(TreeNode node) {
        if (node == null) {
            throw new RuntimeException("节点不能为空");
        }
        StringBuilder nodeBuilder = new StringBuilder("{");
        // 把Map中的键值对构造成json对象属性
        DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) node;
        CustomTreeNode userObject = (CustomTreeNode) mutableTreeNode.getUserObject();

        nodeBuilder.append("\"userObject\":" + userObject.toJsonString() + ",");

        // 构造子节点
        nodeBuilder.append("\"children\":[");

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode child = node.getChildAt(i);
            if (child == null) {
                continue;
            }
            nodeBuilder.append(parseTreeNode(child) + ",");
        }
        // 去掉末尾逗号
        if (nodeBuilder.charAt(nodeBuilder.length() - 1) == ',') {
            nodeBuilder.deleteCharAt(nodeBuilder.length() - 1);
        }
        nodeBuilder.append("]");
        nodeBuilder.append("}");
        return nodeBuilder.toString();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if ("export".equals(ae.getActionCommand())) {
            label.setText("export");
        }
        if ("clean".equals(ae.getActionCommand())) {
            // 下面一行，由DefaultTreeModel的getRoot()方法取得根节点.
            int opt = JOptionPane.showConfirmDialog(rootPane, "清除所有节点及截图？");
            if (opt == JOptionPane.OK_OPTION) {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                // 下面一行删除所有子节点.
                TreeNode[] path = rootNode.getPath();
                StringBuffer pathStr = new StringBuffer(Constant.IMG_PATH);
                for (int i = 0; i < path.length - 1; i++) {
                    pathStr.append(path[i]);
                    pathStr.append("/");
                }
                pathStr.append(path[path.length - 1]);
                if (rootNode.isLeaf()) {
                    pathStr.append(Constant.IMG_TYPE);
                    File file = new File(pathStr.toString());
                    file.delete();
                } else {
                    File file = new File(pathStr.toString());
                    FileUtil.deleteFileDir(file);
                }

                rootNode.removeAllChildren();
                // 删除完后务必运行DefaultTreeModel的reload()操作，整个Tree的节点才会真正被删除.
                treeModel.reload();
                label.setText("清除所有节点成功");
            }
        }
        if ("newPic".equals(ae.getActionCommand())) {
            frame.setExtendedState(Frame.ICONIFIED);

            Object selectNode = tree.getLastSelectedPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;

            ScreenCut.ShowScreenCut(this, node);
        }
        if ("delPic".equals(ae.getActionCommand())) {
            int opt = JOptionPane.showConfirmDialog(rootPane, "是否删除当前截图？");
            if (opt == JOptionPane.OK_OPTION) {
                Object selectNode = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
                CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
                File file = new File(Constant.IMG_PATH + userObject.getImgName() + Constant.IMG_TYPE);
                if (file.exists()) {
                    file.delete();
                }
                showPic("");
                frame.repaint();
            }
        }

    }

    /**
     * @Description 截图完成后执行展示操作
     * @Date 10:21 2021/7/21
     **/
    public void actionForPicChange(DefaultMutableTreeNode node) {
        CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
        showPic(userObject.getImgName());
        frame.setExtendedState(Frame.NORMAL);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
//        TreePath treePath = e.getTreePath();
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
//        try {
//            int[] index = e.getChildIndices();
//            node = (DefaultMutableTreeNode) node.getChildAt(index[0]);
//        } catch (NullPointerException exc) {
//
//        }
//        label.setText(nodeName + "更改数据为:" + node.getUserObject().toString());
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        writeDataToFile();
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        writeDataToFile();
    }

    /**
     * @Description 把树结构Json串写入文件
     * @Date 10:21 2021/7/21
     **/
    public void writeDataToFile() {
        TreeNode rootNode = (TreeNode) treeModel.getRoot();
        String json = parseTreeNode(rootNode);
        DataUtil.setData(json);
    }


    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        writeDataToFile();
    }

    class MouseHandle extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            try {
                JTree tree = (JTree) e.getSource();
                int rowLocation = tree.getRowForLocation(e.getX(), e.getY());
                TreePath treepath = tree.getPathForRow(rowLocation);
                TreeNode treenode = (TreeNode) treepath.getLastPathComponent();
                nodeName = treenode.toString();
            } catch (NullPointerException ne) {
            }
        }
    }

    class TreeCellEditorAction implements CellEditorListener {
        @Override
        public void editingStopped(ChangeEvent e) {
            Object selectNode = tree.getLastSelectedPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
            CellEditor cellEditor = (CellEditor) e.getSource();
            String newName = (String) cellEditor.getCellEditorValue();

            //修改node中的功能名
            CustomTreeNode treeNode = (CustomTreeNode) node.getUserObject();
            treeNode.setFunctionName(newName);
            node.setUserObject(treeNode);

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.nodeStructureChanged(node);
        }

        @Override
        public void editingCanceled(ChangeEvent e) {
            editingStopped(e);
        }
    }

    /**
     * @Description 根据树路径的到文件路径的字符串
     * @Date 10:18 2021/7/21
     **/
    private String getFilePath(TreePath treePath) {
        String s = treePath.toString();
        String substring = s.substring(1, s.length() - 1);
        String s1 = substring.replaceAll(", ", "/");
        return s1;
    }

}
