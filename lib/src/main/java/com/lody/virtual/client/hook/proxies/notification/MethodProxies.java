package com.lody.virtual.client.hook.proxies.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Build;
import android.widget.Toast;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.VLog;

import java.lang.reflect.Method;

import sk.vpkg.notification.SKVPackageNotificationHook;

/**
 * @author Lody
 */

@SuppressWarnings("unused")
class MethodProxies {
    private static final String TAG = "NotificationManagerStub";
    static class EnqueueNotification extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotification";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            SKVPackageNotificationHook hHook = new SKVPackageNotificationHook();
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int id = (int) args[idIndex];
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            Notification notification = (Notification) args[notificationIndex];
            try
            {
                if (!hHook.ProcessNotificationWithoutTag(pkg, idIndex, notification))
                    VLog.e(TAG,"enqueueNotification Error");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

            return 0;
            /*
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            id = VNotificationManager.get().dealNotificationId(id, pkg, null, getAppUserId());
            args[idIndex] = id;
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, null, pkg, getAppUserId());
            args[0] = getHostPkg();
            return method.invoke(who, args);
            */
        }
    }

    /* package */ static class EnqueueNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            SKVPackageNotificationHook hHook = new SKVPackageNotificationHook();
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int id = (int) args[idIndex];
            @SuppressLint("ObsoleteSdkInt")
            int tagIndex = (Build.VERSION.SDK_INT >= 18 ? 2 : 1);
            String tag = (String) args[tagIndex];
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            Notification notification = (Notification) args[notificationIndex];
            try
            {
                if (!hHook.ProcessNotificationWithTag(pkg, tag, idIndex, notification))
                    VLog.e(TAG,"enqueueNotificationWithTag Error");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

            return 0;
            /*
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int id = (int) args[idIndex];

            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag = VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());
            args[idIndex] = id;
            args[tagIndex] = tag;
            //key(tag,id)
            Notification notification = (Notification) args[notificationIndex];
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, tag, pkg, getAppUserId());
            args[0] = getHostPkg();
            if (Build.VERSION.SDK_INT >= 18 && args[1] instanceof String) {
                args[1] = getHostPkg();
            }
            return method.invoke(who, args);
            */
        }
    }

    /* package */ static class EnqueueNotificationWithTagPriority extends EnqueueNotificationWithTag {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTagPriority";
        }
    }

    /* package */ static class CancelNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);

            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            String tag = (String) args[BuildCompat.isR()?2:1];
            int id = (int) args[BuildCompat.isR()?3:2];
            SKVPackageNotificationHook hHook = new SKVPackageNotificationHook();
            try
            {
                if (!hHook.ProcessCancelNotificationWithTag(pkg, tag, id))
                    VLog.e(TAG,"cancelNotificationWithTag Error");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

            return 0;
            /*
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag = VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());

            args[1] = tag;
            args[2] = id;
            return method.invoke(who, args);
            */
        }
    }

    /**
     * @author 陈磊.
     */
    /* package */ static class CancelAllNotifications extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelAllNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            SKVPackageNotificationHook hHook = new SKVPackageNotificationHook();
            try
            {
                if (!hHook.ProcessRemoveAll(pkg))
                    VLog.e(TAG,"cancelNotificationWithTag Error");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            if (VirtualCore.get().isAppInstalled(pkg)) {
                VNotificationManager.get().cancelAllNotification(pkg, getAppUserId());
                return 0;
            }
            return 0;
        }
    }

    static class AreNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "areNotificationsEnabledForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            // Toast.makeText(VirtualCore.get().getContext(),pkg+5,Toast.LENGTH_SHORT).show();
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            return VNotificationManager.get().areNotificationsEnabledForPackage(pkg, getAppUserId());
        }
    }

    static class SetNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationsEnabledForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            // Toast.makeText(VirtualCore.get().getContext(),pkg+6,Toast.LENGTH_SHORT).show();
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            int enableIndex = ArrayUtils.indexOfFirst(args, Boolean.class);
            boolean enable = (boolean) args[enableIndex];
            VNotificationManager.get().setNotificationsEnabledForPackage(pkg, enable, getAppUserId());
            return 0;
        }
    }

    static class  CreateNotificationChannelGroups extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannelGroups";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CreateNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CreateNotificationChannels extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannels";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class OnlyHasDefaultChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "onlyHasDefaultChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetActiveNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getActiveNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetHistoricalNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getHistoricalNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class CancelNotificationFromListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "cancelNotificationFromListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class SetInterruptionFilter extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setInterruptionFilter";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class IsNotificationPolicyAccessGranted extends MethodProxy {
        @Override
        public String getMethodName() {
            return "isNotificationPolicyAccessGranted";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class IsNotificationPolicyAccessGrantedForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "isNotificationPolicyAccessGrantedForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }





    static class GetAppActiveNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getAppActiveNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class SetNotificationPolicyAccessGranted extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationPolicyAccessGranted";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class RemoveAutomaticZenRules extends MethodProxy {
        @Override
        public String getMethodName() {
            return "removeAutomaticZenRules";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }



    static class SetNotificationPolicy extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationPolicy";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class NotifyConditions extends MethodProxy {
        @Override
        public String getMethodName() {
            return "notifyConditions";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class GetNotificationChannelGroupsFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelGroupsFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelsFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelsFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  UpdateNotificationChannelFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "updateNotificationChannelFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  GetNotificationChannelGroupsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelGroupsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannels extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannels";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            //args[0] = VirtualCore.get().getHostPkg();
            MethodParameterUtils.replaceLastUid(args);
            return method.invoke(who,args);
        }
    }


    static class  GetNotificationChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {

            MethodParameterUtils.replaceLastUid(args);
            int sequence = Build.VERSION.SDK_INT >= 29 ? 2 : 1;
            MethodParameterUtils.replaceSequenceAppPkg(args, sequence);
            return method.invoke(who, args);
            /*
            args[0] = VirtualCore.get().getHostPkg();
            try
            {
                return method.invoke(who, args);
            }catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
            */
        }
    }




    static class  GetNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  GetActiveNotificationsFromListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getActiveNotificationsFromListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }



    static class  GetNumNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNumNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  DeleteNotificationChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "deleteNotificationChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  DeleteNotificationChannelGroup extends MethodProxy {
        @Override
        public String getMethodName() {
            return "deleteNotificationChannelGroup";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  ClearData extends MethodProxy {
        @Override
        public String getMethodName() {
            return "clearData";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  EnqueueToast extends MethodProxy {
        @Override
        public String getMethodName() {
            return "enqueueToast";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CancelToast extends MethodProxy {
        @Override
        public String getMethodName() {
            return "cancelToast";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  SetShowBadge extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setShowBadge";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CanShowBadge extends MethodProxy {
        @Override
        public String getMethodName() {
            return "canShowBadge";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  AreNotificationsEnabled extends MethodProxy {
        @Override
        public String getMethodName() {
            return "areNotificationsEnabled";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetPackageImportance extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getPackageImportance";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  UpdateNotificationChannelForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "updateNotificationChannelForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  GetDeletedChannelCount extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getDeletedChannelCount";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }


    static class  GetNotificationChannelGroups extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelGroups";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }
}
