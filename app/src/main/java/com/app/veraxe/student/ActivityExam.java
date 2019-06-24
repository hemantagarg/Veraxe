package com.app.veraxe.student;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelExam;
import com.app.veraxe.model.ModelStudentFees;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 06-01-2016.
 */
public class ActivityExam extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    private Context context;
    private Toolbar toolbar;
    private RelativeLayout mRlExam;
    private Spinner mSpinnerExam;
    private RelativeLayout mRlStudent;
    private Spinner mSpinnerSubject;
    private ArrayAdapter<String> adapterExam;
    private ArrayList<String> examIdList = new ArrayList<>();
    private ArrayList<String> examNameList = new ArrayList<>();
    private ArrayList<ModelExam> studentReportList = new ArrayList<>();
    private ArrayAdapter<String> adapterSubject;
    private ArrayList<String> subjectIdList = new ArrayList<>();
    private ArrayList<String> subjectNameList = new ArrayList<>();
    private Button mBtnExam, mBtnSubject, mBtnFetch;
    boolean isSubectClicked = false;
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        context = this;
        init();
        setListener();
        examList();
        getSubjectList();
    }

    private void init() {

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBtnSubject = findViewById(R.id.mBtnSubject);
        mBtnExam = findViewById(R.id.mBtnExam);
        mRlExam = findViewById(R.id.mRlExam);
        mRlStudent = findViewById(R.id.mRlStudent);
        chart = findViewById(R.id.chart1);
        mSpinnerExam = findViewById(R.id.mSpinnerExam);
        mSpinnerSubject = findViewById(R.id.mSpinnerSubject);
        mBtnFetch = findViewById(R.id.mBtnFetch);
        chart.setVisibility(View.GONE);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

    }

    private void setChartData() {
        chart.setVisibility(View.VISIBLE);
        chart.invalidate();
        chart.clear();

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < studentReportList.size(); i++) {
            xAxisLabel.add(studentReportList.get(i).getName());
        }
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(studentReportList.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(10f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(120f);


        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<BarEntry> entriesMarks = new ArrayList<>();
        for (int i = 0; i < studentReportList.size(); i++) {
            entries.add(new BarEntry(i, (int) Double.parseDouble(studentReportList.get(i).getExamMarks())));
            entriesMarks.add(new BarEntry(i, (int) Double.parseDouble(studentReportList.get(i).getStudentMarks())));
        }
        String title = "Exam";
        if (isSubectClicked) {
            title = "Subject";
        }
        BarDataSet set = new BarDataSet(entries, title);
        BarDataSet set1 = new BarDataSet(entriesMarks, "Student Marks");
        set1.setColor(ContextCompat.getColor(context, R.color.green_timetable));
        BarData data = new BarData(set, set1);

        data.setBarWidth(0.3f); // set custom bar width

        float groupSpace = 0.2f;
        float barSpace = 0.03f;
        chart.setData(data);

        chart.groupBars(-0.2f, groupSpace, barSpace);
        chart.invalidate();

    }

    public class LabelFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }

    private String dummyData() {
        return "{\n" +
                "    \"response\": 1,\n" +
                "    \"result\": [\n" +
                "        {\n" +
                "            \"subjectId\": \"11\",\n" +
                "            \"subjectName\": \"Social\",\n" +
                "            \"examMarks\": \"100\",\n" +
                "            \"studentMarks\": \"72.00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"subjectId\": \"10\",\n" +
                "            \"subjectName\": \"Science\",\n" +
                "            \"examMarks\": \"20\",\n" +
                "            \"studentMarks\": \"13.00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"subjectId\": \"8\",\n" +
                "            \"subjectName\": \"Hindi\",\n" +
                "            \"examMarks\": \"100\",\n" +
                "            \"studentMarks\": \"68.00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"subjectId\": \"9\",\n" +
                "            \"subjectName\": \"English\",\n" +
                "            \"examMarks\": \"100\",\n" +
                "            \"studentMarks\": \"76.00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";
    }

    public void setListener() {

        mBtnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSubectClicked) {
                    if (mSpinnerSubject.getSelectedItemPosition() != 0) {
                        subjectWiseReport();
                    } else
                        Toast.makeText(context, "Please select Subject", Toast.LENGTH_SHORT).show();
                } else {
                    if (mSpinnerExam.getSelectedItemPosition() != 0) {
                        examWiseReport();
                    } else
                        Toast.makeText(context, "Please select Exam", Toast.LENGTH_SHORT).show();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBtnExam.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_selected));
                mBtnSubject.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_unselected));
                mBtnExam.setTextColor(ContextCompat.getColor(context, R.color.white));
                mBtnSubject.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                if (mRlStudent.getVisibility() == View.VISIBLE)
                    chart.setVisibility(View.GONE);
                mRlExam.setVisibility(View.VISIBLE);
                mRlStudent.setVisibility(View.GONE);
                isSubectClicked = false;

            }
        });
        mBtnSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBtnSubject.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_selected));
                mBtnExam.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_unselected));
                mBtnSubject.setTextColor(ContextCompat.getColor(context, R.color.white));
                mBtnExam.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                if (mRlExam.getVisibility() == View.VISIBLE)
                    chart.setVisibility(View.GONE);
                mRlExam.setVisibility(View.GONE);
                mRlStudent.setVisibility(View.VISIBLE);
                isSubectClicked = true;

            }
        });

    }


    public void examList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("studentid", AppUtils.getStudentId(context));
            //   hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_exam_list);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void examWiseReport() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("examid", examIdList.get(mSpinnerExam.getSelectedItemPosition()));
            hm.put("studentid", AppUtils.getStudentId(context));
            //   hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_exam_report);
            new CommonAsyncTaskHashmap(3, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void subjectWiseReport() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("subjectid", subjectIdList.get(mSpinnerSubject.getSelectedItemPosition()));
            hm.put("studentid", AppUtils.getStudentId(context));
            //   hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_subject_report);
            new CommonAsyncTaskHashmap(3, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    public void getSubjectList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("studentid", AppUtils.getStudentId(context));
            //hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_subject_list);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClickListener(int position, int flag) {


    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                examIdList.clear();
                examNameList.clear();
                if (response.getString("response").equalsIgnoreCase("1")) {
                    JSONArray array = response.getJSONArray("result");

                    examIdList.add("-1");
                    examNameList.add("Select Exam");
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        examIdList.add(jo.getString("id"));
                        examNameList.add(jo.getString("examName"));
                    }
                } else {
                    examIdList.add("-1");
                    examNameList.add("Select Exam");
                }
                adapterExam = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, examNameList);
                mSpinnerExam.setAdapter(adapterExam);
            } else if (method == 2) {
                subjectIdList.clear();
                subjectNameList.clear();
                if (response.getString("response").equalsIgnoreCase("1")) {
                    JSONArray array = response.getJSONArray("result");
                    subjectIdList.add("-1");
                    subjectNameList.add("Select Subject");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        subjectIdList.add(jo.getString("id"));
                        subjectNameList.add(jo.getString("subjectName"));
                    }
                } else {
                    subjectIdList.add("-1");
                    subjectNameList.add("Select Subject");
                }
                adapterSubject = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, subjectNameList);
                mSpinnerSubject.setAdapter(adapterSubject);
            } else if (method == 3) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    JSONArray array = response.getJSONArray("result");
                    Gson gson = new Gson();
                    studentReportList.clear();
                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            ModelExam modelExam;
                            modelExam = gson.fromJson(jsonObject.toString(), ModelExam.class);
                            studentReportList.add(modelExam);
                        }
                        setChartData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        }


    }

}
