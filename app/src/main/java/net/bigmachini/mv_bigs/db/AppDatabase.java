package net.bigmachini.mv_bigs.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import net.bigmachini.mv_bigs.db.dao.DeviceDAO;
import net.bigmachini.mv_bigs.db.dao.RecordDAO;
import net.bigmachini.mv_bigs.db.dao.UserDAO;
import net.bigmachini.mv_bigs.db.entities.DateConverter;
import net.bigmachini.mv_bigs.db.entities.DeviceEntity;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.db.entities.UserEntity;


@Database(entities = {DeviceEntity.class, UserEntity.class, RecordEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    public static final String DATABASE_NAME = "Journals.db";

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyDatabase() {
        INSTANCE = null;
    }

    // DAO's
    public abstract DeviceDAO mDeviceDao();

    public abstract UserDAO mUserDao();

    public abstract RecordDAO mRecordDao();
}