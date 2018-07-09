package net.bigmachini.mv_bigs.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.activities.Home2Activity;
import net.bigmachini.mv_bigs.adapters.UserAdapter;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import static net.bigmachini.mv_bigs.activities.Home2Activity.bluetoothSerial;

/**
 * A placeholder fragment containing a simple view.
 */
public class UsersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private LinearLayoutManager mLayoutManager;
    private List<UserModel> users;
    private Button btnDelete;
    private UserAdapter mAdapter;
    ProgressDialog progressDialog;

    public UsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mContext = getActivity();

        btnDelete = view.findViewById(R.id.btn_delete);

        mRecyclerView = view.findViewById(R.id.rv_users);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        users = UserModel.getUsers(mContext);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<UserModel> dUsers = new ArrayList<>(mAdapter.getUsers());
                StringBuilder sb = new StringBuilder();
                for (int i = dUsers.size() - 1; i >= 0; i--) {
                    if (dUsers.get(i).isSelected()) {
                        UserModel user = dUsers.get(i);
                        if (user.ids.size() > 0) {
                            sb.append(user.name + "\n");
                        } else {
                            dUsers.remove(i);
                        }
                    }
                }
                if (sb.toString().isEmpty() || sb.toString().length() != 0) {
                    Utils.toastText(mContext, "Please delete all ids for user(s):\n" + sb.toString());
                }

                mAdapter.clear();
                mAdapter.updateList(dUsers);
            }
        });

        // specify an adapter (see also next example)
        progressDialog = new ProgressDialog(mContext);
        mAdapter = new UserAdapter(mContext, users, btnDelete, progressDialog);
        mRecyclerView.setAdapter(mAdapter);
        // Create a new instance of BluetoothSerial
        bluetoothSerial = ((Home2Activity) getActivity()).getBluetoothSerial();

        return view;


    }

}