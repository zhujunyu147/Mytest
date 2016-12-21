package com.honeywell.printer.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.honeywell.printer.application.SoftApplication;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by zhujunyu on 2016/12/19.
 */

public class BluetoothManager {


    private final static String TAG = NewBluetoothService.class.getSimpleName();

    private static BluetoothManager mBleManager;

    /**
     * the default BLUETOOTH Adapter.
     */
    private BluetoothAdapter mBluetoothAdapter;

    private NewBluetoothService mBluetoothLeService;

    /**
     * single instance
     *
     * @return
     */
    public static BluetoothManager getInstance() {
        if (mBleManager == null) {
            mBleManager = new BluetoothManager();
        }
        return mBleManager;
    }

    private BluetoothManager() {
    }

    /**
     * get the device is support ble or not
     *
     * @return
     */
    public boolean isSupportBLE() {
        return SoftApplication.getInstance().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }


    /**
     * get the ble is enabled or not
     *
     * @return
     */
    public boolean isBLEEnable() {
        android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) SoftApplication.getInstance()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }


    /**
     * Get the default BLUETOOTH Adapter for this device.
     *
     * @return
     */
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            final android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) SoftApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    /**
     * Stops an ongoing Bluetooth LE device scan.
     */
    public void stopBLEScan(BluetoothAdapter.LeScanCallback mLeScanCallback) {
        getBluetoothAdapter().stopLeScan(mLeScanCallback);
    }

    /**
     * Starts a scan for Bluetooth LE devices.
     *
     * @param mLeScanCallback
     */
    public void startBLEScan(BluetoothAdapter.LeScanCallback mLeScanCallback) {
        getBluetoothAdapter().startLeScan(mLeScanCallback);
    }

    public boolean isEnableBluetoothAdapter() {
        return getBluetoothAdapter().isEnabled();
    }

    public boolean isNeedShowOpenBleActivity() {
        return !isEnableBluetoothAdapter();
    }

    /**
     * @param service
     */
    public void setBleServiceFromServiceConnected(IBinder service) {
        mBluetoothLeService = ((NewBluetoothService.LocalBinder) service).getService();
    }

    /**
     * connect BLE
     *
     * @param address
     * @return
     */
    public void connectBle(String address) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(address);
        }
    }


    public void bondBle(BluetoothDevice device) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.bondDevice(device);
        }
    }


    /**
     * disconnect ble service
     */

//    public void disconnectAllDevices() {
//        for (MadAirDeviceModel device : MadAirDeviceModelSharedPreference.getDeviceList()) {
//            if (!device.getDeviceName().equals(""))
//                disconnectBle(device.getMacAddress());
//        }
//    }

//    public void disconnectBle(String address) {
//        if (mBluetoothLeService != null) {
//            mBluetoothLeService.disconnect(address);
//
//            MadAirDeviceModelSharedPreference.saveStatus(address, MadAirDeviceStatus.DISCONNECT);
////            removeBond(getBondDevice(address));
//        }
//    }
    private void removeBond(BluetoothDevice device) {
        if (device == null) {
            return;
        }
        try {
            Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class.getCanonicalName());
            Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
            removeBondMethod.invoke(device);
        } catch (Throwable th) {
            th.printStackTrace();
            return;
        }
    }

    public BluetoothDevice getBondDevice(String address) {
        Set<BluetoothDevice> devices = getBluetoothAdapter().getBondedDevices();
        if (devices != null && devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                if (bluetoothDevice.getAddress().equals(address)) {
                    return bluetoothDevice;
                }
            }
        }

        return null;
    }

    /**
     * reset ble service to null
     */
    public void resetBleService() {
        mBluetoothLeService = null;
    }

    /**
     * init ble service
     *
     * @return
     */
    public boolean initBluetoothLeService() {
        if (mBluetoothLeService != null && mBluetoothLeService.initialize()) {
            return true;
        }

        return false;
    }

    public List<BluetoothDevice> getConnectedDevices() {
        return mBluetoothLeService.getConnectedDevices();
    }

    public NewBluetoothService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setBluetoothLeService(NewBluetoothService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    public void setNotification(String address, String serviceUuid, String characterUuid,
                                String descriptorUuid, boolean enable) {
        BluetoothGattService bluetoothGattService
                = mBluetoothLeService.getService(address, UUID.fromString(serviceUuid));

        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic characteristic
                    = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));

            mBluetoothLeService.setCharacteristicNotification(characteristic, descriptorUuid, address, enable);
        } else {
        }
    }

    public void writeCharacteristic(String address, byte[] value, String serviceUuid, String characterUuid) {
        BluetoothGattService bluetoothGattService
                = mBluetoothLeService.getService(address, UUID.fromString(serviceUuid));
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic characteristic
                    = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));
            mBluetoothLeService.writeCharacteristic(characteristic, address, value);
        } else {
        }
    }

    public void readCharacteristic(String address, String serviceUuid, String characterUuid) {
        BluetoothGattService bluetoothGattService
                = mBluetoothLeService.getService(address, UUID.fromString(serviceUuid));

        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic characteristic
                    = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));
            mBluetoothLeService.readCharacteristic(characteristic, address);
        } else {
        }
    }

    public void pollCharacteristic(String address) {
        List<BluetoothGattService> services = mBluetoothLeService.getSupportedGattServices(address);
        for (BluetoothGattService service : services) {

            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
            }
        }
    }


    public void unregisterBleServiceBroadcast() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.unRegisterServiceBroadcast();
        }
    }

}
