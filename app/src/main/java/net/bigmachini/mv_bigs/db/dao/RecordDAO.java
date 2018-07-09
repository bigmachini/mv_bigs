package net.bigmachini.mv_bigs.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import net.bigmachini.mv_bigs.db.entities.RecordEntity;

import java.util.List;

@Dao
public interface RecordDAO {
    @Query("SELECT * FROM record_table WHERE id =:recordId")
    List<RecordEntity> getRecordById(int recordId);

    @Query("SELECT * FROM record_table WHERE user_id =:userId ORDER BY id DESC")
    List<RecordEntity> getRecordByUserId(String userId);


    @Query("SELECT COUNT(*) FROM record_table")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(RecordEntity Record);

    @Delete
    void delete(RecordEntity Record);

    @Query("DELETE FROM record_table")
    void deleteAll();

    @Query("DELETE  FROM record_table WHERE id=:id")
    void deleteById(int id);
}
