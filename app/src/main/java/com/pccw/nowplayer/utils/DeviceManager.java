package com.pccw.nowplayer.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.pccw.nowplayer.model.Device;
import com.pccw.nowplayer.model.db_orm.OrmController;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.networking.NMAFNetworking;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanion;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.List;

/**
 * Created by Swifty on 6/4/2016.
 */
public class DeviceManager {
    private static final String TAG = "DeviceManager";
    private List<Device> devices;
    private static DeviceManager ourInstance = new DeviceManager();
    public final static String CONNECT_DEVICE_CODE = "SUCCESS";

    public static DeviceManager getInstance() {
        return ourInstance;
    }

    private DeviceManager() {
        refreshDevices();
    }

    public Device getConnectDevice() {
        for (Device device : devices) {
            if (isConnectDevice(device)) {
                return device;
            }
        }
        return null;
    }

    public boolean hasConnectDevice() {
        return getConnectDevice() != null;
    }

    private List<Device> saveDevices(NMAFSTBCompanion.Device[] nowDevices) {
        if (Validations.isEmptyOrNull(nowDevices)) return null;
        for (int i = 0; i < nowDevices.length; i++) {
            Device.updateDevice(this.devices, nowDevices[i], i);
        }
        refreshDevices();
        return this.devices;
    }

    private boolean hasConnect(NMAFSTBCompanion.Device[] nowDevices) {
        for (NMAFSTBCompanion.Device device : nowDevices) {
            if (CONNECT_DEVICE_CODE.equals(device.returnCode)) {
                return true;
            }
        }
        return false;
    }

    public List<Device> getAllDevices() {
        return devices;
    }

    public void saveDevice(Device device) {
        OrmController.save(device);
        refreshDevices();
    }

    private void refreshDevices() {
        this.devices = OrmController.listAll(Device.class);
    }

    public boolean isConnectDevice(Device device) {
        if (device == null) return false;
        return CONNECT_DEVICE_CODE.equals(device.returnCode);
    }

    public void unBindDevice(Device device, NMAFBaseModule.ErrorCallback errorCallback) {
        if (device == null) return;
        NMAFSTBCompanion.getSharedInstance().unbind(device.changeToNowDevice(), errorCallback);
    }

    /**
     * retrieve devices when app start and user login success
     *
     * @param retrieveCallback
     */
    public void retrieveDevice(final RetrieveCallback retrieveCallback) {
        NMAFSTBCompanion.getSharedInstance().getBoundDevices(new NMAFSTBCompanion.BoundDevicesCallback() {
            @Override
            public void boundDevicesResult(NMAFSTBCompanion.Device[] devices) {
                DeviceManager.getInstance().saveDevices(devices);
                if (retrieveCallback != null)
                    retrieveCallback.onDeviceRetrieved(getAllDevices());
            }

            @Override
            public void boundDevicesError(Throwable throwable) {
                Log.w(TAG, throwable.toString());
                if (retrieveCallback != null)
                    retrieveCallback.onDeviceRetrieved(null);
            }
        });
    }

    public Promise<List<Device>, Throwable, Float> retrieveDevice() {
        final DeferredObject<List<Device>, Throwable, Float> deferred = new DeferredObject<>();
        retrieveDevice(new RetrieveCallback() {
            @Override
            public void onDeviceRetrieved(List<Device> deviceList) {
                deferred.resolve(deviceList);
            }
        });
        return deferred;
    }

    public void addDevice(String code, NMAFBaseModule.ErrorCallback errorCallback) {
        NMAFSTBCompanion.getSharedInstance().bind(code, errorCallback);
    }

    public void clearLocalDevice() {
        OrmController.deleteAll(Device.class);
        refreshDevices();
    }

    public void bindDevice(Device device, NMAFBaseModule.ErrorCallback errorCallback) {
        if (device == null) return;
        NMAFSTBCompanion.getSharedInstance().bind(device.changeToNowDevice(), errorCallback);
    }

    public interface RetrieveCallback {
        void onDeviceRetrieved(List<Device> deviceList);
    }

    public void switchToChannel(String id, NMAFBaseModule.ErrorCallback errorCallback) {
        if (hasConnectDevice()) {
            NMAFSTBCompanion.getSharedInstance().switchToChannel(getConnectDevice().changeToNowDevice(), id, errorCallback);
        } else {
            errorCallback.operationComplete(new Throwable("no connect device"));
        }
    }

    public void switchToVodProduct(String id, NMAFBaseModule.ErrorCallback errorCallback) {
        if (hasConnectDevice()) {
            NMAFSTBCompanion.getSharedInstance().switchToVodProduct(getConnectDevice().changeToNowDevice(), id, errorCallback);
        } else {
            errorCallback.operationComplete(new Throwable("no connect device"));
        }
    }

    public void switchToSportsVodCategory(String id, NMAFBaseModule.ErrorCallback errorCallback) {
        if (hasConnectDevice()) {
            NMAFSTBCompanion.getSharedInstance().switchToSportsVodCategory(getConnectDevice().changeToNowDevice(), id, errorCallback);
        } else {
            errorCallback.operationComplete(new Throwable("no connect device"));
        }
    }

    public void getCurrentStatus(NMAFNetworking.NetworkCallback<NMAFSTBCompanion.ProxyActionOutputModel> callback) {
        if (hasConnectDevice()) {
            NMAFSTBCompanion.getSharedInstance().getCurrentStatus(getConnectDevice().changeToNowDevice(), callback);
        }
    }
}
