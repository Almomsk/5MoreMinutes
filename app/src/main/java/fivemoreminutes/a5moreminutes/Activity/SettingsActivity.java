package fivemoreminutes.a5moreminutes.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;

import java.lang.reflect.Field;

import fivemoreminutes.a5moreminutes.Settings.SharPreferences;
import fivemoreminutes.a5moreminutes.R;

public class SettingsActivity extends AppCompatActivity {
    private float x1, x2;
    private final int MIN_DISTANCE = 150;
    public static final String APP_PREFERENCES = "mysettings";
    public SharPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ConstraintLayout drawerSet = (ConstraintLayout) findViewById(R.id.cos_set);

        set_timepicker_text_colour();
        pref = new SharPreferences(getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE));
        pref.loadLimitSwitch(getIntent());
        pref.loadLimit(getIntent());
        pref.loadNotific(getIntent());
        pref.loadPopNotific(getIntent());


        drawerSet.setOnTouchListener(new View.OnTouchListener() {
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
        final Switch sw_limit = (Switch) findViewById(R.id.limit_all_swtch);
        sw_limit.setChecked(pref.isAllLimit());
        if (!sw_limit.isChecked())
        {
            Button but_conf = (Button) findViewById(R.id.confirm_button);
            TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerS);
            time_picker.setEnabled(false);
            time_picker.invalidate();
            time_picker.refreshDrawableState();
            but_conf.setEnabled(false);
            but_conf.invalidate();
            but_conf.refreshDrawableState();
        }

        sw_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.saveLimitSwitch(getIntent(),sw_limit.isChecked());
                TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerS);
                Button but_conf = (Button) findViewById(R.id.confirm_button);
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

                }
            }
        });
        TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerS);
        time_picker.setIs24HourView(true);
        Log.d("onCreate: ", pref.getLimit_hour()+" "+pref.getLimit_min());
        time_picker.setHour(pref.getLimit_hour());
        time_picker.setMinute(pref.getLimit_min());

        Button but_conf = (Button) findViewById(R.id.confirm_button);
        but_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sw_limit = (Switch) findViewById(R.id.limit_all_swtch);
                TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerS);
                pref.saveLimit(getIntent(),time_picker.getHour(),time_picker.getMinute());
            }
        });

        final Switch notific_swtch = (Switch) findViewById(R.id.notific_swtch);
        notific_swtch.setChecked(pref.isNotific());
        notific_swtch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.saveNotific(getIntent(),notific_swtch.isChecked());
            }
        });


        final Switch push_swtch = (Switch) findViewById(R.id.push_swtch);
        push_swtch.setChecked(pref.isPopNotific());
        push_swtch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.savePopNotific(getIntent(),push_swtch.isChecked());
            }
        });
    }

    private void set_timepicker_text_colour(){

        TimePicker time_picker = (TimePicker) findViewById(R.id.timePickerS);
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

    public void onRightSwipe(){
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        SettingsActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        SettingsActivity.this.finish();

    }


}
