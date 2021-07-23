package home;

import lombok.Data;

/**
 * @author Marvin
 */

@Data
public class CustomTreeNode {
    /**
     * 节点对应图片的名字（暂定取时间戳加文件后缀作为完整文件名）
     */
    private String imgName = "";
    /**
     * 功能名，用于树节点展示
     */
    private String functionName = "";

    @Override
    public String toString() {
        return functionName;
    }

    public String toJsonString(){
        return "{\"imgName\":" + imgName + "\"functionName\":"+ functionName +"}";
    }
}
