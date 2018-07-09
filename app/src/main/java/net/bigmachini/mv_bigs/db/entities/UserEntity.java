package net.bigmachini.mv_bigs.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import net.bigmachini.mv_bigs.structures.UserStructure;

@Entity(tableName = "user_table")
public class UserEntity {
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

    @ColumnInfo(name = "device_id")
    @Expose
    private int device_id;

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

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    // Need this empty constructor for the database (SugarORM)
    public UserEntity() {
    }

    @Ignore
    private UserEntity(UserEntity.UserBuilder userBuilder) {
        this.id = userBuilder.id;
        this.name = userBuilder.name;
        this.device_id = userBuilder.deviceId;
    }


    public static class UserBuilder {

        private final int id;
        private final String name;
        private final int deviceId;

        public UserBuilder(UserStructure userStructure) {
            this.id = userStructure.id;
            this.name = userStructure.name;
            this.deviceId = userStructure.deviceId;
        }

        public UserEntity build() {
            return new UserEntity(this);
        }
    }

    @Override
    public String toString() {
        return this.getName();    }
}