package net.bigmachini.mv_bigs.structures;

import com.google.gson.annotations.SerializedName;

public class RecordStructure extends BaseStructure {

    @SerializedName("name")
    public String name;
    @SerializedName("user_id")
    public int userId;
}
