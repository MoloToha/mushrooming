package com.mushrooming.bluetooth;


public interface BluetoothEventHandler {
    void connecting(String device);
    void connected(String device);
    void connection_failed(String device);
    void connection_lost(String device);
    void position_sent(String device);
    void position_received(String device, double x, double y);
}
