package com.mushrooming.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

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
class BluetoothService {

    // Name for the SDP record when creating server socket
    private static final String SOCKET_NAME = "MushroomingBluetoothSocket";

    // Base UUID for this application
    private static final String baseUUID = "5742fc62-a737-484c-b76b-";
    // Unique UUID for my device
    private final UUID MY_UUID;

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private String mMacAddress;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ArrayList<ConnectedThread> mConnections = new ArrayList<>();

    // Constants representing handler message types
    static final int HANDLER_CONNECTING = 0;
    static final int HANDLER_CONNECTED = 1;
    static final int HANDLER_CONNECTION_FAILED = 2;
    static final int HANDLER_CONNECTION_LOST = 3;
    static final int HANDLER_READ = 4;

    static final String KEY_DEVICE_NAME = "device_name";
    static final String KEY_BUFFER = "buffer";

    // Constructor. Prepares a new Bluetooth session.
    @SuppressLint("HardwareIds")
    BluetoothService(Handler handler) {
        Logger.debug(this, "CREATE BluetoothService");

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;

        mMacAddress = mAdapter.getAddress();
        if( mMacAddress.equals("02:00:00:00:00:00") ){
            ContentResolver mContentResolver = App.instance().getApplicationContext().getContentResolver();
            mMacAddress = Settings.Secure.getString(mContentResolver, "bluetooth_address");

            if( mMacAddress == null ){
                Logger.error(this, "can't get mac address of bluetooth adapter");
                mMacAddress = "02:00:00:00:00:00";
            }
        }

        MY_UUID = UUID.fromString(baseUUID + mMacAddress.replace(":",""));

        Logger.debug(this, "My uuid: " + MY_UUID);
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

    // Returns addresses of connected devices
    ArrayList<String> getConnections() {
        ArrayList<String> devices = new ArrayList<>();
        for( ConnectedThread connection : mConnections )
            devices.add( connection.getDevice().getAddress() );
        return devices;
    }

    // Send message to all connected devices
    void writeAll(byte[] buffer) {
        //Logger.debug(this, "writeAll()");

        for( ConnectedThread connection : mConnections )
            connection.write( buffer );
    }

    // Start the ConnectThread to initiate a connection to a remote device.
    synchronized void connect(BluetoothDevice device) {
        Logger.debug(this, "connect()");

        if( device.getAddress().equals(mMacAddress) ){
            Logger.debug(this,"connect() - trying to connect with own device. skipping");
            return;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

        // Sending message to handler
        mHandler.obtainMessage(HANDLER_CONNECTING, -1,-1,device.getAddress()).sendToTarget();
    }

    // Start the ConnectedThread to begin managing a Bluetooth connection
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Logger.debug(this, "connected()");

        // Sending message to handler
        mHandler.obtainMessage(HANDLER_CONNECTED, -1,-1,device.getAddress())
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
        Logger.debug(this, "connectionFailed()");

        // Sending message to handler
        mHandler.obtainMessage(HANDLER_CONNECTION_FAILED, -1,-1,device.getAddress())
                .sendToTarget();
    }

    // Indicate that the connection was lost and notify the UI Activity.
    private void connectionLost(BluetoothDevice device, ConnectedThread connection) {
        Logger.debug(this, "connectionLost()");

        mConnections.remove(connection);

        // Sending message to handler
        mHandler.obtainMessage(HANDLER_CONNECTION_LOST, -1,-1,device.getAddress())
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
                Logger.errorWithException(this, e, "listen() failed");
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
                        Logger.errorWithException(this, e, "accept() failed");
                    }

                    // If a connection was accepted
                    if (socket != null) {
                        connected(socket, socket.getRemoteDevice());
                    }
                }
            }
            else Logger.error(this, "Server Socket is null");

            Logger.debug(this, "END AcceptThread");
        }

        void cancel() {
            Logger.debug(this, "CANCEL AcceptThread");

            interrupt();
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Logger.errorWithException(this, e, "close() of server socket failed");
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
                Logger.errorWithException(this, e, "ConnectThread: creating socket failed");
            }
        }

        public void run() {
            Logger.debug(this, "BEGIN ConnectThread");
            setName("ConnectThread - " + mmDevice);

            if( mmSocket == null ){
                Logger.error(this, "Connect Socket is null");

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
                Logger.errorWithException(this, e, "connect() failed");
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Logger.errorWithException(this, e2, "close() failed during connection failure");
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
                Logger.errorWithException(this, e, "close() of socket failed");
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
                Logger.errorWithException(this, e, "error creating stream");
            }
        }

        public BluetoothDevice getDevice() { return mmDevice; }

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
                    b.putString(KEY_DEVICE_NAME, mmDevice.getAddress());
                    b.putByteArray(KEY_BUFFER, buffer);

                    Logger.debug(this, "read from " + mmDevice);

                    mHandler.obtainMessage(HANDLER_READ, bytes, -1, b)
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
