package com.netease.nim.uikit.session.actions;

import com.grgbanking.supplier.R;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.session.extension.WorkorderAttachment;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class WorkorderAction extends BaseAction {
    private final static String TAG = "WorkorderAction";

    public WorkorderAction() {
        super(R.drawable.nim_message_plus_location_selector, R.string.input_panel_workorder);
    }

    @Override
    public void onClick() {
        //TODO fengtangquan
        showConfirmSource();

    }

    public void showConfirmSource() {
        EasyAlertDialogHelper.createOkCancelDiolag(getActivity(), null, "发送故障填写工单到当前聊天窗口？", true, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {

            }

            @Override
            public void doOkAction() {
                WorkorderAttachment attachment = new WorkorderAttachment();
               //TODO fengtangquan
                attachment.setOrderid("");
                IMMessage message;
                message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment);
                sendMessage(message);
            }
        }).show();
    }
}
