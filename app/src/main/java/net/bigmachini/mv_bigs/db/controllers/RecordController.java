package net.bigmachini.mv_bigs.db.controllers;

import android.content.Context;

import net.bigmachini.mv_bigs.db.AppDatabase;
import net.bigmachini.mv_bigs.db.dao.RecordDAO;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;

import java.util.List;

public class RecordController extends BaseController {
    private RecordDAO mRecordDao;

    public RecordController(Context context) {
        super(context);
        mRecordDao = AppDatabase.getDatabase(super.mContext).mRecordDao();
    }

    public List<RecordEntity> getRecordById(int RecordId) {
        return mRecordDao.getRecordById(RecordId);
    }

    public List<RecordEntity> getRecordsByUserId(int userId) {
        return mRecordDao.getRecordByUserId(userId);
    }

    public List<RecordEntity> getRecordByUser(String recordId, int userId) {
        return mRecordDao.getRecordByUser(recordId, userId);
    }

    public void deleteById(int RecordId) {
        mRecordDao.deleteById(RecordId);
    }
    public void deleteByName(String name) {
        mRecordDao.deleteByName(name);
    }


    public int getCount() {
        return mRecordDao.getCount();
    }

    public long createRecord(RecordEntity RecordEntity) {
        return mRecordDao.insert(RecordEntity);
    }

    public void deleteAllRecords() {
        mRecordDao.deleteAll();
    }
}

