package com.grgbanking.supplier.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuPeng on 2016/9/23.
 */
public class forwardUserInfo implements Serializable {
    //    id	string	被转发人id
//    name	string	被转发人姓名
//    phone	string	被转发人手机
//    userType	string	被转发人用户类型
//    userRoleId	string	被转发人用户角色id
//    roleName	string	角色名称
//    supplierName	string	供应商名称
//    depart	string	部门
    private static final long serialVersionUID = -758459502806858414L;
    private String id;
    private String name;
    private String phone;
    private String userType;
    private String userRoleId;
    private String roleName;
    private String supplierName;
    private String depart;
    private String coordinates;
    private String jobOrderNum;
    /**
     * 精度
     */
    private double latitude;
    /**
     * 纬度
     */
    private double longitude;

    public String getId() {
        return id;
    }

    public forwardUserInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public forwardUserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public forwardUserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getUserType() {
        return userType;
    }

    public forwardUserInfo setUserType(String userType) {
        this.userType = userType;
        return this;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public forwardUserInfo setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public forwardUserInfo setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public forwardUserInfo setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        return this;
    }

    public String getDepart() {
        return depart;
    }

    public forwardUserInfo setDepart(String depart) {
        this.depart = depart;
        return this;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public forwardUserInfo setCoordinates(String coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public String getJobOrderNum() {
        return jobOrderNum;
    }

    public forwardUserInfo setJobOrderNum(String jobOrderNum) {
        this.jobOrderNum = jobOrderNum;
        return this;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public static List<forwardUserInfo> infos = new ArrayList<>();

    static
    {
        infos.add(new forwardUserInfo(22.544151, 113.955796, "英伦贵族小旅馆",
                "123456789", "5"));
        infos.add(new forwardUserInfo(34.242952, 108.972171, "沙井国际洗浴会所",
                "515555284", "5"));
        infos.add(new forwardUserInfo(34.242852, 108.973171, "五环服装城",
                "123456845", "5"));
        infos.add(new forwardUserInfo(34.242152, 108.971971, "老米家泡馍小炒",
                "511002584", "5"));
    }

    public forwardUserInfo()
    {
    }

    public forwardUserInfo(double latitude, double longitude, String name,
                String phone, String jobOrderNum)
    {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phone = phone;
        this.jobOrderNum = jobOrderNum;
    }
}
