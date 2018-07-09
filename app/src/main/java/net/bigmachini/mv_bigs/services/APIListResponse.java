package net.bigmachini.mv_bigs.services;

import com.google.gson.annotations.SerializedName;

import net.bigmachini.mv_bigs.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wangia.Elisha on 3/19/2018.
 */

public class APIListResponse<T> extends BaseModel {
    @SerializedName("data")
    public List<T> data = new ArrayList<T>();

    @SerializedName("message")
    public Object strMessage = "";
}
