package com.zlg.pressurer2.common;

import java.util.HashMap;

/**
 * 云端设备密钥
 * @author linyouru
 */
public class DeviceSecret {

    public static final HashMap<String,String> DEVICES_SECRET;

    static {
        DEVICES_SECRET = new HashMap<>();
        DEVICES_SECRET.put("invert","12345678");
        DEVICES_SECRET.put("can-common","12345678");
    }

}
