package com.bgenterprise.bglmtcinventory;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    String staff_name, staff_id;
    SessionManager session;
    ComponentName componentName;
    private static final int JOB_ID = 50;
    JobScheduler jobScheduler;
    JobInfo jobInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        session = new SessionManager(Main2Activity.this);

        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            staff_name = (String) b.get("staff_name");
            staff_id = (String) b.get("staff_id");
            session.CREATE_LOGIN_SESSION(staff_name, staff_id, BuildConfig.VERSION_NAME);

        } catch (Exception e) {
            Log.d("HERE", "" + staff_id);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            componentName = new ComponentName(this, BackgroundSync.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setMinimumLatency(3600000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();

        } else {
            componentName = new ComponentName(this, BackgroundSync.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setPeriodic(3600000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

        }

        startActivity(new Intent(Main2Activity.this, Operations.class));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            componentName = new ComponentName(this, BackgroundSync.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setMinimumLatency(3600000);

            //builder.setTriggerContentMaxDelay(100000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
            // Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();

        } else {
            componentName = new ComponentName(this, BackgroundSync.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
            builder.setPeriodic(3600000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

        }
    }
}
