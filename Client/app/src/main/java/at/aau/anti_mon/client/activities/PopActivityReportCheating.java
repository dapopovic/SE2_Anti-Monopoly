package at.aau.anti_mon.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import at.aau.anti_mon.client.R;

public class PopActivityReportCheating extends PopActivityObjects {
    public PopActivityReportCheating() {
        super(R.layout.activity_pop_report_cheating);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String reportText = intent.getStringExtra("Report");
        TextView report = findViewById(R.id.report);
        report.setText(reportText);
    }

}