package wristmotion.scorelab.org.wristmotion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseLongArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import wristmotion.scorelab.org.shared.DataMapKeys;

/**
 * Created by wasn on 8/10/15.
 */
public class RecievingDevice {
    private static final String TAG = "wristmotion/DeviceClient";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;

    public static RecievingDevice instance;

    public static RecievingDevice getInstance(Context context) {
        if (instance == null) {
            instance = new RecievingDevice(context.getApplicationContext());
        }

        return instance;
    }

    private Context context;
    private GoogleApiClient googleApiClient;
    private ExecutorService executorService;
    private int filterId;

    private SparseLongArray lastSensorData;

    private RecievingDevice(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();

        executorService = Executors.newCachedThreadPool();
        lastSensorData = new SparseLongArray();
    }

    @SuppressLint("LongLogTag")
    public void setSensorFilter(int filterId) {
        Log.d(TAG, "Now filtering by sensor: " + filterId);

        this.filterId = filterId;
    }

    public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        long t = System.currentTimeMillis();

        long lastTimestamp = lastSensorData.get(sensorType);
        long timeAgo = t - lastTimestamp;

        if (lastTimestamp != 0) {
            if (filterId == sensorType && timeAgo < 100) {
                return;
            }

            if (filterId != sensorType && timeAgo < 3000) {
                return;
            }
        }

        lastSensorData.put(sensorType, t);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sendSensorDataInBackground(sensorType, accuracy, timestamp, values);
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void sendSensorDataInBackground(int sensorType, int accuracy, long timestamp, float[] values) {
        if (sensorType == filterId) {
            Log.i(TAG, "Sensor " + sensorType + " = " + Arrays.toString(values));
        } else {
            Log.d(TAG, "Sensor " + sensorType + " = " + Arrays.toString(values));
        }

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);

        dataMap.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);
        dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
        dataMap.getDataMap().putFloatArray(DataMapKeys.VALUES, values);

        PutDataRequest putDataRequest = dataMap.asPutDataRequest();
        send(putDataRequest);
    }

    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    private void send(PutDataRequest putDataRequest) {
        if (validateConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.v(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }
}
