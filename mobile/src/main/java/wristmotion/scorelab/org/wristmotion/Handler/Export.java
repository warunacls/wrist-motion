package wristmotion.scorelab.org.wristmotion.Handler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toolbar;
import android.os.Handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import io.realm.Realm;
import io.realm.RealmResults;

import wristmotion.scorelab.org.wristmotion.Data.data;
import wristmotion.scorelab.org.wristmotion.DataHandler.TagData;
import android.R;




/**
 * Created by wasn on 7/24/15.
 */
public class Export extends AppCompatActivity {

    private Realm realmDB;
    private ProgressBar dataProgressbar;
    private ProgressBar tagProgressbar;


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.export);

        dataProgressbar = (ProgressBar) findViewById(R.id.export_progress);
        tagProgressbar = (ProgressBar) findViewById(R.id.export_progress_tag);

        setSupportActionBar((Toolbar) findViewById(R.id.Tb));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Export Data");
        Button exportButton = (Button) findViewById(R.id.btn_export);
        exportButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {


                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                exportFile();
                            }
                        };

                        Thread t = new Thread(r);
                        t.start();
                    }
                }

        );


        findViewById(R.id.btn_export_tags).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                exportTagsFile();
                            }
                        };

                        Thread t = new Thread(r);
                        t.start();
                    }
                }

        );



        Button deleteButton = (Button) findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deleteData();
                            }
                        }).start();

                    }
                }
        );

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void deleteData() {

        realmDB = Realm.getInstance(this);
        realmDB.beginTransaction();

        RealmResults<data> result =  realmDB.where(data.class).findAll();
        Log.e("Wristmotion", "rows after delete = " + result.size());

        // Delete all matches
        result.clear();

        realmDB.commitTransaction();

        result =  realmDB.where(data.class).findAll();
        Log.e("Wristmotion", "rows after delete = " + result.size());
    }

    protected void onResume() {
        super.onResume();
    }



    private void exportTagsFile() {


        realmDB = Realm.getInstance(this);

        final String fileprefix = "export_tags_";
        final String date = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        final String filename = String.format("%s_%s.txt", fileprefix, date);

        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Wristmotion";

        final File logfile = new File(directory, filename);
        final File logPath = logfile.getParentFile();

        if (!logPath.isDirectory() && !logPath.mkdirs()) {
            Log.e("Wristmotiond", "Could not create directory for log files");
        }


        try {
            FileWriter filewriter = new FileWriter(logfile);
            BufferedWriter bw = new BufferedWriter(filewriter);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tagProgressbar.setMax(SensorManager.getInstance(Export.this).getTags().size());
                        tagProgressbar.setVisibility(View.VISIBLE);
                        tagProgressbar.setProgress(0);
                    }
                });

            int i = 0;
            for (TagData tag : SensorManager.getInstance(this).getTags()) {
                final int progress = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tagProgressbar.setProgress(progress);
                    }
                });
                ++i;
                bw.write(tag.getTagName() + ", " + tag.getTimestamp() + "\n");
            }
            bw.flush();
            bw.close();



            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tagProgressbar.setVisibility(View.GONE);
                        }
                    }, 1000);

                }
            });

            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("*/*");

            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Wristmotion data export");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logfile));
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));


            Log.i("Wristmotion", "export finished!");




        } catch (IOException e) {
            Log.e("Wristmotion", "IOException while writing Logfile");
        }
    }


    private void exportFile() {

      realmDB = Realm.getInstance(this);

        RealmResults<data> result = realmDB.where(data.class).findAll();
        final int total_row = result.size();
        final int total_col = 8;
        Log.i("Wristmotion", "total_row = " + total_row);
        final String fileprefix = "export";
        final String date = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        final String filename = String.format("%s_%s.txt", fileprefix, date);

        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Wristmotion";

        final File logfile = new File(directory, filename);
        final File logPath = logfile.getParentFile();

        if (!logPath.isDirectory() && !logPath.mkdirs()) {
            Log.e("Wristmotion", "Could not create directory for log files");
        }

        try {
            FileWriter filewriter = new FileWriter(logfile);
            BufferedWriter bw = new BufferedWriter(filewriter);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataProgressbar.setMax(total_row);
                    dataProgressbar.setVisibility(View.VISIBLE);
                    dataProgressbar.setProgress(0);
                }
            });



            for (int i = 1; i < total_row; i++) {
                final int progress = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataProgressbar.setProgress(progress);
                    }
                });


                StringBuffer sb = new StringBuffer(result.get(i).getAndroidDevice());
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).getTimestamp()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).get_x()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).get_y()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).get_z()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).getAccuracy()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).getDatasource()));
                sb.append(" ,");
                sb.append(String.valueOf(result.get(i).getDatatype()));
                sb.append("\n");
                bw.write(sb.toString());
            }
            bw.flush();
            bw.close();


            runOnUiThread(new Runnable() {

                public void run() {

                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            tagProgressbar.setVisibility(View.GONE);
                        }
                    }, 1000);

                }
            });




            Intent mailClient = new Intent(android.content.Intent.ACTION_SEND);
            mailClient.setType("*/*");

            mailClient.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wristmotion data export");
            mailClient.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logfile));
            startActivity(Intent.createChooser(mailClient, "Send mail..."));

            Log.i("Wristmotion", "export finished!");


        } catch (IOException e) {
            Log.e("Wristmotion", "IOException while writing Logfile");
        }


    }

}

