package com.zxl.xposedstudy.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * 拦截应用方法
 * 接口IXposedHookLoadPackage（一个新的app被加载时）
 * 接口IXposedHookZygoteInit （安卓系统启动时）
 * 接口IXposedHookInitPackageResources （资源被初始化时）
 */
public class StudyHookMain
        implements IXposedHookLoadPackage {


    private static String Tag = "----StudyHookMain----";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;
        XposedBridge.log("---init---packageName" + packageName);
        XposedBridge.log("---init---processName" + processName);

        HockAlipay.getInstance().init(lpparam);
        XposedBridge.log(Tag + HockAlipay.getInstance().getHooked());


        if (packageName.equalsIgnoreCase("com.zxl.hookby")) {
            XposedBridge.log("---init---com.zxl.hookby:----" + packageName);


            Class Main2Activity = XposedHelpers.findClass("com.zxl.hookby.Main2Activity", lpparam.classLoader);
            Class Main3Activity = XposedHelpers.findClass("com.zxl.hookby.Main3Activity", lpparam.classLoader);
            XposedBridge.log("---Main2Activity:------" + Main2Activity.getClass().getSimpleName());
            XposedBridge.log("---Main3Activity:------" + Main3Activity.getSimpleName());


            XposedHelpers.findAndHookMethod("com.zxl.hookby.MainActivity",
                    lpparam.classLoader, "setData", String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String s = param.args[0].toString();
                            XposedBridge.log(Tag + "--------beforeHookedMethodsetData----" + s);
                            param.args[0] = "aaaaaaaaaaaa";
                            param.setResult(param.args[0]);

                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String result = param.args[0].toString();
                            //param.setResult("aaaaaaaaaaaa");
                            XposedBridge.log(Tag + "-------result----" + result);
                            XposedBridge.log(Tag + "-------afterHookedMethodsetData----" + param.getResult());
                        }
                    });

            /**
             * 劫持同一个activity中的button的onclick方法，虽然对button的点击事件进行劫持并且重定义，但是由于劫持的是按钮的点击事件，
             * 所以自己的定义的按钮的点击事件只会在系统原来的点击事件执行之后进行执行
             */
            XposedHelpers.findAndHookMethod("com.zxl.hookby.MainActivity",
                    lpparam.classLoader, "onClick", View.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                            Class myclass = param.thisObject.getClass();
                            Field field1 = myclass.getField("testButton");
                            XposedBridge.log(Tag + "---btnTextfield---" + field1);
                            Button button = (Button) field1.get(param.thisObject);
                            String btntext = button.getText().toString();
                            XposedBridge.log(Tag + "---btnText---" + btntext);
                            button.setText("你的button被我劫持了");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Activity startActivity = (Activity) param.thisObject;
                                    Intent intent = new Intent();
                                    //参数一：目标activity的包名;第二个参数：目标activity的全称，包名+类名
                                    intent.setClassName(startActivity, "com.zxl.hookby.Main2Activity");
                                    startActivity.startActivity(intent);

                                    XposedBridge.log(Tag + "---button---" + "我被点击了");
                                }
                            });
                            XposedBridge.log(Tag + "onClick--beforeHookedMethod参数----");
                        }

                        @Override
                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                            XposedBridge.log(Tag + "onClick--afterHookedMethod参数----" + param);
                            Class myclass = param.thisObject.getClass();
                            XposedBridge.log(Tag + "onClick--afterHookedMethod参数myclassName----" + myclass);
                            //通过控件所在的类和声明控件的对象映射到Field，该方法不能hook声明为privite的对象
                            //Field field = myclass.getField("edit");
                            //该方式能找到所有修饰符修饰的对象，其中myclass.getDeclaredField("editText") 中的"editText"是控件声明的对象，不是控件的id
                            Field field = myclass.getDeclaredField("editText");
                            //设置权限
                            field.setAccessible(true);
                            //通过field找到对象
                            EditText editText = (EditText) field.get(param.thisObject);
                            String string = editText.getText().toString();
                            //打印截取出来的数据
                            XposedBridge.log(Tag + "----onClickString---" + string);
                            //设置新密码
                            editText.setText("小哥你被我拦截了");
                        }
                    });


            XposedHelpers.findAndHookMethod("com.zxl.hookby.MainActivity",
                    lpparam.classLoader,
                    "onCreate",
                    Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(Tag + "---onCreatebtn---" + param.args[0]);

                        }

                        @Override
                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                            XposedBridge.log(Tag + "----btnTextafterHookedMethod---" + param.args[0]);
                            final Class hookClass = param.thisObject.getClass();
                            XposedBridge.log(Tag + "---btnTexthookClass---" + hookClass);
                            //hookClass.getField("")这里面需要填入的是控件的声明对象，并且控件的修饰符不能是private
                            Field field = hookClass.getField("testButton");
                            XposedBridge.log(Tag + "---btnTextfield---" + field);
                            Button button = (Button) field.get(param.thisObject);
                            String btntext = button.getText().toString();
                            XposedBridge.log(Tag + "---btnText---" + btntext);
                            button.setText("你的button被我劫持了");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Activity startActivity = (Activity) param.thisObject;
                                    Intent intent = new Intent();
                                    //参数一：目标activity的包名;第二个参数：目标activity的全称，包名+类名
                                    intent.setClassName(startActivity, "com.zxl.hookby.Main2Activity");
                                    startActivity.startActivity(intent);
                                    XposedBridge.log(Tag + "---button---" + "我被点击了");
                                }
                            });
                        }
                    });


            //劫持一个应用的Application
            Class<?> ContextClass = XposedHelpers.findClass("android.content.ContextWrapper", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context applicationContext = (Context) param.getResult();
                    XposedBridge.log("得到上下文------"+applicationContext.getPackageName());
                }
            });






        }


    }


    public static void hook(ClassLoader cl, Context context,boolean red,boolean personal,boolean aa) throws ClassNotFoundException {
        Class ChatDataSyncCallback = cl.loadClass("com.alipay.mobile.socialchatsdk.chat.data.ChatDataSyncCallback");
        findMethod(ChatDataSyncCallback,context,red,personal,aa);
    }

    private static void findMethod(Class hookclass, Context c,boolean red,boolean personal,boolean aa) {

        if (!hookclass.isInterface()) {
            for (Method method : hookclass.getDeclaredMethods()) {

                if (!Modifier.isAbstract(method.getModifiers())) {
                    //context = c;
                    if(method.getName().equals("onReceiveMessage")) {
                        //findParamFormMethod(method, hookclass.getName(), hookclass, context,red,personal,aa);
                    }

                }
            }
        }

    }


}
