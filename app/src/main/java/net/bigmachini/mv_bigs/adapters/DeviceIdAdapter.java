package net.bigmachini.mv_bigs.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.activities.DeviceIdActivity;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.List;

public class DeviceIdAdapter extends RecyclerView.Adapter<DeviceIdAdapter.ViewHolder> {
    private final Context mContext;
    private List<Integer> mDevices;
    UserModel userModel;
    ProgressDialog progressDialog;

    public DeviceIdAdapter(Context context, List<Integer> mDevices, UserModel userModel, ProgressDialog progressDialog) {
        this.mContext = context;
        this.mDevices = mDevices;
        this.userModel = userModel;
        this.progressDialog = progressDialog;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        TextView tvName;
        ImageView ivDelete;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvName = view.findViewById(R.id.tv_name);
            ivDelete = view.findViewById(R.id.iv_delete);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public DeviceIdAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_devices, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int device = mDevices.get(position);
        holder.tvName.setText(userModel.name + " : " + device);
        holder.view.setBackgroundColor(Color.LTGRAY);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((DeviceIdActivity) mContext).bluetoothSerial.checkBluetooth()) {
                    if (!((DeviceIdActivity) mContext).bluetoothSerial.isConnected()) {
                        Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        progressDialog.setMessage("Deleting Record .. ");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        Global.gSelectedAction = Constants.DELETE;
                        Global.gSelectedKey = mDevices.get(position);
                        Utils.sendMessage(((DeviceIdActivity) mContext).bluetoothSerial, Constants.DELETE, mDevices.get(position));
                        mDevices.remove(position);
                        userModel.ids = mDevices;
                        Global.gSelectedUser = userModel;
                    }
                } else

                {
                    ((DeviceIdActivity) mContext).enableBluetooth();
                }
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
