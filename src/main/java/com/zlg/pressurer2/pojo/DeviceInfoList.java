package com.zlg.pressurer2.pojo;

import java.util.List;

public class DeviceInfoList {

    private List<DeviceInfo> list;
    private Pagination pagination;

    public List<DeviceInfo> getList() {
        return list;
    }

    public void setList(List<DeviceInfo> list) {
        this.list = list;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "DeviceInfoList{" +
                "list=" + list +
                ", pagination=" + pagination +
                '}';
    }
}
