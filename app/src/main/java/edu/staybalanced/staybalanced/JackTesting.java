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

public class JackTesting extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor gyro;
    private SensorEventListener gyroListener;

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
                t.setText("ssssss");
                if(sensorEvent.values[2] > 0.5f){
                    //TextView t = findViewById(R.id.textView);
                    t.setText("OOOWWOOO");
                } else if(sensorEvent.values[2] < -.05f){
                    //TextView t = findViewById(R.id.textView);
                    t.setText("reeee");
                    //Toast toast = Toast.makeText(getApplicationContext(), "sddssd", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), (String)"okokok",
                            Toast.LENGTH_LONG).show();
                }
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
        if(sensorEvent.values[2] > 0.5f){
            //TextView t = findViewById(R.id.textView);
            String gyro_reading = String.valueOf(sensorEvent.values[2]);
            t.setText(gyro_reading);
        } else if(sensorEvent.values[2] < -.05f){
            //TextView t = findViewById(R.id.textView);
            String gyro_reading = String.valueOf(sensorEvent.values[2]);
            t.setText(gyro_reading);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}