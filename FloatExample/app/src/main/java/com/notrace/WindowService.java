package com.notrace;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by notrace on 2017/5/3.
 */

public class WindowService extends Service {
    private static final String TAG="WindowService";

    private Handler handler=new Handler();

    private Timer timer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timer==null)
        {
            timer=new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(),0,500);
        }
        return super.onStartCommand(intent, flags, startId);

    }

    class  RefreshTask extends TimerTask{

        @Override
        public void run() {
            //判断当前界面是桌面，且没有悬浮显示，则创建悬浮窗
            if (isHome() && !MyWindowManager.isWindowShowing()){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext());
                    }
                });
            }
            //当前界面不是桌面，且有悬浮窗口显示，则移除悬浮窗口
            else if (!isHome() && MyWindowManager.isWindowShowing()){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.removeSmallWindow(getApplicationContext());
                        MyWindowManager.removeBigWindow(getApplicationContext());
                    }
                }) ;
            }
            // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
            else if (isHome() && MyWindowManager.isWindowShowing()){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer=null;
    }


    /**
     * 判断当前界面是否桌面
     */
    private boolean isHome(){
        ActivityManager mactivityManager =(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mactivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes(){
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo){
            names.add(ri.activityInfo.packageName);
            System.out.println("packageName" + names);
            Log.d(TAG,"tag:"+names);
        }
        return names;
    }
}
