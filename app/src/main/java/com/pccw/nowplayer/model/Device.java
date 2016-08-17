package com.pccw.nowplayer.model;

import android.text.TextUtils;

import com.orm.dsl.Unique;
import com.pccw.nowplayer.model.db_orm.NowModel;
import com.pccw.nowplayer.model.db_orm.OrmController;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowtv.nmaf.stbCompanion.NMAFSTBCompanion;

import java.util.List;

/**
 * Created by swifty on 29/5/2016.
 */
public class Device extends NowModel {
    @Unique
    public String deviceId;
    public String fsa;
    public String ip;
    public boolean isConnected;
    public String name;
    public String returnCode;

    public Device() {

    }

    public Device(String deviceId, String name, String fsa, String ip, String returnCode) {
        this.deviceId = deviceId;
        this.name = name;
        this.fsa = fsa;
        this.ip = ip;
        this.returnCode = returnCode;
    }

    public static void addDevice(NMAFSTBCompanion.Device device, int i) {
        if (device != null) {
            if (TextUtils.isEmpty(device.name)) {
                device.name = "STB " + (i + 1);
            }
            Device d = new Device(device.deviceId, device.name, device.fsa, device.ip, device.returnCode);
            OrmController.save(d);
        }
        return;
    }

    /**
     * @param devices saved devices
     * @param device  new device
     * @param i       index in devices list
     */
    public static void updateDevice(List<Device> devices, NMAFSTBCompanion.Device device, int i) {
        if (device == null) return;

        //if no saved devices just add new one.
        if (devices == null) {
            addDevice(device, i);
            return;
        }

        //check if the new one is in the saved devices list, if yes just update the info.
        for (Device d : devices) {
            if (d.deviceId.equals(device.deviceId)) {
                device.name = d.name;
                device.returnCode = d.returnCode;
                device.fsa = d.fsa;
                device.ip = d.ip;
                addDevice(device, i);
                return;
            }
        }

        // if no update then add the new one.
        addDevice(device, i);
    }

    public NMAFSTBCompanion.Device changeToNowDevice() {
        NMAFSTBCompanion.Device device = new NMAFSTBCompanion.Device(new NMAFSTBCompanion.ProxyActionOutputModel());
        device.returnCode = this.returnCode;
        device.deviceId = this.deviceId;
        device.fsa = this.fsa;
        device.ip = this.ip;
        device.name = this.name;
        return device;
    }
}
