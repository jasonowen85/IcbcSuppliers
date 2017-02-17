package com.grgbanking.supplier.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.supplier.DemoCache;
import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.bean.userRole;
import com.grgbanking.supplier.common.dialog.CommonDialog;
import com.grgbanking.supplier.common.dialog.DialogHelper;
import com.grgbanking.supplier.common.util.PermissionUtils;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.config.preference.UserPreferences;
import com.grgbanking.supplier.main.activity.ForgetpasswordActivity;
import com.grgbanking.supplier.main.activity.MainActivity;
import com.grgbanking.supplier.main.fragment.ToolUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.module.PermissionResult;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录/注册界面
 * <p>
 * Created by huangjun on 2015/2/1.
 */
public class LoginActivity extends UI implements OnKeyListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";
    public userRole[] userRoles;
    public CharSequence[] userRolenames;
    private Button btn_login;  // ActionBar完成按钮
    private TextView tv_forget;
    private ClearableEditTextWithIcon loginAccountEdit;
    private ClearableEditTextWithIcon loginPasswordEdit;
    private ClearableEditTextWithIcon loginRoleEdit;
    private boolean isShowDialog = false;
    private View loginLayout;

    private AbortableFuture<LoginInfo> loginRequest;


    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
//        if (Build.VERSION.SDK_INT >= 23) {
//            if(PermissionUtils.lacksPermissions(this, PermissionUtils.requestPermissions)){
//                List<String> requestPermission = new ArrayList<String>();
//                for(String permission : PermissionUtils.requestPermissions){
//                    if(lacksPermission(permission)){
//                        requestPermission.add(permission);
//                    }
//                }
//                ActivityCompat.requestPermissions(this,
//                        requestPermission.toArray(new String[requestPermission.size()]), PERMISSION_REQUEST_CODE); // 请求权限
//                requestPermission.clear();
//            }
//        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionUtils.lacksPermission(this, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                //缺少必要权限 不让用户登陆
                ActivityCompat.requestPermissions(this,
                        new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE}, PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
                isShowDialog = true;
            }
        }
        ToolBarOptions options = new ToolBarOptions();
        options.isNeedNavigate = false;
        options.logoId = R.drawable.actionbar_white_logo_space;
        setToolBar(R.id.toolbar, options);

        onParseIntent();

        setupLoginPanel();
    }

    private void onParseIntent() {
        if (getIntent().getBooleanExtra(KICK_OUT, false)) {
            int type = NIMClient.getService(AuthService.class).getKickedClientType();
            String client;
            switch (type) {
                case ClientType.Web:
                    client = "网页端";
                    break;
                case ClientType.Windows:
                    client = "电脑端";
                    break;
                case ClientType.REST:
                    client = "服务端";
                    break;
                default:
                    client = "移动端";
                    break;
            }
            EasyAlertDialogHelper.showOneButtonDiolag(LoginActivity.this, getString(R.string.kickout_notify),
                    String.format(getString(R.string.kickout_content), client), getString(R.string.ok), true, null);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        //判断是否有sd权限 否则不让登陆
        boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this
                , PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE );
        Log.e("jiang", "onResume" + "运行  222" + b );
        if(!isShowDialog && !b && PermissionUtils.lacksPermission(this, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE)){
            //弹出对话框 授权;
            showDialogPermission();
            Log.e("jiang", "onResume" + "再次授权" );
        }

    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_DENIED;
    }


    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {

                case PermissionUtils.CODE_GET_ACCOUNTS:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(LoginActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
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
            case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                isShowDialog = false;
                if(grantResults.length >=1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this
                                , permissions[0])) {//点击拒绝，再次弹出
                            ActivityCompat.requestPermissions(this, permissions, PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
                            isShowDialog = true;
                        } else if(!isShowDialog) { // 选择不再询问，并点击拒绝，弹出提示框
                            Log.e("jiang", "toast" + "运行");
                            showDialogPermission();
                        }
                    }
                }
                break;
        }
    }

    public void showDialogPermission() {
        isShowDialog = true;
        Log.e("jianglu", "马上显示dialog");
        EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.helps),
                getString(R.string.readSDcard) + getString(R.string.string_help_text),
                getString(R.string.settings), getString(R.string.quit_apk), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                        //返回上一个界面;
                        Log.e("jianglu", "马上返回上一个界面 退出应用了");
                        isShowDialog = false;
                        finish();
                    }

                    @Override
                    public void doOkAction() {
                        //启动设置引导界面
                        isShowDialog = false;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + LoginActivity.this.getPackageName()));
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * 登录面板
     */
    private void setupLoginPanel() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null != loginAccountEdit && loginAccountEdit.isFocused() ){
//                    getRoles(loginAccountEdit.getText().toString());
                    loginAccountEdit.clearFocus();
//                    loginAccountEdit.setNextFocusDownId(R.id.edit_login_password);
                    if(null == userRolenames){
                        return;
                    }
                }
                login2server();
                //login2test();
            }
        });
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ForgetpasswordActivity.class));
            }
        });
        loginAccountEdit = findView(R.id.edit_login_account);
        loginPasswordEdit = findView(R.id.edit_login_password);
        loginRoleEdit = findView(R.id.edit_login_role);

        loginAccountEdit.setIconResource(R.drawable.user_account_icon);
        loginPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);
        loginRoleEdit.setIconResource(R.drawable.action_bar_role_view_icon);
        loginRoleEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != userRolenames && userRolenames.length > 1){
                    final CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(LoginActivity.this);
                    dialog.setTitle("选择角色");
                    dialog.setNegativeButton(R.string.cancel, null);
                    dialog.setItemsWithoutChk(userRolenames, new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            loginRoleEdit.setText(parent.getAdapter().getItem(position).toString());
                            Preferences.saveUserRole(userRoles[position].getCode());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });

        loginAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginAccountEdit.addTextChangedListener(textWatcher);
        loginPasswordEdit.addTextChangedListener(textWatcher);
        loginAccountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if(null != userRolenames && userRolenames.length != 0){
                        userRolenames  = null;
                        if(loginRoleEdit.getVisibility() == View.VISIBLE){
                            loginRoleEdit.setVisibility(View.GONE);
                        }
                    }
                } else {
                    getRoles(loginAccountEdit.getText().toString());
                }
            }
        });

        loginPasswordEdit.setOnKeyListener(this);

        String account = Preferences.getUserAccount();
        loginAccountEdit.setText(account);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 登录模式
            boolean isEnable = loginAccountEdit.getText().length() > 0
                    && loginPasswordEdit.getText().length() > 0;
            btn_login.setEnabled(isEnable);

        }
    };

    private void getRoles(String userId) {
        ServerApi.getRoles(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                userRolenames = null;
                userRoles = null;
                Preferences.saveUserRoleids("");
                Preferences.saveUserRolenames("");
                String ret_code = null;
                try {
                    ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        JSONObject data = response.getJSONObject("lists");
                        String idstr = data.getString("userRoleIdList");
                        String namestr = data.getString("userRoleNameList");
                        String[] ids = idstr.split(",");
                        String[] names = namestr.split(",");
                        userRolenames = new CharSequence[ids.length];
                        userRoles = new userRole[ids.length];
                        String id = "";
                        String name = "";
                        for (int i = 0; i < ids.length; i++) {
                            userRoles[i] = new userRole(ids[i], names[i]);
                            userRolenames[i] = names[i];
                            if (i + 1 == ids.length) {
                                id += ids[i];
                                name += names[i];
                            } else {
                                id += ids[i] + ",";
                                name += names[i] + ",";
                            }
                        }

                        Preferences.saveUserRoleids(id);
                        Preferences.saveUserRolenames(name);

                        if (ids.length == 1) {
                            loginRoleEdit.setVisibility(View.GONE);
                            Preferences.saveUserRole(idstr);
                        } else if (ids.length > 1) {
                            loginRoleEdit.setVisibility(View.VISIBLE);
                            final CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(LoginActivity.this);
                            dialog.setTitle("选择角色");
                            dialog.setNegativeButton(R.string.cancel, null);
                            dialog.setItemsWithoutChk(userRolenames, new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    loginRoleEdit.setText(parent.getAdapter().getItem(position).toString());
                                    Preferences.saveUserRole(userRoles[position].getCode());
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    } else{
                        Toast.makeText(LoginActivity.this ,response.getString("ret_msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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


    /**
     * ***************************************** 登录 **************************************
     */

    private void login2server() {
        String account = loginAccountEdit.getEditableText().toString().toLowerCase();
        String token = loginPasswordEdit.getEditableText().toString();
        String userRole = Preferences.getUserRole();
        if(userRole==""||userRole==null){
            Toast.makeText(LoginActivity.this,"请选择用户角色",Toast.LENGTH_SHORT).show();
        }else{
            DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (loginRequest != null) {
                        loginRequest.abort();
                        onLoginDone();
                    }
                }
            }).setCanceledOnTouchOutside(false);
            ServerApi.login(account, token, userRole, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String ret_code = null;
                    String ret_msg = null;
                    String accid = null;
                    String token = null;
                    String userid = null;
                    String userrole = null;
                    try {
                        ret_code = response.getString("ret_code");

                        if (ret_code.equals("0")) {
                            JSONObject data = response.getJSONObject("lists");
                            accid = data.getString("accid");
                            token = data.getString("token");
                            userid = data.getString("id");
                            userrole = data.getString("userRole");
                            login2netease(accid, token, userid, userrole);
                        } else {
                            ret_msg = response.getString("ret_msg");
                            Toast.makeText(LoginActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                            onLoginDone();
                        }
                    } catch (JSONException e) {
                        onLoginDone();
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                    LogUtil.e(TAG, "login fail:" + throwable.getMessage());
                    onLoginDone();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                    LogUtil.e(TAG, "login fail:" + throwable.getMessage());
                    onLoginDone();
                }
            });
        }
    }

    private void login2netease(final String account, final String token, final String userid, final String userrole) {
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");

                onLoginDone();
                DemoCache.setAccount(account);
                DemoCache.setUserid(userid);

                saveLoginInfo(account, token, userid);

                // 初始化消息提醒
                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(DemoCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
                DataCacheManager.buildDataCacheAsync();

                // 进入主界面
                MainActivity.start(LoginActivity.this, null);
                finish();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    private void login2test() {
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = loginAccountEdit.getEditableText().toString().toLowerCase();
        final String token = tokenFromPassword(loginPasswordEdit.getEditableText().toString());
        // 登录
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");

                onLoginDone();
                DemoCache.setAccount(account);
                saveLoginInfo(account, token, "");

                // 初始化消息提醒
                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(DemoCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
                DataCacheManager.buildDataCacheAsync();

                // 进入主界面
                MainActivity.start(LoginActivity.this, null);
                finish();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void saveLoginInfo(final String account, final String token, final String userid) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
        Preferences.saveUserid(userid);
    }

    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey)
                || "fe416640c8e8a72734219e1847ad2547".equals(appKey);

        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
