package com.jmengxy.beaconlocationdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;
import com.jmengxy.utillib.utils.DisplayUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RadarActivity extends AppCompatActivity {

    private static final int IMAGE_WIDTH = 300;
    private static final int GRID_WIDTH = 50;
    private static final double DEFAULT_WEIGHT = 1.0;

    private Gson gson = new Gson();

    @BindView(R.id.radar_image)
    ImageView imageView;

    @BindView(R.id.coordinate)
    TextView tvCoordinate;

    private Canvas canvas;

    private Bitmap bitmap;

    private Paint paintAxisThick;

    private Paint paintAxisThin;

    private Paint paintText;

    private Paint paintCircle;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void init() {
        initPaints();
        initBroadcastReceiver();
        draw(null, null, DEFAULT_WEIGHT);
    }

    private void initBroadcastReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.BROADCAST_LOCATION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location location = (Location) intent.getSerializableExtra(BroadcastConstants.BROADCAST_KEY_LOCATION);
                double weight = intent.getDoubleExtra(BroadcastConstants.BROADCAST_KEY_WEIGHT, DEFAULT_WEIGHT);
                List<Base> baseList = gson.fromJson(intent.getStringExtra(BroadcastConstants.BROADCAST_KEY_BASES), new TypeToken<List<Base>>() {
                }.getType());

                if (baseList.size() > 3) {
                    baseList = baseList.subList(0, 3);
                }

                draw(location, baseList, weight);

                tvCoordinate.setText(location == null
                        ? "Current location: NULL"
                        : String.format("Current location: (%f, %f)",
                        location.getxAxis(), location.getyAxis()));
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private float convertDpToPixel(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / 160.0F);
        return px;
    }

    private void initPaints() {
        paintAxisThick = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAxisThick.setColor(Color.BLACK);
        paintAxisThick.setStrokeWidth(convertDpToPixel(3));

        paintAxisThin = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAxisThin.setColor(Color.GRAY);
        paintAxisThin.setStrokeWidth(convertDpToPixel(1));

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(DisplayUtils.convertSpToPixel(this, 8));

        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(Color.CYAN);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(convertDpToPixel(3));
    }

    private void draw(Location location, List<Base> bases, double weight) {
        drawPrepare();
        drawLines();
        drawLabels(weight);
        drawBases(bases, weight);
        drawLocation(location, weight);

        imageView.setImageBitmap(bitmap);
    }

    private void drawBases(List<Base> bases, double weight) {
        if (bases == null) {
            return;
        }

        for (Base base : bases) {
            Bitmap bitmap = drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_lens_orange_24dp));
            canvas.drawCircle(
                    convertDpToPixel(locationToCoordinate(base.getLocation().getxAxis(), weight)),
                    convertDpToPixel(IMAGE_WIDTH - locationToCoordinate(base.getLocation().getyAxis(), weight)),
                    convertDpToPixel(locationToCoordinate(base.getFlatDistance(), weight)),
                    paintCircle);

            canvas.drawBitmap(
                    bitmap,
                    convertDpToPixel(locationToCoordinate(base.getLocation().getxAxis(), weight) - 12),
                    convertDpToPixel(IMAGE_WIDTH - locationToCoordinate(base.getLocation().getyAxis(), weight) - 12),
                    paintAxisThin);
        }
    }

    private void drawPrepare() {
        bitmap = Bitmap.createBitmap(
                (int) convertDpToPixel(IMAGE_WIDTH),
                (int) convertDpToPixel(IMAGE_WIDTH),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    private void drawLines() {
        canvas.drawLine(0, 0, 0, convertDpToPixel(IMAGE_WIDTH), paintAxisThick);
        canvas.drawLine(0, 0, convertDpToPixel(IMAGE_WIDTH), 0, paintAxisThick);
        canvas.drawLine(0, convertDpToPixel(IMAGE_WIDTH), convertDpToPixel(IMAGE_WIDTH), convertDpToPixel(IMAGE_WIDTH), paintAxisThick);
        canvas.drawLine(convertDpToPixel(IMAGE_WIDTH), 0, convertDpToPixel(IMAGE_WIDTH), convertDpToPixel(IMAGE_WIDTH), paintAxisThick);

        for (int i = 1; i < 6; ++i) {
            canvas.drawLine(0, convertDpToPixel(GRID_WIDTH * i), convertDpToPixel(IMAGE_WIDTH), convertDpToPixel(GRID_WIDTH * i), paintAxisThin);
            canvas.drawLine(convertDpToPixel(GRID_WIDTH * i), 0, convertDpToPixel(GRID_WIDTH * i), convertDpToPixel(IMAGE_WIDTH), paintAxisThin);
        }
    }

    private void drawLabels(double weight) {
        for (int i = 1; i < 7; ++i) {
            canvas.drawText(String.format("%.1f", weight * i), convertDpToPixel(GRID_WIDTH * i - 18), convertDpToPixel(IMAGE_WIDTH - 5), paintText);
            canvas.drawText(String.format("%.1f", weight * i), convertDpToPixel(5), convertDpToPixel(IMAGE_WIDTH - GRID_WIDTH * i + 8), paintText);
        }
    }

    private void drawLocation(Location location, double weight) {
        if (location == null) {
            return;
        }

        Bitmap bitmap = drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_location_24dp));
        canvas.drawBitmap(
                bitmap,
                convertDpToPixel(locationToCoordinate(location.getxAxis(), weight) - 12),
                convertDpToPixel(IMAGE_WIDTH - locationToCoordinate(location.getyAxis(), weight) - 24),
                paintAxisThin);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private static float locationToCoordinate(double locationValue, double weight) {
        double v = locationValue / weight;
        return (float) (v * GRID_WIDTH);
    }
}
