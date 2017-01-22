package com.grgbanking.supplier.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ApiHttpClient;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.photo.AlbumActivity;
import com.grgbanking.supplier.common.photo.GalleryActivity;
import com.grgbanking.supplier.common.photo.adapter.MyAdapter;
import com.grgbanking.supplier.common.photo.adapter.PictureAdapter;
import com.grgbanking.supplier.common.photo.model.ImageBean;
import com.grgbanking.supplier.common.photo.popwindow.SelectPicPopupWindow;
import com.grgbanking.supplier.common.photo.utils.Bimp;
import com.grgbanking.supplier.common.photo.utils.BitmapUtils;
import com.grgbanking.supplier.common.photo.utils.FileUtils;
import com.grgbanking.supplier.common.photo.view.NoScrollGridView;
import com.grgbanking.supplier.common.util.sys.ImageUtils;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufei on 2016/8/3.
 */
public class input_confirm_complete_activity extends UI {
    private NoScrollGridView noScrollGridView;
    private PictureAdapter adapter;
    private com.grgbanking.supplier.common.photo.popwindow.SelectPicPopupWindow menuWindow;
    private input_confirm_complete_activity instence;
    private String filepath;
    private ImageView iv_forward;
    private EditText et_confirm_complete;
    private List<String> urllist;
    private static final int TAKE_PICTURE = 0;
    private final int RESULT_CODE_STARTCAMERA = 202;
    private String urls;
    String jobOrderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instence = this;
        setContentView(R.layout.activity_input_confirm_complete);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.confirm_complete;
        setToolBar(R.id.toolbar, options);
        urllist = new ArrayList<String>();
        getParams();
        InitView();

    }

    private void getParams() {
        jobOrderId = this.getIntent().getStringExtra("jobOrderId");
    }

    private void InitView() {
        noScrollGridView = (NoScrollGridView) findViewById(R.id.noScrollgridview);
        noScrollGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        et_confirm_complete = (EditText) findViewById(R.id.et_confirm_complete);
        adapter = new PictureAdapter(this);
        noScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideInput(input_confirm_complete_activity.this.getApplicationContext(), et_confirm_complete);
                if (i == Bimp.getTempSelectBitmap().size()) {
                    selectImgs();
                } else {
                    Intent intent = new Intent(instence,
                            GalleryActivity.class);
                    intent.putExtra("ID", i);
                    startActivity(intent);
                }
            }
        });
        noScrollGridView.setAdapter(adapter);
        iv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_confirm_complete.getText().toString();
                if (StringUtil.isEmpty(content)) {
                    Toast.makeText(input_confirm_complete_activity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadObjFiles();
            }
        });
    }


    private void uploadObjFiles() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            final RequestParams params = new RequestParams();
            int number = 0;
            int j = 0;
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {

                try {
                    number++;
                    File f = new File(ImageUtils.getRealFilePath(this, Uri.parse(Bimp.tempSelectBitmap.get(i).getPath())));
                    File file = ImageUtils.compressBmpToFile(Bimp.tempSelectBitmap.get(i).getBitmap(), f.getName() + ".jpg");
                    params.put("fileName" + j, file);
                    j++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            params.put("number", number);
            ApiHttpClient.setTimeOut(30000);
            ServerApi.uploadSingleFile(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("ret_code").equals("0")) {
                            ApiHttpClient.setTimeOut(10000);
                            try {
                                urls = response.getString("dataset");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            confirmComplete();
                        } else {
                            hideWaitDialog();
                            String msg = response.getString("ret_msg");
                            Toast.makeText(input_confirm_complete_activity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable
                        throwable, JSONObject obj) {
                    hideWaitDialog();
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(input_confirm_complete_activity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String message, Throwable
                        throwable) {
                    hideWaitDialog();
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(input_confirm_complete_activity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    showWaitDialog();
                }
            });

        } else

        {
            confirmComplete();
            return;
        }

    }

    /**
     * 强制隐藏输入法键盘
     */
    private void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void selectImgs() {
        //  ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(instence.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(input_confirm_complete_activity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(input_confirm_complete_activity.this.findViewById(R.id.llImage), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮
                    goCamera();
                    if (Build.VERSION.SDK_INT >= 23) {
                        //请求摄像头权限；
                        //请求摄像头权限；
                        String[] perms = {"android.permission.CAMERA"};
                        ActivityCompat.requestPermissions(input_confirm_complete_activity.this, perms, RESULT_CODE_STARTCAMERA);
                    } else {
                        goCamera();
                    };
                    break;
                case R.id.item_popupwindows_Photo:       //点击从相册中选择按钮
                    Intent intent = new Intent(instence,
                            AlbumActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode) {
            case RESULT_CODE_STARTCAMERA:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    goCamera();
                } else {
                    Toast.makeText(input_confirm_complete_activity.this, "请到设置界面开启摄像头权限", Toast.LENGTH_SHORT).show();
//                    showToast(input_confirm_complete_activity.this, "请到设置界面开启摄像头权限");
                }
                break;
        }
    }
    private void confirmComplete() {

        String situation = et_confirm_complete.getText().toString();
        ServerApi.confirmComplete(jobOrderId, Preferences.getUserid(), "", "", situation, urls, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                try {
                    ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        comfirmOrder(jobOrderId);
                    } else {
                        ret_msg = response.getString("ret_msg");
                        Toast.makeText(input_confirm_complete_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(input_confirm_complete_activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(input_confirm_complete_activity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(input_confirm_complete_activity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //确认
    protected void comfirmOrder(String orderid) {
        ServerApi.comfirmOrder(orderid, Preferences.getUserid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Bimp.tempSelectBitmap.clear();
                    MyAdapter.mSelectedImage.clear();
                    Toast.makeText(input_confirm_complete_activity.this, "确认成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(input_confirm_complete_activity.this, first_workorder_activity.class);
                    intent.putExtra("state", "003");
                    startActivity(intent);
                    finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(input_confirm_complete_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(input_confirm_complete_activity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(input_confirm_complete_activity.this, "确认完成失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(input_confirm_complete_activity.this, "确认完成失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goCamera() {

        filepath = FileUtils.iniFilePath(input_confirm_complete_activity.this);
        urllist.add(filepath);
        File file = new File(filepath);
        // 启动Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = BitmapUtils.getCompressedBitmap(input_confirm_complete_activity.this, filepath);
                    FileUtils.saveBitmap(bm, fileName);

                    ImageBean takePhoto = new ImageBean();
                    takePhoto.setBitmap(bm);
                    takePhoto.setPath(filepath);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


}
