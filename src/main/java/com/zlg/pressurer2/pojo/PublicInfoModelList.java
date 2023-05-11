package com.zlg.pressurer2.pojo;

import java.util.List;

public class PublicInfoModelList {
    private List<InfoModel> list;
    private Pagination pagination;

    public List<InfoModel> getList() {
        return list;
    }

    public void setList(List<InfoModel> list) {
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
        return "PublicInfoModelList{" +
                "list=" + list +
                ", pagination=" + pagination +
                '}';
    }
}
