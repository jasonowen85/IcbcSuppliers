package com.grgbanking.supplier.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grgbanking.supplier.R;

public class dropbox_bank_fragment extends BaseFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dropbox_bank, container, false);
        return rootView;
    }
}
