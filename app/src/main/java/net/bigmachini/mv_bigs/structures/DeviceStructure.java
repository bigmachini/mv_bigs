package net.bigmachini.mv_bigs.structures;

import com.google.gson.annotations.SerializedName;

public class DeviceStructure extends BaseStructure{

    @SerializedName("mac_address")
    public String macAddress;

    @SerializedName("status")
    public String status;
}
