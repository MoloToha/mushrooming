package com.mushrooming.bluetooth;


public interface BluetoothEventHandler {
    void connecting(String device);
    void connected(String device);
    void connectionFailed(String device);
    void connectionLost(String device);
    void positionSent(String device);
    void positionReceived(String device, double x, double y);
}
