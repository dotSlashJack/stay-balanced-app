package edu.staybalanced.staybalanced;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JackTesting extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor gyro;
    private SensorEventListener gyroListener;
    //private float[] gyroVals = new float[100];
    private ArrayList<Float> gyroVals;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jack_testing);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyro == null){
            Toast.makeText(this,"error in gyro", Toast.LENGTH_LONG).show();
            finish();
        }

        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                TextView t = findViewById(R.id.textView);
                //t.setText("ssssss");
                //Gyroscope g = new Gyroscope(sensorEvent);
                //t.setText(Arrays.toString(g.returnVals()));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView t = findViewById(R.id.textView);
        //Toast toast = Toast.makeText(getApplicationContext(), "sddssd", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), (String)"okokok",
        //        Toast.LENGTH_LONG).show();
        /*if(sensorEvent.values[2] > 0.5f){
            //TextView t = findViewById(R.id.textView);
            //String gyro_reading = String.valueOf(sensorEvent.values[2]);
            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
            //t.setText(gyro_reading);
            //t.setText()
        } else if(sensorEvent.values[2] < -.05f){

            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
            //TextView t = findViewById(R.id.textView);
            String gyro_reading = String.valueOf(sensorEvent.values[2]);

            //t.setText(gyro_reading);
            //index+=1;
        }*/
        /*if(sensorEvent.values[2]>0.05f || sensorEvent.values[2]<-0.05f){
            gyroVals[index] = sensorEvent.values[2];
            index+=1;
            t.setText(Arrays.toString(gyroVals));
        }*/
        //t.setText("ssssss");
        Gyroscope g = new Gyroscope(sensorEvent);
        //g.updateGyroVals(gyroVals);
        gyroVals = g.returnVals();

        StringBuilder str = new StringBuilder();
        for (Float v : gyroVals) {
            str.append(v.toString());
            str.append(" ");
        }
        t.setText(str);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}