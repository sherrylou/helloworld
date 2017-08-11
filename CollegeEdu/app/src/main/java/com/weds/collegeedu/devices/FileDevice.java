package com.weds.collegeedu.devices;

import android.content.Intent;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.utils.WedsDataUtils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */

public class FileDevice {

    public static final int OptionTypeOne = 1;
    public static final int OptionTypeTwo = 2;
    public static final int OptionTypeThree = 3;
    public static final int OptionTypeFour = 4;

    /**
     * 创建文件,不存在时创建,存在不处理
     */
    public static boolean CreateFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.mkdirs();
        }
        return true;
    }

    /**
     * 获取文件夹下的所有文件
     */
    public static List<File> GetDirectoryFileList(File dirFile) {
        List<File> resFileList = new ArrayList<>();
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                resFileList.add(dirFile);
                return resFileList;
            } else if (dirFile.isDirectory()) {
                File[] childFile = dirFile.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return resFileList;
                }
                for (File f : childFile) {
                    resFileList.addAll(GetDirectoryFileList(f));
                }
            }
        }
        return resFileList;
    }

    public static boolean RemoveFiles(File file) {
        try {
            if (A23.SafeRmFile(WedsDataUtils.changerStr2C(file.getAbsolutePath() + " "), 1) == 0)
                return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 拷贝文件函数。
     * newpath:源路径
     * oldpath:目标路径
     * type:
     * 1 拷贝目录下的文件,目标目录请带着文件名
     * 2 拷贝目录下的文件夹
     * 3 拷贝目录下的文件和文件夹
     * 4 带着当前目录一起拷
     * 返回值：0  成功，其他失败
     */
    public static int CopyFiles(String srcFile, String desFile, int type) {
        String sDefFile = "", sSrcFile = "";

        sDefFile = desFile + " ";
        sSrcFile = srcFile + " ";
        int ret = 0;

        try {
            ret = A23.SafeCpFile(WedsDataUtils.changerStr2C(sSrcFile), WedsDataUtils.changerStr2C(sDefFile), type);
        } catch (Exception e) {

        }
        if (ret == 0) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * 删除数据基础函数
     *
     * @param filename
     */
    public static void DeleteDataFromDevice(String filename) {
        List<File> files = FileDevice.GetDirectoryFileList(new File(filename));
        int size = files.size();
        Intent intent = new Intent("proChange");
        if (size > 0) {
            for (int i = 0; i < files.size(); i++) {
                boolean delete = files.get(i).delete();
                if (delete) {
//                LogUtils.i("FIle>>>>>>>>>>>>", files.get(i) + "----" + files.get(i).isFile());
//                if (files.get(i).isFile()) {
                    //删除成功
                    int progress = (i + 1) * 100 / size;
                    LogUtils.i("FIle>>>>>>>>>>>>", progress + "----");
                    intent.putExtra("progress", progress);
                    App.getContext().sendBroadcast(intent);
                }
            }
        } else {
            intent.putExtra("progress", 100);
            App.getContext().sendBroadcast(intent);
        }
    }


    public static void copyFile(File sourcefile, File targetFile) throws IOException {

        CreateDir(targetFile, 1);
        //新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);
        BufferedInputStream inbuff = new BufferedInputStream(input);
        //新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);
        //缓冲数组
        byte[] b = new byte[1024 * 5];
        int len = 0;
        while ((len = inbuff.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }
        //刷新此缓冲的输出流
        outbuff.flush();
        //关闭流
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File( oldPath );
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream( oldPath ); //读入原文件
                FileOutputStream fs = new FileOutputStream( newPath );
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read( buffer )) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    LogUtils.i( "Readlenght", bytesum + "" );
                    fs.write( buffer, 0, byteread );
                }
                inStream.close();
                fs.flush();
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e( "ERROR", e.getMessage().toString() );
        }
        FileDevice.syncFlash();

    }


    /**
     * 强制同步flash
     */
    public static void syncFlash() {
        A23.SdkSync();
    }

    /**
     * 创建文件完整路径
     * @param file fileType:1-文件;2-文件夹
     */
    public static void CreateDir(File file, Integer fileType) {
        if (fileType == 1) {
            File parentFile = file.getParentFile();
            if (parentFile.exists() && parentFile.isDirectory()) {
                return;
            } else {
                CreateDir(parentFile, 2);
            }
        } else if (fileType == 2) {
            file.mkdirs();
        }
    }

    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        //新建目标目录
        (new File(targetDir)).mkdirs();
        //获取源文件夹当下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                //源文件
                File sourceFile = file[i];
                //目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                //准备复制的源文件夹
                String dir1 = sourceDir + file[i].getName();
                //准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
