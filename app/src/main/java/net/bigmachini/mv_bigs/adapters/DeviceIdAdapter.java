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
import net.bigmachini.mv_bigs.db.controllers.RecordController;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;

import java.util.List;

public class DeviceIdAdapter extends RecyclerView.Adapter<DeviceIdAdapter.ViewHolder> {
    private final Context mContext;
    private List<RecordEntity> mRecords;
    ProgressDialog progressDialog;
    RecordController mRecordController;

    public DeviceIdAdapter(Context context, ProgressDialog progressDialog) {
        this.mContext = context;
        mRecordController = new RecordController(mContext);
        this.mRecords = mRecordController.getRecordsByUserId(Global.gSelectedUser.getId());
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
        final RecordEntity record = mRecords.get(position);
        Global.gSelectedRecord = record;
        holder.tvName.setText(Global.gSelectedUser.getName() + " : " + record.getName());
        holder.view.setBackgroundColor(Color.LTGRAY);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.CheckConnection(mContext)) {
                    if (((DeviceIdActivity) mContext).bluetoothSerial.checkBluetooth()) {
                        if (!((DeviceIdActivity) mContext).bluetoothSerial.isConnected()) {
                            Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                        } else {
                            if (Utils.CheckConnection(mContext)) {
                                Global.gSelectedAction = Constants.DELETE;
                                Global.gSelectedKey =  Integer.parseInt(mRecords.get(position).getName());
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                progressDialog.setMessage(mContext.getString(R.string.delete_record));
                                progressDialog.setCancelable(true);
                                progressDialog.show();
                                Utils.sendMessage(((DeviceIdActivity) mContext).bluetoothSerial, Constants.DELETE, mRecords.get(position).getName());
                            } else {
                                Utils.toastText(mContext, mContext.getString(R.string.no_internet));
                            }
                        }
                    } else

                    {
                        ((DeviceIdActivity) mContext).enableBluetooth();
                    }

                } else {
                    Utils.toastText(mContext, mContext.getString(R.string.no_internet));
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRecords.size();
    }


}
