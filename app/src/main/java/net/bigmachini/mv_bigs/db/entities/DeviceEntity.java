package net.bigmachini.mv_bigs.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import net.bigmachini.mv_bigs.structures.DeviceStructure;

@Entity(tableName = "device_table")
public class DeviceEntity {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "mac_address")
    @Expose
    private String macAddress;


    @ColumnInfo(name = "status")
    @Expose
    private String status;


    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Need this empty constructor for the database (SugarORM)
    public DeviceEntity() {
    }

    @Ignore
    private DeviceEntity(DeviceEntity.DeviceBuilder deviceBuilder) {
        this.id = deviceBuilder.id;
        this.macAddress = deviceBuilder.macAddress;
        this.status = deviceBuilder.status;
    }


    public static class DeviceBuilder {

        private final int id;
        private final String macAddress;
        private final String status;

        public DeviceBuilder(DeviceStructure deviceStructure) {
            this.id = deviceStructure.id;
            this.macAddress = deviceStructure.macAddress;
            this.status = deviceStructure.status;
        }

        public DeviceEntity build() {
            return new DeviceEntity(this);
        }
    }

    @Override
    public String toString() {
        return this.macAddress;
    }
}