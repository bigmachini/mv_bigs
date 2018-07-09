package net.bigmachini.mv_bigs.services;

import com.google.gson.annotations.SerializedName;

import net.bigmachini.mv_bigs.models.BaseModel;

/**
 * Created by Wangia.Elisha on 3/19/2018.
 */

public class APIResponse<T> extends BaseModel {
    @SerializedName("data")
    public T data = (T) new Object();

    @SerializedName("message")
    public Object strMessage = "";
}
