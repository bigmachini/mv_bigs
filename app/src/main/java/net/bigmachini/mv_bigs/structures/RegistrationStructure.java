package net.bigmachini.mv_bigs.structures;

import com.google.gson.annotations.SerializedName;

public class RegistrationStructure extends BaseStructure {

    @SerializedName("pin")
    public String pin;

    @SerializedName("phoneNumber")
    public String phoneNumber;

    @SerializedName("userId")
    public int userId;
}
