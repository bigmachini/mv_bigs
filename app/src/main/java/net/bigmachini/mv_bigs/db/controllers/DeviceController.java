package net.bigmachini.mv_bigs.db.controllers;

import android.content.Context;

import net.bigmachini.mv_bigs.db.AppDatabase;
import net.bigmachini.mv_bigs.db.dao.DeviceDAO;
import net.bigmachini.mv_bigs.db.entities.DeviceEntity;

import java.util.List;

public class DeviceController extends BaseController {
    private DeviceDAO mDeviceDao;

    public DeviceController(Context context) {
        super(context);
        mDeviceDao = AppDatabase.getDatabase(super.mContext).mDeviceDao();
    }

    public List<DeviceEntity> getDeviceById(int deviceId) {
        return mDeviceDao.getDevicesById(deviceId);
    }

    public List<DeviceEntity> getDeviceByMacAddress(String macAddress) {
        return mDeviceDao.getDeviceByMacAddress(macAddress);
    }

    public List<DeviceEntity> getAllDevices() {
        return mDeviceDao.getAll();
    }

    public void deleteById(int deviceId) {
        mDeviceDao.deleteById(deviceId);
    }


    public int getCount() {
        return mDeviceDao.getCount();
    }

    public long createDevice(DeviceEntity deviceEntity) {
        return mDeviceDao.insert(deviceEntity);
    }

    public void deleteAllDevices() {
        mDeviceDao.deleteAll();
    }
}

