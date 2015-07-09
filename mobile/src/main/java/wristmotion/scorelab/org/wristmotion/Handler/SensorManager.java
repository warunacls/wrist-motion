package wristmotion.scorelab.org.wristmotion.Handler;

/**
 * Created by wasn on 7/17/15.
 */



import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import wristmotion.scorelab.org.shared.ClientPaths;
import wristmotion.scorelab.org.shared.DataMapKeys;
import wristmotion.scorelab.org.wristmotion.DataHandler.SensorDataLine;
import wristmotion.scorelab.org.wristmotion.DataHandler.SensorNames;
import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;
import wristmotion.scorelab.org.wristmotion.DataHandler.TagData;
import wristmotion.scorelab.org.wristmotion.Event.Event_BusProvider;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorBuild;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorUpdate;
import wristmotion.scorelab.org.wristmotion.Event.Event_TagAdd;


public class SensorManager {

    private static final String TAG = "Wristmotion/wristmotion.MainHandler.SensorManager";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;



    public static SensorManager Instance;



    private Context context;
    private ExecutorService executorService;
    private SparseArray<Sensors> SensorMap;
    private ArrayList<Sensors> mySensors;
    private SensorNames sensorNames;
    private GoogleApiClient googleApiClient;


    public static synchronized SensorManager getInstance(Context context) {
        if ( Instance== null) {
            Instance = new SensorManager(context.getApplicationContext());
        }

        return Instance;
    }

    private SensorManager(Context context) {
        this.context = context;
        this.SensorMap = new SparseArray<Sensors>();
        this.mySensors = new ArrayList<Sensors>();
        this.sensorNames = new SensorNames();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();


        this.executorService= Executors.newCachedThreadPool();
    }



    public List<Sensors> getSensors(){

        return (List<Sensors>) mySensors.clone();
    }

    public Sensors getSensor(long id) {
        return SensorMap.get((int) id);
    }

   private Sensors createSensor(int id) {
       Sensors sensor = new Sensors(id, sensorNames.getName(id));

       mySensors.add(sensor);
        SensorMap.append(id, sensor);

        wristmotion.scorelab.org.wristmotion.Event.Event_BusProvider.postOnMainThread(new Event_SensorBuild(sensor));

        return sensor;
    }

    private Sensors getOrCreateSensor(int id) {
        Sensors sensor = SensorMap.get(id);

        if (sensor == null) {
            sensor = createSensor(id);
        }

        return sensor;
    }


    public synchronized void addSensorData(int sensorType, int accuracy, long timestamp, float[] values) {
        Sensors sensor = getOrCreateSensor(sensorType);


        SensorDataLine Dline= new SensorDataLine(timestamp, accuracy, values);

        sensor.addDataLine(Dline);

        Event_BusProvider.BusProvider.postOnMainThread(new Event_SensorUpdate(sensor,Dline));
    }

    public synchronized void addTag(String Tname) {
        TagData tag = new TagData(Tname, System.currentTimeMillis());
        this.tags.add(tag);


        Event_BusProvider.BusProvider.postOnMainThread(new Event_TagAdd(tag));
    }

    public LinkedList<TagData> getTags() {

        return (LinkedList<TagData>) tags.clone();
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

    public  void startMeasurement() {
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

   public void controlMeasurement(final String path) {
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

    public  void getNodes(ResultCallback<NodeApi.GetConnectedNodesResult> pCallback) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(pCallback);
    }


    public static void filterBySensorId(int id) {
    }



}



