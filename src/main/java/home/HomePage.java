package home;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.Constant;
import screencut.ScreenCut;
import util.DataUtil;
import export.FileExport;
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

public class HomePage extends JDialog implements ActionListener, TreeModelListener {
    private JPanel treePanel;
    private JPanel imgPanel;
    private JPanel panel;
    private JFrame frame;
    private JLabel imgLabel;
    private JTree tree;

    private JButton buttonClean;
    private JButton buttonExport;
    private JButton btnDelPic;
    private JButton btnNewPic;

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

        btnDelPic = new JButton("删除截图");
        buttonPanel.add(btnDelPic);
        btnDelPic.addActionListener(this);

        btnNewPic = new JButton("添加截图");
        buttonPanel.add(btnNewPic);
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
    private void showPic(String imgName) {
        if (imgName != null) {
            String path = Constant.IMG_PATH + imgName + Constant.IMG_TYPE;
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
        treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.addTreeModelListener(this);

        tree.getCellEditor().addCellEditorListener(new TreeCellEditorAction());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);
        JPanel panel = new JPanel();
        buttonClean = new JButton("清除所有节点");
        buttonClean.addActionListener(this);
        panel.add(buttonClean);

        buttonExport = new JButton("导出文件");
        buttonExport.addActionListener(this);
        panel.add(buttonExport);
        treePanel.add(panel, BorderLayout.NORTH);
        treePanel.add(scrollPane, BorderLayout.CENTER);
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
                            java.util.List<String> imgNameList = new java.util.ArrayList<String>();
                            getImgNameList(selectionNode,imgNameList);
                            for(String imgName : imgNameList){
                                File file = new File (Constant.IMG_PATH + imgName + Constant.IMG_TYPE);
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                            treeModel.removeNodeFromParent(selectionNode);
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

                    if (pathForLocation != null) {
                        tree.setSelectionPath(pathForLocation);
                        Object selectNode = tree.getLastSelectedPathComponent();
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
                        CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
                        showPic(userObject.getImgName());
                    }

                    if (menu.isVisible()) {
                        menu.setVisible(false);
                    }
                }
            }
        });
    }

    public void getImgNameList(DefaultMutableTreeNode selectionNode,java.util.List imgNameList){
        CustomTreeNode userObject = (CustomTreeNode) selectionNode.getUserObject();
        imgNameList.add(userObject.getImgName());
        for(int i = 0;i<selectionNode.getChildCount();i++){
            getImgNameList((DefaultMutableTreeNode)selectionNode.getChildAt(i),imgNameList);
        }
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
        if (buttonExport.equals(ae.getSource())) {
            new FileExport();
        }
        if (buttonClean.equals(ae.getSource())) {
            // 下面一行，由DefaultTreeModel的getRoot()方法取得根节点.
            int opt = JOptionPane.showConfirmDialog(rootPane, "清除所有节点及截图？");
            if (opt == JOptionPane.OK_OPTION) {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                // 下面一行删除所有子节点.
                File imgDir = new File(Constant.IMG_PATH);
                FileUtil.deleteFileDir(imgDir);
                rootNode.removeAllChildren();
                // 删除完后务必运行DefaultTreeModel的reload()操作，整个Tree的节点才会真正被删除.
                treeModel.reload();
            }
        }
        if (btnNewPic.equals(ae.getSource())) {
            Object selectNode = tree.getLastSelectedPathComponent();
            if(selectNode != null){
                frame.setExtendedState(Frame.ICONIFIED);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
                ScreenCut.ShowScreenCut(this, node);
            }else{
                JOptionPane.showMessageDialog(null, "请先选择功能点", "提示",JOptionPane.INFORMATION_MESSAGE);
            }

        }
        if (btnDelPic.equals(ae.getSource())) {
            Object selectNode = tree.getLastSelectedPathComponent();
            if(selectNode != null){
                int opt = JOptionPane.showConfirmDialog(rootPane, "是否删除当前截图？");
                if (opt == JOptionPane.OK_OPTION) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
                    CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
                    File file = new File(Constant.IMG_PATH + userObject.getImgName() + Constant.IMG_TYPE);
                    if (file.exists()) {
                        file.delete();
                    }
                    userObject.setImgName("");
                    showPic("");
                    frame.repaint();
                }
            }else{
                JOptionPane.showMessageDialog(null, "请先选择功能点", "提示",JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    /**
     * @Description 截图完成后执行展示操作
     * @Date 10:21 2021/7/21
     **/
    public void actionForPicChange(DefaultMutableTreeNode node) {
        CustomTreeNode userObject = (CustomTreeNode) node.getUserObject();
        writeDataToFile();
        showPic(userObject.getImgName());
        frame.setExtendedState(Frame.NORMAL);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        writeDataToFile();
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

}
