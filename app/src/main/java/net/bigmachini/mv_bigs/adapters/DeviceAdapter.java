package net.bigmachini.mv_bigs.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.bigmachini.mv_bigs.R;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final Context mContext;
    private List<BluetoothDevice> mDevices;
    Button btnDelete;

    public DeviceAdapter(Context context, List<BluetoothDevice> mDevices) {
        this.mContext = context;
        this.mDevices = mDevices;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        TextView tvName;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvName = view.findViewById(R.id.tv_name);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_devices, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BluetoothDevice device = mDevices.get(position);
        holder.tvName.setText(device.getAddress() + " : " + device.getName());

        holder.view.setBackgroundColor(Color.LTGRAY);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
