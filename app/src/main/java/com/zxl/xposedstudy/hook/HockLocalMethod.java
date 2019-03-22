package com.zxl.xposedstudy.hook;


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * hook 当前app的方法
 */
public class HockLocalMethod {
    //包名
    private static String packageName = "com.zxl.xposedstudy";
    private static String tag = "----HockLocalMethod----";
    private static HockLocalMethod instance;
    //是否启动hook
    private boolean isHook = false;

    public static HockLocalMethod getInstance() {
        if (instance == null) {
            synchronized (HockLocalMethod.packageName) {
                if (instance == null) {
                    instance = new HockLocalMethod();
                }
            }
        }
        return instance;
    }


    public void init(XC_LoadPackage.LoadPackageParam lpparam) {

        if (!isHook) {
            XposedBridge.log(tag + "已经hook该方法");
            return;
        }

        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {

            XposedBridge.log(tag + "lpparam.appInfo方法不成功");
            return;
        }
        final String processName = lpparam.processName;
        if (packageName.equalsIgnoreCase(lpparam.packageName) && packageName.equalsIgnoreCase(lpparam.processName)) {


            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    final Context context = (Context) param.args[0];
                    final ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                    if (packageName.equals(processName) && !isHook) {
                        try {
                            //启动
                            synchronized (this) {
                                if (isHook == false) {
                                    isHook = true;
                                    XposedBridge.log(tag + "com.zxl.xposedstudy！");

                                    //hock();
                                }
                            }
                        } catch (Exception e) {
                            XposedBridge.log(tag + e.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }

    /**
     *
     * @param className 类名
     * @param method 方法名
     */
    void hock(String className,String method){


    }






}
