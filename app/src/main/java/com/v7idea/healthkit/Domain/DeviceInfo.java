package com.v7idea.healthkit.Domain;

import android.bluetooth.BluetoothDevice;

/**
 * Created by mortal on 2018/6/1.
 */

public class DeviceInfo {
    private boolean ifUpdate = false;
    private BluetoothDevice peripheral = null;

    public DeviceInfo(BluetoothDevice peripheral) {
        this.peripheral = peripheral;
        this.ifUpdate = true;
    }

    public BluetoothDevice getPeripheral(){
        return this.peripheral;
    }

    public void setPeripheral(BluetoothDevice peripheral){
        this.peripheral = peripheral;
        this.ifUpdate = true;
    }

    public void resetIsUpdate(){
        this.ifUpdate = false;
    }

    public boolean isUpdate(){
        return ifUpdate;
    }

    public String getMacAddress(){
        return  peripheral != null ? peripheral.getAddress() : "";
    }
}
