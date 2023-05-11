package com.zlg.pressurer2.pojo;

public class InfoModel {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InfoModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
