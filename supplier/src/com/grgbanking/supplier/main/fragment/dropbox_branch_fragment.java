package com.grgbanking.supplier.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.login.LoginActivity;
import com.grgbanking.supplier.main.Spinner.AbstractSpinerAdapter;
import com.grgbanking.supplier.main.Spinner.CustemObject;
import com.grgbanking.supplier.main.Spinner.CustemSpinerAdapter;
import com.grgbanking.supplier.main.Spinner.SpinerPopWindow;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class dropbox_branch_fragment extends BaseFragment implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener {
    private TextView tv_company;
    private ImageView iv_company;
    private LinearLayout ll_company;
    private List<CustemObject> nameList = new ArrayList<CustemObject>();
    private AbstractSpinerAdapter mAdapter;
    private List<workOrder> orders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dropbox_branch, container, false);
        init(rootView);
        getData();
        return rootView;
    }

    //获取分行
    protected void getData() {
        ServerApi.bankList(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    JSONArray jsonArr = response.optJSONArray("lists");
                    orders = new ArrayList<workOrder>();
                    workOrder order = new workOrder();
                    List<String> names = new ArrayList<String>();
                    List<String> ids = new ArrayList<String>();
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonOb = new JSONObject();
                        try {
                            jsonOb = jsonArr.getJSONObject(i);
                            order.setBranchId(jsonOb.getString("id"));
                            order.setBranchName(jsonOb.getString("name"));
                            orders.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ids.add(order.getBranchId());
                        names.add(order.getBranchName());
                    }
                    CustemObject object1 = new CustemObject();
                    object1.data = "全部";
                    nameList.add(object1);
                    for (int i = 0; i < names.size(); i++) {
                        CustemObject object = new CustemObject();
                        object.data = names.get(i);
                        object.id = ids.get(i);
                        nameList.add(object);
                    }
                    theAssignment();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init(View rootView) {
        ll_company = (LinearLayout) rootView.findViewById(R.id.ll_company);
        tv_company = (TextView) rootView.findViewById(R.id.tv_company);
        tv_company.setOnClickListener(this);
        iv_company = (ImageView) rootView.findViewById(R.id.iv_company);
        iv_company.setOnClickListener(this);
        mAdapter = new CustemSpinerAdapter(getActivity());
        mSpinerPopWindow = new SpinerPopWindow(getActivity());
        mSpinerPopWindow.setAdatper(mAdapter);
        mSpinerPopWindow.setItemListener(this);
    }

    private void theAssignment() {
        //mAdapter = new CustemSpinerAdapter(getActivity());
        mAdapter.refreshData(nameList, 0);
        //mSpinerPopWindow = new SpinerPopWindow(getActivity());
        //mSpinerPopWindow.setAdatper(mAdapter);
       // mSpinerPopWindow.setItemListener(this);
    }

    private SpinerPopWindow mSpinerPopWindow;

    private void showSpinWindow() {
        Log.e("", "showSpinWindow");
        mSpinerPopWindow.setWidth(ll_company.getWidth());
        mSpinerPopWindow.showAsDropDown(ll_company);

    }

    @Override
    public void onItemClick(int pos) {

        setHero(pos);
    }

    private void setHero(int pos) {

        if (pos >= 0 && pos <= nameList.size()) {
            CustemObject value = nameList.get(pos);
            tv_company.setText(value.toString());

            Intent intent = new Intent();
            intent.setAction("ACTION_NAME");
            if (tv_company.getText().equals("全部")) {
                intent.putExtra("id", "0000");
            } else {
                intent.putExtra("id", value.idString());
            } if (getActivity() != null) {
                getActivity().sendBroadcast(intent);//发送广播
            }
        }
    }

    @Override
    public void onClick(View v) {

        showSpinWindow();
    }
}
