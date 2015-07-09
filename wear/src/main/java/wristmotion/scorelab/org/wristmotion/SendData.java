package wristmotion.scorelab.org.wristmotion;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import wristmotion.scorelab.org.shared.DataMapKeys;




/**
 * Created by wasn on 7/16/15.
 */
public class SendData {

    private static final String TAG = "Wristmotion/SendData";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;


    public static SendData Instance;


    public static SendData getInstance(Context context) {
        if (Instance == null) {
            Instance = new SendData(context.getApplicationContext());
        }

        return Instance;
    }


    private Context context;
    private GoogleApiClient googleApiClient = new Builder(context)
                      .addApi(Wearable.API)
                   .build();
    private ExecutorService executorService;

    public SendData(Context context) {

        this.context = context;


        executorService = Executors.newCachedThreadPool();
    }

    private boolean checkConnection() {

        if (googleApiClient.isConnected()) {

            return true;

        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);


        return result.isSuccess();

    }


    public void SensorData(int sensorType, int accuracy, long timestamp, float[] values) {

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/sensors/" + sensorType);

        dataMapRequest.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);
        dataMapRequest.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
        dataMapRequest.getDataMap().putFloatArray(DataMapKeys.VALUES, values);

        PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
        send(putDataRequest);


    }

    private void send(PutDataRequest putDataRequest) {
        if (checkConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Data is send: " + dataItemResult.getStatus().isSuccess());
                }
            });


        }
    }
}