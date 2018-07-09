package net.bigmachini.mv_bigs.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import net.bigmachini.mv_bigs.db.entities.DeviceEntity;

import java.util.List;

@Dao
public interface DeviceDAO {
    @Query("SELECT * FROM device_table WHERE id =:id")
    List<DeviceEntity> getDevicesById(int id);

    @Query("SELECT * FROM device_table")
    List<DeviceEntity> getAll();

    @Query("SELECT COUNT(*) FROM device_table")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(DeviceEntity device);

    @Delete
    void delete(DeviceEntity device);

    @Query("DELETE FROM device_table")
    void deleteAll();

    @Query("DELETE  FROM device_table WHERE id=:id")
    void deleteById(int id);
}
