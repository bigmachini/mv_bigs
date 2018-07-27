package net.bigmachini.mv_bigs.services;

import net.bigmachini.mv_bigs.structures.BaseStructure;
import net.bigmachini.mv_bigs.structures.DeviceStructure;
import net.bigmachini.mv_bigs.structures.LoginStructure;
import net.bigmachini.mv_bigs.structures.RecordStructure;
import net.bigmachini.mv_bigs.structures.ResetPinStructure;
import net.bigmachini.mv_bigs.structures.UserStructure;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Wangia.Elisha on 3/20/2018.
 */

public interface MyAPI {
    @FormUrlEncoded
    @POST("/login_user")
    Call<APIResponse<LoginStructure>> loginUser(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("forgot_pin")
    Call<APIResponse<ResetPinStructure>> forgotPin(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("assign_device")
    Call<APIListResponse<DeviceStructure>> assignDevice(@FieldMap HashMap<String, Object> params);


    @FormUrlEncoded
    @POST("create_device_user")
    Call<APIListResponse<UserStructure>> createDeviceUser(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("check_account")
    Call<APIResponse<Boolean>> checkAccount(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/create_user")
    Call<APIResponse<BaseStructure>> createUser(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/get_devices")
    Call<APIListResponse<DeviceStructure>> getDevices(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/get_device_users")
    Call<APIListResponse<UserStructure>> getDeviceUser(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/get_device_user_records")
    Call<APIListResponse<RecordStructure>> getUserRecords(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/create_record")
    Call<APIListResponse<RecordStructure>> createUserRecord(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/delete_device_user")
    Call<APIListResponse<UserStructure>> deleteUser(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/delete_record")
    Call<APIListResponse<RecordStructure>> deleteRecorod(@FieldMap HashMap<String, Object> params);
}