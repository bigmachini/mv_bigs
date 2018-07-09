package net.bigmachini.mv_bigs.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import net.bigmachini.mv_bigs.db.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM user_table WHERE device_id =:deviceId")
    List<UserEntity> getUsersByDeviceId(int deviceId);

    @Query("SELECT * FROM user_table WHERE id =:userId ORDER BY id DESC")
    List<UserEntity> getUsersById(int userId);


    @Query("SELECT COUNT(*) FROM user_table")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("DELETE  FROM user_table WHERE device_id=:deviceId")
    void deleteByDeviceId(int deviceId);

}
