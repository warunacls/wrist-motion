import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wasn on 7/7/15.
 */
public class SensorManager {

    private static final String TAG = "Wristmotion/SensorManager";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;


    private SensorManager Myinstance;



    private Context context;
    private ExecutorService executorService;
    private SparseArray<Sensor> SensorMap;
    private ArrayList<Sensor> mySensors;
    private SensorNames sensorNames;
    private GoogleApiClient googleApiClient;

    public static synchronized SensorManager getInstance(Context context) {
        if ( instance== null) {
            instance = new SensorManager(context.getApplicationContext());
        }

        return instance;
    }

    private SensorManager(Context context) {
        this.context = context;
        this.SensorMap = new SparseArray<Sensor>();
        this.mySensors = new ArrayList<Sensor>();
        this.sensorNames = new SensorNames();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();


        this.executorService= Executor.newCachedThreadPool();
    }



public List<Sensor> getSensors(){

    return (List<Sensor>) Sensor.clone();
}

    public Sensor getSensor(long id) {
        return SensorMap.get((int) id);
    }

    private Sensor createSensor(int id) {
        Sensor sensor = new Sensor(id, sensorNames.getName(id));

       sensor.add(sensor);
       SensorMap.append(id, sensor);

        wristmotion.scorelab.org.wristmotion.BusProvider.postOnMainThread(new NewSensorEvent(sensor));

        return sensor;
    }

    private Sensor getOrCreateSensor(int id) {
        Sensor sensor = SensorMap.get(id);

        if (sensor == null) {
            sensor = createSensor(id);
        }

        return sensor;
    }


    public synchronized void addSensorData(int sensorType, int accuracy, long timestamp, float[] values) {
        Sensor sensor = getOrCreateSensor(sensorType);

        SensorDataPoint dataPoint = new SensorDataPoint(timestamp, accuracy, values);

        sensor.addDataPoint(dataPoint);

        wristmotion.scorelab.org.wristmotion.BusProvider.postOnMainThread(new SensorUpdatedEvent(sensor, dataPoint));
    }


    private boolean checkConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }
        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }


    public void dataFilter(final int sensorId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dataFilterInBackground(sensorId);
            }

        });
    };

    private void dataFilterInBackground(final int sensorId) {
        Log.d(TAG, "filterd Sensor(" + sensorId + ")");

        if (checkConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create("/filter");

            dataMap.getDataMap().putInt(DataMapKeys.FILTER, sensorId);
            dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, System.currentTimeMillis());

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {


                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Filter by sensor " + sensorId + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    public void startMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurement(ClientPaths.START_MEASUREMENT);
            }
        });
    }

    public void stopMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurement(ClientPaths.STOP_MEASUREMENT);
            }
        });
    }

    private void controlMeasurement(final String path) {
        if (checkConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            Log.w(TAG, "No connection possible");
        }
    }



}



