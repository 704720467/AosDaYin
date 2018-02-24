package com.dothantech.nfcmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dothantech.common.DzLog;
import com.dothantech.common.DzTask;
import com.dothantech.printer.DzPrinter;
import com.dothantech.view.DzWindow;

public class NFCManagerActivity extends Activity {
	public static final DzLog Log = DzLog.getLog("DzNFC");

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		Intent localIntent = new Intent(getIntent());
		ArrayList localArrayList = DzWindow.getViewInfos();
		if (localArrayList == null)
			;
		for (int i = 0; (i < 1)
				|| ((i == 1) && (((DzWindow.ViewInfo) localArrayList.get(0)).mActivity == this)); i = localArrayList
				.size()) {
			DzTask.startActivityWithNFCIntent(this, localIntent);
			return;
		}
		DzPrinter.getInstance().onNfcDiscovery(localIntent);
		DzTask.moveTaskToFront(this,
				((DzWindow.ViewInfo) localArrayList.get(0)).mActivity
						.getTaskId());
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onResume() {
		super.onResume();
		finish();
	}
}
