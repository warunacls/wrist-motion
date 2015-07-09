package wristmotion.scorelab.org.wristmotion;

import android.hardware.Sensor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private SensorManager sensorManager;
    private ViewPager viewPager;
    private TextView view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        view = (TextView) findViewById(R.id.view);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) viewPager.getAdapter();
                if (adapter != null) {
                    Sensor sensor = adapter.getItemObject();
                    if (sensor != null) {
                        SensorManager.filterBySensorId((int) sensor.getId());

                    }


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    protected void OnResume(){

        super.onResume();
         wristmotion.scorelab.org.wristmotion.BusProvider.getInstance().register(this);

    List<Sensor> sensors = SensorManager.getInstance(this).getSensors();
    viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));

    if (sensors.size() > 0) {
       view.setVisibility(View.GONE);
    }
    else
    {
        view.setVisibility(View.VISIBLE);
    }

    sensorManager.startMeasurement();

    }

    protected  void OnPause(){


        super.onPause();
        wristmotion.scorelab.org.wristmotion.BusProvider.getInstance().unregister(this);

        SensorManager.stopMeasurement();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Sensor> sensors;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Sensor> symbol) {
            super(fm);

            this.sensors = symbol;
        }

        public void addNewSensor(Sensor sensor) {
            this.sensors.add(sensor);

        }


        @Override
        public int getCount() {
            return sensors.size();
        }

        public Sensor getItemObject() {
            return sensors.get(position);

        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SensorFragment.newInstance(sensors.get(position).getId());
        }

        private void notifyUSerForNewSensor(Sensor sensor) {
            Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
        }

        public void onNewSensorEvent(final NewSensorEvent event) {
            ((ScreenSlidePagerAdapter) viewPager.getAdapter()).addNewSensor(event.getSensor());
           viewPager.getAdapter().notifyDataSetChanged();
            view.setVisibility(View.GONE);
            notifyUSerForNewSensor(event.getSensor());
        }


    }
}
