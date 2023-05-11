package com.zlg.pressurer2.common;

import com.zlg.pressurer2.pojo.DeviceInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 全局租户设备列表，用于压测时获取设备token
 */
@Component
public class GlobalDeviceList {

    private ArrayList<DeviceInfo> allDeviceInfoList;

    public ArrayList<DeviceInfo> getAllDeviceInfoList() {
        return this.allDeviceInfoList;
    }

    public void setAllDeviceInfoList(ArrayList<DeviceInfo> allDeviceInfoList) {
        this.allDeviceInfoList = allDeviceInfoList;
    }

    public void clearAllDeviceInfoList() {
        if (null != allDeviceInfoList) {
            allDeviceInfoList.clear();
        }
    }

}
