package android.weds.lip_library.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


// TODO

/**
 * FileUtils
 * -----------------------------------------------------------------------------------------------------------------------------------
 * kjframe
 *
 * @author YRain
 */
public final class FileUtils {

    public FileUtils() {
    }

    public static boolean checkSDcard() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static void saveFileCache(byte[] fileData, String folderPath, String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        FileOutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] e = new byte[1024];
                boolean len = false;

                int len1;
                while (-1 != (len1 = is.read(e))) {
                    os.write(e, 0, len1);
                }

                os.flush();
            } catch (Exception var12) {
                throw new RuntimeException(FileUtils.class.getClass().getName(), var12);
            } finally {
                closeIO(new Closeable[]{is, os});
            }
        }

    }

    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator + fileNmae);

        try {
            file.createNewFile();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return file;
    }

    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    public static File getSaveFolder(String folderName) {
        File file = new File(getSDCardPath() + File.separator + folderName + File.separator);
        file.mkdirs();
        return file;
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        } else {
            byte[] in2b = null;
            BufferedInputStream in = new BufferedInputStream(inStream);
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            boolean rc = false;

            try {
                int rc1;
                while ((rc1 = in.read()) != -1) {
                    swapStream.write(rc1);
                }

                in2b = swapStream.toByteArray();
            } catch (IOException var9) {
                var9.printStackTrace();
            } finally {
                closeIO(new Closeable[]{inStream, in, swapStream});
            }

            return in2b;
        }
    }


    public static void copyFile(File from, File to) {
        if (from != null && from.exists()) {
            if (to != null) {
                FileInputStream is = null;
                FileOutputStream os = null;

                try {
                    is = new FileInputStream(from);
                    if (!to.exists()) {
                        to.createNewFile();
                    }

                    os = new FileOutputStream(to);
                    copyFileFast(is, os);
                } catch (Exception var8) {
                    throw new RuntimeException(FileUtils.class.getClass().getName(), var8);
                } finally {
                    closeIO(new Closeable[]{is, os});
                }

            }
        }
    }

    public static void copyFileFast(FileInputStream is, FileOutputStream os) throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0L, in.size(), out);
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            Closeable[] var4 = closeables;
            int var3 = closeables.length;

            for (int var2 = 0; var2 < var3; ++var2) {
                Closeable cb = var4[var2];

                try {
                    if (cb != null) {
                        cb.close();
                    }
                } catch (IOException var6) {
                    throw new RuntimeException(FileUtils.class.getClass().getName(), var6);
                }
            }

        }
    }

    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        } else {
            File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }

            BufferedOutputStream out = null;

            try {
                out = new BufferedOutputStream(new FileOutputStream(filePath), 8192);
                isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (FileNotFoundException var9) {
                var9.printStackTrace();
            } finally {
                closeIO(new Closeable[]{out});
            }

            return isSuccess;
        }
    }

    public static String readFile(String filePath) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            Log.i("获取文件异常",e.toString());
//            throw new RuntimeException(FileUtils.class.getName() + "readFile---->" + filePath + " not found");
        }

        return inputStream2String(is);
    }

    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;

        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception var4) {
            throw new RuntimeException(FileUtils.class.getName() + ".readFileFromAssets---->" + name + " not found");
        }

        return inputStream2String(is);
    }

    public static String inputStream2String(InputStream is) {
        if (is == null) {
            return null;
        } else {
            StringBuilder resultSb = null;

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                resultSb = new StringBuilder();

                String len;
                while ((len = br.readLine()) != null) {
                    resultSb.append(len);
                }
            } catch (Exception var7) {
                ;
            } finally {
                closeIO(new Closeable[]{is});
            }

            return resultSb == null ? null : resultSb.toString();
        }
    }

    /**
     * 读取文本文件
     *
     * @param filePath
     * @return
     */
    public static String readTextFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(filePath);
            InputStream in = null;
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                sb.append((char) tempbyte);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 根据图片名获取id
     */
    public static int getMipmapImgId(String imageName, Context context) {
        int id = 0;
        if (Strings.isEmpty(imageName)) {
            return id;
        }
        id = context.getResources().getIdentifier(imageName, "mipmap", context.getPackageName());
        Log.i("天气资源文件", imageName + "----" + id + "---" + context.getPackageName());
        return id;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 创建文件
     *
     * @param fileName 文件绝对路径
     * @return
     */
    public static boolean CheckAndCreateFile(String fileName) {
        File file = new File( fileName );
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.isFile() || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public static boolean isMove = true;

    /**
     * 删除文件夹和文件夹里面的文件
     */
    public static void deleteDir(String dirName) {

        File dir = new File(dirName);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        List<File> list = listFileSortByModifyTime(dirName);

        for (File file : list) {
            if (!isMove){
                break;
            }
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(dirName); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file){

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }
}
