package com.mushrooming.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.mushrooming.base.App;
import com.mushrooming.base.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/*
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {

    // Debugging
    private static final String TAG = "BluetoothService";

    // Name for the SDP record when creating server socket
    private static final String SOCKET_NAME = "MushroomingBluetoothSocket";

    // Unique UUID for this application
    private static final String baseUUID = "5742fc62-a737-484c-b76b-";

    // Unique UUID for my device
    private final UUID MY_UUID;

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ArrayList<ConnectedThread> mConnections = new ArrayList<>();

    // Constants representing handler message types
    static final int MESSAGE_CONNECTING = 0;
    static final int MESSAGE_CONNECTED = 1;
    static final int MESSAGE_CONNECTION_FAILED = 2;
    static final int MESSAGE_CONNECTION_LOST = 3;
    static final int MESSAGE_WRITE = 4;
    static final int MESSAGE_READ = 5;

    static final String KEY_DEVICE_NAME = "device_name";
    static final String KEY_BUFFER = "buffer";

    // Constructor. Prepares a new Bluetooth session.
    public BluetoothService(Handler handler) {
        Logger.debug(this, "CREATE BluetoothService");

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;

        @SuppressLint("HardwareIds") String macAddress = mAdapter.getAddress();
        if( macAddress.equals("02:00:00:00:00:00") ){
            ContentResolver mContentResolver = App.instance().getApplicationContext().getContentResolver();
            macAddress = Settings.Secure.getString(mContentResolver, "bluetooth_address");

            if( macAddress == null ){
                Log.e(TAG, "can't get mac address of bluetooth adapter");
                macAddress = "02:00:00:00:00:00";
            }
        }

        MY_UUID = UUID.fromString(baseUUID + macAddress.replace(":",""));

        Log.i(TAG, "My uuid: " + MY_UUID);
    }

    /*
     * Start the bluetooth service. Specifically start AcceptThread to begin a
     * session in listening (server) mode.
     */
    synchronized void start() {
        Logger.debug(this, "START BluetoothService");

        // Cancel Thread attempting connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel all existing connections
        for( ConnectedThread connection : mConnections )
            connection.cancel();
        mConnections.clear();

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    // Stop all threads
    synchronized void stop() {
        Logger.debug(this, "STOP BluetoothService");

        // Stop accepting new connections
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Cancel Thread attempting connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel all existing connections
        for( ConnectedThread connection : mConnections )
            connection.cancel();
        mConnections.clear();
    }

    // Send message to all connected devices
    void writeAll(byte[] buffer) {
        //Logger.debug(this, "writeAll()");

        for( ConnectedThread connection : mConnections )
            connection.write( buffer );
    }

    // Start the ConnectThread to initiate a connection to a remote device.
    synchronized void connect(BluetoothDevice device) {
        Log.i(TAG, "connect to: " + device);

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

        // Sending message to handler
        mHandler.obtainMessage(MESSAGE_CONNECTING, -1,-1,device.getName()).sendToTarget();
    }

    // Start the ConnectedThread to begin managing a Bluetooth connection
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.i(TAG, "connected to: " + device);

        // Sending message to handler
        mHandler.obtainMessage(MESSAGE_CONNECTED, -1,-1,device.getName())
                .sendToTarget();

        // Remove connect thread
        mConnectThread = null;

        // Create the thread to manage the connection and perform transmissions
        ConnectedThread connection = new ConnectedThread(socket, device);

        // Add connection to the list of connections
        mConnections.add( connection );

        // Start the thread
        connection.start();
    }

    // Indicate that the connection attempt failed and notify the UI Activity.
    private void connectionFailed(BluetoothDevice device) {
        Log.i(TAG, "failed connecting to " + device);

        // Sending message to handler
        mHandler.obtainMessage(MESSAGE_CONNECTION_FAILED, -1,-1,device.getName())
                .sendToTarget();
    }

    // Indicate that the connection was lost and notify the UI Activity.
    private void connectionLost(BluetoothDevice device, ConnectedThread connection) {
        Log.i(TAG, "lost connection with " + device);

        mConnections.remove(connection);

        // Sending message to handler
        mHandler.obtainMessage(MESSAGE_CONNECTION_LOST, -1,-1,device.getName())
                .sendToTarget();
    }

    /*
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until it is interrupted
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket = null;

        AcceptThread() {
            Logger.debug(this, "CREATE AcceptThread");
            // Create a new listening server socket
            try {
                mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(SOCKET_NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
        }

        public void run() {
            Logger.debug(this, "BEGIN AcceptThread");
            setName("AcceptThread");

            if( mmServerSocket != null ){
                // Listen to the server socket until the thread is interrupted

                while ( !Thread.interrupted() ) {
                    BluetoothSocket socket = null;
                    try {
                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        socket = mmServerSocket.accept();
                    } catch (IOException e) {
                        Log.e(TAG, "accept() failed", e);
                    }

                    // If a connection was accepted
                    if (socket != null) {
                        connected(socket, socket.getRemoteDevice());
                    }
                }
            }
            else Log.e(TAG, "Server Socket is null");

            Logger.debug(this, "END AcceptThread");
        }

        void cancel() {
            Logger.debug(this, "CANCEL AcceptThread");

            interrupt();
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server socket failed", e);
            }
        }
    }


    /*
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket = null;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            Logger.debug(this, "CREATE ConnectThread");
            mmDevice = device;

            UUID OTHER_UUID = UUID.fromString(baseUUID +
                    device.getAddress().replace(":",""));

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(OTHER_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: creating socket failed", e);
            }
        }

        public void run() {
            Logger.debug(this, "BEGIN ConnectThread");
            setName("ConnectThread - " + mmDevice);

            if( mmSocket == null ){
                Log.e(TAG, "Connect Socket is null");

                connectionFailed(mmDevice);
                return;
            }

            // Always cancel discovery because it will slow down a connection
            // mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed(mmDevice);
                return;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);

            Logger.debug(this, "END ConnectThread");
        }

        void cancel() {
            Logger.debug(this, "CANCEL ConnectThread");

            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of socket failed", e);
            }
        }
    }

    /*
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;

        ConnectedThread(BluetoothSocket socket, BluetoothDevice device) {
            Logger.debug(this, "CREATE ConnectedThread");

            mmSocket = socket;
            mmDevice = device;

            // Get the BluetoothSocket input and output streams
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "error creating stream", e);
            }
        }

        public void run() {
            Logger.debug(this, "BEGIN mConnectedThread");
            setName("ConnectedThread - " + mmDevice);
            byte[] buffer = new byte[1024];
            int bytes;

            if( mmInStream == null ){
                Logger.error(this,  "Input Stream is null");

                connectionLost(mmDevice, this);
                return;
            }

            // Keep listening to the InputStream
            while ( !Thread.interrupted() ) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    Bundle b = new Bundle();
                    b.putString(KEY_DEVICE_NAME, mmDevice.getName());
                    b.putByteArray(KEY_BUFFER, buffer);

                    Logger.debug(this, "read from " + mmDevice);

                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, b)
                            .sendToTarget();
                } catch (IOException e) {
                    Logger.error(this,  "disconnected", e);
                    connectionLost(mmDevice, this);
                    break;
                }
            }

            Logger.debug(this, "END mConnectedThread");
        }

        // Write to the connected OutStream.
        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                Logger.debug(this, "write to " + mmDevice);

                // Inform the UI Activity that the message was sent
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, mmDevice.getName())
                        .sendToTarget();
            } catch (IOException e) {
                Logger.errorWithException(this, e, "write() failed");
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Logger.errorWithException(this, e, "close() failed");
            }
        }
    }
}
