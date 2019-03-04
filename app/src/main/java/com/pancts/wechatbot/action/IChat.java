package com.pancts.wechatbot.action;

import android.content.Context;

public abstract class IChat implements IBase {
    protected Context mContext;
    protected ClassLoader mClassLoader;

    public IChat(Context mContext, ClassLoader mClassLoader) {
        this.mContext = mContext;
        this.mClassLoader = mClassLoader;
    }

    public abstract void hookWxChatUIMM();
    public abstract void autoRepeat();
}
