package net.bigmachini.mv_bigs.structures;

import com.google.gson.annotations.SerializedName;

public class DeviceStructure extends BaseStructure{

    @SerializedName("mac_addresss")
    public String macAddress;

    @SerializedName("status")
    public boolean status;
}
