package com.weds.collegeedu.datafile;

import android.util.Log;
import android.weds.lip_library.util.Strings;


import com.weds.collegeedu.files.LoadFileInterface;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lip on 2016/11/11.
 * <p>
 * 数据处理基类
 */

public class BaseFile {
    private int dataSize = 0;
    private Map<String, List<String>> dataMaps = new HashMap<>();
    private Map<String, String> fileHeader = null;//"key","0"
    private Map<String, String> setFunction = null;//"0","key"

    private String fileName;
    private String separator;
    private String fileIndex;
    private String fingerIndex;
    private String fingerPath;
    private String versionIndex;

    public BaseFile(String fileName, String separator, String fileIndex, String fingerIndex, String fingerPath, String versionIndex) {
        this.fileName = fileName;
        this.separator = separator;
        this.fileIndex = fileIndex;
        this.fingerIndex = fingerIndex;
        this.fingerPath = fingerPath;
        this.versionIndex = versionIndex;
    }

    public BaseFile(Object classObject, String fileName, String separator, String fileIndex, String fingerIndex, String fingerPath, String versionIndex) {
        this.fileName = fileName;
        this.separator = separator;
        this.fileIndex = fileIndex;
        this.fingerIndex = fingerIndex;
        this.fingerPath = fingerPath;
        this.versionIndex = versionIndex;
        if (classObject == null) {
            return;
        }

        // 加载指定的类
        try {
            Field fFileHeader, fsetFunction;
            Class c = classObject.getClass();
            fsetFunction = c.getDeclaredField("setFunction");
            fFileHeader = c.getDeclaredField("fileHeader");
            this.fileHeader = (Map<String, String>) fsetFunction.get(classObject);
            this.setFunction = (Map<String, String>) fFileHeader.get(classObject);
        } catch (Exception e) {
            this.fileHeader = new HashMap<>();
            this.setFunction = new HashMap<>();
            e.printStackTrace();
        }
    }

    public int LoadDataToMemory() {
        int ret = 0;
        int i = 0;
        //解析文件标头
        setFunction = (Map<String, String>) new HashMap<String, String>();
        fileHeader = (Map<String, String>) new HashMap<String, String>();
        //装载文件
        ret = LoadFileInterface.getInstence().FileIndexOperationLoad(fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
        if (ret <= 0) {
            return ret;
        }
        //获取文件标头
        String fileHeaderData = LoadFileInterface.getInstence().FileIndexOperationGetTitle(fileName);
        String[] fileHeaderArray = fileHeaderData.split(separator);
        if (fileHeaderArray.length < 1) {
            ret = 0;
            return ret;
        }
        for (i = 0; i < fileHeaderArray.length - 1; i++) {
            fileHeader.put(fileHeaderArray[i + 1], String.valueOf(i));
            setFunction.put(String.valueOf(i), fileHeaderArray[i + 1]);
        }
        return ret;
    }

    /**
     * 根据键值获取数据
     *
     * @param key
     * @param index
     * @return
     */
    public String GetData(String key, int index) {
        synchronized (this) {
            String data = "";
            if (dataMaps == null || Strings.isEmpty(key) || !dataMaps.containsKey(key) || dataMaps.get(key) == null) {
                return data;
            }
            if (index > dataMaps.get(key).size()) {
                return data;
            }
            try {
                data = dataMaps.get(key).get(index);
            } catch (Exception e) {
                data = "";
            }
            return data;
        }
    }

    public String GetData(String key) {
        return GetData(key, 0);
    }

    public int GetDataSize() {
        return dataSize;
    }

    /**
     * 根据键值更新数据
     *
     * @param key
     * @param values
     */
    public void SetData(String key, String values) {
        List<String> strings = dataMaps.get(key);
        if (strings == null) {
            strings = new ArrayList<>();
        }
        strings.add(values);
        synchronized (this) {
            dataMaps.put(key, strings);
        }
    }

    /**
     * 更新类成员数据内容
     *
     * @param Data
     */
    public void SetDataToClass(String Data) {
        int i = 0;
        String[] dataArray = Data.split(separator);
        for (i = 0; i < dataArray.length; i++) {
            SetData(setFunction.get(String.valueOf(i)), dataArray[i]);
        }
    }

    public void ClearDataMaps() {
        synchronized (this) {
            dataMaps.clear();
        }
    }

    /**
     * 方法：
     * 增加一行数据，有该行数据，删除+新增
     * 参数：
     * char *file_name - 文件名
     * char *value - 行数据
     * 返回值：
     * 1 -成功 -1 -失败
     * int FileIndexOperation_AddRow(char *file_name,char *value);
     */
    public int FileIndexOperationAddRow(String rowData) {
        int ret = 0;

        ret = LoadFileInterface.getInstence().FileIndexOperationAddRow(fileName, rowData);
        if (ret <= 0) {
            ret = 0;
        }
        return ret;
    }

    /**
     * 方法：
     * 删除行数据
     * 参数：
     * char *file_name -文件名
     * int index_id -索引序号
     * char *col_value -索引关键字
     * char *row_data -行数据
     * 返回值：
     * 1 -成功 -1 -失败
     * int FileIndexOperation_DeleteRow(char *file_name,int index_id,char *col_value,char *row_data);
     * sxq备注:每次增加删除同一行会出现很多空行
     */
    public int FileIndexOperationDeleteRow(String indexId, String indexKey, String rowData) {
        int ret = 0;
        String sindexId = null;

        if (fileHeader == null) {
            return ret;
        }
        sindexId = fileHeader.get(indexId);

        ret = LoadFileInterface.getInstence().FileIndexOperationDeleteRow(fileName, sindexId, indexKey, rowData);
        if (ret <= 0) {
            ret = 0;
        }
        return ret;
    }


    /**
     * 方法：
     * 修改行数据
     * 参数：
     * char *file_name -文件名
     * int index_id -索引序号
     * char *col_value -索引关键字
     * char *value -行数据
     * 返回值：
     * 1 -成功 -1 -失败
     * int FileIndexOperation_UpdateRow(char*file_name, int index_id, char*col_value, char*value);
     */
    public int FileIndexOperationUpdateRow(String indexId, String indexKey, String rowData) {
        int ret = 0;
        String sindexId = null;

        if (fileHeader == null) {
            return ret;
        }
        sindexId = fileHeader.get(indexId);
        ret = LoadFileInterface.getInstence().FileIndexOperationUpdateRow(fileName, sindexId, indexKey, rowData);
        if (ret <= 0) {
            ret = 0;
        }
        return ret;
    }

    /**
     * 根据列索引精确查找数据
     *
     * @param indexId
     * @param indexKey
     * @return
     */
    public int FileIndexOperationFind(String indexId, String indexKey) {

        int ret = 0;
        String sData = null;
        String sindexId = null;

        if (fileHeader == null) {
            return ret;
        }
        sindexId = this.fileHeader.get(indexId);
        if (sindexId == null) {
            return ret;
        }
        sData = LoadFileInterface.getInstence().FileIndexOperationFind(fileName, sindexId, indexKey);
        if (sData.equals("")) {
            return ret;
        }
        synchronized (this) {
            dataMaps.clear();
            SetDataToClass(sData);
            dataSize = 1;
        }
        ret = 1;
        return ret;
    }

    /**
     * 方法：
     * 设置查询条件
     * 参数：
     * char *file_name -文件名
     * char *oper -操作符[>, >=, <, <=, ==]
     * int index_id -索引列序号
     * char *col_value -索引关键字
     * 返回值：
     * 1 -成功 -1 -失败
     * int FileIndexOperation_SetFilter(char*file_name, char*oper, int index_id, char*col_value);
     */
    public int FileIndexOperationSetFilter(String oper, String indexId, String indexKey) {
        int ret = 0;
        String sindexId = null;

        if (fileHeader == null) {
            return ret;
        }
        sindexId = fileHeader.get(indexId);
        if (sindexId == null) {
            return ret;
        }
        ret = LoadFileInterface.getInstence().FileIndexOperationSetFilter(fileName, oper, sindexId, indexKey);
        if (ret <= 0) {
            return 0;
        }
        return ret;
    }

    public int FileIndexOperationSetFilter(String oper, String indexId, String indexKey, String startRow, String rows) {
        int ret = 0;
        ret = FileIndexOperationSetFilter(oper, indexId, indexKey);
        if (ret == 0) {
            return 0;
        }
        ret = FileIndexOperationGetFilterRows(startRow, rows);
        return ret;
    }

    /*
    方法：
        读取多行查询结果数据
    参数：
        char *file_name -文件名
        int startrow -开始行
        int rows -读取行数
        char *value -返回的数据
    返回值：
        1 -成功 -1 -失败
    int FileIndexOperation_GetFilterRows(char*file_name, int startrow, int rows, char*value);
*/
    public int FileIndexOperationGetFilterRows(String startRow, String rows) {

        String sData = null;

        sData = LoadFileInterface.getInstence().FileIndexOperationGetFilterRows(fileName, startRow, rows);
        if (!Strings.isNotEmpty(sData)) {
            return 0;
        }
        synchronized (this) {
            dataSize = 0;
            dataMaps.clear();
            String[] raws = sData.split("\n");
            for (String raw : raws) {
                SetDataToClass(raw);
                dataSize += 1;
            }
        }
        return dataSize;
    }

    /**
     * 方法：
     * 读取多行数据
     * 参数：
     * char *file_name -文件名
     * int startrow -开始行
     * int rows -读取行数
     * char *value -返回的数据
     * 返回值：
     * 1 -成功 -1 -失败
     * int FileIndexOperation_GetRows(char*file_name, int startrow, int rows, char*value);
     */
    public int FileIndexOperationGetRows(String startRow, String rowCount) {
        String sData = null;

        sData = LoadFileInterface.getInstence().FileIndexOperationGetRows(fileName, startRow, rowCount);
        if (!Strings.isNotEmpty(sData)) {
            return 0;
        }
        synchronized (this) {
            dataSize = 0;
            dataMaps.clear();
            String[] raws = sData.split("\n");
            for (String raw : raws) {
                SetDataToClass(raw);
                dataSize += 1;
            }
        }
        return dataSize;
    }

    public long FileIndexOperationGetRowsCount() {
        long ret = 0;
        ret = LoadFileInterface.getInstence().GetFileRowsCount(fileName);
        return ret;
    }

    public long GetFileSize() {
        long ret = 0;
        ret = LoadFileInterface.getInstence().GetFileSize(fileName);
        return ret;
    }

    public int FileDelete() {
        int ret = 1;

        File moveFile = new File(fileName);
        if (moveFile.exists()) {
            moveFile.delete();
        }
        return ret;
    }
}
