package com.sdt.nepush.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdt.nepush.R;
import com.sdt.nepush.db.User2Model;

/**
 * Created by sdt13411 on 2019/8/10.
 */

public class MineFragment extends Fragment {

    private TextView tvCurrent;
    private User2Model mCurrentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCurrent = view.findViewById(R.id.tv_current);
        initUser();
    }

    private void initUser() {
        mCurrentUser = User2Model.getLoginUser();
        if (mCurrentUser == null) {
            return;
        }
        tvCurrent.append(mCurrentUser.getUserName());
    }
}
