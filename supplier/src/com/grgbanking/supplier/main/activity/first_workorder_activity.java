package com.grgbanking.supplier.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.grgbanking.supplier.R;
import com.grgbanking.supplier.api.ServerApi;
import com.grgbanking.supplier.common.dialog.CommonDialog;
import com.grgbanking.supplier.common.dialog.DialogHelper;
import com.grgbanking.supplier.config.preference.Preferences;
import com.grgbanking.supplier.main.fragment.dropbox_bank_fragment;
import com.grgbanking.supplier.main.fragment.dropbox_branch_fragment;
import com.grgbanking.supplier.main.fragment.dropbox_time_fragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class first_workorder_activity extends UI implements View.OnClickListener {
    dropbox_bank_fragment fragment_dropbox_bank;
    dropbox_branch_fragment fragment_dropbox_branch;
    dropbox_time_fragment fragment_dropbox_time;
    private ViewStub viewstub_maintenance, viewstub_have_inhand, viewstub_confirmed, viewstub_evaluation, viewstub_history_workorder;
    private ImageView iv_maintenance, iv_confirmed, iv_evaluation, iv_history_workorder, iv_have_in_hand;
    private View currentButton;
    private View view_maintenance, view_have_inhand, view_confirmed, view_evaluation, view_history_workorder;
    protected ImageView iv_message, iv_workorder, iv_me;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_workorder);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.workorder;
        setToolBarCenter(R.id.toolbar, options);
        toolbar.setNavigationIcon(null);
        getParams();

        init();
        if (state != null) {
            if (state.equals("001")) {
                iv_maintenance.setImageResource(R.drawable.up1_2);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_maintenance == null) {
                    viewstub_maintenance = (ViewStub) findViewById(R.id.viewstub_maintenance);
                    view_maintenance = viewstub_maintenance.inflate();
                } else {
                    view_maintenance.setVisibility(View.VISIBLE);
                }

            } else if (state.equals("002")) {
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2_2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_have_inhand == null) {
                    viewstub_have_inhand = (ViewStub) findViewById(R.id.viewstub_have_in_hand);
                    view_have_inhand = viewstub_have_inhand.inflate();
                } else {
                    view_have_inhand.setVisibility(View.VISIBLE);
                }
            } else if (state.equals("003")) {
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3_2);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_confirmed == null) {
                    viewstub_confirmed = (ViewStub) findViewById(R.id.viewstub_confirmed);
                    view_confirmed = viewstub_confirmed.inflate();
                } else {
                    view_confirmed.setVisibility(View.VISIBLE);
                }
            } else if (state.equals("004")) {
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4_2);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_evaluation == null) {
                    viewstub_evaluation = (ViewStub) findViewById(R.id.viewstub_evaluation);
                    view_evaluation = viewstub_evaluation.inflate();
                } else {
                    view_evaluation.setVisibility(View.VISIBLE);
                }
            } else if (state.equals("005")) {
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5_2);
                if (view_history_workorder == null) {
                    viewstub_history_workorder = (ViewStub) findViewById(R.id.viewstub_history_workorder);
                    view_history_workorder = viewstub_history_workorder.inflate();
                } else {
                    view_history_workorder.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    private void getParams() {
        state = this.getIntent().getStringExtra("state");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        if (outState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            // remove掉保存的Fragment
            outState.remove(FRAGMENTS_TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    protected void init() {
        fragment_dropbox_bank = (dropbox_bank_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_bank);
        fragment_dropbox_branch = (dropbox_branch_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_branch);
        fragment_dropbox_time = (dropbox_time_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_time);

        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_workorder.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_category_focus));
        iv_me.setOnClickListener(this);
        iv_message.setOnClickListener(this);

        iv_have_in_hand = (ImageView) findViewById(R.id.iv_have_in_hand);
        iv_have_in_hand.setOnClickListener(this);
        iv_maintenance = (ImageView) findViewById(R.id.iv_maintenance);
        iv_maintenance.setOnClickListener(this);
        iv_confirmed = (ImageView) findViewById(R.id.iv_confirmed);
        iv_confirmed.setOnClickListener(this);
        iv_evaluation = (ImageView) findViewById(R.id.iv_evaluation);
        iv_evaluation.setOnClickListener(this);
        iv_history_workorder = (ImageView) findViewById(R.id.iv_history_workorder);
        iv_history_workorder.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn:
                WorkorderSearchActivity.start(first_workorder_activity.this);
                break;
            case R.id.role_btn:
                String id = Preferences.getUserRoleids();
                String name = Preferences.getUserRolenames();

                if (id != null && !StringUtil.isEmpty(id)) {
                    final String[] ids = id.split(",");
                    String[] names = name.split(",");
                    final CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(this);
                    dialog.setTitle("选择角色");
                    dialog.setNegativeButton(R.string.cancel, null);
                    dialog.setItemsWithoutChk(names, new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    //loginRoleEdit.setText(parent.getAdapter().getItem(position).toString());
                                    ServerApi.changeRole(Preferences.getUserAccount(), ids[position], new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            String ret_code = null;
                                            try {
                                                ret_code = response.getString("ret_code");
                                                if (ret_code.equals("0")) {
                                                    Preferences.saveUserRole(ids[position]);
                                                    Intent intent = new Intent();
                                                    intent.setAction("role_changed");
                                                    sendBroadcast(intent);//发送广播
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                                            LogUtil.e("", "change role fail:" + throwable.getMessage());
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                                            LogUtil.e("", "change role :" + throwable.getMessage());
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }

                    );
                    dialog.show();
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_me:
                Intent i2 = new Intent(this, MeProfileActivity.class);
                startActivity(i2);
                break;
            case R.id.iv_message:
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                break;

            case R.id.iv_maintenance:
                iv_maintenance.setImageResource(R.drawable.up1_2);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_maintenance == null) {
                    viewstub_maintenance = (ViewStub) findViewById(R.id.viewstub_maintenance);
                    view_maintenance = viewstub_maintenance.inflate();
                } else {
                    view_maintenance.setVisibility(View.VISIBLE);
                }
                if (view_have_inhand != null)
                    view_have_inhand.setVisibility(View.GONE);
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.GONE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.GONE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.GONE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_history_workorder).show(fragment_maintenance).commit();
                setButton(v);
                break;
            case R.id.iv_have_in_hand:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2_2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_have_inhand == null) {
                    viewstub_have_inhand = (ViewStub) findViewById(R.id.viewstub_have_in_hand);
                    view_have_inhand = viewstub_have_inhand.inflate();
                } else {
                    view_have_inhand.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.GONE);
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.GONE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.GONE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.GONE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_maintenance).hide(fragment_evaluation).hide(fragment_history_workorder).hide(fragment_confirmed).show(fragment_have_inhand).commit();
                setButton(v);
                break;
            case R.id.iv_confirmed:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3_2);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_confirmed == null) {
                    viewstub_confirmed = (ViewStub) findViewById(R.id.viewstub_confirmed);
                    view_confirmed = viewstub_confirmed.inflate();
                } else {
                    view_confirmed.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.GONE);
                if (view_have_inhand != null)
                    view_have_inhand.setVisibility(View.GONE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.GONE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.GONE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_maintenance).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_history_workorder).show(fragment_confirmed).commit();
                setButton(v);
                break;
            case R.id.iv_evaluation:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4_2);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_evaluation == null) {
                    viewstub_evaluation = (ViewStub) findViewById(R.id.viewstub_evaluation);
                    view_evaluation = viewstub_evaluation.inflate();
                } else {
                    view_evaluation.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.GONE);
                if (view_have_inhand != null)
                    view_have_inhand.setVisibility(View.GONE);
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.GONE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.GONE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_maintenance).hide(fragment_history_workorder).show(fragment_evaluation).commit();
                setButton(v);
                break;
            case R.id.iv_history_workorder:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_have_in_hand.setImageResource(R.drawable.up2);
                iv_confirmed.setImageResource(R.drawable.up3);
                iv_evaluation.setImageResource(R.drawable.up4);
                iv_history_workorder.setImageResource(R.drawable.up5_2);
                if (view_history_workorder == null) {
                    viewstub_history_workorder = (ViewStub) findViewById(R.id.viewstub_history_workorder);
                    view_history_workorder = viewstub_history_workorder.inflate();
                } else {
                    view_history_workorder.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.GONE);
                if (view_have_inhand != null)
                    view_have_inhand.setVisibility(View.GONE);
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.GONE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.GONE);

                // getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_maintenance).show(fragment_history_workorder).commit();
                setButton(v);
                break;
        }
    }

    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }
}
