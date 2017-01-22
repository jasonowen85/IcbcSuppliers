package com.grgbanking.supplier.api;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ServerApi {

    /**
     * 用户登陆
     */
    public static void login(String phone, String password, String userRoleId, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("phone", phone));
        list.add(new BasicNameValuePair("password", password));
        list.add(new BasicNameValuePair("userRoleId", userRoleId));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/login", entity, handler);
    }

    /**
     * 获取角色列表
     */
    public static void getRoles(String userid, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("phone", userid));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/getUserRole", entity, handler);
    }

    /**
     * 切换角色
     */
    public static void changeRole(String phone, String userRoleId, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("phone", phone));
        list.add(new BasicNameValuePair("userRoleId", userRoleId));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/switchRoles", entity, handler);
    }


    /**
     * 用户去登陆
     *
     * @param userid
     * @param handler
     */
    public static void logout(String userid, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userid));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/logOut", entity, handler);
    }

    /**
     * 获取用户资料
     */
    public static void getUserInfo(String userId, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/detail", entity, handler);
    }


    /**
     * 更新用户资料
     */
    public static void updateUserInfo(String userId, String phone, String email, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("phone", phone));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/modifyDetail", entity, handler);
    }

    /**
     * 更新密码
     */
    public static void updatePassword(String userId, String oldpassword, String newpassword, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("oldPassword", oldpassword));
        list.add(new BasicNameValuePair("newPassword", newpassword));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/modifyPassword", entity, handler);
    }

    /**
     * 重置密码
     */
    public static void resetPassword(String name, String phone, String email, String password, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("name", name));
        list.add(new BasicNameValuePair("phone", phone));
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("newPassword", password));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supUser/forgetPassword", entity, handler);
    }

    /**
     * 获取工单
     */
    public static void getWorkOrder(String userId, int page, int size, String state, String bankId, String startTime, String endTime, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);//当前页
        params.put("size", size);//页面大小
        params.put("state", state);
        params.put("userId", userId);
        params.put("bankId", bankId);//支行ID
        params.put("startTime", startTime);//开始时间
        params.put("endTime", endTime);//结束时间
        ApiHttpClient.post("/supJobOrder/conditionJobOrderList", params, handler);
    }

    /**
     * 工单详情
     */
    public static void getJobOrderDetails(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/supJobOrder/detail", params, handler);
    }

    /**
     * 2.2.3.工单详情跟踪
     */
    public static void getWorkOrderTracking(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/supJobOrder/track", params, handler);
    }

    /**
     * 转发人列表
     */
    public static void getForwardList(String jobOrderId, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        params.put("userId", userId);//当前页
        ApiHttpClient.post("/supJobOrder/forwardList", params, handler);
    }

    /**
     * 工单转发
     */
    public static void forward(String jobOrderId, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        params.put("userId", userId);//当前页
        ApiHttpClient.post("/supJobOrder/forward", params, handler);
    }

    /**
     * 关闭工单
     */
    public static void closeOrder(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/supJobOrder/closeOrder", params, handler);
    }

    /**
     * 2.2.8.转回
     */
    public static void forwardBack(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);
        ApiHttpClient.post("/supJobOrder/forwardBack", params, handler);
    }

    /**
     * 2.2.7.接单和确认
     */
    public static void comfirmOrder(String jobOrderId, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);
        params.put("userId", userId);
        ApiHttpClient.post("/supJobOrder/comfirmOrder", params, handler);
    }

    /**
     * 2.2.2.工单搜索
     */
    public static void getSearchJobOrder(int page, int size, String userId, String keyWord, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("size", size);
        params.put("userId", userId);
        params.put("keyWord", keyWord);
        ApiHttpClient.post("/supJobOrder/searchJobOrder", params, handler);
    }

    /**
     * 确认完成确认发货
     */
    public static void confirmComplete(String jobOrderId, String userId, String express, String courierNum, String Situation, String imgUrl, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("jobOrderId", jobOrderId));
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("express", express));
        list.add(new BasicNameValuePair("courierNum", courierNum));
        list.add(new BasicNameValuePair("situation", Situation));
        list.add(new BasicNameValuePair("imgUrl", imgUrl));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/supJobOrder/confirmation", entity, handler);
    }

    /**
     * 文件上传
     *
     * @param params
     * @param handler
     */
    public static void uploadSingleFile(RequestParams params, AsyncHttpResponseHandler handler) {
        ApiHttpClient.post("/file/uploadAppFile", params, handler);
    }

    /**
     * 2.2.3.获取支行下拉列表
     *
     * @param
     * @param handler
     */
    public static void bankList(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post("/supJobOrder/bankList", handler);
    }

    /**
     * 签到接口
     */
    public static void sign(String userId, String jobOrderId, String address, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("jobOrderId", jobOrderId);
        params.put("address", address);
        params.put("coordinates","");
        ApiHttpClient.post("/supJobOrder/sign", params, handler);
    }

    /**
     * 2.7.1.提交意见反馈
     */
    public static void feedback(String userId, String advice, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("advice", advice));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/feedback/save", entity, handler);
    }

    public static void download(String dir, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("dir", dir);
        ApiHttpClient.download("", params, handler);
    }

    /**
     * 2.1.12.记录签到路线图
     */
    public static void signLine(String jobOrderId, String userId,String coordinates, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//工单id
        params.put("userId", userId);//用户id
        params.put("coordinates", coordinates);//坐标
        ApiHttpClient.post("/supJobOrder/signLine", params, handler);
    }

    /**
     * 2.1.15.实时定位
     */
    public static void userPosition(String coordinates, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("coordinates", coordinates);//地图坐标
        params.put("userId", userId);//用户id
        ApiHttpClient.post("/supJobOrder/userPosition", params, handler);
    }
}
