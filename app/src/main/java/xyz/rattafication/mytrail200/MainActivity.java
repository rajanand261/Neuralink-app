package xyz.rattafication.mytrail200;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivityTag";

    // COMM SDK handles
    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;
    private SharedPreferences mypref;
    SharedPreferences.Editor myEditor;
    private static final int MSG_UPDATE_STATE = 1002;

    // canned data variables


    private Button neurosky_connect_btn;
    private Button neuro_stop;
    private Button btn_reset;
    private TextView attValue;
    private TextView medVlaue;
    private ImageView double_blink;
    public TextView tv_mode;
    private TextView sqText;
    private TextView tv_cnt_state;
    private TextView tv_raw;
    private LinearLayout wave_layout1;
    private ImageView blink_image;
    private Button controller_cnt;
    private long previous_click_time;
    private long previous_click_time1;
    private long current_click_time;
    private long lapsed_time;
    private volatile boolean running;
    private ImageView iv3;//bulb
    private ImageView iv4;//bulb1
    private ImageView iv1;//fan
    private ImageView iv2;//tv

    private boolean start_roll;
    private int i;//low sampler count
    private int j; //progressbar db count
    private ProgressBar progressBar;
    private  CountDownTimer mCountDownTimer;
    private  CountDownTimer nCountDownTimer;
    private  boolean tunning;//for timer
    private ProgressBar progressbarAtt;
    private ProgressBar progressBar2;
    private int flag;
    private int k;//progressbar selection
    private boolean raw_unlock;
    private boolean bypass;
    private boolean main;
    private int device;
    private boolean device_selected;
    private int m;//used in selection stop counter
    private boolean Red_on;
    private boolean Green_on;
    private boolean Fan_on;
    private boolean Tv_on;
    private boolean att_feed;
    private int att_data;
    private long current_click_time1;
    private long lapsed_time1;
    private boolean click_off;
    private MediaPlayer mediaPlayer;
    private controller mycontroller;
    private int cmd;
    private Button Hc_dis;
    private boolean connecting;
    private ImageView iv_connection;
    private ImageView iv_sdk_state;
    private int sdk_state;
    private boolean isController_cnt;
    private ImageView iv_bulb1;
    private ImageView iv_bulb2;
    private ImageView iv_fan;
    private ImageView iv_tv;
    private int mainkill_counter;
    private boolean mainkill;
    private Button main_regain;
    private ImageView iv_mainkill_indicator;
    private ImageView controller_indicator;
    private int o;
    private long current_click_time2;
    private long lapsed_time2;
    private long previous_click_time2;
    private boolean neuro_cnted;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_view);
        mypref= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        myEditor = mypref.edit();
        mycontroller = new controller();




        neurosky_connect_btn=findViewById(R.id.btn_neur);
        neuro_stop=findViewById(R.id.btn_stop);
        btn_reset=findViewById(R.id.btn_reset);
        controller_cnt=findViewById(R.id.btn_hc);
        progressBar2=findViewById(R.id.progressBar2);
        Hc_dis=findViewById(R.id.hc_dis);
        iv_connection=findViewById(R.id.iv_connection);
        iv_connection.setVisibility(View.INVISIBLE);
        iv_sdk_state=findViewById(R.id.iv_sdk_state);
        iv_sdk_state.setVisibility(View.INVISIBLE);
        iv_mainkill_indicator=findViewById(R.id.iv_mainkill_state);
        iv_mainkill_indicator.setVisibility(View.INVISIBLE);
        controller_indicator=findViewById(R.id.iv_module_state);


        previous_click_time=0;
        previous_click_time1=0;
        i=0;
        j=0;
        k=0;
        start_roll=false;
        tunning=true;
        raw_unlock=true;
        bypass=false;
        main=true;
        click_off=true;
        isController_cnt=false;
        mainkill_counter=0;
        mainkill=false;
        o=0;
        neuro_cnted=false;


        attValue=findViewById(R.id.tv_att);
        sqText=findViewById(R.id.tv_sq);
        progressbarAtt=findViewById(R.id.progressBar_att);
        medVlaue = findViewById(R.id.tv_med);
        tv_mode=findViewById(R.id.tv_mode);
        tv_cnt_state=findViewById(R.id.tv_cnt_state);
        tv_raw = findViewById(R.id.tv_raw);
        wave_layout1 = findViewById(R.id.wave_layout1);
        blink_image=findViewById(R.id.imageView);
        double_blink=findViewById(R.id.imageView2);
        iv1=findViewById(R.id.iv3);
        iv2=findViewById(R.id.iv4);
        iv3=findViewById(R.id.iv5);
        iv4=findViewById(R.id.iv6);
        progressBar=findViewById(R.id.progressBar);
        iv_bulb1=findViewById(R.id.iv_bulb1);
        iv_bulb2=findViewById(R.id.iv_bulb2);
        iv_fan=findViewById(R.id.iv_fan);
        iv_tv=findViewById(R.id.iv_tv);
        main_regain=findViewById(R.id.btn_extra);

        setUpDrawWaveView();
        initControlPanel();



        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Please enable your Bluetooth and re-run this program !", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error:" + e.getMessage());
        }

        neurosky_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!neuro_cnted) {
                    tgStreamReader = new TgStreamReader(mBluetoothAdapter, callback);

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                        // Prepare for connecting
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }

                    tgStreamReader.connect();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Brainsense already connected proceed with operation",Toast.LENGTH_SHORT).show();
                }
            }
        });

        neuro_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(neuro_cnted) {

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                        // Prepare for disconnection
                        tgStreamReader.stop();
                        tgStreamReader.close();
                        iv_connection.setImageResource(R.mipmap.nosignal_v1);
                        connecting = false;
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Connect before you try to disconnect",Toast.LENGTH_SHORT).show();
                }

            }
        });

        main_regain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Main regained, Control now",Toast.LENGTH_SHORT).show();
                mainkill=false;
                mainkill_counter=0;
                iv_mainkill_indicator.setVisibility(View.INVISIBLE);
            }
        });

        controller_cnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mycontroller.BTconnect();
                if(mycontroller.c_connected) {
                    controller_indicator.setImageResource(R.mipmap.device_on);
                    Toast.makeText(getApplicationContext(),"Controller connected now connect Brainsense",Toast.LENGTH_SHORT).show();
                    isController_cnt = true;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Make sure Module is Turned on then connect",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Hc_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mycontroller.c_connected) {
                    mycontroller.BTdisconnect();
                    isController_cnt = false;
                    controller_indicator.setImageResource(R.mipmap.device_off);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Module already not connected",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putBoolean("Green_on",false);
                myEditor.putBoolean("Red_on",false);
                myEditor.putBoolean("Fan_on",false);
                myEditor.putBoolean("Tv_on",false);
                myEditor.apply();
                initControlPanel();
                if(isController_cnt){
                    cmd=16;
                    cmd_evaluator();
                }

            }
        });


    }


    DrawWaveView rawView = null;

    public void setUpDrawWaveView() {
        mycontroller.BTinit();
        rawView = new DrawWaveView(getApplicationContext());
        wave_layout1.addView(rawView, new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        rawView.setValue(4000, 5000, -5000);
    }



    public void updateWaveView(int data){
        if (rawView != null) {
            rawView.updateData(data);
        }
    }

    public void mainkill(int data){
        if(o==2) {
            if ((data > 1500) || (data < -1500)) {
                current_click_time2=System.currentTimeMillis();
                if(previous_click_time2!=0) {
                    lapsed_time2=current_click_time2-previous_click_time2;
                    if(lapsed_time2>50) {
                        mainkill_counter = mainkill_counter + 1;
                        medVlaue.setText(String.valueOf(mainkill_counter));
                        if (mainkill_counter > 50) {
                            mainkill = true;
                            iv_mainkill_indicator.setVisibility(View.VISIBLE);
                            iv_mainkill_indicator.setImageResource(R.mipmap.device_off);
                            Toast.makeText(getApplicationContext(), "wear the brainsense properly then press Main R, As main is off", Toast.LENGTH_LONG).show();
                            mainkill_counter = 0;
                        }
                    }
                }
                previous_click_time2=current_click_time2;
            }
            o=0;
        }
        else{
            o++;
        }
    }



    public void initControlPanel(){
        Red_on = mypref.getBoolean("Red_on", false);
        Green_on = mypref.getBoolean("Green_on", false);
        Fan_on = mypref.getBoolean("Fan_on", false);
        Tv_on = mypref.getBoolean("Tv_on", false);
        if(Red_on){
            iv_bulb1.setImageResource(R.mipmap.device_on);
        }
        else{
            iv_bulb1.setImageResource(R.mipmap.device_off);
        }
        if(Green_on){
            iv_bulb2.setImageResource(R.mipmap.device_on);
        }
        else{
            iv_bulb2.setImageResource(R.mipmap.device_off);
        }
        if(Fan_on){
            iv_fan.setImageResource(R.mipmap.device_on);
        }
        else{
            iv_fan.setImageResource(R.mipmap.device_off);
        }
        if(Tv_on){
            iv_tv.setImageResource(R.mipmap.device_on);
        }
        else{
            iv_tv.setImageResource(R.mipmap.device_off);
        }
    }



    public void blinkdetector(int data){
        if(!mainkill) {
            if (main) {
                tv_mode.setText("standby");

                if ((data > 600) || (data < -600)) {
                    tv_mode.setText("Dblink");
                    blink_image.setImageResource(R.mipmap.blink_on);
                    if (tunning) {
                        progressbar_db();
                    }
                    db_detector();
                } else {
                    blink_image.setImageResource(R.mipmap.blink_off);
                    double_blink.setImageResource(R.mipmap.blink_off);
                }
            } else if (bypass) {
                if (click_off) {
                    previous_click_time1 = 0;
                }
                if ((data > 400) || (data < -400)) {
                    current_click_time1 = System.currentTimeMillis();
                    click_off = false;
                    if (previous_click_time1 != 0) {
                        lapsed_time1 = current_click_time1 - previous_click_time1;
                        if (lapsed_time1 > 1000) {
                            device_selected = true;
                            device = flag;
                            tv_mode.setText("focus");
                            running = false;
                            click_off = true;
                            cmd = 1;
                            cmd_evaluator();
                            cmd = 3;
                            cmd_evaluator();
                            cmd = 5;
                            cmd_evaluator();
                            cmd = 7;
                            cmd_evaluator();

                           // tv_raw.setText(String.valueOf(flag));
                            if ((flag == 0) || (flag == 1)){
                                iv2.setVisibility(View.INVISIBLE);
                                iv3.setVisibility(View.INVISIBLE);
                                iv4.setVisibility(View.INVISIBLE);
                                cmd = 0;
                                cmd_evaluator();

                            } else if ((flag == 2) || (flag == 3)){
                                iv1.setVisibility(View.INVISIBLE);
                                iv3.setVisibility(View.INVISIBLE);
                                iv4.setVisibility(View.INVISIBLE);
                                cmd = 2;
                                cmd_evaluator();
                            } else if ((flag == 4) || (flag == 5)){
                                iv1.setVisibility(View.INVISIBLE);
                                iv2.setVisibility(View.INVISIBLE);
                                iv4.setVisibility(View.INVISIBLE);
                                cmd = 4;
                                cmd_evaluator();
                            } else if ((flag == 6) || (flag == 7)){
                                iv1.setVisibility(View.INVISIBLE);
                                iv2.setVisibility(View.INVISIBLE);
                                iv3.setVisibility(View.INVISIBLE);
                                cmd = 6;
                                cmd_evaluator();
                            }
                            else {

                            }
                            att_feed = true;
                            bypass = false;
                            att_read();
                        } else {
                            double_blink.setImageResource(R.mipmap.blink_off);
                        }
                    }
                    previous_click_time1 = current_click_time1;

                } else {
                    tv_mode.setText("command");
                    blink_image.setImageResource(R.mipmap.blink_off);
                }
            } else {
                tv_mode.setText("focus");
            }
        }
    }


    public void db_detector(){
        current_click_time = System.currentTimeMillis();
        if(previous_click_time!=0){
            lapsed_time=current_click_time-previous_click_time;
           // tv.setText(String.valueOf(lapsed_time));
            if((lapsed_time<2600)&&(lapsed_time>600)){
                mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.blink_init);
                mediaPlayer.start();
                mediaPlayer.stop();
                mediaPlayer.release();
                j=0;
                progressBar.setProgress(0);
                progressBar.setVisibility(View.INVISIBLE);
                main=false;
                tunning=true;
                bypass=true;
                double_blink.setImageResource(R.mipmap.blink_on);
                tv_mode.setText("command");
                running=true;
                mCountDownTimer.cancel();
                selection_shuffle();


            }
            else{
                double_blink.setImageResource(R.mipmap.blink_off);
            }
        }
        previous_click_time=current_click_time;
    }

    public void progressbar_db(){
        mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.blink_init);
        mediaPlayer.start();
        tunning=false;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(j);
        mCountDownTimer=new CountDownTimer(2000,10) {

            @Override
            public void onTick(long millisUntilFinished) {
                j++;
                progressBar.setProgress((int)j*100/(2000/10));
            }

            @Override
            public void onFinish() {
                j=0;
                progressBar.setProgress(0);
                progressBar.setVisibility(View.INVISIBLE);
                tunning=true;
                tv_mode.setText("standby");
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        };
        mCountDownTimer.start();
    }


   /* public void progressbar_selection(){
        previous_click_time=0;
        progressBar2.setVisibility(View.VISIBLE);
        k=0;
        progressBar2.setProgress(k);
        nCountDownTimer=new CountDownTimer(10000,10) {

            @Override
            public void onTick(long millisUntilFinished) {
                k++;
                progressBar2.setProgress((int)k*100/(10000/10));
            }

            @Override
            public void onFinish() {
                k=0;
                progressBar2.setProgress(0);
                progressBar2.setVisibility(View.INVISIBLE);
                running=false;
                main=true;
                iv3.setVisibility(View.INVISIBLE);
                iv4.setVisibility(View.INVISIBLE);
                iv5.setVisibility(View.INVISIBLE);

            }
        };
        nCountDownTimer.start();
    }*/


    public void selection_shuffle(){
        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
        iv3.setVisibility(View.VISIBLE);
        iv4.setVisibility(View.VISIBLE);
        m=0;
        device_selected=false;
        Red_on = mypref.getBoolean("Red_on", false);
        Green_on = mypref.getBoolean("Green_on", false);
        Fan_on = mypref.getBoolean("Fan_on", false);
        Tv_on = mypref.getBoolean("Tv_on", false);
        new Thread(new Runnable() {
                public void run() {
                    while (running) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                if(device_selected){
                                    flag=device;
                                }
                                else{
                                    cmd=0;
                                    cmd_evaluator();
                                    cmd=7;
                                    cmd_evaluator();

                                    if(Red_on){
                                        iv1.setImageResource(R.mipmap.selected_bulb_red_on);
                                        flag=1;
                                    }
                                    else{
                                        iv1.setImageResource(R.mipmap.selected_bulb_red_off);
                                        flag=0;
                                    }
                                    if(Green_on){
                                        iv2.setImageResource(R.mipmap.blub_green_on);

                                    }
                                    else{
                                        iv2.setImageResource(R.mipmap.bulb_green_off);

                                    }
                                    if(Fan_on){
                                        iv3.setImageResource(R.mipmap.fan_on);
                                    }
                                    else{
                                        iv3.setImageResource(R.mipmap.fan_off);
                                    }
                                    if(Tv_on){
                                        iv4.setImageResource(R.mipmap.tv_on);
                                    }
                                    else{
                                        iv4.setImageResource(R.mipmap.tv_off);
                                    }
                                }
                            }
                        });

                        if(!device_selected){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                if(device_selected){
                                    flag=device;
                                }
                                else{
                                    cmd=1;
                                    cmd_evaluator();
                                    cmd=2;
                                    cmd_evaluator();

                                    if(Red_on){
                                        iv1.setImageResource(R.mipmap.bulb_red_on);

                                    }
                                    else{
                                        iv1.setImageResource(R.mipmap.bulb_red_off);

                                    }
                                    if(Green_on){
                                        iv2.setImageResource(R.mipmap.selected_bulb_green_on);
                                        flag=3;
                                    }
                                    else{
                                        iv2.setImageResource(R.mipmap.selected_bulb_green_off);
                                        flag=2;
                                    }
                                    if(Fan_on){
                                        iv3.setImageResource(R.mipmap.fan_on);
                                    }
                                    else{
                                        iv3.setImageResource(R.mipmap.fan_off);
                                    }
                                    if(Tv_on){
                                        iv4.setImageResource(R.mipmap.tv_on);
                                    }
                                    else{
                                        iv4.setImageResource(R.mipmap.tv_off);
                                    }
                                }
                            }
                        });
                        if(!device_selected){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                if(device_selected){
                                    flag=device;
                                }
                                else{
                                    cmd=3;
                                    cmd_evaluator();
                                    cmd=4;
                                    cmd_evaluator();

                                    if(Red_on){
                                        iv1.setImageResource(R.mipmap.bulb_red_on);
                                    }
                                    else{
                                        iv1.setImageResource(R.mipmap.bulb_red_off);
                                    }
                                    if(Green_on){
                                        iv2.setImageResource(R.mipmap.blub_green_on);
                                    }
                                    else{
                                        iv2.setImageResource(R.mipmap.bulb_green_off);
                                    }
                                    if(Fan_on){
                                        iv3.setImageResource(R.mipmap.selected_fan_on);
                                        flag=5;
                                    }
                                    else{
                                        iv3.setImageResource(R.mipmap.selected_fan_off);
                                        flag=4;
                                    }
                                    if(Tv_on){
                                        iv4.setImageResource(R.mipmap.tv_on);
                                    }
                                    else{
                                        iv4.setImageResource(R.mipmap.tv_off);
                                    }

                                }

                            }
                        });
                        if(!device_selected){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                if(device_selected){
                                    flag=device;
                                }else {
                                    cmd=5;
                                    cmd_evaluator();
                                    cmd=6;
                                    cmd_evaluator();

                                    if(Red_on){
                                        iv1.setImageResource(R.mipmap.bulb_red_on);
                                    }
                                    else{
                                        iv1.setImageResource(R.mipmap.bulb_red_off);
                                    }
                                    if(Green_on){
                                        iv2.setImageResource(R.mipmap.blub_green_on);
                                    }
                                    else{
                                        iv2.setImageResource(R.mipmap.bulb_green_off);
                                    }
                                    if(Fan_on){
                                        iv3.setImageResource(R.mipmap.fan_on);
                                    }
                                    else{
                                        iv3.setImageResource(R.mipmap.fan_off);
                                    }
                                    if(Tv_on){
                                        iv4.setImageResource(R.mipmap.selected_tv_on);
                                        flag=7;
                                    }
                                    else{
                                        iv4.setImageResource(R.mipmap.selected_tv_off);
                                        flag=6;
                                    }

                                }

                            }
                        });
                        if(!device_selected){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        if(!device_selected){
                            m=m+1;
                        }
                        if(m==3){
                            cmd=1;
                            cmd_evaluator();
                            cmd=3;
                            cmd_evaluator();
                            cmd=5;
                            cmd_evaluator();
                            cmd=7;
                            cmd_evaluator();
                            running=false;
                            iv1.setVisibility(View.INVISIBLE);
                            iv2.setVisibility(View.INVISIBLE);
                            iv3.setVisibility(View.INVISIBLE);
                            iv4.setVisibility(View.INVISIBLE);
                            bypass=false;
                            main=true;
                        }
                    }
                }
        }).start();

    }


    public void cmd_evaluator() {
        if(isController_cnt) {
            switch (cmd) {
                case 0:
                    try {
                        mycontroller.Indicator1on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    try {
                        mycontroller.Indicator1off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    try {
                        mycontroller.Indicator2on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    try {
                        mycontroller.Indicator2off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 4:
                    try {
                        mycontroller.Indicator3on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case 5:
                    try {
                        mycontroller.Indicator3off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 6:
                    try {
                        mycontroller.Indicator4on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 7:
                    try {
                        mycontroller.Indicator4off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 8:

                    try {
                        mycontroller.light_red_off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 9:

                    try {
                        mycontroller.light_red_on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 10:

                    try {
                        mycontroller.light_green_off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 11:

                    try {
                        mycontroller.light_green_on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 12:

                    try {
                        mycontroller.fan_off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 13:

                    try {
                        mycontroller.fan_on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 14:

                    try {
                        mycontroller.tv_off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 15:

                    try {
                        mycontroller.tv_on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 16:
                    try {
                        mycontroller.all_off();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 17:
                    try {
                        mycontroller.all_on();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:

                    break;
            }

        }
    }



    public void att_read(){
        mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.attention_music);
        mediaPlayer.start();
        m=0;
        new Thread(new Runnable() {
            public void run() {
                while (att_feed) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            if (att_data > 65){
                                att_feed=false;
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                switch (flag) {
                                    case 0:
                                        iv1.setImageResource(R.mipmap.bulb_red_on);
                                        myEditor.putBoolean("Red_on", true);
                                        myEditor.commit();
                                        cmd=9;
                                        iv_bulb1.setImageResource(R.mipmap.device_on);
                                        cmd_evaluator();
                                        cmd=1;
                                        cmd_evaluator();
                                        break;
                                    case 1:
                                        iv1.setImageResource(R.mipmap.bulb_red_off);
                                        myEditor.putBoolean("Red_on", false);
                                        myEditor.commit();
                                        cmd=8;
                                        iv_bulb1.setImageResource(R.mipmap.device_off);
                                        cmd_evaluator();
                                        cmd=1;
                                        cmd_evaluator();
                                        break;
                                    case 2:
                                        iv2.setImageResource(R.mipmap.blub_green_on);
                                        myEditor.putBoolean("Green_on", true);
                                        myEditor.commit();
                                        cmd=3;
                                        iv_bulb2.setImageResource(R.mipmap.device_on);
                                        cmd_evaluator();
                                        cmd=11;
                                        cmd_evaluator();
                                        break;
                                    case 3:
                                        iv2.setImageResource(R.mipmap.bulb_green_off);
                                        myEditor.putBoolean("Green_on", false);
                                        myEditor.commit();
                                        cmd=3;
                                        iv_bulb2.setImageResource(R.mipmap.device_off);
                                        cmd_evaluator();
                                        cmd=10;
                                        cmd_evaluator();
                                        break;
                                    case 4:
                                        iv3.setImageResource(R.mipmap.fan_on);
                                        myEditor.putBoolean("Fan_on", true);
                                        myEditor.commit();
                                        cmd=5;
                                        iv_fan.setImageResource(R.mipmap.device_on);
                                        cmd_evaluator();
                                        cmd=13;
                                        cmd_evaluator();
                                        break;
                                    case 5:
                                        iv3.setImageResource(R.mipmap.fan_off);
                                        myEditor.putBoolean("Fan_on", false);
                                        myEditor.commit();
                                        cmd=12;
                                        iv_fan.setImageResource(R.mipmap.device_off);
                                        cmd_evaluator();
                                        cmd=5;
                                        cmd_evaluator();
                                        break;
                                    case 6:
                                        iv4.setImageResource(R.mipmap.tv_on);
                                        myEditor.putBoolean("Tv_on", true);
                                        myEditor.commit();
                                        cmd=15;
                                        iv_tv.setImageResource(R.mipmap.device_on);
                                        cmd_evaluator();
                                        cmd=7;
                                        cmd_evaluator();
                                        break;
                                    case 7:
                                        iv4.setImageResource(R.mipmap.tv_off);
                                        myEditor.putBoolean("Tv_on", false);
                                        myEditor.commit();
                                        cmd=14;
                                        iv_tv.setImageResource(R.mipmap.device_off);
                                        cmd_evaluator();
                                        cmd=7;
                                        cmd_evaluator();
                                        break;
                                    default:
                                        break;
                                }
                            }

                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            if(!att_feed){
                                main=true;
                                iv1.setVisibility(View.INVISIBLE);
                                iv2.setVisibility(View.INVISIBLE);
                                iv3.setVisibility(View.INVISIBLE);
                                iv4.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    m=m+1;
                    if(m==40){
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                cmd=1;
                                cmd_evaluator();
                                cmd=3;
                                cmd_evaluator();
                                cmd=5;
                                cmd_evaluator();
                                cmd=7;
                                cmd_evaluator();
                                att_feed=false;
                                iv1.setVisibility(View.INVISIBLE);
                                iv2.setVisibility(View.INVISIBLE);
                                iv3.setVisibility(View.INVISIBLE);
                                iv4.setVisibility(View.INVISIBLE);
                                main=true;
                                mediaPlayer.stop();
                                mediaPlayer.release();


                            }
                        });
                    }
                }
            }
        }).start();

    }


    public void attentionDetector(int data){
        progressbarAtt.setProgress(data);
        att_data=data;

    }

    public void updateWaveViewSignal(int data){
        if(data>100){
            sqText.setText("very poor");
        }
        else if(data>50 && data<100){
            sqText.setText("poor");
        }
        else if(data>24 && data<51){
            sqText.setText("good");
        }
        else{
            sqText.setText("Excellent");
        }
    }

    public void connecting_shuffle(){
        iv_connection.setVisibility(View.VISIBLE);
        new Thread(new Runnable(){
            public void run() {
                while (connecting){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            if(neuro_cnted){
                                iv_connection.setImageResource(R.mipmap.connected_v1);
                            }
                            else{
                                iv_connection.setImageResource(R.mipmap.connecting1_v1);
                            }
                        }
                    });
                    if(!neuro_cnted){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            if(neuro_cnted){
                                iv_connection.setImageResource(R.mipmap.connected_v1);
                            }
                            else{
                                iv_connection.setImageResource(R.mipmap.connecting2_v1);
                            }
                        }
                    });
                    if(!neuro_cnted){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            if(neuro_cnted){
                                iv_connection.setImageResource(R.mipmap.connected_v1);
                            }
                            else{
                                iv_connection.setImageResource(R.mipmap.connecting3_v1);
                            }
                        }
                    });
                    if(!neuro_cnted){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void sdk_state_label(){
        iv_sdk_state.setVisibility(View.VISIBLE);
        switch (sdk_state) {
            case 0:
                tv_cnt_state.setText("connecting");
                connecting=true;
                connecting_shuffle();
                break;
            case 1:
                connecting=false;
                neuro_cnted=true;
                iv_connection.setImageResource(R.mipmap.connected_v1);
                tgStreamReader.start();
                tv_cnt_state.setText("connected");
                if(isController_cnt){
                    mycontroller.BTconnect();
                }
                break;
            case 2:
                tv_cnt_state.setText("working");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        iv_sdk_state.setImageResource(R.mipmap.green_indicator);
                    }
                });


                break;
            case 3:
                tv_cnt_state.setText("timed out");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        iv_sdk_state.setImageResource(R.mipmap.yellow_indicator);
                        iv_connection.setImageResource(R.mipmap.nosignal_v1);
                        Toast.makeText(getApplicationContext(),"Connection Timed out start Again",Toast.LENGTH_SHORT).show();
                        neuro_cnted=false;
                    }
                });
                break;
            case 4:
                tv_cnt_state.setText("disconnected");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        iv_sdk_state.setImageResource(R.mipmap.red_indicator);
                        iv_connection.setImageResource(R.mipmap.nosignal_v1);
                        neuro_cnted=false;
                    }
                });
                break;
            case 5:
                tv_cnt_state.setText("state failed");
                connecting=false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        iv_sdk_state.setImageResource(R.mipmap.pink_indicator);
                        iv_connection.setImageResource(R.mipmap.nosignal_v1);
                        Toast.makeText(getApplicationContext(),"connection failed try again to connect",Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 6:
                tv_cnt_state.setText("state error");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        iv_sdk_state.setImageResource(R.mipmap.skyblue_indicator);
                        iv_connection.setImageResource(R.mipmap.nosignal_v1);
                    }
                });
                break;
            default:
                tv_cnt_state.setText("stoped");
                iv_sdk_state.setImageResource(R.mipmap.indigo_indicator);


                break;
        }
    }

    //
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // auto generated stub
            Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    sdk_state=0;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    showToast("Neuralink Connected");
                    sdk_state=1;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_WORKING:
                    sdk_state=2;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    sdk_state=3;
                    sdk_state_label();

                    break;
                case ConnectionStates.STATE_STOPPED:
                    sdk_state=7;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    sdk_state=4;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_ERROR:
                    sdk_state=6;
                    sdk_state_label();
                    break;
                case ConnectionStates.STATE_FAILED:
                    sdk_state=5;
                    sdk_state_label();
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionStates;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e(TAG,"onRecordFail: " +flag);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.
            //Log.i(TAG,"onDataReceived");
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);

        }

        private Handler LinkDetectedHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // (8) demo of MindDataType
                switch (msg.what) {
                    case MindDataType.CODE_RAW:
                        updateWaveView(msg.arg1);
                        //mainkill(msg.arg1);
                        if(i==70){
                            blinkdetector(msg.arg1);
                            tv_raw.setText("" +msg.arg1 );
                            i=0;
                        }
                        else{i++;}

                        break;
                    case MindDataType.CODE_MEDITATION:
                        //medVlaue.setText("" +msg.arg1 );
                        break;
                    case MindDataType.CODE_ATTENTION:
                            attentionDetector(msg.arg1);
                            attValue.setText("" +msg.arg1 );
                        break;

                    case MindDataType.CODE_POOR_SIGNAL:
                        updateWaveViewSignal(msg.arg1);
                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        void showToast(final String msg) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

            });
        }
};}
