package wristmotion.scorelab.org.wristmotion.Handler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wristmotion.scorelab.org.wristmotion.DataHandler.TagData;
import wristmotion.scorelab.org.wristmotion.R;

/**
 * Created by wasn on 7/22/15.
 */
public class SensorGraphView extends View {


    private static final int CIRCLE_SIZE_ACCURACY_HIGH = 4;
    private static final int CIRCLE_SIZE_ACCURACY_MEDIUM = 10;
    private static final int CIRCLE_SIZE_ACCURACY_LOW = 20;
    private static final int MAX_DATA_SIZE = 300;

    private static final int CIRCLE_SIZE_DEFAULT = 4;

    private static final int NUM_DRAW_SENSOR = 20;
    private static final int NUM_RECT_PAINTS = 9;

    private ArrayList<Float>[] normalisedDataLines;
    private ArrayList<Integer>[] dataLineAccuracy;
    private ArrayList<Long>[] dataLineTimeStamps;


    private Paint[] rectPaints = new Paint[9];

    private Paint infoPaint;
    private Paint tagPaint;

    private LinkedList<TagData> tags = new LinkedList<>();
    private float zeroline = 0;
    private String maxValueLabel = "";
    private String minValueLabel = "";


    private boolean[] sensorGraphs = new boolean[6];
    private ArrayList<Float>[] setNormalisedDataLines;
    private int Accuracy;



    public SensorGraphView(Context context, AttributeSet atrib) {

        super(context, atrib);

        Resources res = context.getResources();

        rectPaints[0] = new Paint();
        rectPaints[0].setColor(res.getColor(R.color.graph_color_1));


        rectPaints[1] = new Paint();
        rectPaints[1].setColor(res.getColor(R.color.graph_color_2));

        rectPaints[2] = new Paint();
        rectPaints[2].setColor(res.getColor(R.color.graph_color_3));

        rectPaints[3] = new Paint();
        rectPaints[3].setColor(res.getColor(R.color.graph_color_4));

        rectPaints[4] = new Paint();
        rectPaints[4].setColor(res.getColor(R.color.graph_color_5));

        rectPaints[5] = new Paint();
        rectPaints[5].setColor(res.getColor(R.color.graph_color_6));

        rectPaints[6] = new Paint();
        rectPaints[6].setColor(res.getColor(R.color.graph_color_7));

        rectPaints[7] = new Paint();
        rectPaints[7].setColor(res.getColor(R.color.graph_color_8));

        rectPaints[8] = new Paint();
        rectPaints[8].setColor(res.getColor(R.color.graph_color_9));


        infoPaint = new Paint();
        infoPaint.setColor(res.getColor(R.color.graph_color_info));
        infoPaint.setTextSize(48f);
        infoPaint.setAntiAlias(true);

        tagPaint = new Paint();
        tagPaint.setColor(res.getColor(R.color.graph_color_info));
        tagPaint.setAntiAlias(true);

    }


    public void setsensorGraphs(boolean[] sensorGraphs) {

    }


    public void addNewDataLine(float normalised, int accuracy, int i, long timestamp) {
    }


    public void setDrawSensors(boolean[] sensorGraphs) {
        this.sensorGraphs = sensorGraphs;
        invalidate();
    }


    public void setNormalisedDataLines(ArrayList<Float>[] normalisedDataLines, ArrayList<Integer>[] dataLineAccuracy, ArrayList<Long>[] dataPointsTimeStamps, LinkedList<TagData> tags) {

        this.tags = tags;

        this.dataLineTimeStamps = dataPointsTimeStamps;


        for (int i = 0; i < this.dataLineTimeStamps.length; ++i) {
            if (this.dataLineTimeStamps[i].size() > MAX_DATA_SIZE) {

                List tmp = this.dataLineTimeStamps[i].subList(this.dataLineTimeStamps[i].size() - MAX_DATA_SIZE - 1, this.dataLineTimeStamps[i].size() - 1);
                this.dataLineTimeStamps[i] = new ArrayList<>();
                this.dataLineTimeStamps[i].addAll(tmp);
            }
        }


        this.setNormalisedDataLines = normalisedDataLines;

        for (int i = 0; i < this.setNormalisedDataLines.length; ++i) {
            if (this.setNormalisedDataLines[i].size() > MAX_DATA_SIZE) {

                List tmp = this.setNormalisedDataLines[i].subList(this.setNormalisedDataLines[i].size() - MAX_DATA_SIZE - 1, this.setNormalisedDataLines[i].size() - 1);
                this.setNormalisedDataLines[i] = new ArrayList<>();
                this.setNormalisedDataLines[i].addAll(tmp);
            }
        }

        this.dataLineAccuracy = dataLineAccuracy    ;


        for (int i = 0; i < this.dataLineAccuracy.length; ++i) {
            if (this.dataLineAccuracy[i].size() > MAX_DATA_SIZE) {

                List tmp = this.dataLineAccuracy[i].subList(this.dataLineAccuracy[i].size() - MAX_DATA_SIZE - 1, this.dataLineAccuracy[i].size() - 1);
                this.dataLineAccuracy[i] = new ArrayList<>();
                this.dataLineAccuracy[i].addAll(tmp);
            }
        }


        for (int i = 0; i < this.dataLineAccuracy.length; ++i) {


            ArrayList<Integer> temp = new ArrayList<>();
            for (Integer integer : this.dataLineAccuracy[i]) {

                temp.add(dataLineAccuracyToDotSize(integer));
            }
            this.dataLineAccuracy[i] = temp;

        }


        invalidate();
    }


    public void setMaxValueLabel(String maxValue) {
        this.maxValueLabel = maxValue;
    }

    public void setMinValueLabel(String minValue) {
        this.minValueLabel = minValue;
    }

    public void setZeroLine(float zeroline) {
        this.zeroline = zeroline;
    }


    public void addNewTag(TagData tagData) {
        this.tags.add(tagData);


        if (this.tags.size() > MAX_DATA_SIZE / 2) {
            this.tags.removeFirst();
        }
    }


    public void addNewDataLines(long point, int accuracy, int index, long timestamp) {
        if (index >= normalisedDataLines.length) {
            throw new ArrayIndexOutOfBoundsException("index too large!!");
        }


        this.dataLineTimeStamps[index].add(timestamp);

        if (this.dataLineTimeStamps[index].size() > MAX_DATA_SIZE) {
            this.dataLineTimeStamps[index].remove(0);
        }


        this.dataLineTimeStamps[index].add((long) point);

        if (this.normalisedDataLines[index].size() > MAX_DATA_SIZE) {
            this.normalisedDataLines[index].remove(0);
        }


        this.dataLineAccuracy[index].add(dataLineAccuracyToDotSize(accuracy));

        if (this.dataLineAccuracy[index].size() > MAX_DATA_SIZE) {
            this.dataLineAccuracy[index].remove(0);
        }


        invalidate();
    }

    private Integer dataLineAccuracyToDotSize(Integer integer) {


        switch (Accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                return CIRCLE_SIZE_ACCURACY_HIGH;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                return CIRCLE_SIZE_ACCURACY_MEDIUM;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                return CIRCLE_SIZE_ACCURACY_LOW;
            default:

                return CIRCLE_SIZE_DEFAULT;
        }
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (normalisedDataLines.length <= 0) {
            return;
        }


        int height = canvas.getHeight();
        int width = canvas.getWidth();

        float zeroLine = height - (height * zeroline);


        canvas.drawLine(0, zeroLine, width, zeroLine, infoPaint);
        if (zeroline < 0.8f && zeroline > 0.2f) {
            canvas.drawText("0", width - 70, zeroLine - 5, infoPaint);
        }

        canvas.drawText(maxValueLabel, width - 70, 60, infoPaint);
        canvas.drawText(minValueLabel, width - 70, height - 40, infoPaint);


        int maxValues = MAX_DATA_SIZE;


        int pointSpan = width / maxValues;

        boolean firstSensorDrawn = true;
        long previousTimeStamp = -1;
        float previousX = -1;
        float previousY = -1;
        for (int i = 0; i < this.normalisedDataLines.length; ++i) {

            if (!sensorGraphs[i]) {
                continue;
            }

            if (this.normalisedDataLines[i] == null) {
                continue;
            }
            int currentX = 0;//width - pointSpan;
            int index = 0;
            int lastDrawnTagIndex = -1;
            for (Float dataPoint : this.normalisedDataLines[i]) {


                float y = height - (height * dataPoint);


                canvas.drawCircle(currentX, y, dataLineAccuracy[i].get(index), rectPaints[i]);


                if (previousX != -1 && previousY != -1) {
                    canvas.drawLine(previousX, previousY, currentX, y, rectPaints[i]);

                }


                if (firstSensorDrawn) {

                    if (previousTimeStamp != -1) {
                        int nextIndexToDraw = findStartingIndexForTag(previousTimeStamp / 1000000, dataLineTimeStamps[i].get(index) / 1000000, lastDrawnTagIndex + 1);
                        if (nextIndexToDraw != -1) {
                            drawTag(canvas, this.tags.get(nextIndexToDraw), previousX + ((currentX - previousX) / 2));
                            lastDrawnTagIndex = nextIndexToDraw;
                        }
                    }
                    previousTimeStamp = dataLineTimeStamps[i].get(index);
                }

                previousX = currentX;
                previousY = y;

                currentX += pointSpan;
                ++index;
            }


            firstSensorDrawn = false;
            previousX = -1;
            previousY = -1;


        }

    }


    private void drawTag(Canvas canvas, TagData tags, float x) {
        canvas.drawRect(x - 3, 0 + 1, x + 3, canvas.getHeight() - 1, tagPaint);
    }


    private int findStartingIndexForTag(long startTimestamp, long endTimestamp, int startIndex) {



        for (int i = startIndex; i < this.tags.size(); ++i) {



            if (this.tags.get(i).getTimestamp() > startTimestamp && this.tags.get(i).getTimestamp() <= endTimestamp) {
                return i;
            }
        }


        return -1;
    }
}