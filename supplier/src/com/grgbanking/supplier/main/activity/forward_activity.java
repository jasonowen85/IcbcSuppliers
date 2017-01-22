package com.grgbanking.supplier.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.grgbanking.supplier.DemoCache;
import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.forwardUserInfo;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.common.util.widget.IconCenterEditText;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufei on 2016/7/31.
 * 转发工单界面
 */
public class forward_activity extends UI implements OnClickListener {
    private ListView lv_news;
    private ListAdapt mListAdapt;
    private IconCenterEditText icet_search;
    private ImageView iv_confirmforward;
    private ImageView[] btnArr;
    private List<workOrder> datas;
    private String mSelectUserId;
    private String mOrderId;
    private MapView mMapView = null;
    //地图实例
    private BaiduMap mBaiduMap;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor mIconMaker;
    private Context mContext;
    private List<forwardUserInfo> forwardUserInfos = new ArrayList<>();
    private LatLng bankLatLng;
    private GeoCoder geoCoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_activity);
        mContext = this;
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.forward;
        setToolBar(R.id.toolbar, options);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapview);
        // 获得地图的实例
        mBaiduMap = mMapView.getMap();
        mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.map_supplier);
        datas = new ArrayList<>();
        getParams();
        if (Preferences.getUserRole().equals("20002")) {
            mMapView.setVisibility(View.VISIBLE);
            getData();
            initMarkerClickEvent();
            initMapClickEvent();
        } else {
            mMapView.setVisibility(View.GONE);
            getData1();
            initID();
        }


    }

    private void getParams() {
        mOrderId = this.getIntent().getStringExtra("orderId");
    }

    private void initID() {
        iv_confirmforward = (ImageView) findViewById(R.id.iv_confirmforward);
        iv_confirmforward.setOnClickListener(this);
        lv_news = (ListView) findViewById(R.id.lv_news);
        mListAdapt = new ListAdapt(this);
        lv_news.setAdapter(mListAdapt);
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < datas.size(); i++) {
                    if (Integer.parseInt(parent.getAdapter().getItem(position).toString()) == i) {
                        btnArr[i].setImageResource(R.drawable.check);
                        mSelectUserId = datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getForwardId();
                    } else {
                        btnArr[i].setImageResource(R.drawable.kongbai);
                    }
                }
            }
        });
        //ToolUtil.ReCalListViewHeightBasedOnChildren(lv_news);
        /*icet_search=(IconCenterEditText)findViewById(R.id.icet_search);
        icet_search.setOnSearchClickListener(new IconCenterEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                Toast.makeText(forward_activity.this, "i'm going to seach", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public void getData1() {
        ServerApi.getForwardList(mOrderId, DemoCache.getUserid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    datas.clear();
                    JSONObject jsonObject = response.optJSONObject("lists");
                    JSONArray jsonArray = jsonObject.optJSONArray("lists");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        workOrder work = new workOrder();
                        JSONObject jsonOb;
                        try {
                            jsonOb = jsonArray.getJSONObject(i);
                            work.setForwardId(jsonOb.getString("id"));
                            work.setForwardName(jsonOb.getString("name"));
                            work.setForwardPhone(jsonOb.getString("phone"));
                            work.setUserType(jsonOb.getString("userType"));
                            work.setUserRoleId(jsonOb.getString("userRoleId"));
                            work.setRoleName(jsonOb.getString("roleName"));
                            work.setSupplierName(jsonOb.getString("supplierName"));
                            work.setDepart(jsonOb.getString("depart"));
                            // work.setForwardImageUrls(jsonOb.get(""));
                            datas.add(work);
                            btnArr = null;
                            btnArr = new ImageView[datas.size()];
                            mListAdapt.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(forward_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(forward_activity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(forward_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(forward_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData() {
        ServerApi.getForwardList(mOrderId, DemoCache.getUserid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    forwardUserInfos.clear();
                    JSONObject jsonObject = response.optJSONObject("lists");
                    JSONArray jsonArray = jsonObject.optJSONArray("lists");
                    String address = jsonObject.optString("bankAddress");
                    reverseGeoCode(address);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        forwardUserInfo work = new forwardUserInfo();
                        JSONObject jsonOb;
                        try {
                            jsonOb = jsonArray.getJSONObject(i);
                            work.setCoordinates(jsonOb.getString("coordinates"));
                            LogUtil.e("qzc", "coordinates==" + work.getCoordinates());
                            if (!work.getCoordinates().equals("")) {
                                work.setLatitude(Double.valueOf(work.getCoordinates().split(",")[0]));
                                work.setLongitude(Double.valueOf(work.getCoordinates().split(",")[1]));
                                work.setId(jsonOb.getString("id"));
                                work.setName(jsonOb.getString("name"));
                                work.setPhone(jsonOb.getString("phone"));
                                work.setUserType(jsonOb.getString("userType"));
                                work.setUserRoleId(jsonOb.getString("userRoleId"));
                                work.setRoleName(jsonOb.getString("roleName"));
                                work.setSupplierName(jsonOb.getString("supplierName"));
                                work.setDepart(jsonOb.getString("depart"));
                                work.setJobOrderNum(jsonOb.getString("jobOrderNum"));
                                forwardUserInfos.add(work);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    addInfosOverlay(forwardUserInfos);
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(forward_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(forward_activity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(forward_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(forward_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getForward(String userId, final String state) {
        ServerApi.forward(mOrderId, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(forward_activity.this, "转发成功！", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent();
                    it.setClass(forward_activity.this, first_workorder_activity.class);
                    it.putExtra("state", state);
                    startActivity(it);
                    finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(forward_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(forward_activity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(forward_activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(forward_activity.this, message, Toast.LENGTH_SHORT).show();
            }

        });
    }

    class ViewHolder {
        TextView tv_supplierName, tv_roleName, tv_name, tv_news;
        ImageView iv_head_portrait, iv_check;
    }

    class ListAdapt extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public ListAdapt(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            setData();
        }

        private void setData() {
            for (int i = 0; i < datas.size(); i++) {
                btnArr[i] = new ImageView(mContext);
                btnArr[i].setTag(i);
            }
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
                convertView = mLayoutInflater.inflate(R.layout.forward_list, null);
                vHolder.tv_supplierName = (TextView) convertView.findViewById(R.id.tv_supplierName);//公司名字
                vHolder.tv_roleName = (TextView) convertView.findViewById(R.id.tv_roleName);//角色
                vHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//姓名
                vHolder.tv_news = (TextView) convertView.findViewById(R.id.tv_news);//处理工单数
                vHolder.iv_head_portrait = (ImageView) convertView.findViewById(R.id.iv_head_portrait);//头像

                vHolder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_supplierName.setText(datas.get(position).getSupplierName());
            vHolder.tv_roleName.setText(datas.get(position).getRoleName());
            vHolder.tv_name.setText(datas.get(position).getForwardName());

          /*  Glide.with(forward_activity.this)
                    //.load(datas.get(position).getImageUrls().get(1))
                    .load("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=618261607,3271831828&fm=58")
                    .override(100, 100)
                    .into(vHolder.iv_head_portrait);
*/
            btnArr[position] = vHolder.iv_check;
//            vHolder.iv_check.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        for (int i = 0; i < datas.size(); i++) {
//                            if (position == i) {
//                                btnArr[i].setImageResource(R.drawable.check);
//                                mSelectUserId = datas.get(position).getForwardId();
//                            } else {
//                                btnArr[i].setImageResource(R.drawable.kongbai);
//                            }
//                        }
//                    }
//            });
            return convertView;
        }

    }

    @Override
    public void onClick(View v) {
        if (v == iv_confirmforward) {
            if (mSelectUserId == "" || mSelectUserId == null) {
                Toast.makeText(forward_activity.this, "请选择转发人", Toast.LENGTH_SHORT).show();
            } else {
                getForward(mSelectUserId, "002");
            }

        }
    }

    /**
     * 初始化图层
     */
    public void addInfosOverlay(List<forwardUserInfo> infos) {
        //mBaiduMap.clear();
        LatLng latLng = null;
        OverlayOptions overlayOptions;
        Marker marker;
        for (forwardUserInfo info : infos) {
            // 位置
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            // 图标
            overlayOptions = new MarkerOptions().position(latLng)
                    .icon(mIconMaker).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }

        // 将地图移到到最后一个经纬度位置
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(15).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(u);
    }

    private void initMarkerClickEvent() {
        // 对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker.getExtraInfo() == null) {
                    return false;
                }
                // 获得marker中的数据
                final forwardUserInfo info = (forwardUserInfo) marker.getExtraInfo().get("info");

                InfoWindow mInfoWindow;
                //从xml创建要显示的View，并设置相应的值
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.layout_map_item, null);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView phone = (TextView) view.findViewById(R.id.phone);
                TextView jobNum = (TextView) view.findViewById(R.id.jobnum);
                Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                name.setText("工程师:" + info.getName());
                phone.setText("电 话:" + info.getPhone());
                jobNum.setText("当前处理工单数:" + info.getJobOrderNum());
                btnOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getForward(info.getId(), "002");
                    }
                });
                // 将marker所在的经纬度的信息转化成屏幕上的坐标
                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 40;
                p.x -= 40;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                // 为弹出的InfoWindow添加点击事件
                mInfoWindow = new InfoWindow(view, llInfo, 0);
                // 显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    private void initMapClickEvent() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();
            }
        });
    }

    /**
     * 反地理编码得到地址信息
     */
    private void reverseGeoCode(String address) {
        // 创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(mContext, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                } else {
                    bankLatLng = result.getLocation();
                    LogUtil.e("qzc", "---latitude=" + bankLatLng.latitude + "---longitude=" + bankLatLng.longitude);
                    // 图标
                    OverlayOptions overlayOptions = new MarkerOptions().position(bankLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_customer)).zIndex(5);
                    mBaiduMap.addOverlay(overlayOptions);
                    MapStatus mapStatus = new MapStatus.Builder().target(bankLatLng).zoom(12).build();
                    MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
                    mBaiduMap.setMapStatus(u);
                }

            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.geocode(new GeoCodeOption().city("").address(address));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // 释放地理编码检索实例
        if (geoCoder != null) {
            geoCoder.destroy();
        }
        mMapView.onDestroy();
        mIconMaker.recycle();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
