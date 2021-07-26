package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.Constant;

import java.io.*;

public class DataUtil {

    public static JSONObject getData(){
        String jsonStr = readJsonFile(Constant.BASE_PATH + "data.json");
        JSONObject jobj = null;
        if(jsonStr != null && !"".equals(jsonStr)){
            try{
                jobj = JSON.parseObject(jsonStr);
            }catch (Exception e){
                System.out.println("json结构转换失败");
            }
        }
        return jobj;
    }

    public static void setData(String data){
        File file = new File(Constant.BASE_PATH + "data.json");
        writeStringToFile(file,data);
    }

    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeStringToFile(File file,String data) {
        //若文件已经存在，则删除后重新写入
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());;
        }


        BufferedWriter writer = null;
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
