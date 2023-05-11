package com.zlg.pressurer2.pojo;

public class DeviceInfo {

    private Integer id;
    private String third_things_id;
    private Integer tenant_id;
    private String tenant_name;
    private String name;
    private Integer is_online;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThird_things_id() {
        return third_things_id;
    }

    public void setThird_things_id(String third_things_id) {
        this.third_things_id = third_things_id;
    }

    public Integer getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(Integer tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getTenant_name() {
        return tenant_name;
    }

    public void setTenant_name(String tenant_name) {
        this.tenant_name = tenant_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIs_online() {
        return is_online;
    }

    public void setIs_online(Integer is_online) {
        this.is_online = is_online;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", third_things_id='" + third_things_id + '\'' +
                ", tenant_id=" + tenant_id +
                ", tenant_name='" + tenant_name + '\'' +
                ", name='" + name + '\'' +
                ", is_online=" + is_online +
                '}';
    }
}
