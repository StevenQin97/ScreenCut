package home;

import com.google.gson.Gson;
import common.Constant;
import screencut.ScreenCut;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

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

    public  HomePage() {
        frame = new JFrame("功能截图管理工具");
        frame.setSize(1024,800);
        frame.setResizable(false);
        initTree();
        initComponents();
        frame.requestFocus();
        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage();

    }

    public void initComponents(){
        JButton btnDelPic = new JButton("删除截图");
        JButton btnNewPic = new JButton("添加截图");
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(btnDelPic);
        btnDelPic.setActionCommand("delPic");
        btnDelPic.addActionListener(this);

        buttonPanel.add(btnNewPic);
        btnNewPic.setActionCommand("newPic");
        btnNewPic.addActionListener(this);

        imgLabel = new JLabel();
        showPic("");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(imgLabel);
        scrollPane.setBackground(Color.white);

        imgPanel.add(buttonPanel, BorderLayout.NORTH);
        imgPanel.add(scrollPane, BorderLayout.CENTER);
        imgPanel.add(new JLabel("  "), BorderLayout.SOUTH);

    }

    private void showPic(String filepath){
        if(filepath !=null && !"".equals(filepath)){
            String path = Constant.IMG_PATH + filepath + Constant.IMG_TYPE;
            ImageIcon icon = new ImageIcon(path);
            icon.setImage(icon.getImage().getScaledInstance(685, 500, Image.SCALE_DEFAULT));
            imgLabel.setIcon(icon);
        }
    }

    public void getTreeData(){

    }

    public void initTree(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("功能清单");
        tree = new JTree(root);
        tree.setEditable(true);
        tree.addMouseListener(new MouseHandle());
        treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.addTreeModelListener(this);
        tree.getCellEditor().addCellEditorListener(new Tree_CellEditorAction());
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
        treePanel.setPreferredSize(new Dimension(300,-1));

        JPopupMenu menu=new JPopupMenu();		//创建菜单
        JMenuItem menuItemNew=new JMenuItem("新增子节点");//创建菜单项(点击菜单项相当于点击一个按钮)
        JMenuItem menuItemDel=new JMenuItem("删除当前节点及子节点");//创建菜单项(点击菜单项相当于点击一个按钮)
        menu.add(menuItemNew);
        menu.add(menuItemDel);
        //菜单项绑定监听
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                DefaultMutableTreeNode parentNode = null;
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("新节点");
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
                //dosomething
            }
        });

        menuItemDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                TreePath treepath = tree.getSelectionPath();
                if (treepath != null) {
                    // 下面两行取得选取节点的父节点.
                    DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) treepath.getLastPathComponent();
                    TreeNode parent = (TreeNode) selectionNode.getParent();
                    if (parent != null) {
                        // 由DefaultTreeModel的removeNodeFromParent()方法删除节点，包含它的子节点。
                        treeModel.removeNodeFromParent(selectionNode);
                        label.setText("删除节点成功");
                    }
                }
                //dosomething
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                super.mouseClicked(e);
                int x = e.getX();
                int y = e.getY();

                if(e.getButton()==MouseEvent.BUTTON3){
                    //menuItem.doClick(); //编程方式点击菜单项
                    TreePath pathForLocation = tree.getPathForLocation(x, y);//获取右键点击所在树节点路径
                    if(pathForLocation!=null){
                        tree.setSelectionPath(pathForLocation);
                        getFilePath(pathForLocation);
                        menu.show(tree, x, y);
                    }
                }
                if(e.getButton()==MouseEvent.BUTTON1){
                    //menuItem.doClick(); //编程方式点击菜单项
                    TreePath pathForLocation = tree.getPathForLocation(x, y);//获取右键点击所在树节点路径
                    if(pathForLocation!=null){
                        tree.setSelectionPath(pathForLocation);
                        System.out.println();
                        showPic(getFilePath(pathForLocation));
                    }
                }
            }
        });


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


    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("export")) {
            label.setText("export");
        }
        if (ae.getActionCommand().equals("clean")) {
            // 下面一行，由DefaultTreeModel的getRoot()方法取得根节点.
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
            // 下面一行删除所有子节点.
            rootNode.removeAllChildren();
            // 删除完后务必运行DefaultTreeModel的reload()操作，整个Tree的节点才会真正被删除.
            treeModel.reload();
            label.setText("清除所有节点成功");
        }
        if (ae.getActionCommand().equals("newPic")) {
            frame.setExtendedState(Frame.ICONIFIED);
            TreePath treePath = tree.getSelectionPath();
            String filePath = getFilePath(treePath);
            ScreenCut.ShowScreenCut(this,filePath);
        }

    }


    public void actionForPicChange(String filePath){
        showPic(filePath);
        frame.setExtendedState(Frame.NORMAL);
        frame.repaint();
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        TreePath treePath = e.getTreePath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        try {
            int[] index = e.getChildIndices();
            node = (DefaultMutableTreeNode) node.getChildAt(index[0]);
        } catch (NullPointerException exc) {

        }
        label.setText(nodeName + "更改数据为:" + (String) node.getUserObject());
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {

    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {

    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {

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

    class Tree_CellEditorAction implements CellEditorListener {
        @Override
        public void editingStopped(ChangeEvent e) {
            Object selectnode = tree.getLastSelectedPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectnode;
            CellEditor cellEditor = (CellEditor) e.getSource();
            String newName = (String) cellEditor.getCellEditorValue();
            node.setUserObject(newName);
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.nodeStructureChanged(node);
        }

        @Override
        public void editingCanceled(ChangeEvent e) {
            editingStopped(e);
        }
    }

    private String getFilePath(TreePath treePath){
        String s = treePath.toString();
        String substring = s.substring(1, s.length()-1);
        String s1 = substring.replaceAll(", ", "/");
        return s1;
    }

}
