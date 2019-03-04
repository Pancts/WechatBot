package com.pancts.wechatbot;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;

import com.lzy.okgo.OkGo;
import com.pancts.wechatbot.action.impl.ChatImpl;
import com.pancts.wechatbot.log.LogUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WxHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.contains("com.tencent.mm")) {
            LogUtils.getConfig().setGlobalTag("MM_TAG");

            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context context = (Context) param.args[0];
                    LogUtils.init(context);
                    LogUtils.i("初始化操作完成", param.thisObject);



                    new ChatImpl(context, lpparam.classLoader).autoRepeat();
                }
            });
        }
    }
}
