package com.grgbanking.supplier.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ApiHttpClient;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.common.util.UPlayer;
import com.grgbanking.supplier.common.util.sys.ImageUtils;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.login.LoginActivity;
import com.grgbanking.supplier.main.activity.ImagePreviewActivity;
import com.grgbanking.supplier.main.activity.SignInActivity;
import com.grgbanking.supplier.main.activity.first_workorder_activity;
import com.grgbanking.supplier.main.activity.forward_activity;
import com.grgbanking.supplier.main.activity.input_confirm_complete_activity;
import com.grgbanking.supplier.main.activity.input_confirmation_delivery_activity;
import com.grgbanking.supplier.main.eventbus.EventLatLng;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.greenrobot.event.EventBus;


/**
 *
 */
public class OrderDetailsFragment extends Fragment {

    private Context mContext;
    private View view;
    private String mOrderId;
    private UPlayer player;
    private AnimationDrawable drawable; // 录音播放时的动画背景
    private String voiceUrl;
    private ImageView details_ImgVolume;
    private ImageView star1, star2, star3, star4, star5;
    private View tweet_layout_record;
    private final static String MAINTENANCE = "001";//待维修001
    private final static String HAVEINHAND = "002";//进行中002
    private final static String CONFIRMED = "003";//待评价003
    private final static String EVALUATION = "004";//待确认004
    private final static String HISTORY = "005";//历史工单005
    private ImageView iv_action2, iv_action1;
    private workOrder mWorkOrder;
    private LinearLayout ll_express, ll_evaluate, ll_complete,ll_contact_address;
    private ImageView iv_picture1, iv_picture2, iv_picture3, iv_picture4, iv_picture5, iv_picture6, iv_picture7, iv_picture8, iv_picture9;
    private ImageView[] iv_pictures = new ImageView[9];
    private ImageView[] iv_picturecompletes = new ImageView[9];
    private TextView tv_workorder_No, tv_singleState, tv_repair_outlets, tv_supplier, tv_equipment, tv_model, tv_note, tv_fault_condition;
    private TextView tv_complete, tv_line, tv_contact_phone, tv_therepair_name, tv_contact_address, tv_express, tv_courierNum, tv_evaluate;
    private int mOrderType = 0;//1 上门维修   2 寄件返修
    //计算得到图片的间距 单位是dp
    private final  int PaddingIndance = 56;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_details, container, false);
        mContext = getActivity();
        EventBus.getDefault().register(this);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Glide.with(mContext.getApplicationContext()).pauseRequests();
    }

    private void initView() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        float scale = getResources().getDisplayMetrics().density;
        int width = (metric.widthPixels -  (int) (PaddingIndance * scale + 0.5f))/3 ;     // 屏幕宽度（像素）
        int height = metric.heightPixels;
        LogUtil.i(getTag(), "屏幕宽度/3 = " + width);


        star1 = (ImageView) view.findViewById(R.id.star1);
        star2 = (ImageView) view.findViewById(R.id.star2);
        star3 = (ImageView) view.findViewById(R.id.star3);
        star4 = (ImageView) view.findViewById(R.id.star4);
        star5 = (ImageView) view.findViewById(R.id.star5);
        tv_express = (TextView) view.findViewById(R.id.tv_express);
        tv_courierNum = (TextView) view.findViewById(R.id.tv_courierNum);
        ll_express = (LinearLayout) view.findViewById(R.id.ll_express);
        ll_complete = (LinearLayout) view.findViewById(R.id.ll_complete);
        ll_evaluate = (LinearLayout) view.findViewById(R.id.ll_evaluate);
        tv_evaluate = (TextView) view.findViewById(R.id.tv_evaluate);
        tv_complete = (TextView) view.findViewById(R.id.tv_complete);
        //tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
        tv_therepair_name = (TextView) view.findViewById(R.id.tv_therepair_name);//维修人
        tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);//维修人电话
        //ll_workorder_tracking = (LinearLayout) view.findViewById(R.id.ll_workorder_tracking);

        tv_line = (TextView) view.findViewById(R.id.tv_line);
        ll_contact_address = (LinearLayout) view.findViewById(R.id.ll_contact_address);
        tv_contact_address = (TextView) view.findViewById(R.id.tv_contact_address);//地址
        iv_action2 = (ImageView) view.findViewById(R.id.iv_action2);
        iv_action1 = (ImageView) view.findViewById(R.id.iv_action1);
        iv_picture1 = (ImageView) view.findViewById(R.id.iv_picture1);
        iv_picture2 = (ImageView) view.findViewById(R.id.iv_picture2);
        iv_picture3 = (ImageView) view.findViewById(R.id.iv_picture3);
        iv_picture4 = (ImageView) view.findViewById(R.id.iv_picture4);
        iv_picture5 = (ImageView) view.findViewById(R.id.iv_picture5);
        iv_picture6 = (ImageView) view.findViewById(R.id.iv_picture6);
        iv_picture7 = (ImageView) view.findViewById(R.id.iv_picture7);
        iv_picture8 = (ImageView) view.findViewById(R.id.iv_picture8);
        iv_picture9 = (ImageView) view.findViewById(R.id.iv_picture9);
        iv_pictures[0] = iv_picture1;
        iv_pictures[1] = iv_picture2;
        iv_pictures[2] = iv_picture3;
        iv_pictures[3] = iv_picture4;
        iv_pictures[4] = iv_picture5;
        iv_pictures[5] = iv_picture6;
        iv_pictures[6] = iv_picture7;
        iv_pictures[7] = iv_picture8;
        iv_pictures[8] = iv_picture9;
        LinearLayout.LayoutParams Params =  (LinearLayout.LayoutParams)iv_picture1.getLayoutParams();
        Params.height = width;
        for(ImageView iv : iv_pictures){
            iv.setLayoutParams(Params);
        }
        ImageView iv_picturecomplete1 = (ImageView) view.findViewById(R.id.iv_picturecomplete1);
        ImageView iv_picturecomplete2 = (ImageView) view.findViewById(R.id.iv_picturecomplete2);
        ImageView iv_picturecomplete3 = (ImageView) view.findViewById(R.id.iv_picturecomplete3);
        ImageView iv_picturecomplete4 = (ImageView) view.findViewById(R.id.iv_picturecomplete4);
        ImageView iv_picturecomplete5 = (ImageView) view.findViewById(R.id.iv_picturecomplete5);
        ImageView iv_picturecomplete6 = (ImageView) view.findViewById(R.id.iv_picturecomplete6);
        ImageView iv_picturecomplete7 = (ImageView) view.findViewById(R.id.iv_picturecomplete7);
        ImageView iv_picturecomplete8 = (ImageView) view.findViewById(R.id.iv_picturecomplete8);
        ImageView iv_picturecomplete9 = (ImageView) view.findViewById(R.id.iv_picturecomplete9);

        iv_picturecompletes[0] = iv_picturecomplete1;
        iv_picturecompletes[1] = iv_picturecomplete2;
        iv_picturecompletes[2] = iv_picturecomplete3;
        iv_picturecompletes[3] = iv_picturecomplete4;
        iv_picturecompletes[4] = iv_picturecomplete5;
        iv_picturecompletes[5] = iv_picturecomplete6;
        iv_picturecompletes[6] = iv_picturecomplete7;
        iv_picturecompletes[7] = iv_picturecomplete8;
        iv_picturecompletes[8] = iv_picturecomplete9;
        for(ImageView iv : iv_picturecompletes){
            iv.setLayoutParams(Params);
        }
        tv_workorder_No = (TextView) view.findViewById(R.id.tv_workorder_no);//工单号
        tv_singleState = (TextView) view.findViewById(R.id.tv_singleState);//维修网点
        tv_repair_outlets = (TextView) view.findViewById(R.id.tv_repair_outlets);//工单进度操作
        tv_supplier = (TextView) view.findViewById(R.id.tv_supplier);//供应商
        tv_equipment = (TextView) view.findViewById(R.id.tv_equipment);//设备
        tv_model = (TextView) view.findViewById(R.id.tv_model);//型号
        tv_note = (TextView) view.findViewById(R.id.et_note);//备注
        tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
        details_ImgVolume = (ImageView) view.findViewById(R.id.details_img_volume);
        tweet_layout_record = view.findViewById(R.id.tweet_layout_record);
        tweet_layout_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceUrl != null)
                    downloadVoice(voiceUrl);
            }
        });
    }

    private void initData() {
        mOrderId = getActivity().getIntent().getStringExtra("mOrderId");
        LogUtil.e("OrderDetailsFragment","mOrderId == " + mOrderId);
        getOrderData();
    }

    //接收到eventbus通知更新界面
    public void onEventMainThread(EventLatLng event) {
//        tv_workorder_No.setText(event.mWorkOrder.getJobNum());
//        tv_singleState.setText(event.mWorkOrder.getBankName());
//        tv_supplier.setText(event.mWorkOrder.getSupName());
//        tv_equipment.setText(event.mWorkOrder.getDeviceName());
//        tv_model.setText(event.mWorkOrder.getDmName());
//        tv_note.setText(event.mWorkOrder.getRemark());
//        tv_fault_condition.setText(event.mWorkOrder.getSituation());
//        tv_repair_outlets.setText(event.mWorkOrder.getBankName());
        //showPicture(event.arrs, iv_pictures);
        //voiceUrl = event.voiceUrl;
        //if (!voiceUrl.equals("")){
        //    tweet_layout_record.setVisibility(View.VISIBLE);
        //}
    }

    /**
     * 语音播放效果
     */
    public void startAnim() {
        drawable = (AnimationDrawable) details_ImgVolume.getBackground();
        details_ImgVolume.setVisibility(View.VISIBLE);
        drawable.start();
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
                                closedOrder(mOrderId);
                                iv_action1.setClickable(false);
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
                if (Preferences.getUserRole().equals("20002")) { //服务工程师   ---  action2 确认完成
                    if (schedule.equals("5")){
                        iv_action2.setVisibility(View.VISIBLE);
                        iv_action2.setImageResource(R.drawable.button11);//签到
                        iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext,  SignInActivity.class);
                                intent.putExtra("id",mOrderId);
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
                                intent.putExtra("jobOrderId", mOrderId);
                                startActivity(intent);
                                iv_action2.setClickable(false);
                                getActivity().finish();
                            }
                        });
                    }
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
                        mWorkOrder = new workOrder();
                        mWorkOrder.setJobNum(jsonObj.getString("jobNum"));
                        mWorkOrder.setBankName(jsonObj.getString("bankName"));
                        if (jsonObj2.has("courierNum") && !StringUtil.isEmpty(jsonObj2.getString("courierNum"))) {
                            tv_express.setText(jsonObj2.getString("express"));//工单号
                            tv_courierNum.setText(jsonObj2.getString("courierNum"));//工单号
                            mWorkOrder.setExpress(jsonObj2.getString("express"));
                            mWorkOrder.setCourierNum(jsonObj2.getString("courierNum"));
                            ll_express.setVisibility(View.VISIBLE);
                        }
                        if (jsonObj2.has("evaluate")) {
                            tv_evaluate.setText(jsonObj2.getString("evaluate").toString().replace("null", ""));//工单号
                            switch (jsonObj2.getInt("starLevel")) {
                                case 1:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star1);
                                    star3.setImageResource(R.drawable.star1);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 2:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star1);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 3:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 4:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star2);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 5:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star2);
                                    star5.setImageResource(R.drawable.star2);
                                    break;
                            }

                            ll_evaluate.setVisibility(View.VISIBLE);
                        }

                        String picUrls = jsonObj.getString("imgSerialNum");
                        mWorkOrder.setSupName(jsonObj.getString("supName"));
                        mWorkOrder.setDeviceName(jsonObj.getString("deviceName"));
                        mWorkOrder.setDmName(jsonObj.getString("dmName"));
                        mWorkOrder.setRemark(jsonObj.getString("remark"));
                        mWorkOrder.setSituation("故障情况：  " + jsonObj.getString("situation"));

                        tv_workorder_No.setText(mWorkOrder.getJobNum());
                        tv_singleState.setText(mWorkOrder.getBankName());
                        tv_supplier.setText(mWorkOrder.getSupName());
                        tv_equipment.setText(mWorkOrder.getDeviceName());
                        tv_model.setText(mWorkOrder.getDmName());
                        tv_note.setText(mWorkOrder.getRemark());
                        tv_fault_condition.setText(mWorkOrder.getSituation());
                        tv_repair_outlets.setText(mWorkOrder.getBankName());

                        mOrderType = jsonObj2.getInt("type");
                        if (mOrderType == 1) {
                            ll_contact_address.setVisibility(View.GONE);
                            tv_line.setVisibility(View.GONE);
                        }
                        if (jsonObj2.has("comAddress")) {
                            tv_contact_address.setText(jsonObj2.getString("comAddress"));
                        }
                        if (jsonObj.has("execution")) {
                            ll_complete.setVisibility(View.VISIBLE);
                            tv_complete.setText(jsonObj.getString("execution"));
                        }
                        if (jsonObj.has("imageStr")) {
                            String picUrl2s = jsonObj.getString("imageStr");
                            String[] arrs = picUrl2s.split(",");
                            showPicture(arrs, iv_picturecompletes);
                        }
                        if (jsonObj2.has("voice")) {
                            voiceUrl = jsonObj2.getString("voice");
                            if (!voiceUrl.equals("")){
                                tweet_layout_record.setVisibility(View.VISIBLE);
                            }
                        }
                        String[] arrs = picUrls.split(",");
                        showPicture(arrs, iv_pictures);
                        //EventBus.getDefault().post(new EventLatLng(mWorkOrder,arrs,voiceUrl));
                        tv_therepair_name.setText(jsonObj.getString("userName"));
                        tv_contact_phone.setText(jsonObj.getString("phone"));
                        String schedule = jsonObj.getString("schedule");
                        String state = jsonObj.getString("state");
                        setButtons(state, schedule);
                        //getTrackingData();
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

    private void showPicture(final String[] urls, ImageView[] pictures) {
        if (urls.length == 0) {
            return;
        }
        for (int i = 0; i < urls.length; i++) {
            pictures[i].setVisibility(View.VISIBLE);
            final String url = String.format(ApiHttpClient.API_URL_IMG, urls[i]);
            final String thumbnailurl = String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(urls[i]));
            if (Util.isOnMainThread()) {
                Glide.with(this)
                        .load(thumbnailurl)
                        .centerCrop()
                        .crossFade()
                        .into(pictures[i]);
            }
            Rect frame = new Rect();
            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            final int[] location = new int[2];
            pictures[i].getLocationOnScreen(location);
            location[1] += statusBarHeight;

            final int width = pictures[i].getWidth();
            final int height = pictures[i].getHeight();
            final int finalI = i;
            pictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bundle extras = new Bundle();
//                    extras.putString(ImagesDetailActivity.INTENT_IMAGE_URL_TAG, url);
//                    Intent intent = new Intent(mContext, ImagesDetailActivity.class);
//                    if (null != extras) {
//                        intent.putExtras(extras);
//                    }
//                    startActivity(intent);
                    ImagePreviewActivity.showImagePrivew(mContext, finalI,
                            urls);
                }
            });
        }

        if (urls.length % 3 > 0) {
            for (int i = urls.length; i < 9; i++) {
                if (i < urls.length + (3 - (urls.length % 3))) {
                    pictures[i].setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(R.drawable.kongbai)
                            .centerCrop()
                            .crossFade()
                            .into(pictures[i]);
                }
            }
        }
    }

    private File getPhotoDir(Context ctx) {
        final String dirName = "myrecords";
        File root = ctx.getExternalFilesDir(null);
        File dir = new File(root, dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        return dir;
    }

    private void downloadVoice(String voiceUrl) {
        File file = new File(getPhotoDir(mContext.getApplicationContext()), mOrderId + ".amr");
        if (file.exists()) {
            playSound(file);
        } else {
            ServerApi.download(voiceUrl, new FileAsyncHttpResponseHandler(file) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                    Toast.makeText(mContext, "下载文件失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, File file) {
                    playSound(file);
                }
            });
        }
    }

    private void playSound(File file) {
        if (file != null) {
            if (null == player) {
                player = new UPlayer(file.getPath(),
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 0) {
                                    startAnim();
                                } else if (msg.what == 1) {
                                    if (drawable != null) {
                                        drawable.stop();
                                        details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                                        details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                                    }
                                } else {
                                    if (drawable != null) {
                                        drawable.stop();
                                        details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                                        details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                                    }
                                }
                            }
                        }
                );
                player.start();
            } else if (player.getMediaPlayer().isPlaying()) {
                player.stop();
                if (drawable != null) {
                    drawable.stop();
                    details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                    details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                }
//            player = null;
            } else {
                player.start(file.getPath());
                startAnim();
            }
        }
    }
}
