package wristmotion.scorelab.org.wristmotion;

/**
 * Created by wasn on 7/17/15.
 */

import android.R;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.squareup.otto.Subscribe;

import java.util.List;

import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;
import wristmotion.scorelab.org.wristmotion.Event.Event_BusProvider;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorBuild;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SensorManager sensormanager;
    private ViewPager viewPager;
    private TextView view;
    private NavigationView navigationView;
    private Menu navigationViewMenu;
    Toolbar toolbar;
    private List<Node> nodes;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.Tb);
        view = findViewById(R.id.View);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewMenu = navigationView.getMenu();

        initToolbar();
        initViewPager();

     sensormanager = SensorManager.getInstance(this);

        final EditText tagname = (EditText) findViewById(R.id.tag_name);


        findViewById(R.id.btn_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagnameText = "EMPTY";
                if (!tagname.getText().toString().isEmpty()) {
                    tagnameText = tagname.getText().toString();
                }

                SensorManager.getInstance(MainActivity.this).addTag(tagnameText);
            }
        });


        tagname.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(tagname
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);


                    return true;

                }
                return false;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(R.string.app_name);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_about:
                            startActivity(new Intent(MainActivity.this, Fragment.class));
                            return true;
                        case R.id.action_export:
                            startActivity(new Intent(MainActivity.this, Fragment.class));
                            return true;
                    }

                    return true;
                }
            });
        }
    }


    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int id) {
                ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) viewPager.getAdapter();
                if (adapter != null) {
                    Sensors sensor = adapter.getItemObject(id);
                    if (sensor != null) {
                        SensorManager.filterBySensorId((int) viewPager.getId());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

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


    protected void onResume() {
        super.onResume();
        Event_BusProvider.BusProvider.getInstance().register(this);
        List<Sensors> sensors = SensorManager.getInstance(this).getSensors();
     viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));

        if (sensors.size() > 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }


      sensormanager.startMeasurement();

       navigationViewMenu.clear();
      sensormanager.getNodes(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
          @Override
          public void onResult(final NodeApi.GetConnectedNodesResult pGetConnectedNodesResult) {
              nodes = pGetConnectedNodesResult.getNodes();
              for (Node node : nodes) {
                  SubMenu menu = navigationViewMenu.addSubMenu(node.getDisplayName());

                  MenuItem item = menu.add("15 sensors");
                  if (node.getDisplayName().startsWith("G")) {
                      item.setChecked(true);
                      item.setCheckable(true);
                  } else {
                      item.setChecked(false);
                      item.setCheckable(false);
                  }
              }
          }
      });
    }
    protected void onPause() {
        super.onPause();
        Event_BusProvider.BusProvider.getInstance().unregister(this);

    sensormanager.stopMeasurement();
    }

    public boolean onNavigationItemSelected(final MenuItem pMenuItem) {
        Toast.makeText(this, "Device: " + pMenuItem.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Sensors> sensors;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Sensors> symbol) {
            super(fm);

            this.sensors = symbol;
        }





        private Sensors getItemObject(int position) {
            return sensors.get(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SensorFragment.newInstance(sensors.get(position).getID());
        }

        @Override
        public int getCount() {
            return sensors.size();
        }

        public void addNewSensor(Sensors sensor) {
            this.sensors.add(sensor);
        }
    }


    private void notifyUSerForNewSensor(Sensors sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onNewSensorEvent(final Event_SensorBuild event) {
        ((ScreenSlidePagerAdapter) viewPager.getAdapter()).addNewSensor(event.getSensor());
        viewPager.getAdapter().notifyDataSetChanged();
        view.setVisibility(View.GONE);
        notifyUSerForNewSensor(event.getSensor());
    }


}
