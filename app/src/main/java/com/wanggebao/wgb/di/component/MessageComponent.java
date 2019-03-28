package com.wanggebao.wgb.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.wanggebao.wgb.di.module.MessageModule;
import com.wanggebao.wgb.mvp.contract.MessageContract;

import com.jess.arms.di.scope.FragmentScope;
import com.wanggebao.wgb.mvp.ui.fragment.MessageFragment;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/29/2019 04:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = MessageModule.class, dependencies = AppComponent.class)
public interface MessageComponent {
    void inject(MessageFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        MessageComponent.Builder view(MessageContract.View view);

        MessageComponent.Builder appComponent(AppComponent appComponent);

        MessageComponent build();
    }
}