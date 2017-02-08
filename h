[33mcommit 0049220b47db8db87c18138c78fa850300e720a0[m
Author: jasonnowen85 <410133912@qq.com>
Date:   Wed Feb 8 15:20:58 2017 +0800

    录音权限

[1mdiff --git a/supplier/src/com/grgbanking/supplier/common/util/UPlayer.java b/supplier/src/com/grgbanking/supplier/common/util/UPlayer.java[m
[1mindex a85e021..1c1c115 100644[m
[1m--- a/supplier/src/com/grgbanking/supplier/common/util/UPlayer.java[m
[1m+++ b/supplier/src/com/grgbanking/supplier/common/util/UPlayer.java[m
[36m@@ -56,6 +56,7 @@[m [mpublic class UPlayer implements IVoiceManager, MediaPlayer.OnCompletionListener,[m
         Message msg = mHandler.obtainMessage();[m
         msg.what = 1;[m
         mHandler.sendMessage(msg);[m
[32m+[m[32m        stop();[m
     }[m
 [m
     @Override[m
[36m@@ -72,6 +73,7 @@[m [mpublic class UPlayer implements IVoiceManager, MediaPlayer.OnCompletionListener,[m
 //        mPlayer.stop();[m
 //        mPlayer.release();[m
 //        mPlayer = null;[m
[32m+[m[32m//        mPlayer.seekTo(0);[m
         mPlayer.reset();[m
         return false;[m
     }[m
