package com.grgbanking.supplier.main.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.grgbanking.supplier.NimApplication;
import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ApiHttpClient;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.common.util.PermissionUtils;
import com.grgbanking.supplier.common.util.sys.ImageUtils;
import com.grgbanking.supplier.common.util.widget.ListViewCompat;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.login.LoginActivity;
import com.grgbanking.supplier.main.activity.SignInActivity;
import com.grgbanking.supplier.main.activity.first_workorder_activity;
import com.grgbanking.supplier.main.activity.forward_activity;
import com.grgbanking.supplier.main.activity.input_confirm_complete_activity;
import com.grgbanking.supplier.main.activity.input_confirmation_delivery_activity;
import com.grgbanking.supplier.main.activity.input_order_details_activity;
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

public class input_workorder_baskfragment extends BaseFragment implements
        ListViewCompat.OnRefreshListener, ListViewCompat.OnLoadListener {

    private ListViewCompat listView1;
    private ListAdapt mListAdapt;
    List<workOrder> datas;
    private int allCount = 200;
    private final static String MAINTENANCE = "001";
    private final static String HAVEINHAND = "002";
    private final static String CONFIRMED = "003";
    private final static String EVALUATION = "004";
    private final static String HISTORY = "005";

    private String mType;
    private ReceiveBroadCast receiveBroadCast;
    private String startTime;
    private String endTime;
    private String bankId;
    private int currentPage = 1;

    @Override
    public void onAttach(Activity activity) {
        //注册广播
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("role_changed");
        filter.addAction("ACTION_NAME");    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_input_maintenance, container, false);
        datas = new ArrayList<workOrder>();

        init(rootView);
        return rootView;
    }

    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("ACTION_NAME")) {
                startTime = intent.getExtras().getString("startTime");
                endTime = intent.getExtras().getString("endTime");
                bankId = intent.getExtras().getString("id");
                if (bankId != null) {
                    NimApplication.lastBranchId = bankId;
                }
                if (startTime != null || endTime != null) {
                    NimApplication.lastEndtime = endTime;
                    NimApplication.lastStarttime = startTime;
                }
            }
            getData(ListViewCompat.REFRESH);
        }
    }

    /*类型 001 待维修，002 进行中，003 待确认，004 待评价，005 历史工单*/
    protected void SetType(String type) {
        this.mType = type;
    }

    private String Return(String str) {
        if (str == null) {
            return "0000";
        }
        return str;
    }

    protected void init(View view) {
        listView1 = (ListViewCompat) view.findViewById(R.id.listView1);
        mListAdapt = new ListAdapt(getActivity());
        listView1.setAdapter(mListAdapt);
        listView1.setOnRefreshListener(this);
        listView1.setOnLoadListener(this);
        listView1.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (datas.size() == 0) {
                            return;
                        }
                        if (view.getId() != R.id.iv_action1 && view.getId() != R.id.iv_action2) {
                            if (parent.getAdapter().getItem(position) == null) {
                                return;
                            }
                            Intent it = new Intent(getActivity(), input_order_details_activity.class);
                            it.putExtra("orderId", datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getId());
                            startActivity(it);
                        }
                    }
                }
        );
        ToolUtil.ReCalListViewHeightBasedOnChildren(listView1);
    }

    protected void getData(final int what) {
        if(what == ListViewCompat.REFRESH  && currentPage != 1){
            currentPage =1;
        }
        ServerApi.getWorkOrder(Preferences.getUserid(), currentPage, 10, mType, Return(NimApplication.lastBranchId), Return(NimApplication.lastStarttime), Return(NimApplication.lastEndtime), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    JSONObject jsonObject = response.optJSONObject("lists");
                    JSONArray jsonArr = jsonObject.optJSONArray("lists");
                    int totalItem = jsonObject.optInt("total");
                    allCount= totalItem;
                    int totalPager = totalItem%10 == 0 ? totalItem/10 : totalItem/10 + 1  ;
                    List<workOrder> orders = new ArrayList<workOrder>();
                    for (int i = 0; i < jsonArr.length(); i++) {
                        workOrder order = new workOrder();
                        JSONObject jsonOb = new JSONObject();
                        try {
                            jsonOb = jsonArr.getJSONObject(i);
                            order.setId(jsonOb.getString("id"));
                            order.setDeviceName(jsonOb.getString("deviceName"));
                            order.setSchedule(jsonOb.getString("schedule"));
                            order.setScheduleStr(jsonOb.getString("scheduleStr"));
                            order.setSituation(jsonOb.getString("situation"));
                            order.setCreateTime(jsonOb.getString("createTime"));
                            if (jsonOb.has("deviceNum")) {
                                order.setDeviceNum(jsonOb.getString("deviceNum"));//数量
                            }
                            if (jsonOb.has("imgSerialNum")) {
                                String picUrls = jsonOb.getString("imgSerialNum");
                                String[] arrs = picUrls.split(",");
                                for (String url : arrs) {
                                    if (!StringUtil.isEmpty(url)) {
                                        order.getImageUrls().add(url);
                                    }
                                }
                            }
                            orders.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (ListViewCompat.REFRESH == what) {
                        datas.clear();
                        datas.addAll(orders);
                        listView1.onRefreshComplete();
                    } else if (ListViewCompat.LOAD == what) {
                        datas.addAll(orders);
                        listView1.onLoadComplete();
                    }
                    LogUtil.i("jiang", "当前请求页 = " + currentPage + "总datas size=" + datas.size() +
                            "   总totalItem = " + totalItem + "  本次请求获取到的数据条数= " + orders.size() + "  总页数= " + totalPager);
                    if(totalPager == currentPage){
                        listView1.setNoNextPagerDatas();
                    } else {
                        if(totalPager > 1){
                            currentPage++;
                        }
                        listView1.setResultSize(orders.size());
                    }

                    mListAdapt.notifyDataSetChanged();
                    orders.clear();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                listView1.setResultSize(0);
                listView1.onLoadComplete();
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                listView1.setResultSize(0);
                listView1.onLoadComplete();
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "转回成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                    intent.putExtra("state", "002");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
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
                    Toast.makeText(getActivity(), "关闭成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                if(null != getActivity())
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoad() {
        if (mListAdapt.getCount() < allCount) {
            getData(ListViewCompat.LOAD);
        } else {
            listView1.onLoadComplete();
            Toast.makeText(getActivity(), "已加载全部！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        getData(ListViewCompat.REFRESH);
    }

    class ViewHolder {
        TextView tv_schedule, tv_number, tv_deviceName, tv_situation, tv_createTime;
        ImageView img1, img2, img3;
        ImageView iv_action1, iv_action2;
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
            ViewHolder vHolder = null;
            if (convertView == null) {
                vHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.job_order_details_listview, null);

                vHolder.img1 = (ImageView) convertView.findViewById(R.id.img1);
                vHolder.img2 = (ImageView) convertView.findViewById(R.id.img2);
                vHolder.img3 = (ImageView) convertView.findViewById(R.id.img3);

                vHolder.tv_createTime = (TextView) convertView.findViewById(R.id.tv_createTime);
                vHolder.tv_situation = (TextView) convertView.findViewById(R.id.tv_situation);
                vHolder.tv_schedule = (TextView) convertView.findViewById(R.id.tv_schedule);
                vHolder.tv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
                vHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

                vHolder.iv_action1 = (ImageView) convertView.findViewById(R.id.iv_action1);
                vHolder.iv_action2 = (ImageView) convertView.findViewById(R.id.iv_action2);

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_deviceName.setText(datas.get(position).getDeviceName());
            vHolder.tv_situation.setText(datas.get(position).getSituation());
            vHolder.tv_schedule.setText(datas.get(position).getScheduleStr());
            vHolder.tv_createTime.setText(datas.get(position).getCreateTime());
            vHolder.tv_number.setText(datas.get(position).getDeviceNum());

            if (datas.get(position).getImageUrls().size() == 0) {
                vHolder.img1.setImageResource(R.drawable.nim_default_img_failed);
                vHolder.img2.setImageResource(R.drawable.nim_default_img_failed);
                vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
            }

            if (datas.get(position).getImageUrls().size() > 2) {
                if (Util.isOnMainThread()) {
                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(2))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img3);

                }
            }
            if (datas.get(position).getImageUrls().size() > 1) {
                if (Util.isOnMainThread()) {
                    if(datas.get(position).getImageUrls().size() == 2){
                        vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    }

                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(1))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img2);

                }
            }


            if (datas.get(position).getImageUrls().size() > 0) {
                if (Util.isOnMainThread()) {
                    if(datas.get(position).getImageUrls().size() == 1){
                        vHolder.img2.setImageResource(R.drawable.nim_default_img_failed);
                        vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    }
                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(0))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img1);
                }
            }

            switch (mType) {
                case MAINTENANCE: //待维修001
                    if (Preferences.getUserRole().equals("20001")) { //客服工程师   ---  action1 关闭， action2 转发
                        if (datas.get(position).getSchedule().equals("1") || datas.get(position).getSchedule().equals("11") || datas.get(position).getSchedule().equals("-1")) { //---  action1 关闭， action2 转发
                            vHolder.iv_action1.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action1.setImageResource(R.drawable.button6);
                            vHolder.iv_action2.setImageResource(R.drawable.button5);
                            vHolder.iv_action1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EasyAlertDialogHelper.createOkCancelDiolag(mContext, mContext.getString(R.string.helps), mContext.getString(R.string.confirm_close_order),
                                            mContext.getString(R.string.close_order), mContext.getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                                                @Override
                                                public void doCancelAction() {
                                                    //什么都不干
                                                }

                                                @Override
                                                public void doOkAction() {
                                                    closedOrder(datas.get(position).getId());
                                                }
                                            }).show();
                                }
                            });
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(getActivity(), forward_activity.class);
                                    it.putExtra("orderId", datas.get(position).getId());
                                    startActivity(it);
                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }

                    } else if (Preferences.getUserRole().equals("20002")) { //服务主管   action1 转回客服  action2 转发工程师
                        if (datas.get(position).getSchedule().equals("2") || datas.get(position).getSchedule().equals("-1")||datas.get(position).getSchedule().equals("-2")) {
                            vHolder.iv_action1.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action1.setImageResource(R.drawable.button2);
                            vHolder.iv_action2.setImageResource(R.drawable.button5);
                            vHolder.iv_action1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forwardBack(datas.get(position).getId());
                                }
                            });
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(getActivity(), forward_activity.class);
                                    it.putExtra("orderId", datas.get(position).getId());
                                    startActivity(it);
                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                    } else if (Preferences.getUserRole().equals("20003")) { //服务工程师   action1 转回主管 ，action2 接单
                        if (datas.get(position).getSchedule().equals("3")) {
                            vHolder.iv_action1.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action1.setImageResource(R.drawable.button7);
                            vHolder.iv_action2.setImageResource(R.drawable.button8);
                            vHolder.iv_action1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forwardBack(datas.get(position).getId());
                                }
                            });
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //定位权限 确认一下；
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        //请求定位权限；
                                        String[] perms = {PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, PermissionUtils.PERMISSION_ACCESS_FINE_LOCATION};
                                        if(PermissionUtils.lacksPermissions(getActivity(), perms)){
                                            ActivityCompat.requestPermissions(getActivity(), perms, PermissionUtils.CODE_ACCESS_FINE_LOCATION);
                                        } else {
                                            //服务工程师接单
                                            comfirmOrder(datas.get(position).getId(), Preferences.getUserid());
                                            signLine(datas.get(position).getId(), Preferences.getUserid());
                                        }

                                    } else {
                                        //服务工程师接单
                                        comfirmOrder(datas.get(position).getId(), Preferences.getUserid());
                                        signLine(datas.get(position).getId(), Preferences.getUserid());
                                    }


                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }

                    } else if (Preferences.getUserRole().equals("20004")) { //维修接口人   action1 转回客服

                        if (datas.get(position).getSchedule().equals("12")) {
                            vHolder.iv_action1.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action1.setImageResource(R.drawable.button2);
                            vHolder.iv_action2.setImageResource(R.drawable.button8);
                            vHolder.iv_action1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forwardBack(datas.get(position).getId());
                                }
                            });
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comfirmOrder(datas.get(position).getId(), Preferences.getUserid());//接单
                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                    } else {
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                    break;
                case HAVEINHAND: //进行中002
                    LogUtil.e("qzc","UserRole=="+Preferences.getUserRole());
                    LogUtil.e("qzc","Schedule=="+datas.get(position).getSchedule());
                    if (Preferences.getUserRole().equals("20001")) { //客服
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    } else if (Preferences.getUserRole().equals("20002")) { //服务主管
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    } else if (Preferences.getUserRole().equals("20003")) { //服务工程师
                        if (datas.get(position).getSchedule().equals("5")) {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setImageResource(R.drawable.button11);//签到
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, SignInActivity.class);
                                    intent.putExtra("id", datas.get(position).getId());
                                    startActivity(intent);
                                }
                            });
                        } else if (datas.get(position).getSchedule().equals("6")) {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setImageResource(R.drawable.button12);//确认完成
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), input_confirm_complete_activity.class);
                                    intent.putExtra("jobOrderId", datas.get(position).getId());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                    } else if (Preferences.getUserRole().equals("20004")) { //维修接口人  ---  action2 确认到货 确认维修完成 确认发货
                        if (datas.get(position).getSchedule().equals("13")) {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setImageResource(R.drawable.recived);//确认到货
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comfirmOrder(datas.get(position).getId(), Preferences.getUserid());
                                }
                            });
                        } else if (datas.get(position).getSchedule().equals("14")) {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setImageResource(R.drawable.completeconfirm);//确认维修完成
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comfirmOrder(datas.get(position).getId(), Preferences.getUserid());
                                }
                            });
                        } else if (datas.get(position).getSchedule().equals("15")) {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.VISIBLE);
                            vHolder.iv_action2.setImageResource(R.drawable.confirmation_delivery);//确认发货
                            vHolder.iv_action2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), input_confirmation_delivery_activity.class);
                                    intent.putExtra("jobOrderId", datas.get(position).getId());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            vHolder.iv_action1.setVisibility(View.GONE);
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                    } else {
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                    break;
                case EVALUATION: //待评价003
                    vHolder.iv_action1.setVisibility(View.GONE);
                    vHolder.iv_action2.setVisibility(View.GONE);
                    break;
                case CONFIRMED://待确认004
                    vHolder.iv_action1.setVisibility(View.GONE);
                    vHolder.iv_action2.setVisibility(View.GONE);
                    break;
                case HISTORY://历史工单005
                    vHolder.iv_action1.setVisibility(View.GONE);
                    vHolder.iv_action2.setVisibility(View.GONE);
                    break;
            }


            return convertView;
        }
    }

    /**
     * 注销广播
     */
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(receiveBroadCast);
        super.onDestroyView();
    }

}
