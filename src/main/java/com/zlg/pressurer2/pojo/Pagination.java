package com.zlg.pressurer2.pojo;

public class Pagination {

    private Integer current_page;
    private Integer page_size;
    private Integer total_size;

    public Integer getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(Integer current_page) {
        this.current_page = current_page;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    public Integer getTotal_size() {
        return total_size;
    }

    public void setTotal_size(Integer total_size) {
        this.total_size = total_size;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "current_page=" + current_page +
                ", page_size=" + page_size +
                ", total_size=" + total_size +
                '}';
    }
}
