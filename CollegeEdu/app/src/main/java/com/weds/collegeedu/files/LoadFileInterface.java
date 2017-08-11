package com.weds.collegeedu.files;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.utils.WedsDataUtils;

/**
 * Created by Administrator on 2016/11/9.
 */

public class LoadFileInterface {

    private static LoadFileInterface loadFileInterface;

    public static LoadFileInterface getInstence() {
        if (loadFileInterface == null) {
            loadFileInterface = new LoadFileInterface();
        }
        return loadFileInterface;
    }

    private static byte[] changerStr2C(String s) {
        byte[] bytes = (s + " ").getBytes();
        bytes[bytes.length - 1] = '\0';
        return bytes;
    }

    /**
     * 进入程序后加载文件（加载哪个写哪个）
     *
     * @param fileName  文件名
     * @param fileIndex 文件索引
     * @return 是否成功
     */
    public int FileIndexOperationLoad(String fileName, String Separator, String fileIndex, String fingerIndex, String fingerPath, String versionIndex) {

        byte[] sFileName, sSeparator, sFileIndex, sFingerIndex, sFingerPath;
        int sVersionIndex = 0, ret = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(Separator) && Strings.isNotEmpty(fileIndex)
                && Strings.isNotEmpty(versionIndex))) {
            LogUtils.i("aaaaa", "装载错误");
            return ret;
        }
        sFileName = new byte[1024];
        sSeparator = new byte[1024];
        sFileIndex = new byte[1024];
        sFingerIndex = new byte[1024];
        sFingerPath = new byte[1024];

        sFileName = changerStr2C(fileName);
        sSeparator = changerStr2C(Separator);
        sFileIndex = changerStr2C(fileIndex);
        sFingerIndex = changerStr2C(fingerIndex);
        sFingerPath = changerStr2C(fingerPath);
        sVersionIndex = Integer.parseInt(versionIndex);

        ret = A23.FileIndexOperationLoad(sFileName, sSeparator, sFileIndex, sFingerIndex, sFingerPath, sVersionIndex);
        LogUtils.i("resultaaaaaaaaaa", fileName + "----------" + ret);
        return ret;
    }

    /*
    方法：
        增加一行数据，有该行数据，删除+新增
    参数：
        char *file_name - 文件名
        char *value - 行数据
    返回值：
        1 -成功 -1 -失败
    int FileIndexOperation_AddRow(char *file_name,char *value);
*/
    public int FileIndexOperationAddRow(String fileName, String addData) {
        int result = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(addData))) {
            return result;
        }
        result = A23.FileIndexOperationAddRow(changerStr2C(fileName), changerStr2C(addData));
        return result;
    }

    /*
    方法：
        删除行数据
    参数：
        char *file_name -文件名
        int index_id -索引序号
        char *col_value -索引关键字
        char *row_data -行数据
    返回值：
        1 -成功 -1 -失败
    int FileIndexOperation_DeleteRow(char *file_name,int index_id,char *col_value,char *row_data);
    sxq备注:每次增加删除同一行会出现很多空行
*/
    public int FileIndexOperationDeleteRow(String fileName, String indexId, String indexKey, String rowData) {
        int ret = 0;
        int bIndexId = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(indexId) && Strings.isNotEmpty(indexKey) && Strings.isNotEmpty(rowData))) {
            return ret;
        }
        bIndexId = Integer.parseInt(indexId);
        ret = A23.FileIndexOperationDeleteRow(changerStr2C(fileName), bIndexId, changerStr2C(indexKey), changerStr2C(rowData));
        return ret;
    }


    /*
    方法：
        修改行数据
    参数：
        char *file_name -文件名
        int index_id -索引序号
        char *col_value -索引关键字
        char *value -行数据
    返回值：
        1 -成功 -1 -失败
  int FileIndexOperation_UpdateRow(char*file_name, int index_id, char*col_value, char*value);
*/
    public int FileIndexOperationUpdateRow(String fileName, String indexId, String indexKey, String rowData) {
        int ret = 0;
        int bIndexId = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(indexId) && Strings.isNotEmpty(indexKey) && Strings.isNotEmpty(rowData))) {
            return ret;
        }
        bIndexId = Integer.parseInt(indexId);
        ret = A23.FileIndexOperationUpdateRow(changerStr2C(fileName), bIndexId, changerStr2C(indexKey), changerStr2C(rowData));
        return ret;
    }

    /*
        方法：
            精确查找行数据
        参数：
            char *file_name -文件名
            int index_id -索引列序号
            char *col_value -索引关键字
            char *value -返回的行数据
        返回值：
            1 -成功 -1 -失败
        int FileIndexOperation_Find(char*file_name, int index_id, char*col_value, char*value);
    */
    public String FileIndexOperationFind(String fileName, String indexId, String indexKey) {
        int ret = 0;
        int bIndexId = 0;
        byte[] bOutData = new byte[1024];
        String sOutData = "";

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(indexId) && Strings.isNotEmpty(indexKey))) {
            return sOutData;
        }
        bIndexId = Integer.parseInt(indexId);
        ret = A23.FileIndexOperationFind(changerStr2C(fileName), bIndexId, changerStr2C(indexKey), bOutData);
        if (ret <= 0) {
            return sOutData;
        }
        return WedsDataUtils.ChangeCode(bOutData);
    }


    /*
        方法：
            精确查找行数据_顺序查找
        参数：
            char *file_name -文件名
            int index_id -索引列序号
            char *col_value -索引关键字
            char *value -返回的行数据
            truncation_mode -截取方式 0-左截取 1-右截取
            int truncation_count -截取位数
        返回值：
            1 -成功 -1 -失败
            sxq备注:未实现最后2个参数
    int FileIndexOperation_Find_ex(char*file_name, int index_id, char*col_value, char*value, int truncation_mode,  int truncation_count);
    */
    public static String FileIndexOperationFindEx(String fileName, String indexId, String indexKey, String truncationMode, String truncationCount) {
        int ret = 0;
        int bIndexId = 0;
        int btruncationMode = 0;
        int btruncationCount = 0;
        byte[] bOutData = new byte[1024];
        String sOutData = "";

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(indexId) && Strings.isNotEmpty(indexKey)
                && Strings.isNotEmpty(truncationMode) && Strings.isNotEmpty(truncationCount))) {
            return sOutData;
        }
        bIndexId = Integer.parseInt(indexId);
        btruncationCount = Integer.parseInt(truncationCount);
        btruncationMode = Integer.parseInt(truncationMode);

        ret = A23.FileIndexOperationFindEx(changerStr2C(fileName), bIndexId, changerStr2C(indexKey), bOutData, btruncationMode, btruncationCount);
        if (ret <= 0) {
            return sOutData;
        }
        return WedsDataUtils.ChangeCode(bOutData);
    }


    /*
    方法：
        读取多行数据
    参数：
        char *file_name -文件名
        int startrow -开始行
        int rows -读取行数
        char *value -返回的数据
    返回值：
        1 -成功 -1 -失败
int FileIndexOperation_GetRows(char*file_name, int startrow, int rows, char*value);
*/
    public String FileIndexOperationGetRows(String fileName, String startRow, String rowCount) {
        int ret = 0;
        int bStartRow = 0;
        int bRowCount = 0;
        String sOutData = "";
        long fileCount = 0, fileSize = 0;
        int byteSize = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(startRow) && Strings.isNotEmpty(rowCount))) {
            return sOutData;
        }
        bStartRow = Integer.parseInt(startRow);
        bRowCount = Integer.parseInt(rowCount);
        fileSize = GetFileSize(fileName);
        fileCount = GetFileRowsCount(fileName);
        if (fileCount == 0) {
            return sOutData;
        }
        byteSize = (int) (fileSize / fileCount) * (bRowCount + 10);
        if (byteSize != 0) {
            byteSize = bRowCount * 1024;
            LogUtils.i("byte大小比对", String.valueOf(byteSize) + "----" + fileSize);
        }
        byte[] bOutData = new byte[((int) fileSize)];

        ret = A23.FileIndexOperationGetRows(changerStr2C(fileName), bStartRow, bRowCount, bOutData);
        if (ret <= 0) {
            return sOutData;
        }
        return WedsDataUtils.ChangeCode(bOutData);
    }


    /*
        方法：
            设置查询条件
        参数：
            char *file_name -文件名
            char *oper -操作符[>, >=, <, <=, ==]
            int index_id -索引列序号
            char *col_value -索引关键字
        返回值：
            1 -成功 -1 -失败
        int FileIndexOperation_SetFilter(char*file_name, char*oper, int index_id, char*col_value);
    */
    public int FileIndexOperationSetFilter(String fileName, String Operator, String indexId, String indexKey) {
        int ret = 0;
        int bIndexId = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(Operator)
                && Strings.isNotEmpty(indexId) && Strings.isNotEmpty(indexKey))) {
            return ret;
        }
        bIndexId = Integer.parseInt(indexId);
        ret = A23.FileIndexOperationSetFilter(changerStr2C(fileName), changerStr2C(Operator), bIndexId, changerStr2C(indexKey));
        if (ret < 0) {
            ret = 0;
        }
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
    public String FileIndexOperationGetFilterRows(String fileName, String startRow, String rowCount) {
        int ret = 0;
        int bStartRow = 0;
        int bRowCount = 0;
        String sOutData = "";
        long fileCount = 0, fileSize = 0;
        int byteSize = 0;

        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(startRow)
                && Strings.isNotEmpty(rowCount))) {
            return sOutData;
        }
        bStartRow = Integer.parseInt(startRow);
        bRowCount = Integer.parseInt(rowCount);
        fileSize = GetFileSize(fileName);
        fileCount = GetFileRowsCount(fileName);
        byteSize = (int) (fileSize / fileCount) * (bRowCount + 10);
        if (byteSize == 0) {
            byteSize = bRowCount * 1024;
        }
        byte[] bOutData = new byte[(int) fileSize];
        ret = A23.FileIndexOperationGetFilterRows(changerStr2C(fileName), bStartRow, bRowCount, bOutData);
        if (ret <= 0) {
            return sOutData;
        }
        return WedsDataUtils.ChangeCode(bOutData);
    }

    /*
        方法：
            获取文件总行数
        参数：
            char *file_name -文件名
        返回值：行数
    int FileIndexOperation_GetRowsCount(char*file_name);
*/
    public long GetFileRowsCount(String fileName) {
        long ret = 0;

        if (!(Strings.isNotEmpty(fileName))) {
            return ret;
        }
        ret = A23.FileIndexOperationGetRowsCount(changerStr2C(fileName));
        return ret;
    }

    /*
    *获取文件大小
    *filename 文件路径
    * 返回值：成功：文件字节数；失败：-1
     */
    public static long GetFileSize(String fileName) {
        long ret = 0;

        if (!(Strings.isNotEmpty(fileName))) {
            return ret;
        }
        ret = A23.GetFileSize(changerStr2C(fileName));
        if (ret <= 0) {
            ret = 0;
            return ret;
        }
        return ret;
    }

    /*
    方法：
        获取文件版本号
    参数：
        char *file_name -文件名
    返回值：版本号
    int FileIndexOperation_GetVersion(char*file_name);
*/
    public int FileIndexOperationGetVersion(String fileName) {
        int ret = 0;
        if (!(Strings.isNotEmpty(fileName))) {
            return ret;
        }
        ret = A23.FileIndexOperationGetVersion(changerStr2C(fileName));
        return ret;
    }


    /*
        方法：
            修改文件版本号
        参数：
            char *file_name -文件名
            int versionno - 版本号
        返回值：0-失败 1-成功
        int FileIndexOperation_SetVersion(char*file_name, int versionno);
        sxq备注:设置版本后再获取仍为旧值;
    */
    public int FileIndexOperationSetVersion(String fileName, String versionNo) {
        int ret = 0;
        int bVersionId = 0;
        if (!(Strings.isNotEmpty(fileName) && Strings.isNotEmpty(versionNo))) {
            return ret;
        }
        bVersionId = Integer.parseInt(versionNo);
        ret = A23.FileIndexOperationSetVersion(changerStr2C(fileName), bVersionId);
        return ret;
    }

    //装载标志
    public int FileIndexOperationGetLoadStat(String fileName) {
        int ret = 0;

        if (!(Strings.isNotEmpty(fileName))) {
            return ret;
        }
        ret = A23.FileIndexOperationGetLoadStat(changerStr2C(fileName));
        return ret;
    }

    /*
        方法：
            获取所加载文件的标题头
        参数：
            char *file_name -文件名
            char *title    返回的标题头
        返回值：
            0 -成功 -1 -失败
    */
    public String FileIndexOperationGetTitle(String fileName) {
        int ret = 0;
        String sOutData = "";
        byte[] bOutData = new byte[1024];

        if (!(Strings.isNotEmpty(fileName))) {
            return sOutData;
        }
        ret = A23.FileIndexOperationGetTitle(changerStr2C(fileName), bOutData);
        if (ret < 0) {
            return sOutData;
        }
        return WedsDataUtils.ChangeCode(bOutData);
    }

}
