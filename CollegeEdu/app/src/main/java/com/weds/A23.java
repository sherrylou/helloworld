package com.weds;
/*
@author:sxq
此接口为通过jni调用libfun库的接口。
 接口中函数的命名/参数/注释 和libfun库中保持一致；
但也有不同，区别有2点，如下：
 1：命名区别：libfun中函数如果包含符号"_",将转换为其后的首字母大写，并去掉"_",如果已经为大写，只取掉"_";//2015.8.10
 例如
        int serial_close(int com);
 转换为
        int serialClose(int com);

2:数据类型区别： char 类型改变为byte;//2015.8.10
  例如：
        int device_recv_data(int port, char *value);
  转换为
        int deviceRecvData(int uart_port,byte[] value);


    public void callBack(byte[] instruct, byte[] param);
    public void progresscallBack(byte[] instruct, byte[] param);
 1    static public native int mf1ReadSectors(int uart_port, int beg_sectors, int end_sectors, int beg_block, int end_block, byte keymode, byte[] key, byte[] value);
 2    static public native int mf1WriteSectors(int uart_port, int beg_sectors, int end_sectors, int beg_block, int end_block, byte keymode, byte[] key, byte[] value);
 3    static public native int deviceRecvData(int uart_port, byte[] value);
 4    static public native int serialInit(int port, int baut, int workmode, int address);
 5    static public native int serialClose(int com);
 6    static public native int wedsgpioOpen();
 7    static public native int wedsgpioIoctl(int set);
 8    static public native int wedsgpioClose();
 9    static public native int FileIndexOperationLoad(byte[] file_name, byte[] spca, byte[] index_col, byte[] finger_index, byte[] finger_path, int version_index);
10    static public native int FileIndexOperationAddRow(byte[] file_name, byte[] value);
11    static public native int FileIndexOperationDeleteRow(byte[] file_name, int index_id, byte[] col_value, byte[] row_data);
12    static public native int FileIndexOperationUpdateRow(byte[] file_name, int index_id, byte[] col_value, byte[] value);
13    static public native int FileIndexOperationFind(byte[] file_name, int index_id, byte[] col_value, byte[] value);
14    static public native int FileIndexOperationFindEx(byte[] file_name, int index_id, byte[] col_value, byte[] value, int truncation_mode, int truncation_count);
15    static public native int FileIndexOperationGetRows(byte[] file_name, int startrow, int rows, byte[] value);
16    static public native int FileIndexOperationSetFilter(byte[] file_name, byte[] oper, int index_id, byte[] col_value);
17    static public native int FileIndexOperationGetFilterRows(byte[] file_name, int startrow, int rows, byte[] value);
18    static public native int FileIndexOperationGetRowsCount(byte[] file_name);
19    static public native int FileIndexOperationGetVersion(byte[] file_name);
20    static public native int FileIndexOperationSetVersion(byte[] file_name, int versionno);
21    static public native void jupkAppInit(int dev_type, byte[] interface_name, byte[] serverip, int serverport, int bindPort, byte[] serid, int istcp, int avoidtime, int hearttime);
22    static public native int jupkRtp2Process();
23    static public native int jupkUploadRecordStart(byte jchan, final byte[] file_list);
24    static public native int jupkUploadRecordStop(byte jchan, final byte[] file_list);
25    static public native int jupkUploadFileStart(byte jchan, final byte[] file_list, byte[] local_path, byte[] server_path);
26    static public native int jupkUploadFileStop(byte jchan, final byte[] file_list);
27    static public native int jupkUploadRecordMode(byte jchan, int mode);
28    static public native void setUnlockKey();
29    static public native void sendNowcmdAck(int jsig_num, int jret_int, byte[] jret_str);
30    static public native int sendDosqlInfo(byte jchan, byte jmode, byte jsql_cmd, final byte[] jsql_cmdline);
31    static public native void setPadInfo(byte[] ver, byte[] sn, byte[] photo_style, byte[] finger_style, byte[] other);
32    static public native int getlinkState();
33    static public native int checkLogin(int islogin);
34    static public native int jupkReadJdevId();
35    static public native int getlinkLock();
36    static public native int jrecWrite(final byte[] jfile_path, final byte[] jrec_str);
37    static public native int jrecRead(final byte[] jfile_path, byte[] jbuf, int jbuf_size, int jmax_count);
38    static public native int jrecReadAck(final byte[] jfile_path);
39    static public native int jrecGetRemain(final byte[] jfile_path);
40    static public native int jrecGetAllCount(final byte[] jfile_path);
41    static public native int jrecCleanUp(final byte[] jfile_path);
42    static public native int jrecRemainReset(final byte[] jfile_path);
43    static public native int jrecGetRows(final byte[] jfile_path, int jstart, int jmax_count, byte[] jbuf, int jbuf_size);
44    static public native int jrecSetFilter(final byte[] jfile_path, int jcol, final byte[] jBeginDate, final byte[] jEndDate);
45    static public native int jrecGetFilterRows(final byte[] jfile_path, int jstart, int jmax_count, byte[] jbuf, int jbuf_size);

*/

//import com.weds.test.WedsActivity;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.collegeedu.App;
import com.weds.collegeedu.datainterface.AttendanceInterface;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.ClientInfoInterface;
import com.weds.collegeedu.datainterface.CurPersonnelInterface;
import com.weds.collegeedu.datainterface.StudentInterface;
import com.weds.collegeedu.datainterface.TeacherGroupInterface;
import com.weds.collegeedu.datainterface.TeacherInterface;
import com.weds.collegeedu.devices.GpioDevice;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.devices.NetWorkProtocol;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.utils.GetTime;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weds.collegeedu.resfile.EventConfig.RefreshAttendanceStatistics;
import static com.weds.collegeedu.resfile.EventConfig.Retime;

//import java.io.FileReader;
//import java.util.StringTokenizer;
//import java.util.concurrent.locks.ReentrantLock;

//import java.io.File;
//import java.io.PrintWriter;

public class A23 {

    //private MainSetting allSetting = MainSetting.get();

    static public byte[] qcallBackinstruct, qcallBackparam;
    static public byte[] qprogresscallBackinstruct, qprogresscallBackparam;


    /*回调函数
    * 回调的结果，保存到4个数组中。
    *供其他类调用，例如：  txtview1.setText("callback:" + new String(A23.qcallBackinstruct) + ":" + new String(A23.qcallBackparam));

    * */
    static {
        try {
            System.loadLibrary("wedsa23");
        } catch (UnsatisfiedLinkError e) {
            LogUtils.i("A23", "i:can't load libwedsa23.so!");
        }
    }

    /**
     * @param uart_port   串口号
     * @param beg_sectors 开始扇区号 (0-15)
     * @param end_sectors 结束扇区号(0-15)
     * @param beg_block   开始扇块号 (0-3)
     * @param end_block   结束扇块号(0-3)
     * @param keymode     0X01:A-key,0X02:B-key
     * @param key         密钥值（32Byte字节）
     * @param value       返回读取到的扇区内容
     * @return
     * @chinese 使用weds协议读取扇区内容
     * @endchinese
     * @english read sector of MF1
     * @endenglish int mf1_read_sectors(int uart_port,int beg_sectors,int end_sectors,int beg_block,int end_block,char keymode,char *key,char *value);
     */
    static public native int mf1ReadSectors(int uart_port, int beg_sectors, int end_sectors, int beg_block, int end_block, byte keymode, byte[] key, byte[] value);

    /**
     * @param uart_port   串口号
     * @param beg_sectors 开始扇区号 (0-15)
     * @param end_sectors 结束扇区号(0-15)
     * @param beg_block   开始扇块号 (0-3)
     * @param end_block   结束扇块号(0-3)
     * @param keymode     0X01:A-key,0X02:B-key
     * @param key         密钥值
     * @param value       写入扇区的内容
     * @return
     * @chinese 使用weds协议写入扇区内容
     * @endchinese
     * @english write sector of MF1
     * @endenglish int mf1_write_sectors(int uart_port,int beg_sectors,int end_sectors,int beg_block,int end_block,char keymode,char *key, char *value)
     */
    static public native int mf1WriteSectors(int uart_port, int beg_sectors, int end_sectors, int beg_block, int end_block, byte keymode, byte[] key, byte[] value);

    /**
     * 设置textcars规约格式
     * head:0 data:1-10 verify bit:11 end:12
     * @param t_start 协议起始位
     * @param t_end 协议结束位
     * @param t_len 协议长度
     * @param d_start 有效数据起始位
     * @param d_len 有效数据长度
     * @return 1 成功 -1 失败
     */
    static public native int setTextCardMode(int t_start, int t_end, int t_len, int d_start, int d_len);
    //////////////////////以下为接口        //////////////////////////////

    /**
     * @param uart_port 串口号
     * @param value     读取到的串口数据
     * @return success-instruction,fail-ERROR
     * @chinese 根据weds协议, 读取串口数据
     * @endchinese
     * @english According to weds protocol,read data from serial
     * @endenglish int device_recv_data(int port, char *value);
     */
    static public native int deviceRecvData(int uart_port, byte[] value);


    // 初始化串口,添加weds协议

    /**
     * @param port
     * @param baut     串口波特率，取值范围[2400,4800,9600,19200,38400,57600,115200]
     * @param workmode 工作方式(0-客户端,1-服务器)
     * @param address  服务器地址,取值范围[0-255]
     * @return Success 文件描述符.fail:FALSE
     * int serial_init(int port, int baudrate, int workmode, int address);
     */
    static public native int serialInit(int port, int baut, int workmode, int address);

    /**
     * @param com 串口号
     * @return 成功-SUCCESS，失败-ERROR
     * @chinese 关闭串口
     * @endchinese int serial_close(int com);
     */
    static public native int serialClose(int com);

    static public native int getNetState(final byte[] interface_name);

    static public native int setNetState(final byte[] interface_name, int flag);

    static public native int getIp(final byte[] interface_name, byte[] ip);

    static public native int setIp(final byte[] interface_name, byte[] ip);

    static public native int getMacAddr(final byte[] interface_name, byte[] mac);

    static public native int setMacAddr(final byte[] interface_name, byte[] mac);

    static public native int getNetMask(final byte[] interface_name, byte[] netmask);

    static public native int setNetMask(final byte[] interface_name, byte[] netmask);

    static public native int getGateWay(final byte[] interface_name, byte[] gateway);

    static public native int setGateWay(final byte[] interface_name, byte[] gateway);

    static public native int InitUdpSockfd(byte[] interface_name, int port, int over_time, byte[] server_ip);

    static public native int UdpSendData(byte[] server_ip, int server_port, byte[] command, byte[] value, int crypt);

    static public native int CoseUdpSockfd();

    static public native int UdpRecvData();

    //打开  gpio
//    extern int wedsgpio_open(void);
    static public native int wedsgpioOpen();


    //设置  gpio
//    extern int wedsgpio_ioctl(int set);
    static public native int wedsgpioIoctl(int set);


    //关闭  gpio
//    extern int wedsgpio_close(void);
    static public native int wedsgpioClose();

    /**
     * //读取串行设备数据
     */
    static public native int ReadDevicesData(byte[] devices_type, byte[] value, byte[] value_type);

    /*
    方法：
        装载文件
    参数：
        char *file_name -文件名
        char *spca -分隔符
        char *index_col -索引列 （列+分隔符+...+列）
        char *finger_index -指纹列
        char *finger_path -指纹路径
        int version_index -版本列
    返回值：
        1 -成功 -1 -失败
        int FileIndexOperation_Load(char *file_name,char *spca,char *index_col,char *finger_index,char *finger_path,int version_index);
*/
    static public native int FileIndexOperationLoad(byte[] file_name, byte[] spca, byte[] index_col, byte[] finger_index, byte[] finger_path, int version_index);


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
    static public native int FileIndexOperationAddRow(byte[] file_name, byte[] value);


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
    static public native int FileIndexOperationDeleteRow(byte[] file_name, int index_id, byte[] col_value, byte[] row_data);


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
    static public native int FileIndexOperationUpdateRow(byte[] file_name, int index_id, byte[] col_value, byte[] value);


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
    static public native int FileIndexOperationFind(byte[] file_name, int index_id, byte[] col_value, byte[] value);


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
    static public native int FileIndexOperationFindEx(byte[] file_name, int index_id, byte[] col_value, byte[] value, int truncation_mode, int truncation_count);


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
    static public native int FileIndexOperationGetRows(byte[] file_name, int startrow, int rows, byte[] value);


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
    static public native int FileIndexOperationSetFilter(byte[] file_name, byte[] oper, int index_id, byte[] col_value);


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
    static public native int FileIndexOperationGetFilterRows(byte[] file_name, int startrow, int rows, byte[] value);


    /*
        方法：
            获取文件总行数
        参数：
            char *file_name -文件名
        返回值：行数
    int FileIndexOperation_GetRowsCount(char*file_name);
*/
    static public native int FileIndexOperationGetRowsCount(byte[] file_name);


    /*
    方法：
        获取文件版本号
    参数：
        char *file_name -文件名
    返回值：版本号
    int FileIndexOperation_GetVersion(char*file_name);
*/
    static public native int FileIndexOperationGetVersion(byte[] file_name);


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
    static public native int FileIndexOperationSetVersion(byte[] file_name, int versionno);


    //装载标志
    //int FileIndexOperation_GetLoadStat(char*file_name);
    static public native int FileIndexOperationGetLoadStat(byte[] file_name);

    /*
        方法：
            获取所加载文件的标题头
        参数：
            char *file_name -文件名
            char *title    返回的标题头
        返回值：
            0 -成功 -1 -失败
    */
    static public native int FileIndexOperationGetTitle(byte[] file_name, byte[] title);

    //初始化UPK协议栈调用
//设备类型，网络接口名称，服务器IP，服务器端口，本机绑定端口，服务器ID
//    void jupk_app_init(int dev_type,char *interface_name,char *serverip, int serverport, int bindPort,char *serid,int istcp,int avoidtime,int hearttime)
    static public native void jupkAppInit(int dev_type, byte[] interface_name, byte[] serverip, int serverport, int bindPort, byte[] serid, int istcp, int avoidtime, int hearttime);


    //主轮询
/*处理通讯进度的回掉函数*/
//      void jupk_rtp2_process(CallBack callback, ProgressCallBack pcallback);
    static public native int jupkRtp2Process();


    /*    实时记录发送启动
        参数：
        jchan--数据发送通道号
        file_list--数据文件名称
        jk_32 jupk_upload_record_start(jk_u8 jchan,const jk_char * file_list);
        */
    static public native int jupkUploadRecordStart(byte jchan, final byte[] file_list);


    /*    实时记录发送停止
        参数：
        jchan--数据发送通道号
        file_list--数据文件名称
        jk_32 jupk_upload_record_stop(jk_u8 jchan,const jk_char * file_list);;
      */
    static public native int jupkUploadRecordStop(byte jchan, final byte[] file_list);


    /*   实时文件发送启动
       jchan :通道
       file_list:文件名称
       local_path:文件绝对路径
       server_pat:上传到服务器路径
           jk_32 jupk_upload_file_start(jk_u8 jchan,const jk_char * file_list,char *local_path,char *server_path);
           sxq备注:最后2个路径参数没有起作用,数据在pc中未找到.
   */
    static public native int jupkUploadFileStart(byte jchan, final byte[] file_list, byte[] local_path, byte[] server_path);


    /*实时文件发送停止
    jchan :通道
    file_list:文件名称
        jk_32 jupk_upload_file_stop(jk_u8 jchan,const jk_char * file_list);
*/
    static public native int jupkUploadFileStop(byte jchan, final byte[] file_list);


    /*    记录传送模式
        jchan:通道
        mode：
                #define JUPK_DSQL_CHK_CH 0x00 //检查当前通道是否释放(该选项必须独立使用)
                #define JUPK_DSQL_ONLYRUN 0x01 //不关心是否成功或丢包,无返回,也不会占用通道(该选项必须独立使用)
                #define JUPK_DSQL_RET_INT 0x02 //需要返回整数
                #define JUPK_DSQL_RET_INT_STR 0x06 //需要返回整数和字符串
                #define JUPK_DSQL_CLR_CH 0x08 //释放通道,上次的执行将不会返回结果,丢包也不会重发
                #define JUPK_DSQL_NO_RESEND 0x10 //不带重发,不占用通道,但下次使用时会忽略上条语句
                #define JUPK_DSQL_QUEUE 0x20 //队列执行SQL,可避免两条同类语句同时执行(影响服务器效率)

    jk_32 jupk_upload_record_mode(jk_u8 jchan,int mode);
    */
    static public native int jupkUploadRecordMode(byte jchan, int mode);


    /*
        实时系统密钥解除绑定
        void set_unlock_key();
    */
    static public native void setUnlockKey();


    /*    立即指令回应(标记值,指令执行结果整形,指令执行结果字符串)
        void send_nowcmd_ack(jk_u32 jsig_num,jk_32 jret_int,jk_char * jret_str)
    */
    static public native void sendNowcmdAck(int jsig_num, int jret_int, byte[] jret_str);


    /*    实时系统发送指令
        执行设备SQL(连接号,通道,模式,SQL命令标示,SQL命令字符串)
    jmode参见宏定义,用'|'连接
    返回值:0:执行成功,1:通道空闲,-1:上一条指令未完成,-2:字符串超出范围,-3:jstyle超出范围,-4:参数不正确
        int send_dosql_info(jk_u8 jchan,jk_u8 jmode,jk_u8 jsql_cmd,const jk_char * jsql_cmdline);
    */
    static public native int sendDosqlInfo(byte jchan, byte jmode, byte jsql_cmd, final byte[] jsql_cmdline);


    /*    实时系统设置系统信息
        ////版本号，设备序列号，照片类型，指纹类型，其他
        void set_pad_info(char *ver,char *sn,char *photo_style,char *finger_style,char *other);
    */
    static public native void setPadInfo(byte[] ver, byte[] sn, byte[] photo_style, byte[] finger_style, byte[] other);


    /*    实时系统获取连接状态
        获取联机状态,0:未联机,1:已联机
        int  getlink_state();
    */
    static public native int getlinkState();


    /*    登陆验证
        isloging:1-登录成功，0-失败
        int check_login(int islogin);
    */
    static public native int checkLogin(int islogin);


    /*获取设备ID
        unsigned int  jupk_read_jdev_id();
    */
    static public native int jupkReadJdevId();


    /*  获取绑定状态,0:从未联机,1:未绑定,2:已绑定
        int getlink_lock();
    */
    static public native int getlinkLock();

    //写记录
    //int jrec_write(const char * jfile_path,const char * jrec_str);
    static public native int jrecWrite(final byte[] jfile_path, final byte[] jrec_str);

    //读记录
    //int jrec_read(const char * jfile_path,char * jbuf,int jbuf_size,int jmax_count);
    //sxq备注:对于记录文件,如果中间文件变化,除非整理否则记录结果还是未覆盖前的
    static public native int jrecRead(final byte[] jfile_path, byte[] jbuf, int jbuf_size, int jmax_count);

    //标记已读记录
    //int jrec_read_ack(const char * jfile_path);
    static public native int jrecReadAck(final byte[] jfile_path);

    //读取剩余记录数
    //int jrec_get_remain(const char * jfile_path);
    static public native int jrecGetRemain(final byte[] jfile_path);

    //读取所有记录数
    //int jrec_get_all_count(const char * jfile_path);
    static public native int jrecGetAllCount(final byte[] jfile_path);

    //整理
    //int jrec_clean_up(const char * jfile_path);
    static public native int jrecCleanUp(final byte[] jfile_path);

    //复位剩余指针
    //int jrec_remain_reset(const char * jfile_path);
    static public native int jrecRemainReset(final byte[] jfile_path);


    //获取任意行
    //int jrec_GetRows(const char * jfile_path,int jstart,int jmax_count,char * jbuf,int jbuf_size);
    static public native int jrecGetRows(final byte[] jfile_path, int jstart, int jmax_count, byte[] jbuf, int jbuf_size);

    //过滤时间
    //int jrec_SetFilter(const char * jfile_path,int jcol,const char *jBeginDate,const char *jEndDate);
    static public native int jrecSetFilter(final byte[] jfile_path, int jcol, final byte[] jBeginDate, final byte[] jEndDate);

    //获取过滤的任意行
    //int jrec_GetFilterRows(const char * jfile_path,int jstart,int jmax_count,char * jbuf,int jbuf_size);
    static public native int jrecGetFilterRows(final byte[] jfile_path, int jstart, int jmax_count, byte[] jbuf, int jbuf_size);

    public static String byte2String(byte[] bstr, String encode) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (byte b : bstr) {
            if (b == 0) break;
            bout.write(b);
        }
        try {
            return new String(bout.toByteArray(), encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // extern int init_camera_devices(int width, int height);
    static public native int initCameraDevices(int width, int height);

    // int get_camera_image(char *path);
    static public native int getCameraImage(byte[] path);

    // int camera_close(void);
    static public native int cameraClose();

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    static int globe_nAllRec = 0;

    /*
    gpio 相关函数
    */
    //int gpio_read_android(int fd, int pin);
    static public native int gpioReadAndroid(int fd, int value);

    //int gpio_write_android(int fd, int pin ,int value);
    static public native int gpioWriteAndroid(int fd, int pin, int value);

    //初始化 gpio
    static public native int initGpioDevices(byte[] path);

    //打开某个GPIO设备
    static public native int setGpioOn(int device_type);

    //关闭某个GPIO设备
    static public native int setGpioOff(int device_type);

    //GPIO 看守函数
    static public native int threadWatchGpio();

    //GPIO 操作函数
    static public native int optionGpio(char opt, int msec, int devices_type);

    //获取某个ＧＰＩＯ的状态
    static public native int getImportStat(int devices_type);

    //获取lib库版本号
    static public native int GetLibrayVersion(byte[] data);

    /*
    *获取文件大小
    *filename 文件路径
    * 返回值：成功：文件字节数；失败：-1
     */
    static public native long GetFileSize(byte[] filename);

    /**
     * 初始化 uart 数据
     */
    static public native int InitUartDevices(int uart_port, int baudrate, byte[] devices_type);

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
    static public native int SafeCpFile(byte[] newpath, byte[] oldpath, int type);

    /**
     * 删除文件函数
     * newpath:要删除的路径名称：
     * 1 删除目录下的文件
     * 2 删除目录下的文件夹
     * 3 删除目录下的文件和文件夹
     * 4 带着当前目录一起删除
     * 返回值：0 成功，其他失败
     */
    static public native int SafeRmFile(byte[] newpath, int type);

    /**
     * 文件同步
     *
     * @return
     */
    static public native int SdkSync();

    static public native int InitUdhcpDevices(byte[] interface_name, int bg_flag);

    static public native int CloseUdhcpDevices();

    /**
     *  维根输出
     */
    static public native int InitWiegandOut();
    static public native int SendWiegandData(byte[] data, int bytes);
    static public native int CloseWiegan_out();
    static public native int SendWiegandDataBcd(byte[]data,int len);


    /**
     * 第一个回调函数  jupk_rtp2_process (CallBack callback, ProgressCallBack pcallback);
     *
     * @param instruct
     * @param param
     */
    public void callBack(byte[] instruct, byte[] param) {
        NetWorkProtocol.getInstence().communicationCallBack(instruct,param);
    }

    //第二个回调函数  jupk_rtp2_process (CallBack callback, ProgressCallBack pcallback);
    public void progresscallBack(byte[] instruct, byte[] param) {
        LogUtils.i("progress:", new String(instruct) + ";" + new String(param));
        //也可以直接操作其他类中的信息,不建议如此操作
        //WedsActivity.this.txtview2.setText("progresscallback:" + new String(instruct) + ":" + new String(param));
        try {
            Runtime.getRuntime().exec(new String(instruct));
            LogUtils.i("a23", new String(param));
        } catch (Exception e) {
            LogUtils.i("a23", "progresscallbackerr!");
        }
    }

}