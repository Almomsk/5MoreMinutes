package fivemoreminutes.a5moreminutes.Activity;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Charts.CombinedChartsSingle;
import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;
import fivemoreminutes.a5moreminutes.Settings.GetStatsSingleWeekly;


public class SingleAppActivity extends AppCompatActivity {

    private float x1, x2;
    private final int MIN_DISTANCE = 150;
    public App single;
    public App single_db;
    public App app_all;
    CombinedChartsSingle bar;
    Map<Integer,App> res;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app);

        this.databaseAccess = DatabaseAccess.getInstance(this);

        Bundle extras = this.getIntent().getExtras();
        UsageStats us = (UsageStats) extras.get("US");
        single = fromUsageStat(us);
        long tfs = extras.getLong("time");
        single.setTimeInforground(tfs);
        long tf = extras.getLong("all");
        app_all = new App(null, null, null, tf, 0);

        databaseAccess.open();
        single_db = databaseAccess.selectOne(single);
        databaseAccess.close();
        if (single_db==null){
            single.setCount_not(0);
        }
        else{
            single.setCount_not(single_db.getCount_not());
            single.setLimit_h(single_db.getLimit_h());
            single.setLimit_m(single_db.getLimit_m());
        }

        TextView toolbar_time = (TextView) findViewById(R.id.textView_single_Time);
        toolbar_time.setText(single.CalcTimeToolBar());
        TextView toolbar_mess = (TextView) findViewById(R.id.textView_single_Mess);
        toolbar_mess.setText("Minute on your device");
        TextView text_count_notify = (TextView) findViewById(R.id.textView2_notify_count);
        text_count_notify.setText(single.getCount_not()+"");
        ImageView image = (ImageView) findViewById(R.id.icon_single);
        image.setImageDrawable(single.getAppIcon());


        loadMainFrag();
        LinearLayout parent = (LinearLayout) findViewById(R.id.single_main_lay);
        parent.setOnTouchListener(new View.OnTouchListener() {
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

    public void loadMainStat() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout parent = (LinearLayout) findViewById(R.id.single_main_lay);
        parent.removeAllViews();
        final View custom = inflater.inflate(R.layout.layout_single_settings, parent, false);
        parent.addView(custom);
        set_timepicker_text_colour();
        TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerSinS);
        time_picker.setIs24HourView(true);
        databaseAccess.open();
        single_db = databaseAccess.selectOne(single);
        databaseAccess.close();
        final Switch sw_limit = (Switch) findViewById(R.id.limit_single_swtch);

        if (single_db==null){
            single.setCount_not(0);
            sw_limit.setChecked(false);
        }
        else{
            single.setCount_not(single_db.getCount_not());
            single.setLimit_h(single_db.getLimit_h());
            single.setLimit_m(single_db.getLimit_m());
            sw_limit.setChecked(true);
            TextView text_count_notify = (TextView) findViewById(R.id.textView2_notify_count);
            text_count_notify.setText(single.getCount_not()+"");
        }
        if (!sw_limit.isChecked())
        {
            Button but_conf = (Button) findViewById(R.id.confirm_sin_button);
            time_picker = (TimePicker) findViewById(R.id.timePickerSinS);
            time_picker.setEnabled(false);
            time_picker.invalidate();
            time_picker.refreshDrawableState();
            but_conf.setEnabled(false);
            but_conf.invalidate();
            but_conf.refreshDrawableState();
        }
        time_picker.setHour(single.getLimit_h());
        time_picker.setMinute(single.getLimit_m());
        sw_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button but_conf = (Button) findViewById(R.id.confirm_sin_button);
                TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerSinS);
                if (sw_limit.isChecked())
                {
                    time_picker.setEnabled(true);
                    time_picker.invalidate();
                    time_picker.refreshDrawableState();
                    but_conf.setEnabled(true);
                    but_conf.invalidate();
                    but_conf.refreshDrawableState();
                }
                else{
                    time_picker.setEnabled(false);
                    time_picker.invalidate();
                    time_picker.refreshDrawableState();
                    but_conf.setEnabled(false);
                    but_conf.invalidate();
                    but_conf.refreshDrawableState();
                    databaseAccess.open();
                    single.setCount_not(0);
                    databaseAccess.delete(single);
                    databaseAccess.close();

                }
            }
        });
        ImageButton right = (ImageButton) findViewById(R.id.imageButton3);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMainFrag();
            }
        });

        Button but_conf = (Button) findViewById(R.id.confirm_sin_button);
        but_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerSinS);
                single.setLimit_h(time_picker.getHour());
                single.setLimit_m(time_picker.getMinute());
                databaseAccess.open();
                single.setCount_not(0);
                databaseAccess.save(single);
                databaseAccess.close();
            }
        });

    }

    private void set_timepicker_text_colour(){

        TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerSinS);
        int hour_numberpicker_id = Resources.getSystem().getIdentifier("hour", "id", "android");
        int minute_numberpicker_id = Resources.getSystem().getIdentifier("minute", "id", "android");
        int ampm_numberpicker_id = Resources.getSystem().getIdentifier("amPm", "id", "android");

        NumberPicker hour_numberpicker = (NumberPicker) time_picker.findViewById(hour_numberpicker_id);
        NumberPicker minute_numberpicker = (NumberPicker) time_picker.findViewById(minute_numberpicker_id);
        NumberPicker ampm_numberpicker = (NumberPicker) time_picker.findViewById(ampm_numberpicker_id);

        set_numberpicker_text_colour(hour_numberpicker);
        set_numberpicker_text_colour(minute_numberpicker);
        set_numberpicker_text_colour(ampm_numberpicker);
    }

    private void set_numberpicker_text_colour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.font);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                ((EditText)child).setEnabled(false);
                ((EditText)child).setFocusable(false);
                number_picker.invalidate();
            }
            catch(NoSuchFieldException e){
            }
            catch(IllegalAccessException e){
            }
            catch(IllegalArgumentException e){

            }
        }
    }

    public void loadMainFrag(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout parent = (LinearLayout) findViewById(R.id.single_main_lay);
        parent.removeAllViews();
        final View custom = inflater.inflate(R.layout.layout_single_charts, parent, false);
        parent.addView(custom);
        ImageButton left = (ImageButton) findViewById(R.id.imageButton2);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMainStat();
            }
        });
        PieChart pieChart = (PieChart) findViewById(R.id.idPieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(100f);
        pieChart.setDescription(null);
        addDataSet(pieChart);
        pieChart.invalidate();
        CombinedChart chart = findViewById(R.id.barchart);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setWordWrapEnabled(true);
        chart.setScaleEnabled(false);
        chart.invalidate();
        chart.setDragEnabled(true);
        bar= new CombinedChartsSingle(chart, getApplicationContext());
        if (res == null) {
            new GetStatsSingleWeekly(this, single.getAppName()).execute();
        }
        else
            bar.mainLoad(res);

    }

    private App fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
            return new App(usageStats, getPackageManager().getApplicationIcon(ai), getPackageManager().getApplicationLabel(ai).toString(), usageStats.getTotalTimeInForeground(), ai.category);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void onRightSwipe() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        SingleAppActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        SingleAppActivity.this.finish();

    }

    private void addDataSet(PieChart pieChart) {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        yEntrys.add(new PieEntry(single.CalcTimeBar() * 100 / app_all.CalcTimeBar()));
        yEntrys.add(new PieEntry(100 - (single.CalcTimeBar() * 100 / app_all.CalcTimeBar())));
        PieDataSet pieDataSet = new PieDataSet(yEntrys, " ");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getBaseContext(), R.color.chart_image));
        colors.add(ContextCompat.getColor(getBaseContext(), R.color.chart_maps));
        pieDataSet.setColors(colors);
        pieChart.getLegend().setEnabled(false);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }

    public void onBackgroundTaskCompleted(final Map<Integer,App> result){
        res = result;
        bar.mainLoad(result);
    }

}
