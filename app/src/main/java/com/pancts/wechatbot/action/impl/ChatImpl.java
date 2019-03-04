package com.pancts.wechatbot.action.impl;

import android.content.ContentValues;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pancts.wechatbot.action.IChat;
import com.pancts.wechatbot.api.tuling;
import com.pancts.wechatbot.bean.LuckyXml;
import com.pancts.wechatbot.log.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ChatImpl extends IChat {

    HashMap<String, JSONObject> payMap = new HashMap<>();

    public ChatImpl(Context mContext, ClassLoader mClassLoader) {
        super(mContext, mClassLoader);
        LogUtils.i("聊天初始化操作完成");
    }

    @Override
    public void hookWxChatUIMM() {

    }

    @Override
    public void autoRepeat() {
        Class<?> SqlClass = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mClassLoader);
        final Class<?> mmiClass = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", mClassLoader);
        final Class<?> avClass = XposedHelpers.findClass("com.tencent.mm.model.av", mClassLoader);



        final Class<?> apClass = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.ap", mClassLoader);
        final Class<?> kgClass = XposedHelpers.findClass("com.tencent.mm.kernel.g", mClassLoader);
        final Class<?> amClass = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.am", mClassLoader);

        XposedHelpers.findAndHookMethod(SqlClass, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                ContentValues content = (ContentValues) param.args[2];
                LogUtils.i("insert: " + content.toString());
                if (content.getAsString("type").equals("1")
                        && content.getAsString("isSend").equals("0")
                        && !content.getAsString("talker").contains("@chatroom")) {
                    final String talker = content.getAsString("talker");


                    tuling.getAsk(content.getAsString("content"), talker, new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            JSONObject body = JSON.parseObject(response.body());
                            JSONArray results = body.getJSONArray("results");
                            JSONObject results_0 = results.getJSONObject(0);
                            JSONObject values = results_0.getJSONObject("values");
                            String text = values.getString("text");

                            LogUtils.i(text);

                            Object io = XposedHelpers.newInstance(mmiClass, new Class[]{String.class, String.class, int.class, int.class, Object.class}, talker, text, 1, 0, null);
                            Object Pw = XposedHelpers.callStaticMethod(avClass, "Pw");
                            XposedHelpers.callMethod(Pw, "a", io, 0);
                        }
                    });
                } else if (content.getAsString("type").equals("1")
                        && content.getAsString("isSend").equals("0")
                        && content.getAsString("talker").contains("@chatroom")) {
                    final String talker = content.getAsString("talker");

                    String[] con = content.getAsString("content").split(":");
                    tuling.getAsk(con[1], talker, new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            JSONObject body = JSON.parseObject(response.body());
                            JSONArray results = body.getJSONArray("results");
                            JSONObject results_0 = results.getJSONObject(0);
                            JSONObject values = results_0.getJSONObject("values");
                            String text = values.getString("text");

                            LogUtils.i(text);

                            Object io = XposedHelpers.newInstance(mmiClass, new Class[]{String.class, String.class, int.class, int.class, Object.class}, talker, text, 1, 0, null);
                            Object Pw = XposedHelpers.callStaticMethod(avClass, "Pw");
                            XposedHelpers.callMethod(Pw, "a", io, 0);
                        }
                    });

                } else if (content.getAsString("type").equals("436207665") && content.getAsString("isSend").equals("0")) {
                    XmlToJson xmlToJson = new XmlToJson.Builder(content.getAsString("content")).build();
                    LogUtils.i(xmlToJson.toString());
                    JSONObject json = JSON.parseObject(xmlToJson.toString());
                    JSONObject wcpayinfo = json.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");
                    String paymsgid = wcpayinfo.getString("paymsgid");
                    payMap.put(paymsgid, wcpayinfo);
                    String nativeurl = wcpayinfo.getString("nativeurl");
                    Object ap = XposedHelpers.newInstance(apClass,
                            new Class[]{int.class, String.class, String.class, int.class, String.class},
                            1, paymsgid, nativeurl, 1, "v1.0");
                    Object qd = XposedHelpers.callStaticMethod(kgClass, "Qd");
                    Object Pw = XposedHelpers.callMethod(qd, "Pw");
                    Object success = XposedHelpers.callMethod(Pw, "a", ap, 0);
                    LogUtils.i("success: " + success);
                } else if (content.getAsString("type").equals("419430449") && content.getAsString("isSend").equals("0")){

                }
            }
        });



        final Class<?> mqClass = XposedHelpers.findClass("com.tencent.mm.model.q", mClassLoader);
        final Class<?> mwClass = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.w", mClassLoader);

        XposedHelpers.findAndHookMethod(apClass, "a", int.class, String.class, org.json.JSONObject.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                org.json.JSONObject json = (org.json.JSONObject) param.args[2];

                LogUtils.i("ap json: " + json.toString());

                JSONObject jsonObj = JSON.parseObject(json.toString());
                String timingIdentifier = jsonObj.getString("timingIdentifier");
                String sendUserName = jsonObj.getString("sendUserName");
                String sendId = jsonObj.getString("sendId");

                if (payMap.containsKey(sendId)) {
                    JSONObject wcpayinfo = (JSONObject) payMap.get(sendId);
                    Object wv = XposedHelpers.callStaticMethod(mqClass, "Wv");
                    Object bHy = XposedHelpers.callStaticMethod(mwClass, "bHy");
                    String nativeurl = wcpayinfo.getString("nativeurl");
                    Object am = XposedHelpers.newInstance(amClass,
                            new Class[]{int.class, int.class,
                                    String.class, String.class, String.class,
                                    String.class, String.class, String.class, String.class},
                            1, 1, sendId, nativeurl, bHy, wv, sendUserName, "v1.0", timingIdentifier);

                    Object qd = XposedHelpers.callStaticMethod(kgClass, "Qd");
                    Object Pw = XposedHelpers.callMethod(qd, "Pw");
                    Object success = XposedHelpers.callMethod(Pw, "a", am, 0);
                    LogUtils.i("success: " + success);
                }
            }
        });


        XposedHelpers.findAndHookConstructor(amClass,
                int.class,
                int.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class,
                new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                LogUtils.i("am string: " + Arrays.toString(param.args));
            }
        });
    }
}
