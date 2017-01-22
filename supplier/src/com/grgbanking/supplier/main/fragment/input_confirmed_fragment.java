package com.grgbanking.supplier.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grgbanking.supplier.R;
import com.grgbanking.supplier.common.bean.workOrder;
import com.grgbanking.supplier.common.util.widget.ListViewCompat;

import java.util.ArrayList;

public class input_confirmed_fragment extends input_workorder_baskfragment  {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_input_maintenance, container, false);
        datas = new ArrayList<workOrder>();
        SetType("003");
        getData(ListViewCompat.REFRESH);
        init(rootView);
        return rootView;
    }

}
