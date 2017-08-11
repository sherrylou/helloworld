package com.weds.collegeedu.datainterface;

import com.weds.collegeedu.entity.ClientInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by lxy on 2016/12/9.
 */

public class ClientInfoInterface {

    private static String fileName = AppArchivePath + "client_info.ini";
    private static ClientInfo clientInfoContent;
    private static ClientInfoInterface clientInfoInterface;

    public static ClientInfoInterface getInstence() {
        if (clientInfoInterface == null) {
            clientInfoInterface = new ClientInfoInterface();
        }
        return clientInfoInterface;
    }


    public void loadClientInfoToData() {
        List<String> clientList = new ArrayList<>();
        String lineContent;
        int line = 0;
        String[] nr = new String[2];
        nr[1] = "0";
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((lineContent = reader.readLine()) != null) {
                if (line == 0) {
                    nr = lineContent.split("=");
                } else {
                    clientList.add(lineContent);
                }
                line++;
            }
            clientInfoContent = new ClientInfo(nr[1], clientList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public ClientInfo getClientInfoContent() {
        return clientInfoContent;
    }
}
