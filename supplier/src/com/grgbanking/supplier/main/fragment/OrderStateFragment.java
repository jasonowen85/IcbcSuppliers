package com.grgbanking.supplier.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.tracking;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.common.util.widget.TimeLineView;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.login.LoginActivity;
import com.grgbanking.supplier.main.activity.SignInActivity;
import com.grgbanking.supplier.main.activity.first_workorder_activity;
import com.grgbanking.supplier.main.activity.forward_activity;
import com.grgbanking.supplier.main.activity.input_confirm_complete_activity;
import com.grgbanking.supplier.main.activity.input_confirmation_delivery_activity;
import com.grgbanking.supplier.session.SessionHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderStateFragment extends Fragment {

    private Context mContext;
    private View view;
    private String mOrderId;
    private final static String MAINTENANCE = "001";//待维修001
    private final static String HAVEINHAND = "002";//进行中002
    private final static String CONFIRMED = "003";//待评价003
    private final static String EVALUATION = "004";//待确认004
    private final static String HISTORY = "005";//历史工单005
    private ImageView iv_action2, iv_action1;
    private List<tracking> datas;
    private TimeLineView mTimeLineView;
    private ListAdapt mListAdapt;
    private ListView mListView;
//    private ImageView star1, star2, star3, star4, star5;
//    private workOrder mWorkOrder;
    private String voiceUrl;
    private int mOrderType = 0;//1 上门维修   2 寄件返修
//    private ImageView[] iv_picturecompletes = new ImageView[9];
//    private LinearLayout ll_express, ll_evaluate, ll_complete;
    private LinearLayout ll_workorder_tracking, ll_contact_address;
    private TextView tv_complete, tv_line, tv_contact_phone, tv_therepair_name, tv_contact_address, tv_express, tv_courierNum, tv_evaluate;
    private TextView tv_buttomLine;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_state, container, false);
        mContext = getActivity();
        initView();
        initData();
        return view;
    }

    private void initView() {
        //tweet_layout_record = view.findViewById(R.id.tweet_layout_record);
        iv_action2 = (ImageView) view.findViewById(R.id.iv_action2);
        iv_action1 = (ImageView) view.findViewById(R.id.iv_action1);
        mTimeLineView = (TimeLineView) view.findViewById(R.id.tl_tracking_step);
        mListView = (ListView) view.findViewById(R.id.list_tracking);
        tv_buttomLine = (TextView) view.findViewById(R.id.tv_buttom_line);
//        star1 = (ImageView) view.findViewById(R.id.star1);
//        star2 = (ImageView) view.findViewById(R.id.star2);
//        star3 = (ImageView) view.findViewById(R.id.star3);
//        star4 = (ImageView) view.findViewById(R.id.star4);
//        star5 = (ImageView) view.findViewById(R.id.star5);
//        tv_express = (TextView) view.findViewById(R.id.tv_express);
//        tv_courierNum = (TextView) view.findViewById(R.id.tv_courierNum);
//        ll_express = (LinearLayout) view.findViewById(R.id.ll_express);
//        ll_complete = (LinearLayout) view.findViewById(R.id.ll_complete);
//        ll_evaluate = (LinearLayout) view.findViewById(R.id.ll_evaluate);
//        tv_evaluate = (TextView) view.findViewById(R.id.tv_evaluate);
//        tv_complete = (TextView) view.findViewById(R.id.tv_complete);
//        //tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
//        tv_therepair_name = (TextView) view.findViewById(R.id.tv_therepair_name);//维修人
//        tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);//维修人电话
//        ll_workorder_tracking = (LinearLayout) view.findViewById(R.id.ll_workorder_tracking);
//
//        tv_line = (TextView) view.findViewById(R.id.tv_line);
//        ll_contact_address = (LinearLayout) view.findViewById(R.id.ll_contact_address);
//        tv_contact_address = (TextView) view.findViewById(R.id.tv_contact_address);//地址
//        ImageView iv_picturecomplete1 = (ImageView) view.findViewById(R.id.iv_picturecomplete1);
//        ImageView iv_picturecomplete2 = (ImageView) view.findViewById(R.id.iv_picturecomplete2);
//        ImageView iv_picturecomplete3 = (ImageView) view.findViewById(R.id.iv_picturecomplete3);
//        ImageView iv_picturecomplete4 = (ImageView) view.findViewById(R.id.iv_picturecomplete4);
//        ImageView iv_picturecomplete5 = (ImageView) view.findViewById(R.id.iv_picturecomplete5);
//        ImageView iv_picturecomplete6 = (ImageView) view.findViewById(R.id.iv_picturecomplete6);
//        ImageView iv_picturecomplete7 = (ImageView) view.findViewById(R.id.iv_picturecomplete7);
//        ImageView iv_picturecomplete8 = (ImageView) view.findViewById(R.id.iv_picturecomplete8);
//        ImageView iv_picturecomplete9 = (ImageView) view.findViewById(R.id.iv_picturecomplete9);
//
//        iv_picturecompletes[0] = iv_picturecomplete1;
//        iv_picturecompletes[1] = iv_picturecomplete2;
//        iv_picturecompletes[2] = iv_picturecomplete3;
//        iv_picturecompletes[3] = iv_picturecomplete4;
//        iv_picturecompletes[4] = iv_picturecomplete5;
//        iv_picturecompletes[5] = iv_picturecomplete6;
//        iv_picturecompletes[6] = iv_picturecomplete7;
//        iv_picturecompletes[7] = iv_picturecomplete8;
//        iv_picturecompletes[8] = iv_picturecomplete9;
    }

    private void initData() {
        mOrderId = getActivity().getIntent().getStringExtra("mOrderId");
        LogUtil.e("OrderStateFragment","mOrderId == " + mOrderId);
        datas = new ArrayList<>();
        mListAdapt = new ListAdapt(mContext);
        mListView.setAdapter(mListAdapt);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionHelper.startP2PSession(mContext, datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getContactId());
            }
        });
        getTrackingData();
//        getOrderData();
    }

    private void setButtons(String type, String schedule) {
        switch (type) {
            case MAINTENANCE: //待维修001
                if (Preferences.getUserRole().equals("20001")) { //客服工程师   ---  action1 关闭， action2 转发
                    if (schedule.equals("1")||schedule.equals("11")||schedule.equals("-1")){ //---  action1 关闭， action2 转发
                        iv_action1.setVisibility(View.VISIBLE);
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action1.setImageResource(R.drawable.button6);
                        iv_action2.setImageResource(R.drawable.button5);
                        iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyAlertDialogHelper.createOkCancelDiolag(mContext, mContext.getString(R.string.helps), mContext.getString(R.string.confirm_close_order),
                                        mContext.getString(R.string.close_order), mContext.getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                                            @Override
                                            public void doCancelAction() {
                                                //什么都不做
                                            }

                                            @Override
                                            public void doOkAction() {
                                                closedOrder(mOrderId);
                                                iv_action1.setClickable(false);
                                            }
                                        }).show();

                            }
                        });
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(mContext,  forward_activity.class);
                                it.putExtra("orderId", mOrderId);
                                startActivity(it);
                                iv_action2.setClickable(false);
                                getActivity().finish();
                            }
                        });
                    }
                } else if (Preferences.getUserRole().equals("20002")) { //服务主管   action1 转回客服  action2 转发工程师
                    if( schedule.equals("2")||schedule.equals("-1")||schedule.equals("-2")){
                        iv_action1.setVisibility(View.VISIBLE);
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action1.setImageResource(R.drawable.button2);
                        iv_action2.setImageResource(R.drawable.button5);
                        iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forwardBack(mOrderId);
                                iv_action1.setClickable(false);
                            }
                        });
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(mContext, forward_activity.class);
                                it.putExtra("orderId", mOrderId);
                                startActivity(it);
                                iv_action2.setClickable(false);
                                getActivity().finish();
                            }
                        });
                    }
                } else if (Preferences.getUserRole().equals("20003")) { //服务工程师   action1 转回主管 ，action2 接单
                    if (schedule.equals("3")){
                        iv_action1.setVisibility(View.VISIBLE);
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action1.setImageResource(R.drawable.button7);
                        iv_action2.setImageResource(R.drawable.button8);
                        iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forwardBack(mOrderId);
                                iv_action1.setClickable(false);
                            }
                        });
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(mOrderId, Preferences.getUserid());
                                signLine(mOrderId,Preferences.getUserid());
                                iv_action2.setClickable(false);
                            }
                        });
                    }
                } else if (Preferences.getUserRole().equals("20004")) { //维修接口人   action1 转回客服
                    if (schedule.equals("12")){
                        iv_action1.setVisibility(View.VISIBLE);
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action1.setImageResource(R.drawable.button2);
                        iv_action2.setImageResource(R.drawable.button8);
                        iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forwardBack(mOrderId);
                                iv_action1.setClickable(false);
                            }
                        });
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(mOrderId, Preferences.getUserid());
                                iv_action2.setClickable(false);
                            }
                        });
                    }
                }
                break;
            case HAVEINHAND: //进行中002
                if (Preferences.getUserRole().equals("20002")) { //服务主管
//                    if (schedule.equals("5")){
//                        iv_action2.setVisibility(View.VISIBLE);
//                        iv_action2.setImageResource(R.drawable.button11);//签到
//                        iv_action2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext,  SignInActivity.class);
//                                intent.putExtra("id",mOrderId);
//                                startActivity(intent);
//                                iv_action2.setClickable(false);
//                                getActivity().finish();
//                            }
//                        });
//                    }else if (schedule.equals("6")){
//                        iv_action2.setVisibility(View.VISIBLE);
//                        iv_action2.setImageResource(R.drawable.button12);//确认完成
//                        iv_action2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, input_confirm_complete_activity.class);
//                                intent.putExtra("jobOrderId", mOrderId);
//                                startActivity(intent);
//                                iv_action2.setClickable(false);
//                                getActivity().finish();
//                            }
//                        });
//                    }
                } else if (Preferences.getUserRole().equals("20003")) { //服务工程师
                    if (schedule.equals("5")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.button11);//签到
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, SignInActivity.class);
                                intent.putExtra("id", mOrderId);
                                startActivity(intent);
                                iv_action2.setClickable(false);
                                getActivity().finish();
                            }
                        });
                    }else if (schedule.equals("6")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.button12);//确认完成
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, input_confirm_complete_activity.class);
                                intent.putExtra("jobOrderId",mOrderId);
                                startActivity(intent);
                                iv_action2.setClickable(false);
                                getActivity().finish();
                            }
                        });
                    }
                } else if (Preferences.getUserRole().equals("20004")) { //维修接口人  ---  action2 确认到货 确认维修完成 确认发货
                    if(schedule.equals("13")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.recived);//确认到货
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(mOrderId,  Preferences.getUserid());
                                iv_action2.setClickable(false);
                            }
                        });
                    }else if (schedule.equals("14")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.completeconfirm);//确认维修完成
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(mOrderId,  Preferences.getUserid());
                                iv_action2.setClickable(false);
                            }
                        });
                    }else if(schedule.equals("15")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.confirmation_delivery);//确认发货
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, input_confirmation_delivery_activity.class);
                                intent.putExtra("jobOrderId",mOrderId);
                                startActivity(intent);
                                getActivity().finish();
                                iv_action2.setClickable(false);
                            }
                        });
                    }
                }
                break;
            case EVALUATION: //待评价003

                break;
            case CONFIRMED://待确认004

                break;
            case HISTORY://历史工单005

                break;
        }

        if(iv_action1.getVisibility()==View.GONE && iv_action2.getVisibility()==View.GONE){
            //如果没有button 隐藏底部的line
            tv_buttomLine.setVisibility(View.GONE);
        }

    }

    /* 2.1.12.记录签到路线图 */
    protected void signLine(String orderid, String userid) {
        ServerApi.signLine(orderid, userid, "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                } else {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
            }
        });
    }

    /*关闭工单*/
    protected void closedOrder(String orderid) {
        ServerApi.closeOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "关闭成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* 转回*/
    private void forwardBack(String orderid) {
        ServerApi.forwardBack(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "转回成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* 2.2.7.接单和确认*/
    protected void comfirmOrder(String orderid, String userid) {
        ServerApi.comfirmOrder(orderid, userid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "操作成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "002");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderData() {
        ServerApi.getJobOrderDetails(mOrderId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    JSONObject jsonOb = response.optJSONObject("lists");
                    JSONObject jsonObj = jsonOb.optJSONObject("lists");
                    JSONObject jsonObj2 = jsonOb.optJSONObject("jobOrderDetail");
                    try {
                        String schedule = jsonObj.getString("schedule");
                        String state = jsonObj.getString("state");
                        setButtons(state, schedule);
//                        getTrackingData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTrackingData() {
        ServerApi.getWorkOrderTracking(mOrderId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    datas.clear();
                    JSONObject jsonOb = response.optJSONObject("lists");
                    JSONArray jsonArr = jsonOb.optJSONArray("lists");
                    List<tracking> ts = new ArrayList<tracking>();
                    try {
                        for (int i = 0; i < jsonArr.length(); i++) {
                            tracking t = new tracking();
                            jsonOb = jsonArr.getJSONObject(i);
                            t.setSupplierSendUserId(jsonOb.getString("supplierSendUserId"));
                            t.setSendSupName(jsonOb.getString("sendSupName"));
                            t.setSupplierAcceptUserId(jsonOb.getString("supplierAcceptUserId"));
                            t.setAcceptSupName(jsonOb.getString("acceptSupName"));
                            t.setCreateTime(jsonOb.getString("createTime"));
                            t.setBankUserId(jsonOb.has("bankUserId") ? jsonOb.getString("bankUserId") : "");
                            t.setBankName(jsonOb.has("bankName") ? jsonOb.getString("bankName") : "");
                            t.setBankPhone(jsonOb.has("bankUserPhone") ? jsonOb.getString("bankUserPhone") : "");
                            t.setSupplierAcceptphone(jsonOb.has("supUserPhone") ? jsonOb.getString("supUserPhone") : "");
                            t.setState(jsonOb.getString("state"));
                            if (jsonOb.has("express") && !StringUtil.isEmpty(jsonOb.getString("express"))) {
                                t.setExpress(jsonOb.getString("express"));
                                t.setCourierNum(jsonOb.getString("courierNum"));//工单号
                            }
                            if (jsonOb.has("signAddress") && !StringUtil.isEmpty(jsonOb.getString("signAddress"))) {
                                t.setSignAddress(jsonOb.getString("signAddress"));
                            }
                            t.setContent(getTracktingStr(t));
                            datas.add(t);

                        }
                        mTimeLineView.setTimelineCount(datas.size());
                        mListAdapt.notifyDataSetChanged();
                        //页面加载 比button慢  所以先加载Track;
                        getOrderData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTracktingStr(tracking t) {
        if (t.getState().equals("1")) { //1.已填写工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step1), t.getBankName(), t.getAcceptSupName());
        } else if (t.getState().equals("2")) { //2.客服将工单转发给维修接口人
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step2), t.getSendSupName());
        } else if (t.getState().equals("3")) { //3.接口人接单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step3), t.getAcceptSupName());
        } else if (t.getState().equals("4")) { //4.接口人确认收货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step4), t.getAcceptSupName());
        } else if (t.getState().equals("5")) { //5.接口人确认维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step5), t.getAcceptSupName());
        } else if (t.getState().equals("6")) { //6.接口人确认发货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step6), t.getAcceptSupName());
        } else if (t.getState().equals("7")) { //7.客户确定到货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step7), t.getAcceptSupName());
        } else if (t.getState().equals("8")) { //8.客户评价
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step8), t.getAcceptSupName());
        } else if (t.getState().equals("11")) { //11.已填写工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step11), t.getBankName(), t.getAcceptSupName());
        } else if (t.getState().equals("12")) { //12.服务主管接收工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step12), t.getAcceptSupName());
        } else if (t.getState().equals("13")) { //13.服务主管下发给服务工程师
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step13), t.getAcceptSupName());
        } else if (t.getState().equals("14")) { //14.服务工程师接单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step14), t.getAcceptSupName());
        } else if (t.getState().equals("15")) { //15.服务工程师已签到
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step15), t.getAcceptSupName());
        } else if (t.getState().equals("16")) { //16.服务工程师已维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step16), t.getAcceptSupName());
        } else if (t.getState().equals("17")) { //17.客户已确认维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step17), t.getAcceptSupName());
        } else if (t.getState().equals("18")) { //8.客户评价
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step18), t.getAcceptSupName());
        }
        return "";
    }

    class ViewHolder {
        TextView tv_content, tv_express, tv_time,tv_SignAddress;
        View lineOne,lineTwo, line_buttom;
        ImageView ivHeadPoint;
    }

    class ListAdapt extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public ListAdapt(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView == null) {
                vHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.job_order_tracking_listview, null);
                vHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                vHolder.tv_express = (TextView) convertView.findViewById(R.id.tv_express);
                vHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                vHolder.tv_SignAddress=(TextView)convertView.findViewById(R.id.tv_SignAddress);
                vHolder.ivHeadPoint = (ImageView) convertView.findViewById(R.id.head_point);
                vHolder.lineOne = convertView.findViewById(R.id.head_line_one);
                vHolder.lineTwo = convertView.findViewById(R.id.head_line_two);
                vHolder.line_buttom = convertView.findViewById(R.id.view_line_buttom);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_time.setText(datas.get(position).getCreateTime());
            if (datas.get(position).getExpress() != null) {
                vHolder.tv_express.setVisibility(View.VISIBLE);
                vHolder.tv_express.setText("快递：" + datas.get(position).getExpress() + datas.get(position).getCourierNum());
            }else {
                vHolder.tv_express.setVisibility(View.GONE);
            }
            if (datas.get(position).getState().equals("15")){
                vHolder.tv_SignAddress.setVisibility(View.VISIBLE);
                vHolder.tv_SignAddress.setText(datas.get(position).getSignAddress());
            }else {
                vHolder.tv_SignAddress.setVisibility(View.GONE);
            }
            vHolder.tv_content.setText(Html.fromHtml(datas.get(position).getContent()));
            if(position==getCount()-1){
                vHolder.lineTwo.setVisibility(View.INVISIBLE);
                vHolder.line_buttom.setVisibility(View.GONE);
            }else{
                vHolder.lineTwo.setVisibility(View.VISIBLE);
                vHolder.line_buttom.setVisibility(View.VISIBLE);
            }
            if(position==0){
                vHolder.lineOne.setVisibility(View.INVISIBLE);
                vHolder.ivHeadPoint.setImageDrawable(getResources().getDrawable(R.drawable.time_list_circle));
            }else{
                vHolder.lineOne.setVisibility(View.VISIBLE);
                vHolder.ivHeadPoint.setImageDrawable(getResources().getDrawable(R.drawable.time_list_circle_one));
            }
            return convertView;
        }
    }
}
