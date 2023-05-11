package com.zlg.pressurer2.common;

import java.util.HashMap;

/**
 * 云端设备密钥
 * @author linyouru
 */
public class DeviceSecret {

    private static final HashMap<String,String> DEVICES_SECRET;

    static {
        DEVICES_SECRET = new HashMap<>();
        DEVICES_SECRET.put("invert","ASfa@#regksajFAwvI)");
        DEVICES_SECRET.put("candtu-200","QIOFDSA8#FsQEW21&^=");
        DEVICES_SECRET.put("candtu-400","QIOFDSA9#FsQEW21&^=");
        DEVICES_SECRET.put("can-common","QIOFDSA8#FsQEW21&^=");
    }

    public static String getDeviceSecret(String deviceType){
        return DEVICES_SECRET.get(deviceType);
    }

}
