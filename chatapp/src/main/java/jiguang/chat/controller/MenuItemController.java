package jiguang.chat.controller;

import android.content.Intent;
import android.view.View;

import jiguang.chat.R;
import jiguang.chat.activity.SearchAddOpenGroupActivity;
import jiguang.chat.activity.CommonScanActivity;
import jiguang.chat.activity.CreateGroupActivity;
import jiguang.chat.activity.SearchForAddFriendActivity;
import jiguang.chat.activity.fragment.ConversationListFragment;
import jiguang.chat.model.Constant;

/**
 * Created by ${chenyn} on 2017/4/9.
 */

public class MenuItemController implements View.OnClickListener {
    private ConversationListFragment mFragment;

    public MenuItemController(ConversationListFragment fragment) {
        this.mFragment = fragment;
    }

    //会话界面的加号
    @Override
    public void onClick(View v) {
        Intent intent;
        int i = v.getId();
        if (i == R.id.create_group_ll) {
            mFragment.dismissPopWindow();
            intent = new Intent(mFragment.getContext(), CreateGroupActivity.class);
            mFragment.getContext().startActivity(intent);
        } else if (i == R.id.add_friend_with_confirm_ll) {
            mFragment.dismissPopWindow();
            intent = new Intent(mFragment.getContext(), SearchForAddFriendActivity.class);
            intent.setFlags(1);
            mFragment.startActivity(intent);
        } else if (i == R.id.send_message_ll) {
            mFragment.dismissPopWindow();
            intent = new Intent(mFragment.getContext(), SearchForAddFriendActivity.class);
            intent.setFlags(2);
            mFragment.startActivity(intent);
            //扫描二维码
        } else if (i == R.id.ll_saoYiSao) {
            intent = new Intent(mFragment.getContext(), CommonScanActivity.class);
            intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_QRCODE_MODE);
            mFragment.getContext().startActivity(intent);
            //加入公开群
        } else if (i == R.id.add_open_group) {
            intent = new Intent(mFragment.getContext(), SearchAddOpenGroupActivity.class);
            mFragment.getContext().startActivity(intent);
        }

    }
}
