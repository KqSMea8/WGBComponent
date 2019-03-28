package com.wanggebao.wgb.app;

import android.content.Intent;

import com.jess.arms.base.BaseApplication;
import com.wanggebao.wgb.commonsdk.location.LocationService;
import com.wanggebao.wgb.commonsdk.location.LocationStatusManager;

public class WGBComponentApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //启动定位
        startService(new Intent(this, LocationService.class));
        LocationStatusManager.getInstance().resetToInit(this);
    }
}
