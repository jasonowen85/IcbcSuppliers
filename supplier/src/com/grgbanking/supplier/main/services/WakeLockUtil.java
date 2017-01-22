package com.grgbanking.supplier.main.services;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class WakeLockUtil {

	private static final String TAG = WakeLockUtil.class.getSimpleName();

	private Context context;
	private WakeLock wakeLock;

	public WakeLockUtil(Context context) {
		super();
		this.context = context;
	}

	/**
	 * Acquires the wake lock.
	 */
	public void acquireWakeLock() {
		try {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			if (powerManager == null) {
				return;
			}
			if (wakeLock == null) {
				wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
				if (wakeLock == null) {
					Log.e(TAG, "wakeLock is null.");
					return;
				}
			}
			if (!wakeLock.isHeld()) {
				wakeLock.acquire();
				Log.e(TAG, "hold wakeLock.");
				if (!wakeLock.isHeld()) {
					Log.e(TAG, "Unable to hold wakeLock.");
				}
			}
		} catch (RuntimeException e) {
			Log.e(TAG, "Caught unexpected exception", e);
		}
	}

	/**
	 * Releases the wake lock.
	 */
	public void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}

}
