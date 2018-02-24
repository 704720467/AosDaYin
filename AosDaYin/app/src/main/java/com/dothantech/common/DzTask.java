package com.dothantech.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DzTask {
    public static final String EXTRA_NDEF_INTENT = "com.dothantech.manager.EXTRA_NDEF_INTENT";

    protected static List<ResolveInfo> getAllInstallAppResolveInfo(Context paramContext) {
        Intent localIntent = new Intent("android.intent.action.MAIN", null);
        localIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager localPackageManager = paramContext.getPackageManager();
        List localList = localPackageManager.queryIntentActivities(localIntent, 0);
        Collections.sort(localList, new ResolveInfo.DisplayNameComparator(localPackageManager));
        return localList;
    }

    protected static ComponentName getComponentNameByResolveInfo(ResolveInfo paramResolveInfo) {
        if (paramResolveInfo == null)
            return null;
        return new ComponentName(paramResolveInfo.activityInfo.packageName, paramResolveInfo.activityInfo.name);
    }


    protected static ResolveInfo getThisResolveInfo(Context paramContext) {
        List localList = getAllInstallAppResolveInfo(paramContext);
        String str = paramContext.getPackageName();
        Iterator localIterator = localList.iterator();
        ResolveInfo localResolveInfo;
        do {
            if (!localIterator.hasNext())
                return null;
            localResolveInfo = (ResolveInfo) localIterator.next();
        } while (!localResolveInfo.activityInfo.packageName.equals(str));
        return localResolveInfo;
    }

    public static boolean moveTaskToFront(Context paramContext, int paramInt) {
        if (DzSystem.getSystemVersion() < 11)
            try {
                Object localObject = DzClass.getMethod(Class.forName("ActivityManagerService"), "self", null).invoke(null, new Object[0]);
                Class localClass = localObject.getClass();
                Class[] arrayOfClass = new Class[1];
                arrayOfClass[0] = Integer.TYPE;
                Method localMethod = DzClass.getMethod(localClass, "moveTaskToFront", arrayOfClass);
                Object[] arrayOfObject = new Object[1];
                arrayOfObject[0] = Integer.valueOf(paramInt);
                localMethod.invoke(localObject, arrayOfObject);
                return true;
            } catch (Throwable localThrowable) {
                return false;
            }
        ((ActivityManager) paramContext.getSystemService(Activity.ACTIVITY_SERVICE)).moveTaskToFront(paramInt, 0);
        return true;
    }

    public static boolean startActivityWithNFCIntent(Context paramContext, Intent paramIntent) {
        ResolveInfo localResolveInfo = getThisResolveInfo(paramContext);
        ComponentName localComponentName = null;
        if (localResolveInfo != null)
            localComponentName = getComponentNameByResolveInfo(localResolveInfo);
        if ((paramIntent != null) && (localComponentName != null)) {
            Intent localIntent = new Intent();
            localIntent.putExtra("com.dothantech.manager.EXTRA_NDEF_INTENT", paramIntent);
            localIntent.setComponent(localComponentName);
            localIntent.setFlags(268435456);
            paramContext.startActivity(localIntent);
            return true;
        }
        return false;
    }
}

/* Location:           C:\Users\admin\classes_dex2jar.jar
 * Qualified Name:     com.dothantech.common.DzTask
 * JD-Core Version:    0.6.2
 */