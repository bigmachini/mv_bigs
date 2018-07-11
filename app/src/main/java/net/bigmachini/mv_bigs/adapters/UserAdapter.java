package net.bigmachini.mv_bigs.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.activities.DeviceIdActivity;
import net.bigmachini.mv_bigs.activities.HomeActivity;
import net.bigmachini.mv_bigs.db.controllers.RecordController;
import net.bigmachini.mv_bigs.db.controllers.UserController;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.db.entities.UserEntity;

import java.util.List;
import java.util.Random;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context mContext;
    private List<UserEntity> mUsers;
    ProgressDialog progressDialog;
    private UserController mUserController;
    private RecordController mRecordController;
    Button btnDelete;

    public UserAdapter(Context context, Button btnDelete, ProgressDialog progressDialog) {
        this.mContext = context;
        mUserController = new UserController(context);
        mRecordController = new RecordController(context);
        this.mUsers = mUserController.getUserByDeviceId(Global.gSelectedDevice.getId());
        this.btnDelete = btnDelete;
        this.progressDialog = progressDialog;
        showDeleteButton();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        TextView tvName, tvIds;
        CheckBox cbSelected;
        ImageView ivAdd;
        LinearLayout ll_checkbox, ll_add;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvName = view.findViewById(R.id.tv_name);
            cbSelected = view.findViewById(R.id.cb_selected);
            ivAdd = view.findViewById(R.id.iv_add);
            ll_checkbox = view.findViewById(R.id.ll_checkbox);
            ll_add = view.findViewById(R.id.ll_add);
            tvIds = view.findViewById(R.id.tv_ids);
        }

        ;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_users, parent, false);
        return new UserAdapter.ViewHolder(itemView);
    }

    public void updateList(List<UserEntity> users) {
        saveUser(users);
        updateList();
    }

    private void saveUser(List<UserEntity> users) {
        for (UserEntity userEntity : users) {
            mUserController.createUser(userEntity);
        }
    }

    public void updateList() {
        this.mUsers = mUserController.getUserByDeviceId(Global.gSelectedDevice.getId());
        showDeleteButton();
        notifyDataSetChanged();
    }

    public List<UserEntity> getUsers() {
        return mUsers;
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {
        final UserEntity user = mUsers.get(position);
        holder.tvName.setText(user.getName());
        holder.cbSelected.setChecked(user.isSelected());
        holder.tvIds.setText(getIds(mRecordController.getRecordsByUserId(user.getId())));
        holder.view.setBackgroundColor(Color.LTGRAY);

        holder.ll_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cbSelected.setChecked(!holder.cbSelected.isChecked());
            }
        });

        holder.ll_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((HomeActivity) mContext).bluetoothSerial.checkBluetooth()) {
                    if (!((HomeActivity) mContext).bluetoothSerial.isConnected()) {
                        Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                    } else {

                        int key = Utils.incrementCounter(mContext, 1);
                        Global.gSelectedKey = key;
                        Global.gSelectedUser = user;
                        Global.gSelectedAction = Constants.ENROLL;
                        Utils.sendMessage(((HomeActivity) mContext).bluetoothSerial, Constants.ENROLL, String.valueOf(key));
                    }
                } else {
                    ((HomeActivity) mContext).enableBluetooth();
                }
            }
        });

        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setSelected(isChecked);
                holder.view.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.LTGRAY);
                mUserController.createUser(user);
                updateList();
            }
        });

        holder.view.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.LTGRAY);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.gSelectedUser = user;
                mContext.startActivity(new Intent(mContext, DeviceIdActivity.class));
            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((HomeActivity) mContext).bluetoothSerial.checkBluetooth()) {

                    if (!((HomeActivity) mContext).bluetoothSerial.isConnected()) {
                        Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                    } else {
                        List<RecordEntity> records;
                        int recordId;
                        Global.gSelectedUser = user;
                        do {
                            recordId = new Random().nextInt(127);
                            records = mRecordController.getRecordByUser(String.valueOf(recordId), Global.gSelectedUser.getId());
                        } while (records.size() > 0);
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        progressDialog.setMessage("Adding Record .. ");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        Global.gSelectedKey = recordId;

                        Global.gSelectedAction = Constants.ENROLL;
                        Utils.sendMessage(((HomeActivity) mContext).bluetoothSerial, Constants.ENROLL, String.valueOf(recordId));
                    }
                } else {
                    ((HomeActivity) mContext).enableBluetooth();
                }
            }
        });
    }


    public String getIds(List<RecordEntity> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Records: ");
        for (RecordEntity recordEntity : data) {
            sb.append(recordEntity.getId() + ", ");
        }

        return sb.toString();
    }


    private void showDeleteButton() {
        boolean showButton = false;
        int count = 0;

        for (UserEntity user : mUsers) {
            if (user.isSelected() == true) {
                showButton = true;
                count++;
            }
        }

        if (showButton == true) {
            btnDelete.setText("Delete ( " + count + " )");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
