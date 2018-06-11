package net.bigmachini.mv_bigs.models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Utils;

import java.util.List;
import java.util.UUID;

public class UserModel {
    private String uuid;
    public String name;
    public List<Integer> ids;
    private boolean isSelected;

    public void createUser(Context mContext, String name) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        List<UserModel> users = getUsers(mContext);
        users.add(this);
        Utils.setStringSetting(mContext, Constants.USERS, new Gson().toJson(users));
    }

    public void addKey(int key) {
        this.ids.add(key);
    }

    public void deleteKey(int key) {
        int i = 0;
        for (int id : this.ids) {
            if (id == key) {
                ids.remove(i);
                return;
            }

            i++;
        }
    }

    public String getIds() {
        StringBuilder sb = new StringBuilder();
        for (int id : ids) {
            sb.append(id + " ");
        }

        return sb.toString();
    }

    public static List<UserModel> getUsers(Context mContext) {
        return new Gson().fromJson(Utils.getStringSetting(mContext, Constants.USERS, "[]"), new TypeToken<List<UserModel>>() {
        }.getType());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserModel) {
            if (new String(this.uuid).equals(new String(((UserModel) obj).uuid)))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}
