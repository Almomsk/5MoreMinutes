package fivemoreminutes.a5moreminutes.Activity;


import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Charts.CombinedCharts;
import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;
import fivemoreminutes.a5moreminutes.Settings.*;

import com.github.mikephil.charting.charts.CombinedChart;

public class MainActivity extends AppCompatActivity {

    public MainActivity caller;
    public SortedMap<String, App> map;
    public List<UsageStats> all_apps;
    public List<App> list;
    public TextView toolbar_mess;
    public TextView toolbar_time;
    public LinearLayout liner;
    public long sum;
    public float allTime;
    public CombinedCharts bar;
    LayoutInflater inflater;
    public Notifications notific;

    public App single_db;
    private DatabaseAccess databaseAccess;

    private static final int SETTINGS_ACT = 50;

    private static final int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.databaseAccess = DatabaseAccess.getInstance(this);
        toolbar_time = (TextView) findViewById(R.id.textView_Time);
        toolbar_mess = (TextView) findViewById(R.id.textView_Mess);
        liner = (LinearLayout) findViewById(R.id.liner_main);
        notific = new Notifications(this);
        ImageButton setBut = (ImageButton) findViewById(R.id.optBut);

        setBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_ACT);
            }
        });

        ImageButton globalBut = (ImageButton) findViewById(R.id.globalBut);
        globalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GlobalActivity.class);
                startActivityForResult(intent, SETTINGS_ACT);
            }
        });

        caller = this;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        new GetStats(caller).execute();

        CombinedChart chart = findViewById(R.id.barchart);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setWordWrapEnabled(true);

        chart.setScaleEnabled(false);
        chart.invalidate();
        chart.setDragEnabled(true);
        bar = new CombinedCharts(chart, getApplicationContext(), getIntent());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Timer myTimer = new Timer(); // Создаем таймер
            final Handler uiHandler = new Handler();

            myTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            new GetStats(caller).execute();

                        }
                    });
                }
            }, 0L, 300L * 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTINGS_ACT: {
                this.onload(new App().sort(map));
                notific.sendAllLimitNotification(allTime);
                notific.sendAppLimitNotification(map);
            }
            break;
        }
    }

    private List<String> getInstalledAppList() {
        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(flags);
        List<String> installedApps = new ArrayList<>();
        for (ApplicationInfo info : infos) {
            if (!isSystemPackage(info))
                installedApps.add(info.packageName);
        }
        return installedApps;
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private App fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
            return new App(usageStats, getPackageManager().getApplicationIcon(ai), getPackageManager().getApplicationLabel(ai).toString(), usageStats.getTotalTimeInForeground(), ai.category);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void onBackgroundTaskCompleted(final List<UsageStats> result) throws IllegalArgumentException {
        List<String> installedApps = getInstalledAppList();
        all_apps = result;
        list = new ArrayList<>();
        map = new TreeMap<>();
        for (String name : installedApps) {
            for (UsageStats stat : all_apps) {
                if (name.equals(stat.getPackageName())) {
                    App value = map.get(stat.getPackageName());
                    if (value != null) {
                        value.setTimeInforground(value.getTimeInforground() + fromUsageStat(stat).getTimeInforground());
                        map.put(stat.getPackageName(), value);
                    } else {
                        map.put(stat.getPackageName(), fromUsageStat(stat));
                    }
                }
            }
        }
        sum = 0;
        for (Map.Entry<String, App> app : map.entrySet())
            sum += app.getValue().getTimeInforground();
        App app = new App(null, null, null, sum, 0);
        toolbar_time.setText(app.CalcTimeToolBar());
        toolbar_mess.setText("Minute on your device");
        allTime = app.CalcTimeCompare();
        this.onload(app.sort(map));
        notific.sendAllLimitNotification(allTime);
        notific.sendAppLimitNotification(map);
    }

    public void onload(final Map<String,App> result) {
        final App app_all = new App(null, null, null, sum, 0);
        if (result.size() > 0) {
            bar.mainLoad(result,app_all);

            LinearLayout parent = (LinearLayout) findViewById(R.id.main_lay);
            if (((LinearLayout) parent).getChildCount() > 0)
                ((LinearLayout) parent).removeAllViews();
            int count = 0;
            for (Map.Entry<String, App> app : result.entrySet()) {
                View custom = inflater.inflate(R.layout.usage_stat_item, parent, false);

                ImageView image = (ImageView) custom.findViewById(R.id.icon);
                image.setImageDrawable(app.getValue().getAppIcon());

                TextView appName = (TextView) custom.findViewById(R.id.title);
                appName.setText(app.getValue().getAppName());

                TextView Time = (TextView) custom.findViewById(R.id.timing);
                Time.setText(app.getValue().CalcTime());
                TextView Cat = (TextView) custom.findViewById(R.id.categ);
                Cat.setText(app.getValue().getCategory());
                TextView cou = (TextView) custom.findViewById(R.id.text_notify_All);
                databaseAccess.open();
                single_db = databaseAccess.selectOne(app.getValue());
                databaseAccess.close();
                if (single_db==null){
                    cou.setVisibility(View.GONE);
                }
                else{
                    cou.setVisibility(View.VISIBLE);
                    cou.setText(single_db.getCount_not()+"");
                }
                RelativeLayout single_app = (RelativeLayout) custom.findViewById(R.id.app_lay);
                final App single = app.getValue();
                single_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(caller, SingleAppActivity.class);
                        intent.putExtra("US", single.getUsageStats());
                        intent.putExtra("time", single.getTimeInforground());
                        intent.putExtra("all", app_all.getTimeInforground());
                        startActivityForResult(intent, SETTINGS_ACT);
                    }
                });
                if (count == 0)
                    custom.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.first_board));
                else if (count == 1)
                    custom.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.second_board));
                else if (count == 2)
                    custom.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.third_board));
                else
                    custom.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.other_board));
                    parent.addView(custom);
                count++;

            }
        }
    }
}
