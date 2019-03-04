package com.pancts.wechatbot.api;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class tuling {
    public static void getAsk(String quest, String key, StringCallback sc){

        String user = "";
        if (key.contains("_")) {
            user = key.split("_")[1];
        } else if (key.contains("@")){
            user = key.split("@")[0];
        }


        JSONObject json = null;
        try {
            json = new JSONObject("{\"reqType\":0,\"perception\":{\"inputText\":{\"text\":\"" + quest + "\"}},\"userInfo\":{\"apiKey\":\"85fba6919f0448cf92f2b01335c7dabe\",\"userId\":\"" + user + "\"}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert json != null;
        OkGo.<String>post("http://openapi.tuling123.com/openapi/api/v2")
                .upJson(json)
                .execute(sc);
    }
}
