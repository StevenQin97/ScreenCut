package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class DataUtil {
    public static String getData(){
        String jsonStr = readJsonFile("/data.json");
        if(jsonStr != null){
            JSONObject jobj = JSON.parseObject(jsonStr);
        }
        return null;
    }
    public static String setData(){
        return null;
    }
    public boolean cleanData(){
        return true;
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

}
