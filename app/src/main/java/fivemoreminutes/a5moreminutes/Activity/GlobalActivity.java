package fivemoreminutes.a5moreminutes.Activity;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Charts.CombinedChartsSingle;
import fivemoreminutes.a5moreminutes.Charts.StackedCharts;
import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;
import fivemoreminutes.a5moreminutes.Settings.GetStatsSingleWeekly;
import fivemoreminutes.a5moreminutes.Settings.GetStatsWeekly;
import fivemoreminutes.a5moreminutes.Settings.SharPreferences;

public class GlobalActivity extends AppCompatActivity {

    private DatabaseAccess databaseAccess;
    public List<App> topFive;
    public LayoutInflater inflater;
    public Map<String,App> week_stat;
    public Map<String,Map<String,App>> result;
    private float x1, x2;
    private final int MIN_DISTANCE = 150;
    StackedCharts bar;

    TextView toolbar_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);

        CombinedChart chart = findViewById(R.id.barchart);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setWordWrapEnabled(true);
        chart.setScaleEnabled(false);
        chart.invalidate();
        chart.setDragEnabled(true);

        bar = new StackedCharts(chart, getApplicationContext());

        new GetStatsWeekly(this).execute();


        this.databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        topFive = databaseAccess.getAllAppsByCount();
        databaseAccess.close();

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout parent = (LinearLayout) findViewById(R.id.main_global_notify);

        if (((LinearLayout) parent).getChildCount() > 0)
            ((LinearLayout) parent).removeAllViews();

        toolbar_time = (TextView) findViewById(R.id.textView_Time);



        int size = 5;

        if (topFive==null)
            size = 0;
        else if (topFive.size()<5)
            size = topFive.size();

        for(int i = 0;i<size;i++) {
            View custom = inflater.inflate(R.layout.global_notify_item, parent, false);
            fromAppName(topFive.get(i));
            ImageView image = (ImageView) custom.findViewById(R.id.icon_gn);
            image.setImageDrawable(topFive.get(i).getAppIcon());

            TextView appName = (TextView) custom.findViewById(R.id.title_gn);
            appName.setText(topFive.get(i).getAppName());

            TextView Time = (TextView) custom.findViewById(R.id.timing_gn);
            Time.setText(topFive.get(i).getCategory());

            TextView Cat = (TextView) custom.findViewById(R.id.categ_gn);
            Cat.setText("about "+topFive.get(i).getCount_not()+" times");
            parent.addView(custom);
        }
        LinearLayout par = (LinearLayout) findViewById(R.id.LinerFirst);
        par.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (deltaX > MIN_DISTANCE)
                            onRightSwipe();
                }
                return true;
            }
        });
    }

    public void onRightSwipe() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        GlobalActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        GlobalActivity.this.finish();

    }

    public void onBackgroundTaskCompleted(final Map<String,Map<String,App>>res){
        long summ = 0L;
        result = res;

        bar.mainLoad(result);
        week_stat = new TreeMap<>();

        for (Map.Entry<String,Map<String,App>> app : res.entrySet()) {
            for (Map.Entry <String,App> item : app.getValue().entrySet()){
                summ +=item.getValue().getTimeInforground();
                App value = week_stat.get(item.getValue().getAppName());
                if (value!=null){
                    value.setTimeInforground(value.getTimeInforground()+item.getValue().getTimeInforground());
                    week_stat.put(value.getAppName(),value);
                }
                else
                {
                    week_stat.put(item.getValue().getAppName(),item.getValue());
                }
            }
        }

        App appAll = new App(null, null, null, summ, 0);
        toolbar_time.setText(appAll.CalcTimeGlobal());
        week_stat = appAll.sort(week_stat);

        LinearLayout parent = (LinearLayout) findViewById(R.id.main_global_stat);

        if (((LinearLayout) parent).getChildCount() > 0)
            ((LinearLayout) parent).removeAllViews();


        int size = 5,count =0;

        if (week_stat==null)
            size = 0;
        else if (week_stat.size()<5)
            size = week_stat.size();

        for (Map.Entry <String,App> item : week_stat.entrySet()){
            if (count<size){
            View custom = inflater.inflate(R.layout.global_notify_item, parent, false);
            ImageView image = (ImageView) custom.findViewById(R.id.icon_gn);
            image.setImageDrawable(item.getValue().getAppIcon());

            TextView appName = (TextView) custom.findViewById(R.id.title_gn);
            appName.setText(item.getValue().getAppName());

            TextView Time = (TextView) custom.findViewById(R.id.timing_gn);
            Time.setText(item.getValue().getCategory());

            TextView Cat = (TextView) custom.findViewById(R.id.categ_gn);
            float percent = item.getValue().getTimeInforground() * 100 / appAll.getTimeInforground();
            Cat.setText("Time Screen / "+percent+" %");
            parent.addView(custom);
            count++;
            }
        }



    }

    private App fromAppName(App app) throws IllegalArgumentException {
        try {

            ApplicationInfo ai = getPackageManager().getApplicationInfo(app.getPackName(), 0);
            app.setAppIcon(getPackageManager().getApplicationIcon(ai));
            return app;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
