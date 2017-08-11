package com.weds.collegeedu.datainterface;


import com.weds.collegeedu.datafile.ClassRoomFile;
import com.weds.collegeedu.entity.ClassRoom;

/**
 * Created by Administrator on 2016/11/22.
 */

public class ClassRoomInterface {
    private ClassRoom classRoom;
    private static ClassRoomInterface classRoomInterface = null;

    public static ClassRoomInterface getInstence() {
        if (classRoomInterface == null) {
            classRoomInterface = new ClassRoomInterface();
        }
        return classRoomInterface;
    }

    /**
     * 获取班级信息
     *
     * @return
     */
    public int LoadDataToClassRoom() {
        int fileLines = 0;
        int ret = 0;

        fileLines = (int) ClassRoomFile.getInstence().FileIndexOperationGetRowsCount();
        ret = ClassRoomFile.getInstence().FileIndexOperationGetRows("0", String.valueOf(fileLines));
        if (ret <= 0) {
            classRoom = new ClassRoom("", "", "", "", "", "", "", "", "", "", "","");
            return 0;
        }
        String sjsbh = ClassRoomFile.getInstence().GetData(ClassRoomFile.jsbh);
        String smc = ClassRoomFile.getInstence().GetData(ClassRoomFile.mc);
        String skclx = ClassRoomFile.getInstence().GetData(ClassRoomFile.kclx);
        String skcmc = ClassRoomFile.getInstence().GetData(ClassRoomFile.kcmc);
        String sbzr = ClassRoomFile.getInstence().GetData(ClassRoomFile.bzr);
        String sbjxh = ClassRoomFile.getInstence().GetData(ClassRoomFile.bjxh);
        String sxxmc = ClassRoomFile.getInstence().GetData(ClassRoomFile.xxmc);
        String sfbzr = ClassRoomFile.getInstence().GetData(ClassRoomFile.fbzr);
        String sbjkh = ClassRoomFile.getInstence().GetData(ClassRoomFile.bjkh);
        String sbzrxy = ClassRoomFile.getInstence().GetData(ClassRoomFile.bzrxy);
        String sbjjj = ClassRoomFile.getInstence().GetData(ClassRoomFile.bjjj);
        String jsrl = ClassRoomFile.getInstence().GetData(ClassRoomFile.jsrl);

        classRoom = new ClassRoom(sjsbh, smc, skclx, skcmc, sbzr, sbjxh, sxxmc, sfbzr, sbjkh, sbzrxy, sbjjj, jsrl);
        return 1;
    }
    public ClassRoom getClassRoom(){
        if (classRoom == null) {
            classRoom = new ClassRoom();
        }
        return classRoom;
    }
}
