package com.ethons;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.ho.yaml.Yaml;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws Exception{
       Map<String , Object> map = new HashMap();
        loadTitle(map);
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + map.get("dataUrl") + "/" + map.get("dataTableName");
        String userName = (String) map.get("dataUserName");
        String passWord = (String) map.get("dataPassWord");
        Connection connection = DriverManager.getConnection(url, userName, passWord);
        Statement statement = connection.createStatement();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, "%", null, new String[]{"TABLE"});
        ArrayList<Object> list = new ArrayList();
        while (tables.next()){
            Map<String , Object> tableMap = new HashMap<String, Object>();
            String tableName = tables.getString("TABLE_NAME");
            String sql = "SHOW CREATE TABLE " + tableName;
            ResultSet resultSet = statement.executeQuery(sql);
            String tableMean = "";
            while (resultSet.next()){
                String comment = resultSet.getString(2);
                String[] split = comment.split("COMMENT='");
                if(split.length > 1){
                    tableMean = split[1].replaceAll("'" , "");
                }
            }
            tableMap.put("name" , tableMean + " " +tableName);
            list.add(tableMap);
            ArrayList<Object> columnList = new ArrayList();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()){
                Map<String , Object> columnMap = new HashMap<String, Object>();
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String columnSize = columns.getString("COLUMN_SIZE");
                String columnMean = columns.getString("REMARKS");
                columnMap.put("columnName" , columnName);
                columnMap.put("columnType" , columnType.toLowerCase());
                columnMap.put("columnSize" , columnSize);
                columnMap.put("columnMean" , columnMean);
                columnList.add(columnMap);
            }
            tableMap.put("columnList" , columnList);
        }
        map.put("tableList" , list);
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/src/main/resources"));
        Template template = configuration.getTemplate("demo.kt.ftl", "utf-8");
        File outFile = new File((String) map.get("outFile"));
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"), 10240);
        template.process(map, out);
        out.close();
    }

    private static void loadTitle( Map<String , Object> map) throws Exception{
        File file = new File(System.getProperty("user.dir") + "/src/main/resources/application.yaml");
        HashMap hashMap = Yaml.loadType(file, HashMap.class);
        map.put("name" , hashMap.get("count"));
        map.put("outFile" , hashMap.get("outFile"));
        String str = hashMap.get("data").toString();
        Map<String, Object> stringObjectMap = splitStr(str);
        map.put("dataUrl" , stringObjectMap.get("url"));
        map.put("dataUserName" , stringObjectMap.get("userName"));
        map.put("dataPassWord" , stringObjectMap.get("passWord"));
        map.put("dataTableName" , stringObjectMap.get("tableName"));
    }


    private static Map<String , Object> splitStr(String str){
        str = str.replaceAll("\\{" ,"").replaceAll("\\}" , "");
        Map<String , Object> map = new HashMap<String, Object>();
        String[] split = str.split(",");
        for (int i = 0 ; i < split.length; i++){
            String[] split1 = split[i].split("=");
            map.put(split1[0].trim() , split1[1].trim());
        }
        return map;
    }
}


