package fivemoreminutes.a5moreminutes.Entity;
import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class App implements Comparable<App>{

    private UsageStats usageStats;
    private Drawable appIcon;
    private String appName;
    private String packName;
    public long TimeInforground;
    public int NumCategory;
    public String category;
    public int limit_h;
    public int limit_m;
    public int count_not;


    public App(){}

    public App(UsageStats usageStats, Drawable appIcon, String appName, long timeInforground,int NumCategory) {
        this.usageStats = usageStats;
        this.appIcon = appIcon;
        this.appName = appName;
        this.TimeInforground = timeInforground;
        this.NumCategory = NumCategory;
        if (usageStats!=null)
            this.packName = usageStats.getPackageName();
        switch (NumCategory){
            case -1: {this.category = "UNDEFINED";break;}
            case 0: {this.category = "GAME";break;}
            case 1: {this.category = "AUDIO";break;}
            case 2: {this.category = "VIDEO";break;}
            case 3: {this.category = "IMAGE";break;}
            case 4: {this.category = "SOCIAL";break;}
            case 5: {this.category = "NEWS";break;}
            case 6: {this.category = "MAPS";break;}
            case 7: {this.category = "PRODUCTIVITY";break;}
        }
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public int getCount_not() {
        return count_not;
    }

    public void setCount_not(int count_not) {
        this.count_not = count_not;
    }

    public int getLimit_h() {
        return limit_h;
    }

    public void setLimit_h(int limit_h) {
        this.limit_h = limit_h;
    }

    public int getLimit_m() {
        return limit_m;
    }

    public void setLimit_m(int limit_m) {
        this.limit_m = limit_m;
    }

    public void setNumCategory(int numCategory) {
        NumCategory = numCategory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumCategory() {
        return NumCategory;
    }

    public String getCategory() {
        return category;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(UsageStats usageStats)   {
        this.usageStats = usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getTimeInforground() {
        return TimeInforground;
    }

    public void setTimeInforground(long timeInforground) {
        TimeInforground = timeInforground;
    }

    public String CalcTime () {
        long minutes = 500, seconds = 500, hours = 500;
        minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
        seconds = (int) (TimeInforground / 1000) % 60;
        hours = (int) (TimeInforground / (1000 * 60 * 60));
        return hours + "h" + ":" + minutes + "m" +":"+ seconds + "s";
    }

    public String CalcTimeGlobal () {
        long minutes = 500, hours = 500;
        minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
        hours = (int) (TimeInforground / (1000 * 60 * 60));
        return hours + "h " + minutes + "min";
    }

    public String CalcTimeToolBar () {
        long minutes = 500, seconds = 500, hours = 500;
        minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
        seconds = (int) (TimeInforground / 1000) % 60;
        hours = (int) (TimeInforground / (1000 * 60 * 60));
        seconds = seconds / 60;
        hours = hours * 60;
        minutes+= hours+seconds;
        return minutes+"";

    }

    public Long CalcTimeCompare () {
        long minutes = 500, seconds = 500, hours = 500;
        minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
        seconds = (int) (TimeInforground / 1000) % 60;
        hours = (int) (TimeInforground / (1000 * 60 * 60));
        seconds = seconds/60;
        hours= hours *60;
        minutes+=hours+seconds;
        return minutes;
    }

    public float CalcTimeBar () {
        float minutes = 500f, seconds = 500f, hours = 500f;
        minutes = (float) ((TimeInforground / (1000 * 60)) % 60);
        seconds = (float) (TimeInforground / 1000) % 60;
        hours = (float) (TimeInforground / (1000 * 60 * 60));
        seconds = seconds/60f;
        hours= hours *60f;
        minutes+=hours+seconds;
        return minutes;
    }

    @Override
    public int compareTo(@NonNull App app) {
        if (usageStats == null && app.getUsageStats() != null) {
            return 1;
        } else if (app.getUsageStats() == null && usageStats != null) {
            return -1;
        } else if (app.getUsageStats() == null && usageStats == null) {
            return 0;
        } else {
            return Long.compare(app.TimeInforground,this.TimeInforground);
        }
    }

    public Map<String,App> sort (Map<String,App> map){
        List<Map.Entry<String, App>> list_to_map =
                new LinkedList<>(map.entrySet());
        Collections.sort(list_to_map, new Comparator<Map.Entry<String,App>>() {
            public int compare(Map.Entry<String, App> o1,
                               Map.Entry<String, App> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, App> sortedMap = new LinkedHashMap<String, App>();
        for (Map.Entry<String, App> entry : list_to_map) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
