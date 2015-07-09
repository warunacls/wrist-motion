package wristmotion.scorelab.org.wristmotion;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView textView;
    private SendData dataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                textView = (TextView) stub.findViewById(R.id.text);
            }


        });

        dataClient = SendData.getInstance(this);
    }


    public void indicate(View view) {
        dataClient.SensorData(0, 1,

                System.currentTimeMillis(),
                new float[]{1.0f}
        );

    }
}
