package home;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author Marvin
 */

@Data
public class CustomTreeNode {

    /**
     * 创建时间戳作为node的id
     */
    private Long id;

    /**
     * 节点对应图片的名字（暂定取时间戳加文件后缀作为完整文件名）
     */
    private String imgName;
    /**
     * 功能名，用于树节点展示
     */
    private String functionName;

    @Override
    public String toString() {
        return functionName;
    }

    public String toJsonString(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        stringBuffer.append("\"id\":");
        stringBuffer.append(id);
        stringBuffer.append(",");
        stringBuffer.append("\"imgName\":\"");
        stringBuffer.append(imgName);
        stringBuffer.append("\",");
        stringBuffer.append("\"functionName\":\"");
        stringBuffer.append( functionName);
        stringBuffer.append( "\"}");
        return stringBuffer.toString();
    }

    public CustomTreeNode(Long id,String functionName){
        this.id = id;
        this.functionName = functionName;
        this.imgName = "";
    }

    public CustomTreeNode(JSONObject jsonObject){
        this.id = (Long)jsonObject.get("id");
        this.functionName = (String)jsonObject.get("functionName");
        this.imgName = (String)jsonObject.get("imgName");
    }
}
