package net.bigmachini.mv_bigs.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.activities.HomeActivity;
import net.bigmachini.mv_bigs.db.controllers.DeviceController;
import net.bigmachini.mv_bigs.db.entities.DeviceEntity;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final Context mContext;
    private List<DeviceEntity> devices;
    DeviceController mDeviceController;

    public DeviceAdapter(Context context) {
        this.mContext = context;
        mDeviceController = new DeviceController(mContext);
        this.devices = mDeviceController.getAllDevices();
        ContextWrapper cw = new ContextWrapper(context);

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tvMacAddress;
        TextView tvStatus;
        TextView tvSerialNo;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            tvMacAddress = view.findViewById(R.id.tv_mac_address);
            tvStatus = view.findViewById(R.id.tv_status);
            tvSerialNo = view.findViewById(R.id.tv_serial_no);
            linearLayout = view.findViewById(R.id.layout);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DeviceEntity device = devices.get(position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.gSelectedDevice = device;
                Global.gDevice = null;
                mContext.startActivity(new Intent(mContext, HomeActivity.class));
            }
        });

        holder.tvMacAddress.setText(device.getMacAddress());
        holder.tvStatus.setText(device.getStatus().toString());
        holder.tvSerialNo.setText(device.getSerialNo());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
