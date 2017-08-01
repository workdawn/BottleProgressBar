package com.views.bottleprogressbar.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * 设备相关信息获取类
 * Created by Administrator on 2016/9/9.
 */
public class DeviceInformation {
    private static Display display;
    private static DisplayMetrics metrics;
    private static Point point;

    /**
     * 获取设备屏幕尺寸
     * @param activity Activity实例
     * @return 屏幕尺寸。 返回的屏幕尺寸在不同型号、不同品牌的设备上可能会有些许差别这和android设备制造规范不统一有关
     */
    public static double getScreenSize(Activity activity){
        display = activity.getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        DecimalFormat format = new DecimalFormat("#0.0");
        return Double.valueOf(format.format(Math.sqrt(Math.pow(metrics.widthPixels, 2)+Math.pow(metrics.heightPixels, 2))/(160*metrics.density)));
    }

    /**
     * 获取设备屏幕分辨率
     * 说明：要正确的获取到屏幕分辨率需要在清单文件中加上
     * <supports-screens
     android:smallScreens="true"
     android:normalScreens="true"
     android:largeScreens="true"
     android:resizeable="true"
     android:anyDensity="true"/>
     * @param activity Activity实例
     * @return 返回包含屏幕分辨率的数组，第0个是宽度，第一个是高度
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int[] getScreenResolution(Activity activity){
        display = activity.getWindowManager().getDefaultDisplay();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            point = new Point();
            display.getSize(point);
            return new int[]{point.x, point.y};
        }else{
            return new int[]{display.getWidth(), display.getHeight()};
        }
    }

    /**
     * 获取设备屏幕密度
     * @param activity Activity实例
     * @return 屏幕密度
     */
    public static float getScreenDensity(Activity activity){
        display = activity.getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.density;
    }

    /**
     * 获取屏幕密度DPI
     * @param activity Activity实例
     * @return 屏幕密度DPI
     */
    public static int getScreenDensityDPI(Activity activity){
        display = activity.getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.densityDpi;
    }

    /**
     * 获取设备CPU信息，CPU名字和频率
     * @return 返回设备cpu信息; 1-频率，0-cpu型号
     */
    public static String[] getDeviceCPUInfo(){
        //cpu信息存放文件
        String str = "/proc/cpuinfo";
        String line="";
        String[] cpuInfo = {"",""};
        try {
            FileReader fr = new FileReader(str);
            BufferedReader reader = new BufferedReader(fr);
            line = reader.readLine();
            String[] array = line.split(":\\s+" , 2);
            for(int i=1; i<array.length; i++){
                cpuInfo[0] = cpuInfo[0] + array[i] +" ";
            }
            line = reader.readLine();
            array = line.split(":\\s+" ,2);
            cpuInfo[1] = array[1];
            reader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }

    /**
     * 获取设备cpu最小或最大频率
     * @param type 获取的频率类型；0-最小频率、1-最大频率，单位(KHZ)
     * @return cpu最小频率
     */
    public static String getMinOrMaxCPUFreq(int type) {
        // cup最小/最大频率信息存放文件
        String str = "";
        if(type == 0){
            str = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
        }else{
            str = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        }

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(str);
            br = new BufferedReader(fr);
            return br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 获取设备系统版本号
     * @return 当前设备系统版本号
     */
    public static int getSysVersion(){

        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备RAM大小
     * @return RAM大小；单位MB
     */
    public static String getDeviceRAMSize(){

        String str = "/proc/meminfo";
        String [] array = null;
        try {
            FileReader fr = new FileReader(str);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();//MemTotal:396808 kB
            array = line.split(":\\s+");
            reader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formatSize(Long.valueOf(Integer.valueOf(array[1].substring(0, array[1].indexOf("k")-1).trim())*1024));
    }

    /**
     * 获取设备ROM大小
     * @return 返回当前设备ROM大小；结果包含单位(KB,MB,GB)
     */
    @SuppressWarnings("deprecation")
    public static String getDeviceROMSize(){

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();

        return formatSize(blockCount*blockSize);
    }

    /**
     * 获取设备可用ROM大小
     * @return 返回当前设备可用ROM大小；结果包含单位(KB,MB,GB)
     */
    @SuppressWarnings("deprecation")
    public static String getDeviceAvailROMSize(){

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availBlockSize = stat.getAvailableBlocks();

        return formatSize(blockSize*availBlockSize);
    }

    /**
     * 获取设备可用RAM大小
     * @param context 上下文对象
     * @return 返回设备当前可用内存；结果包含单位(KB,MB,GB)
     */
    public static String getDeviceAvailMemory(Context context){

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);

        return formatSize(mi.availMem);
    }

    /**
     * 获取设备SD卡容量
     * @return 如果SD卡存在，则返回SD卡容量;，否则返回0；结果包含单位(KB,MB,GB)
     */
    @SuppressWarnings("deprecation")
    public static String getSDCardSize(){

        if(isExternalStorageAvailable()){
            File path = Environment.getExternalStorageDirectory();
            if(path.exists()){
                StatFs statFs = new StatFs(path.getPath());
                long blockCount = statFs.getBlockCount();
                long blockSize = statFs.getBlockSize();

                return formatSize(blockSize*blockCount);
            }else{
                return "0";
            }
        }else{
            return "0";
        }

    }

    /**
     * 获取设备SD卡剩余存储空间
     * @return 如果SD卡存在则返回SD卡剩余容量，，否则返回0；结果包含单位(KB,MB,GB)
     */
    @SuppressWarnings("deprecation")
    public static String getSDCardAvailSize(){

        if(isExternalStorageAvailable()){
            File path = Environment.getExternalStorageDirectory();
            if(path.exists()){
                StatFs statFs = new StatFs(path.getPath());
                long blockSize = statFs.getBlockSize();
                long availBlockSize = statFs.getAvailableBlocks();

                return formatSize(blockSize*availBlockSize);
            }else{
                return "0";
            }
        }else{
            return "0";
        }

    }

    /**
     * 判断SD卡是否可用，是否有可读写权限
     * @return 可用的话则返回true，否则返回false
     */
    public static boolean isExternalStorageAvailable(){

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取唯一设备ID
     * @param context 上下文对象
     * @return 返回GSM设备IMEI号或者CDMA手机的MEID号
     */
    public static String getDeviceIMEINumber(Context context){

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getDeviceId();
    }

    /**
     * 获取手机型号
     * @return
     */
    public static String getDeviceMobileType(){

        return Build.MODEL;
    }

    /**
     * 获取设备制造商
     * @return 返回设备制造商名称
     */
    public static String getDeviceManufacturers(){

        return Build.MANUFACTURER;
    }

    /**
     * 获取手机电话号码
     * @param context 上下文对象
     * @return 返回手机号码；因为很多SIM卡并没有把手机号集成进去，所以该方法只能获取到部分SIM卡的手机号
     */
    public static String getPhoneNumber(Context context){

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getLine1Number();
    }

    /**
     * 获取设备MAC地址
     * @param context 上下文对象
     * @return 返回设备MAC地址
     */
    public static String getDeviceMACAddress(Context context){

        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        return info.getMacAddress();
    }

    /**
     * 获取设备SIM卡相关信息
     * @param context 上下文对象
     * @return 返回包含SIM卡相关信息的json串。
     * simOperator:运营商名称，46000/46002表示中国移动、46001中国联通、46003中国电信
     * netWorkType:移动网络类型，
     * phoneType:手机制式
     * dataState:数据连接状态
     * simState:SIM卡状态
     * subScriberId:国际用户识别码
     * networkCountryIso:ISO标志国家码，即区号
     */
    public static String getSIMCardInfo(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        /**
         * 获取运营商名称
         * 46000/46002表示中国移动、46001中国联通、46003中国电信
         */
        String simOperator = tm.getSimOperator();
        /**
         * 获取移动网络类型
         * int NETWORK_TYPE_CDMA 网络类型为CDMA
         * int NETWORK_TYPE_EDGE 网络类型为EDGE
         * int NETWORK_TYPE_EVDO_0 网络类型为EVDO0
         * int NETWORK_TYPE_EVDO_A 网络类型为EVDOA
         * int NETWORK_TYPE_GPRS 网络类型为GPRS
         * int NETWORK_TYPE_HSDPA 网络类型为HSDPA
         * int NETWORK_TYPE_HSPA 网络类型为HSPA
         * int NETWORK_TYPE_HSUPA 网络类型为HSUPA
         * int NETWORK_TYPE_UMTS 网络类型为UMTS
         * 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
         */
        int netWorkType = tm.getNetworkType();
        /**
         * 获取手机制式
         * int PHONE_TYPE_CDMA 手机制式为CDMA，电信
         * int PHONE_TYPE_GSM 手机制式为GSM，移动和联通
         * int PHONE_TYPE_NONE 手机制式未知
         */
        int phoneType = tm.getPhoneType();
        /**
         * 获取数据连接状态
         * int DATA_CONNECTED 数据连接状态：已连接
         * int DATA_CONNECTING 数据连接状态：正在连接
         * int DATA_DISCONNECTED 数据连接状态：断开
         * int DATA_SUSPENDED 数据连接状态：暂停
         */
        int dataState = tm.getDataState();
        /**
         * 获取SIM卡状态
         * int SIM_STATE_ABSENT SIM卡未找到
         * int SIM_STATE_NETWORK_LOCKED SIM卡网络被锁定，需要Network PIN解锁
         * int SIM_STATE_PIN_REQUIRED SIM卡PIN被锁定，需要User PIN解锁
         * int SIM_STATE_PUK_REQUIRED SIM卡PUK被锁定，需要User PUK解锁
         * int SIM_STATE_READY SIM卡可用 int SIM_STATE_UNKNOWN SIM卡未知
         */
        int simState = tm.getSimState();
        /**
         * 获取国际用户识别码
         */
        String subScriberId = tm.getSubscriberId();
        /**
         * 获取ISO标准国家码即区号
         */
        String networkCountryIso = tm.getNetworkCountryIso();

        return "[{\"simOperator\":\""+simOperator+"\",\"netWorkType\":\""+netWorkType+"\",\"phoneType\":\""+phoneType+"\",\"dataState\":\""+dataState+"\",\"simState\":\""+simState+"\",\"subScriberId\":\""+subScriberId+"\",\"networkCountryIso\":\""+networkCountryIso+"\"}]";
    }

    /**
     * 获取手机运营商类型
     * @param context
     * @return 运营商 中国移动，中国联通，中国电信
     */
    public static String getOperators(Context context) {
        String backVal = "";
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        /**
         * 获取运营商名称 46000/46002表示中国移动、46001中国联通、46003中国电信
         */
        String simOperator = tm.getSimOperator();
        try {
            if ("46000".equals(simOperator) || "46002".equals(simOperator)) {
                // 中国移动
                backVal = "中国移动";
            } else if ("46001".equals(simOperator)) {
                // 中国联通
                backVal = "中国联通";
            } else if ("46003".equals(simOperator)) {
                // 中国电信
                backVal = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backVal;
    }

    /**
     * 得到当前的手机网络类型
     *
     * @param context
     * @return 网络类型
     */
    public static String getCurrentNetType(Context context) {
        String type = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                    || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }
        }
        return type;
    }

    /**
     * 获取设备电池信息
     * @param context 上下文对象
     * @return 返回电池的相关信息 以json串的形式。
     * json串字段说明=status：电池当前状态；health：电池健康状况；level：当前电量；scale：最大电量；plugged：连接的电源插座；temprature：温度，单位10℃ ；technology：电池类型
     */
    public static String getDeviceBatteryInfo(Context context){
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryInfoIntent.getIntExtra("status", 0 );//当前状态
        int health = batteryInfoIntent.getIntExtra("health", 1 );//健康状况
        boolean present = batteryInfoIntent.getBooleanExtra("present", false);
        int level = batteryInfoIntent.getIntExtra("level", 0);//当前电量
        int scale = batteryInfoIntent.getIntExtra("scale", 0);//电量最大值
        int plugged = batteryInfoIntent.getIntExtra("plugged", 0);//连接的电源插座
        int voltage = batteryInfoIntent.getIntExtra("voltage", 0);
        int temperature = batteryInfoIntent.getIntExtra("temperature", 0); //温度的单位是10℃
        String technology = batteryInfoIntent.getStringExtra("technology");//电池类型
        return "{\"status\":\""+status+"\",\"health\":\""+health+"\",\"present\":\""+present+"\",\"level\":\""+level+"\",\"scale\":\""+scale+"\",\"plugged\":\""+plugged+"\",\"voltage\":\""+voltage+"\",\"temperature\":\""+temperature+"\",\"technology\":\""+technology+"\"}";
    }

    /**
     * 获取当前设备网络状况
     * @return
     */
    public static double getDeviceInternetStatus(){
        TrafficStats.getTotalRxBytes();//手机总的接收字节数
        TrafficStats.getTotalTxBytes();//手机总的发送字节数
        return 0.0;
    }
    private static long getInternetInformation(){
        String line;
        String [] segs;
        boolean isNum;;
        long received = 0;
        long tmp = 0;
        BufferedReader br=null;
        try {
            FileReader fr=new FileReader("/proc/net/dev");
            br=new BufferedReader(fr, 500);
            while ((line=br.readLine())!=null) {
                line=line.trim();
                if (line.startsWith("rmnet")||line.startsWith("eth")||line.startsWith("wlan")) {
                    line=line.trim();
                    line=line.replaceAll(" +",",");
//						segs=line.split(":")[1].split(",");
                    String information=line.split(":")[1];
                    information=information.substring(1, information.length());
                    segs=information.split(",");
                    for(int i=0;i<segs.length;i++){
                        isNum=true;
                        try {
                            tmp=Long.parseLong(segs[i]);
                        } catch (NumberFormatException e) {
                            isNum=false;
                            e.printStackTrace();
                        }
                        if (isNum) {
                            received=received+tmp;
                        }

                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }finally{
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return received;
    }
    /**
     * 统计手机实时流量，实际上统计的是1秒之间消耗的流量
     * @return 每秒消耗的流量
     */
    public static String getRealTimeFlow(){
        try {
            long flow1=getInternetInformation();
            Thread.sleep(1000);
            long flow2=getInternetInformation();
            long flow=flow2-flow1;
            return formatSize(flow);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "-1";
        }
    }
    /**
     * 格式化数值
     * @param size 未格式化的数值
     * @return 返回经过格式化后的数值，带单位
     */
    public static String formatSize(long size) {

        String suffix = null;
        float fSize=0;

        if (size >= 1024) {
            suffix = "KB";
            fSize=size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            suffix = "B";
            fSize = size;
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
    /**
     * 检查网络连接状态
     * @param activity
     * @return 正常返回true,异常返回false.
     */
    public static boolean hasInternet(Activity activity) {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;//网络异常，暂不可用
        }
        return true;
    }

    /**
     * 获取屏幕高度，无视虚拟键
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取 虚拟按键的高度
     * @param context 上下文对象
     * @return 虚拟按键高度
     */
    public static  int getBottomStatusHeight(Context context){
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight  - contentHeight;
    }

    /**
     * 获得屏幕高度，去除虚拟键
     *
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
