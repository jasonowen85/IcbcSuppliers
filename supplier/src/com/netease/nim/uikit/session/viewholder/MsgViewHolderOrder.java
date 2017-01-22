package com.netease.nim.uikit.session.viewholder;

import android.widget.TextView;

import com.grgbanking.supplier.R;
import com.netease.nim.uikit.common.ui.imageview.MsgThumbImageView;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.extension.WorkorderAttachment;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderOrder extends MsgViewHolderBase {
    private WorkorderAttachment attachment;
    public MsgThumbImageView orderView;
    public TextView orderText;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_order;
    }

    @Override
    protected void inflateContentView() {
        orderView = (MsgThumbImageView) view.findViewById(R.id.message_item_order_image);
        orderText = (TextView) view.findViewById(R.id.message_item_order_text);
    }

    @Override
    protected void bindContentView() {
        final WorkorderAttachment order = (WorkorderAttachment) message.getAttachment();
        orderText.setText(order.getOrderid());

        int[] bound = ImageUtil.getBoundWithLength(getLocationDefEdge(), R.drawable.my_order1, true);
        int width = bound[0];
        int height = bound[1];

        setLayoutParams(width, height, orderView);
        setLayoutParams(width, (int) (0.38 * height), orderText);

        orderView.loadAsResource(R.drawable.my_order1, width, height, R.drawable.nim_message_item_round_bg);
    }

    @Override
    protected void onItemClick() {
     //   WorkorderAttachment workorder = (WorkorderAttachment) message.getAttachment();
       // Toast.makeText(context, workorder.getOrderid(), Toast.LENGTH_SHORT).show();
    }

    public static int getLocationDefEdge() {
        return (int) (0.25 * ScreenUtil.screenWidth);
    }
}
