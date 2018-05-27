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
import com.jmengxy.beaconlocationdroid.views.MainActivity;

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

    @BindView(R.id.til_algorithm)
    TextInputLayout tilAlgorithm;

    @BindView(R.id.ed_algorithm)
    EditText edAlgorithm;


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
            beaconsInfo.setAlgorithm(Integer.parseInt(edAlgorithm.getText().toString()));
        } catch (NumberFormatException e) {
            tilAlgorithm.setErrorEnabled(true);
            tilAlgorithm.setError(getString(R.string.incorrect_format));
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

        edAlgorithm.setOnClickListener(v -> tilAlgorithm.setErrorEnabled(false));

        edDistanceWeight.setText(Double.toString(beaconsInfo.getDistanceWeight()));
        edDistanceWeight.setSelection(edDistanceWeight.getText().length());

        edHeight.setText(Double.toString(beaconsInfo.getHeight()));
        edHeight.setSelection(edHeight.getText().length());

        edMeasurePower.setText(Integer.toString(this.beaconsInfo.getMeasurePower()));
        edMeasurePower.setSelection(edMeasurePower.getText().length());

        edAlgorithm.setText(Integer.toString(this.beaconsInfo.getAlgorithm()));
        edAlgorithm.setSelection(edAlgorithm.getText().length());
    }
}
