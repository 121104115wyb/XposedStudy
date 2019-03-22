package com.zxl.xposedstudy.hook;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hock支付宝
 * 支付宝启动会执行以下方法
 */
public class HockAlipay {

    private static String TAG = "---HockAlipay---";
    private static String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
    public static boolean ALIPAY_PACKAGE_ISHOOK = false;
    private boolean loaded = false;
    static HockAlipay instance;

    public static HockAlipay getInstance() {
        if (null == instance) {
            synchronized (HockAlipay.class) {
                if (null == instance) {
                    instance = new HockAlipay();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化
     *
     * @param lpparam
     */
    public void init(XC_LoadPackage.LoadPackageParam lpparam) {

        if (this.loaded) {
            XposedBridge.log("已经加载handleLoadPackage过了,无需再次加载");
            return;
        }
        this.loaded = true;
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        //包名和进程名称
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;

        if (ALIPAY_PACKAGE.equals(packageName) && ALIPAY_PACKAGE.equals(processName)) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    final Context context = (Context) param.args[0];
                    final ClassLoader cl = ((Context) param.args[0]).getClassLoader();


                    if (ALIPAY_PACKAGE.equals(processName) && !ALIPAY_PACKAGE_ISHOOK) {
                        try {
                            //登录
                            synchronized (this) {
                                if (ALIPAY_PACKAGE_ISHOOK == false) {
                                    ALIPAY_PACKAGE_ISHOOK = true;
                                    XposedBridge.log(TAG + "支付宝启动成功！");
                                }
                            }
                        } catch (Exception e) {
                            XposedBridge.log(TAG + e.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }


    /**
     * 判断是否已经被劫持了
     *
     * @return
     */
    public Boolean getHooked() {
        return ALIPAY_PACKAGE_ISHOOK;
    }

    /**
     * 是否已经加载过了
     *
     * @return
     */
    public Boolean getLoaded() {
        return loaded;
    }

}
