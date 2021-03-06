package com.netease.nim.uikit.session.actions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.grgbanking.supplier.R;
import com.grgbanking.supplier.common.util.PermissionUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nim.uikit.session.fragment.MessageFragment;
import com.netease.nim.uikit.session.helper.VideoMessageHelper;
import com.netease.nim.uikit.session.module.PermissionResult;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class VideoAction extends BaseAction implements PermissionResult {
    // 视频
    protected VideoMessageHelper videoMessageHelper;

    public VideoAction() {
        super(R.drawable.nim_message_plus_video_selector, R.string.input_panel_video);

    }

    public VideoAction(MessageFragment fragment) {
        super(R.drawable.nim_message_plus_video_selector, R.string.input_panel_video);
        mFragment = fragment;
    }

    @Override
    public void onClick() {
        videoHelper().showVideoSource(makeRequestCode(RequestCode.GET_LOCAL_VIDEO), makeRequestCode(RequestCode.CAPTURE_VIDEO));
    }

    /**
     * ********************** 视频 *******************************
     */
    private void initVideoMessageHelper() {
        videoMessageHelper = new VideoMessageHelper(getActivity(), new VideoMessageHelper.VideoMessageHelperListener() {

            @Override
            public void onVideoPicked(File file, String md5) {
                MediaPlayer mediaPlayer = getVideoMediaPlayer(file);
                long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
                int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
                IMMessage message = MessageBuilder.createVideoMessage(getAccount(), getSessionType(), file, duration, width, height, md5);
                sendMessage(message);
            }
        });
        videoMessageHelper.setFragment(mFragment);
    }

    /**
     * 获取视频mediaPlayer
     * @param file 视频文件
     * @return mediaPlayer
     */
    private MediaPlayer getVideoMediaPlayer(File file) {
        try {
            return MediaPlayer.create(getActivity(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case RequestCode.GET_LOCAL_VIDEO:
            videoHelper().onGetLocalVideoResult(data);
            break;
        case RequestCode.CAPTURE_VIDEO:
            videoHelper().onCaptureVideoResult(data);
            break;
        }
    }

    private VideoMessageHelper videoHelper() {
        if (videoMessageHelper == null) {
            initVideoMessageHelper();
        }
        return videoMessageHelper;
    }

    @Override
    public void requestPermissionAudio() {

    }

    @Override
    public void requestPermissionCarame(boolean isAllow) {
        LogUtil.i("video", "摄像头权限回调");
        if(isAllow)
            videoHelper().chooseVideoFromCamera();
    }

    @Override
    public void requestPermissionSDcard(boolean isAllow) {
        LogUtil.i("video", "sd权限回调");
        if(isAllow)
            videoHelper().chooseVideoFromLocal();
    }
}
