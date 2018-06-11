package net.bigmachini.mv_bigs.adapters;

public class PairedDeviceAdapter  {

 /*   private final Context mContext;
    private final List<BluetoothDeviceDecorator> mDevices;
    private final LayoutInflater mInflater;
    private OnAdapterItemClickListener mOnItemClickListener;


    public PairedDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        super();
        mContext = context;
        mDevices = decorateDevices(devices);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static List<BluetoothDeviceDecorator> decorateDevices(Collection<BluetoothDevice> btDevices) {
        List<BluetoothDeviceDecorator> devices = new ArrayList<>();
        for (BluetoothDevice dev : btDevices) {
            devices.add(new BluetoothDeviceDecorator(dev, 0));
        }
        return devices;
    }

    public PairedDeviceAdapter(Context context, Set<BluetoothDevice> devices) {
        this(context, new ArrayList<>(devices));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = new ViewHolder(mInflater.inflate(R.layout.list_item_devices, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BluetoothDeviceDecorator device = mDevices.get(position);

        holder.tvName.setText(device.getAddress() + " : " + device.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(device, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public List<BluetoothDeviceDecorator> getDevices() {
        return mDevices;
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnAdapterItemClickListener {
        public void onItemClick(BluetoothDeviceDecorator device, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }*/
}
