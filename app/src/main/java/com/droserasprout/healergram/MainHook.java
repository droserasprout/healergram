package com.droserasprout.healergram;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {
    public final static List<String> hookPackages = Arrays.asList(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.telegram.messenger.beta",
            "nekox.messenger",
            "com.cool2645.nekolite",
            "org.telegram.plus",
            "com.iMe.android",
            "org.telegram.BifToGram",
            "ua.itaysonlab.messenger",
            "org.forkclient.messenger",
            "org.forkclient.messenger.beta",
            "org.aka.messenger",
            "ellipi.messenger",
            "org.nift4.catox",
            "it.owlgram.android");

    void log(String msg) {
        XposedBridge.log("healergram: " + msg);
    }

    private final XC_MethodHook onGetDialogsHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            XposedHelpers.callMethod(param.thisObject, "removeFolder", 1);
        }
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (hookPackages.contains(lpparam.packageName)) {
            this.log("patching " + lpparam.packageName);
            try {
                Class<?> messagesControllerClass = XposedHelpers.findClassIfExists(
                        "org.telegram.messenger.MessagesController",
                        lpparam.classLoader
                );
                if (messagesControllerClass != null) {
                    this.log("patching MessagesController");
                    XposedBridge.hookAllMethods(
                            messagesControllerClass,
                            "getDialogs",
                            onGetDialogsHook
                    );
                }
            } catch (Throwable err) {
                this.log(err.toString());
            }
        }
    }
}
