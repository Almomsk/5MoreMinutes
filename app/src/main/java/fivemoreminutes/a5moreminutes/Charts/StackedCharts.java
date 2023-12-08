package fivemoreminutes.a5moreminutes.Charts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;
import fivemoreminutes.a5moreminutes.Settings.SharPreferences;

public class StackedCharts {
    public CombinedChart chart;
    public  Context context;


    public StackedCharts(CombinedChart chart, Context context) {
        this.chart = chart;
        this.context = context;
    }

    public CombinedChart getChart() {
        return chart;
    }

    public void setChart(CombinedChart chart) {
        this.chart = chart;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void mainLoad(Map<String,Map<String,App>> map){
        YAxis ya = chart.getAxisRight();
        ya.setDrawGridLines(false);
        ya.setDrawAxisLine(false);
        ya.setDrawZeroLine(false);
        ya.setDrawTopYLabelEntry(false);
        ya.setDrawGridLinesBehindData(false);
        ya.setDrawLimitLinesBehindData(false);
        ya.setDrawLabels(false);
        chart.getDescription().setText("");
        ya = chart.getAxisLeft();
        ya.setDrawGridLines(false);
        ya.setDrawZeroLine(true);
        ya.setDrawTopYLabelEntry(false);
        ya.setDrawGridLinesBehindData(false);
        ya.setDrawLimitLinesBehindData(true);
        ya.setDrawLabels(false);
        ya.removeAllLimitLines();
        XAxis xa = chart.getXAxis();
        xa.setDrawGridLines(false);
        xa.setDrawAxisLine(false);
        xa.setDrawGridLinesBehindData(true);
        xa.setDrawLimitLinesBehindData(true);
        //xa.setDrawLabels(false);
        xa.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xa.setAxisMinimum(0.5f);
        xa.setGranularity(0.5f);
        xa.setAxisMaximum(7.5f);
        CombinedData Cdata = new CombinedData();
        BarData data = new BarData();
        int[] arr_color = new int[]{ContextCompat.getColor(context,R.color.chart_undef),
                ContextCompat.getColor(context,R.color.chart_games),
                ContextCompat.getColor(context,R.color.chart_audio),
                ContextCompat.getColor(context,R.color.chart_video),
                ContextCompat.getColor(context,R.color.chart_image),
                ContextCompat.getColor(context,R.color.chart_social),
                ContextCompat.getColor(context,R.color.chart_news),
                ContextCompat.getColor(context,R.color.chart_maps),
                ContextCompat.getColor(context,R.color.chart_product)};

        final ArrayList<String> xLabel = new ArrayList<>();
        xLabel.add("");

        List<String> list = new ArrayList<>();
        ArrayList <BarDataSet> bar_data_list = new ArrayList<BarDataSet>();
        int count = 0;
        for (Map.Entry<String,Map<String,App>> app : map.entrySet()) {
            float[] arr = new float[9];
            xLabel.add(app.getKey());
            for (Map.Entry<String, App> item : app.getValue().entrySet()) {
                switch (item.getValue().getNumCategory()) {
                    case -1: {
                        arr[0] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 0: {
                        arr[1] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 1: {
                        arr[2] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 2: {
                        arr[3] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 3: {
                        arr[4] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 4: {
                        arr[5] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 5: {
                        arr[6] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 6: {
                        arr[7] += item.getValue().CalcTimeBar();
                        break;
                    }
                    case 7: {
                        arr[8] += item.getValue().CalcTimeBar();
                        break;
                    }
                }

            }
            ArrayList<BarEntry> Yval = new ArrayList<BarEntry>();
            Yval.add(new BarEntry(count + 1, arr));
            list.add(app.getKey());
            bar_data_list.add(count, new BarDataSet(Yval, app.getKey()));
            ArrayList<Integer> int_list = new ArrayList<>();
            bar_data_list.get(count).setColors(arr_color);

            bar_data_list.get(count).setDrawValues(false);
            data.addDataSet(bar_data_list.get(count));

            count++;


        }




        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });

       /* Legend l = chart.getLegend();
        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = ContextCompat.getColor(context,R.color.font);
            entry.label = list.get(i);
            entries.add(entry);
        }

        l.setCustom(entries);
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);*/

        chart.getLegend().setEnabled(false);


        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });
        chart.setDrawValueAboveBar(false);
        chart.setHighlightFullBarEnabled(false);
        chart.animateY(1000);
        Cdata.setData(data);
        chart.setData(Cdata);
        chart.invalidate();

    }

}
