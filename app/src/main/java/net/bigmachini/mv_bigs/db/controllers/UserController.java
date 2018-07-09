package net.bigmachini.mv_bigs.db.controllers;

import android.content.Context;

import net.bigmachini.mv_bigs.db.AppDatabase;
import net.bigmachini.mv_bigs.db.dao.UserDAO;
import net.bigmachini.mv_bigs.db.entities.UserEntity;

import java.util.List;

public class UserController extends BaseController {
    private UserDAO mUserDao;

    public UserController(Context context) {
        super(context);
        mUserDao = AppDatabase.getDatabase(super.mContext).mUserDao();
    }

    public List<UserEntity> getUserById(int userId) {
        return mUserDao.getUsersById(userId);
    }

    public List<UserEntity> getUserByDeviceId(int deviceId) {
        return mUserDao.getUsersByDeviceId(deviceId);
    }


    public void deleteByDeviceId(int deviceId) {
        mUserDao.deleteByDeviceId(deviceId);
    }


    public int getCount() {
        return mUserDao.getCount();
    }

    public long createUser(UserEntity userEntity) {
        return mUserDao.insert(userEntity);
    }

    public void deleteAllUsers() {
        mUserDao.deleteAll();
    }
}

