package net.bigmachini.mv_bigs;

import net.bigmachini.mv_bigs.db.entities.DeviceEntity;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.db.entities.UserEntity;
import net.bigmachini.mv_bigs.structures.LoginStructure;

import java.util.UUID;

public class Constants {
    public static final String USERS = "_USERS";
    public static final String KEYS = "_KEYS";
    public static final String USER_MODEL = "_USER_MODEL";
    public static final String REGISTRATION_MODEL = "_REGISTRATION_MODEL";
    public static final String COUNTER = "_COUNTER";

    public static final String ENROLL = "enroll";
    public static final String DELETE = "delete";
    public static final String DELETE_ALL = "off";
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    public static DeviceEntity gSelectedDevice;
    public static UserEntity gSelectedUser;
    public static RecordEntity gSelectedRecord;
    public static LoginStructure gLoginStructure;
}
