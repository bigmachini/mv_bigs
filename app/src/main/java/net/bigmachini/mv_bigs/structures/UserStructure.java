package net.bigmachini.mv_bigs.structures;

import com.google.gson.annotations.SerializedName;

public class UserStructure extends BaseStructure{

    @SerializedName("name")
    public String name;

    @SerializedName("device_id")
    public int deviceId;
}
