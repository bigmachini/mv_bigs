package net.bigmachini.mv_bigs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context mContext;
    private List<UserModel> mUsers;
    Button btnDelete;

    public UserAdapter(Context context, List<UserModel> mUsers, Button btnDelete) {
        this.mContext = context;
        this.mUsers = mUsers;
        this.btnDelete = btnDelete;
        showDeleteButton();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        TextView tvName;
        CheckBox cbSelected;
        ImageView ivAdd;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvName = view.findViewById(R.id.tv_name);
            cbSelected = view.findViewById(R.id.cb_selected);
            ivAdd = view.findViewById(R.id.iv_add);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_users, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserModel user = mUsers.get(position);
        holder.tvName.setText(user.name);
        holder.view.setBackgroundColor(Color.LTGRAY);
        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setSelected(!user.isSelected());
                holder.view.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.LTGRAY);
                showDeleteButton();

            }
        });

        holder.view.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.LTGRAY);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.createUser(mContext);
            }
        });
    }

    private void showDeleteButton() {
        boolean showButton = false;
        int count = 0;

        for (UserModel user : mUsers) {
            if (user.isSelected() == true) {
                showButton = true;
                count ++;
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
