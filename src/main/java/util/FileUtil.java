package util;

import java.io.File;

public class FileUtil {
    public static void deleteFileDir(File file) {
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFileDir(f);
            } else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
}
