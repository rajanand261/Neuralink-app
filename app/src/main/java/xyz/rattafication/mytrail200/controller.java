package xyz.rattafication.mytrail200;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class controller {
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    public boolean c_connected=false;





    public void BTinit() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        //check for paired devices
        if (bondedDevices.isEmpty()) {

        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                //MAC Address of Bluetooth Module
                String DEVICE_ADDRESS = "74:47:09:0B:A5:11";
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    break;
                }
            }
        }

    }


    public boolean BTconnect() {

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();


            c_connected = true;
        } catch (IOException e) {
            e.printStackTrace();
            c_connected = false;
        }

        if (c_connected) {
            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return c_connected;
    }

    public void BTdisconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Indicator1off() throws IOException {
        int command=15;
        outputStream.write(command);
    }

    public void Indicator1on() throws IOException {
        int command=16;
        outputStream.write(command);
    }
    public void Indicator2off() throws IOException {
        int command=17;
        outputStream.write(command);
    }
    public void Indicator2on() throws IOException {
        int command=18;
        outputStream.write(command);
    }
    public void Indicator3off() throws IOException {
        int command=19;
        outputStream.write(command);
    }
    public void Indicator3on() throws IOException {
        int command=20;
        outputStream.write(command);
    }
    public void Indicator4off() throws IOException {
        int command=21;
        outputStream.write(command);
    }
    public void Indicator4on() throws IOException {
        int command=22;
        outputStream.write(command);
    }
    public void light_red_off() throws IOException {
        int command=0;
        outputStream.write(command);
    }
    public void light_red_on() throws IOException {
        int command=1;
        outputStream.write(command);
    }
    public void fan_off() throws IOException {
        int command=4;
        outputStream.write(command);
    }
    public void fan_on() throws IOException {
        int command=5;
        outputStream.write(command);
    }
    public void light_green_off() throws IOException {
        int command=2;
        outputStream.write(command);
    }
    public void light_green_on() throws IOException {
        int command=3;
        outputStream.write(command);
    }
    public void tv_off() throws IOException {
        int command=6;
        outputStream.write(command);
    }
    public void tv_on() throws IOException {
        int command=7;
        outputStream.write(command);
    }
    public void all_on() throws IOException {
        int command=31;
        outputStream.write(command);
    }
    public void all_off() throws IOException {
        int command=30;
        outputStream.write(command);
    }

}
