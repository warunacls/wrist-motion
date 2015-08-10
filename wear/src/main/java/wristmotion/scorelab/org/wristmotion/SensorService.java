package wristmotion.scorelab.org.wristmotion;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wasn on 8/10/15.
 */
public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "wristmotion/SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;

    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_LIGHT = Sensor.TYPE_LIGHT;
    private final static int SENS_PRESSURE = Sensor.TYPE_PRESSURE;

    private final static int SENS_PROXIMITY = Sensor.TYPE_PROXIMITY;
    private final static int SENS_GRAVITY = Sensor.TYPE_GRAVITY;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;
    private final static int SENS_HUMIDITY = Sensor.TYPE_RELATIVE_HUMIDITY;

    private final static int SENS_AMBIENT_TEMPERATURE = Sensor.TYPE_AMBIENT_TEMPERATURE;
    private final static int SENS_MAGNETIC_FIELD_UNCALIBRATED = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
    private final static int SENS_GAME_ROTATION_VECTOR = Sensor.TYPE_GAME_ROTATION_VECTOR;
    private final static int SENS_GYROSCOPE_UNCALIBRATED = Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    private final static int SENS_SIGNIFICANT_MOTION = Sensor.TYPE_SIGNIFICANT_MOTION;
    private final static int SENS_STEP_DETECTOR = Sensor.TYPE_STEP_DETECTOR;
    private final static int SENS_STEP_COUNTER = Sensor.TYPE_STEP_COUNTER;
    private final static int SENS_GEOMAGNETIC = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
    private final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;

    SensorManager sensorManager;

    private Sensor heartrateSensor;

    private RecievingDevice client;
    private ScheduledExecutorService scheduler;

    @Override
    public void onCreate() {
        super.onCreate();

        client = RecievingDevice.getInstance(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Wristmotion");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("LongLogTag")
    protected void startMeasurement() {
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor ambientTemperatureSensor = sensorManager.getDefaultSensor(SENS_AMBIENT_TEMPERATURE);
        Sensor gameRotationVectorSensor = sensorManager.getDefaultSensor(SENS_GAME_ROTATION_VECTOR);
        Sensor geomagneticSensor = sensorManager.getDefaultSensor(SENS_GEOMAGNETIC);
        Sensor gravitySensor = sensorManager.getDefaultSensor(SENS_GRAVITY);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(SENS_GYROSCOPE);
        Sensor gyroscopeUncalibratedSensor = sensorManager.getDefaultSensor(SENS_GYROSCOPE_UNCALIBRATED);
       heartrateSensor = sensorManager.getDefaultSensor(SENS_HEARTRATE);
        Sensor heartrateSamsungSensor = sensorManager.getDefaultSensor(65562);
        Sensor lightSensor = sensorManager.getDefaultSensor(SENS_LIGHT);
        Sensor linearAccelerationSensor = sensorManager.getDefaultSensor(SENS_LINEAR_ACCELERATION);
        Sensor magneticFieldSensor = sensorManager.getDefaultSensor(SENS_MAGNETIC_FIELD);
        Sensor magneticFieldUncalibratedSensor = sensorManager.getDefaultSensor(SENS_MAGNETIC_FIELD_UNCALIBRATED);
        Sensor pressureSensor = sensorManager.getDefaultSensor(SENS_PRESSURE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(SENS_PROXIMITY);
        Sensor humiditySensor = sensorManager.getDefaultSensor(SENS_HUMIDITY);
        Sensor rotationVectorSensor = sensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);
        Sensor significantMotionSensor = sensorManager.getDefaultSensor(SENS_SIGNIFICANT_MOTION);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(SENS_STEP_COUNTER);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(SENS_STEP_DETECTOR);


        // Register the listener
        if (sensorManager != null) {
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }

            if (ambientTemperatureSensor != null) {
                sensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Ambient Temperature Sensor not found");
            }

            if (gameRotationVectorSensor != null) {
                sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Gaming Rotation Vector Sensor not found");
            }

            if (geomagneticSensor != null) {
                sensorManager.registerListener(this, geomagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Geomagnetic Sensor found");
            }

            if (gravitySensor != null) {
                sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gravity Sensor");
            }

            if (gyroscopeSensor != null) {
                sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }

            if (gyroscopeUncalibratedSensor != null) {
                sensorManager.registerListener(this, gyroscopeUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Uncalibrated Gyroscope Sensor found");
            }

            if (heartrateSensor != null) {
                final int measurementDuration   = 10;   // Seconds
                final int measurementBreak      = 5;    // Seconds

                scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "register Heartrate Sensor");
                                sensorManager.registerListener(SensorService.this, heartrateSensor, SensorManager.SENSOR_DELAY_NORMAL);

                                try {
                                    Thread.sleep(measurementDuration * 1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
                                }

                                Log.d(TAG, "unregister Heartrate Sensor");
                                sensorManager.unregisterListener(SensorService.this, heartrateSensor);
                            }
                        }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);

            } else {
                Log.d(TAG, "No Heartrate Sensor found");
            }

            if (heartrateSamsungSensor != null) {
                sensorManager.registerListener(this, heartrateSamsungSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Log.d(TAG, "Samsungs Heartrate Sensor not found");
            }

            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Light Sensor found");
            }

            if (linearAccelerationSensor != null) {
                sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Linear Acceleration Sensor found");
            }

            if (magneticFieldSensor != null) {
                sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Magnetic Field Sensor found");
            }

            if (magneticFieldUncalibratedSensor != null) {
                sensorManager.registerListener(this, magneticFieldUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No uncalibrated Magnetic Field Sensor found");
            }

            if (pressureSensor != null) {
                sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Pressure Sensor found");
            }

            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Proximity Sensor found");
            }

            if (humiditySensor != null) {
                sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Humidity Sensor found");
            }

            if (rotationVectorSensor != null) {
                sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }

            if (significantMotionSensor != null) {
                sensorManager.registerListener(this, significantMotionSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Significant Motion Sensor found");
            }

            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Counter Sensor found");
            }

            if (stepDetectorSensor != null) {
                sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Detector Sensor found");
            }
        }
    }

    private void stopMeasurement() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (scheduler != null && !scheduler.isTerminated()) {
            scheduler.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
