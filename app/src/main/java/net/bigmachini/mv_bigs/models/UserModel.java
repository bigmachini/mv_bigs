package net.bigmachini.mv_bigs.models;

import java.util.List;
import java.util.UUID;

public class UserModel {
    private String uuid;
    public String name;
    public List<Integer> ids;

    public void createUser(String name) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
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

}
