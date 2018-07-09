package net.bigmachini.mv_bigs.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import net.bigmachini.mv_bigs.structures.RecordStructure;

@Entity(tableName = "record_table")
public class RecordEntity {
    /**
     * The Remote ID of the model as found in the backend database.
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    @Expose
    private String name;

    @ColumnInfo(name = "user_id")
    @Expose
    private int userId;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Need this empty constructor for the database (SugarORM)
    public RecordEntity() {
    }

    @Ignore
    private RecordEntity(RecordEntity.RecordBuilder recordBuilder) {
        this.id = recordBuilder.id;
        this.name = recordBuilder.name;
        this.userId = recordBuilder.userId;
    }


    public static class RecordBuilder {

        private final int id;
        private final int userId;
        private final String name;

        public RecordBuilder(RecordStructure recordStructure) {
            this.id = recordStructure.id;
            this.name = recordStructure.name;
            this.userId = recordStructure.userId;
        }

        public RecordEntity build() {
            return new RecordEntity(this);
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}