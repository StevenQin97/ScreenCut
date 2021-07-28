package export;
import common.Constant;
import util.FileUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

/**
 * @author qinh
 */
public class FileExport extends JDialog implements ActionListener {
    JFrame frame = new JFrame("导出文件");
    JPanel panel;
    JLabel label;
    JTextField text;
    JButton buttonChooseFile;
    JFileChooser jfc = new JFileChooser();
    JButton buttonExport;

    public FileExport() {
        jfc.setCurrentDirectory(new File("d://"));
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 150);
        init();
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void init(){
        panel = new JPanel();
        JPanel  subPanel = new JPanel();
        JPanel  subPanel1 = new JPanel();

        label = new JLabel("导出文件目录");
        text = new JTextField();
        text.setPreferredSize(new Dimension(100,25));
        buttonChooseFile = new JButton("...");
        buttonExport = new JButton("导出");

        subPanel.add(label);
        subPanel.add(text);
        subPanel.add(buttonChooseFile);
        subPanel1.add(buttonExport);

        panel.add(subPanel, BorderLayout.CENTER);
        panel.add(subPanel1, BorderLayout.SOUTH);


        buttonChooseFile.addActionListener(this);
        buttonExport.addActionListener(this);
    }
    /**
     * 事件监听的方法
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.getSource().equals(buttonChooseFile)) {
            jfc.setFileSelectionMode(1);
            int state = jfc.showOpenDialog(null);
            if (state == 1) {
                return;
            } else {
                File f = jfc.getSelectedFile();
                text.setText(f.getAbsolutePath());
            }
        }
        if (e.getSource().equals(buttonExport)) {
            if(text.getText()==null||"".equals(text.getText())){
                JOptionPane.showMessageDialog(null, "请选择文件路径", "错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            File source = new File(Constant.BASE_PATH);
            if(!source.exists()){
                JOptionPane.showMessageDialog(null, "未获取到本地文件数据", "错误",JOptionPane.ERROR_MESSAGE);
                return;
            }

            File targetDir = new File(text.getText());
            if(!targetDir.exists()){
                //此处判断目标文件夹是否存在，尝试过自己建立文件夹，有可能导致不规范输入，所以此处限制为选择已经存在的文件夹
                JOptionPane.showMessageDialog(null, "目标文件夹不存在", "错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            File target = new File(text.getText()+"/screenCut/");
            if(target.exists()){
                JOptionPane.showMessageDialog(null, "该目录下已存在同名文件夹，请更换目录", "错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                FileUtil.copyFolder(Constant.BASE_PATH,text.getText());
                JOptionPane.showMessageDialog(null, "导出成功", "success",JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    }
}
