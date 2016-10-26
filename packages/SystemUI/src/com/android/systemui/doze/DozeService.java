/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.doze;

import android.os.PowerManager;
import android.os.SystemClock;
import android.service.dreams.DreamService;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DozeService extends DreamService implements DozeMachine.Service {
    private static final String TAG = "DozeService";
    static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    private DozeMachine mDozeMachine;

    public DozeService() {
        setDebug(DEBUG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setWindowless(true);

        if (DozeFactory.getHost(this) == null) {
            finish();
            return;
        }

	mDozeMachine = DozeFactory.getInstance(getApplication()).assembleMachine(this);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        mDozeMachine.requestState(DozeMachine.State.INITIALIZED);
	startDozing();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        mDozeMachine.requestState(DozeMachine.State.FINISH);
    }

    @Override
    protected void dumpOnHandler(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (mDozeMachine != null) {
            mDozeMachine.dump(pw);
        }
    }

    @Override
    public void requestWakeUp() {
        PowerManager pm = getSystemService(PowerManager.class);
        pm.wakeUp(SystemClock.uptimeMillis(), "com.android.systemui:NODOZE");
    }
}
