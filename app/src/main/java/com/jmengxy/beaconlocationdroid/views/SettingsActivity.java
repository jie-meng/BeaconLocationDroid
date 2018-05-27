package com.jmengxy.beaconlocationdroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jmengxy.beaconlocationdroid.R;
import com.jmengxy.beaconlocationdroid.models.BeaconsInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    private Gson gson = new Gson();

    private BeaconsInfo beaconsInfo;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.til_distance_weight)
    TextInputLayout tilDistanceWeight;

    @BindView(R.id.ed_distance_weight)
    EditText edDistanceWeight;

    @BindView(R.id.til_height)
    TextInputLayout tilHeight;

    @BindView(R.id.ed_height)
    EditText edHeight;


    @BindView(R.id.til_measure_power)
    TextInputLayout tilMeasurePower;

    @BindView(R.id.ed_measure_power)
    EditText edMeasurePower;

    @BindView(R.id.til_location_beacon_count)
    TextInputLayout tiLocationBeaconCount;

    @BindView(R.id.ed_location_beacon_count)
    EditText edLocationBeaconCount;

    @BindView(R.id.til_reliable_threshold)
    TextInputLayout tilReliableThreshold;

    @BindView(R.id.ed_reliable_threshold)
    EditText edReliableThreshold;

    @OnClick(R.id.ok)
    void clickOk() {
        if (checkEdit()) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.ARG_BEACON_INFO, gson.toJson(beaconsInfo));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean checkEdit() {
        try {
            beaconsInfo.setDistanceWeight(Double.parseDouble(edDistanceWeight.getText().toString()));
        } catch (NumberFormatException e) {
            tilDistanceWeight.setErrorEnabled(true);
            tilDistanceWeight.setError(getString(R.string.incorrect_format));
            return false;
        }

        try {
            beaconsInfo.setHeight(Double.parseDouble(edHeight.getText().toString()));
        } catch (NumberFormatException e) {
            tilHeight.setErrorEnabled(true);
            tilHeight.setError(getString(R.string.incorrect_format));
            return false;
        }

        try {
            beaconsInfo.setMeasurePower(Integer.parseInt(edMeasurePower.getText().toString()));
        } catch (NumberFormatException e) {
            tilMeasurePower.setErrorEnabled(true);
            tilMeasurePower.setError(getString(R.string.incorrect_format));
            return false;
        }

        try {
            beaconsInfo.setLocationBeaconCount(Integer.parseInt(edLocationBeaconCount.getText().toString()));
        } catch (NumberFormatException e) {
            tiLocationBeaconCount.setErrorEnabled(true);
            tiLocationBeaconCount.setError(getString(R.string.incorrect_format));
            return false;
        }

        try {
            beaconsInfo.setReliableThreshold(Double.parseDouble(edReliableThreshold.getText().toString()));
        } catch (NumberFormatException e) {
            tilReliableThreshold.setErrorEnabled(true);
            tilReliableThreshold.setError(getString(R.string.incorrect_format));
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        String stringExtra = getIntent().getStringExtra(MainActivity.ARG_BEACON_INFO);
        beaconsInfo = gson.fromJson(stringExtra, BeaconsInfo.class);

        edDistanceWeight.setOnClickListener(v -> tilDistanceWeight.setErrorEnabled(false));
        edHeight.setOnClickListener(v -> tilHeight.setErrorEnabled(false));
        edMeasurePower.setOnClickListener(v -> tilMeasurePower.setErrorEnabled(false));
        edLocationBeaconCount.setOnClickListener(v -> tiLocationBeaconCount.setErrorEnabled(false));
        edReliableThreshold.setOnClickListener(v -> tilReliableThreshold.setErrorEnabled(false));

        edDistanceWeight.setText(Double.toString(beaconsInfo.getDistanceWeight()));
        edDistanceWeight.setSelection(edDistanceWeight.getText().length());

        edHeight.setText(Double.toString(beaconsInfo.getHeight()));
        edHeight.setSelection(edHeight.getText().length());

        edMeasurePower.setText(Integer.toString(this.beaconsInfo.getMeasurePower()));
        edMeasurePower.setSelection(edMeasurePower.getText().length());

        edLocationBeaconCount.setText(Integer.toString(this.beaconsInfo.getLocationBeaconCount()));
        edLocationBeaconCount.setSelection(edLocationBeaconCount.getText().length());

        edReliableThreshold.setText(Double.toString(beaconsInfo.getReliableThreshold()));
        edReliableThreshold.setSelection(edReliableThreshold.getText().length());
    }
}
