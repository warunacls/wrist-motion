package wristmotion.scorelab.org.wristmotion.DataHandler;

import android.hardware.Sensor;
import android.util.SparseArray;

/**
 * Created by wasn on 7/20/15.
 */
public class SensorNames {


public SparseArray<String> snames;

    public SensorNames() {

        snames = new SparseArray<String>();


        snames.append(0, "Debug Sensor");
        snames.append(android.hardware.Sensor.TYPE_ACCELEROMETER, "Accelerometer");
        snames.append(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient temperatur");
        snames.append(Sensor.TYPE_GAME_ROTATION_VECTOR, "Game Rotation Vector");
        snames.append(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "Geomagnetic Rotation Vector");
        snames.append(Sensor.TYPE_GRAVITY, "Gravity");
        snames.append(Sensor.TYPE_GYROSCOPE, "Gyroscope");
        snames.append(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "Gyroscope (Uncalibrated)");
        snames.append(Sensor.TYPE_HEART_RATE, "Heart Rate");
        snames.append(Sensor.TYPE_LIGHT, "Light");
        snames.append(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration");
        snames.append(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field");
        snames.append(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "Magnetic Field (Uncalibrated)");
        snames.append(Sensor.TYPE_PRESSURE, "Pressure");
        snames.append(Sensor.TYPE_PROXIMITY, "Proximity");
        snames.append(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity");
        snames.append(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector");
        snames.append(Sensor.TYPE_SIGNIFICANT_MOTION, "Significant Motion");
        snames.append(Sensor.TYPE_STEP_COUNTER, "Step Counter");
        snames.append(Sensor.TYPE_STEP_DETECTOR, "Step Detector");


    }


    public String getName(int sensorId) {
        String name = snames.get(sensorId);

        if (name == null) {
            name = "Unknown";
        }

        return name;
    }

}
