/*
 * Copyright 2018 JessYan
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
 * limitations under the License.
 */
package com.wanggebao.wgb.mvp.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.telecom.Connection;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.wanggebao.wgb.R;
import com.wanggebao.wgb.commonres.tab.FragmentPagerItemAdapter;
import com.wanggebao.wgb.commonres.tab.TabLayout;
import com.wanggebao.wgb.commonsdk.core.RouterHub;
import com.wanggebao.wgb.commonsdk.location.LocationService;
import com.wanggebao.wgb.commonsdk.location.LocationStatusManager;
import com.wanggebao.wgb.commonsdk.utils.Utils;
import com.wanggebao.wgb.commonservice.gank.service.GankInfoService;
import com.wanggebao.wgb.commonservice.gold.service.GoldInfoService;
import com.wanggebao.wgb.commonservice.zhihu.service.ZhihuInfoService;
import com.wanggebao.wgb.mvp.ui.fragment.ContactFragment;
import com.wanggebao.wgb.mvp.ui.fragment.HomeFragment;
import com.wanggebao.wgb.mvp.ui.fragment.MessageFragment;
import com.wanggebao.wgb.mvp.ui.fragment.MineFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 宿主 App 的主页
 *
 * @see <a href="https://github.com/JessYanCoding/ArmsComponent/wiki">ArmsComponent wiki 官方文档</a>
 * Created by JessYan on 19/04/2018 16:10
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@Route(path = RouterHub.APP_MAINACTIVITY)
public class MainActivity extends BaseActivity {
    @BindView(R.id.bt_zhihu)
    Button mZhihuButton;
    @BindView(R.id.bt_gank)
    Button mGankButton;
    @BindView(R.id.bt_gold)
    Button mGoldButton;

    @Autowired(name = RouterHub.ZHIHU_SERVICE_ZHIHUINFOSERVICE)
    ZhihuInfoService mZhihuInfoService;
    @Autowired(name = RouterHub.GANK_SERVICE_GANKINFOSERVICE)
    GankInfoService mGankInfoService;
    @Autowired(name = RouterHub.GOLD_SERVICE_GOLDINFOSERVICE)
    GoldInfoService mGoldInfoService;

    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.tab_main)
    TabLayout tabMain;

    private long mPressedTime;

    public static final String RECEIVER_ACTION = "location_in_background";

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION);
        registerReceiver(locationChangeBroadcastReceiver, intentFilter);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);
        vpMain.setAdapter(new FragmentPagerItemAdapter.Builder(this)
                .add(R.drawable.home,"主页", HomeFragment.class)
                .add(R.drawable.message,"消息", MessageFragment.class)
                .add(R.drawable.contact,"通讯录", ContactFragment.class)
                .add(R.drawable.mine,"我的", MineFragment.class)
                .build());
        tabMain.setViewPager(vpMain);
        //这里想展示组件向外提供服务的功能, 模拟下组件向宿主提供一些必要的信息, 这里为了简单就直接返回本地数据不请求网络了
        loadZhihuInfo();
        loadGankInfo();
        loadGoldInfo();
        startLocationService();
    }

    private void loadZhihuInfo() {
        //当非集成调试阶段, 宿主 App 由于没有依赖其他组件, 所以使用不了对应组件提供的服务
        if (mZhihuInfoService == null) {
            mZhihuButton.setEnabled(false);
            return;
        }
        mZhihuButton.setText(mZhihuInfoService.getInfo().getName());
    }

    private void loadGankInfo() {
        //当非集成调试阶段, 宿主 App 由于没有依赖其他组件, 所以使用不了对应组件提供的服务
        if (mGankInfoService == null) {
            mGankButton.setEnabled(false);
            return;
        }
        mGankButton.setText(mGankInfoService.getInfo().getName());
    }

    private void loadGoldInfo() {
        //当非集成调试阶段, 宿主 App 由于没有依赖其他组件, 所以使用不了对应组件提供的服务
        if (mGoldInfoService == null) {
            mGoldButton.setEnabled(false);
            return;
        }
        mGoldButton.setText(mGoldInfoService.getInfo().getName());
    }

    @Override
    public void onBackPressed() {
        //获取第一次按键时间
        long mNowTime = System.currentTimeMillis();
        //比较两次按键时间差
        if ((mNowTime - mPressedTime) > 2000) {
            ArmsUtils.makeText(getApplicationContext(),
                    "再按一次退出" + ArmsUtils.getString(getApplicationContext(), R.string.public_app_name));
            mPressedTime = mNowTime;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 这里注意下在组件的页面中(使用了 R2 的页面)使用 {@link OnClick} 会有概率出现 id 不正确的问题, 使用以下方式解决
     * <pre>
     * @OnClick({R2.id.button1, R2.id.button2})
     * public void Onclick(View view){
     *      if (view.getId() == R.id.button1){
     *          ...
     *      } else if(view.getId() == R.id.button2){
     *          ...
     *      }
     * }
     * </pre>
     * <p>
     * 在注解上使用 R2, 下面使用 R, 并且使用 {@code if else}, 替代 {@code switch}
     *
     * @param view
     */
    @OnClick({R.id.bt_zhihu, R.id.bt_gank, R.id.bt_gold})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_zhihu:
                Utils.navigation(MainActivity.this, RouterHub.ZHIHU_HOMEACTIVITY);
                break;
            case R.id.bt_gank:
                Utils.navigation(MainActivity.this, RouterHub.GANK_HOMEACTIVITY);
                break;
            case R.id.bt_gold:
                Utils.navigation(MainActivity.this, RouterHub.GOLD_HOMEACTIVITY);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (locationChangeBroadcastReceiver != null)
            unregisterReceiver(locationChangeBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * 开始定位服务
     */
    private void startLocationService(){
        getApplicationContext().startService(new Intent(this, LocationService.class));
        LocationStatusManager.getInstance().resetToInit(getApplicationContext());
    }

    /**
     * 关闭服务
     * 先关闭守护进程，再关闭定位服务
     */
    private void stopLocationService(){
        sendBroadcast(com.wanggebao.wgb.commonsdk.location.Utils.getCloseBrodecastIntent());
        LocationStatusManager.getInstance().resetToInit(getApplicationContext());
    }

    private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECEIVER_ACTION)) {
                String locationResult = intent.getStringExtra("result");
                if (null != locationResult && !locationResult.trim().equals("")) {
                    Log.e(TAG,locationResult);
                }
            }
        }
    };

}
