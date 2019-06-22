package com.example.androidcore_jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private JobScheduler jobScheduler;
    private static final int JOB_ID = 0;

    private Switch idleSwitch;
    private Switch chargingSwitch;

    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idleSwitch = findViewById(R.id.idleSwitch);
        chargingSwitch = findViewById(R.id.chargingSwitch);
        seekBar = findViewById(R.id.seekbar);

        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 0){
                    seekBarProgress.setText(getString(R.string.seconds, progress));
                }
                else {
                    seekBarProgress.setText(R.string.notSet);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void scheduleJob(View view) {
        RadioGroup networkOptions = findViewById(R.id.networkOptions);
        int selectedNetworkID = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        int seekBarInteger = seekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        switch (selectedNetworkID){
            case R.id.noOption:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyOption:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiOption:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        //The ComponentName is used to associate the JobService with the JobInfo object.
        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
        builder.setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(idleSwitch.isChecked())
                .setRequiresCharging(chargingSwitch.isChecked());

        if(seekBarSet){
            //The parameter is in milliseconds, and you want the user to set the deadline in seconds.
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }

        //The crash will happen because the "No Network Required" condition is the default,
        // and this condition does not count as a constraint.
        // To properly schedule the JobService, the JobScheduler needs at least one constraint.
        boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE)
                || idleSwitch.isChecked() || chargingSwitch.isChecked() || seekBarSet;
        if(constraintSet){
            JobInfo myJobInfo = builder.build();
            jobScheduler.schedule(myJobInfo);
            Toast.makeText(this, "Job scheduled, job will run when " +
                    "the constraints are met.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Please set atleast one constraint",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelJobs(View view) {
        if(jobScheduler != null){
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(this, "Jobs Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
