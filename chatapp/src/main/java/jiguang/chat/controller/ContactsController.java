package jiguang.chat.controller;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.R;
import jiguang.chat.activity.GroupActivity;
import jiguang.chat.activity.SearchContactsActivity;
import jiguang.chat.activity.SearchForAddFriendActivity;
import jiguang.chat.activity.VerificationMessageActivity;
import jiguang.chat.adapter.StickyListAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.UserEntry;
import jiguang.chat.utils.pinyin.HanziToPinyin;
import jiguang.chat.utils.pinyin.PinyinComparator;
import jiguang.chat.utils.sidebar.SideBar;
import jiguang.chat.view.ContactsView;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ContactsController implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener {
    private ContactsView mContactsView;
    private Activity mContext;
    private List<FriendEntry> mList = new ArrayList<>();
    private StickyListAdapter mAdapter;
    private TextView mAllContactNumber;
    private List<FriendEntry> forDelete = new ArrayList<>();


    public ContactsController(ContactsView mContactsView, FragmentActivity context) {
        this.mContactsView = mContactsView;
        this.mContext = context;
        mAllContactNumber = mContext.findViewById(R.id.all_contact_number);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();//标题栏加号添加好友
//验证消息
//群组
//查找
        if (i == R.id.ib_goToAddFriend) {
            intent.setClass(mContext, SearchForAddFriendActivity.class);
            mContext.startActivity(intent);
        } else if (i == R.id.verify_ll) {//intent.setClass(mContext, FriendRecommendActivity.class);
            intent.setClass(mContext, VerificationMessageActivity.class);
            mContext.startActivity(intent);
            mContactsView.dismissNewFriends();
            mAllContactNumber.setVisibility(View.GONE);
        } else if (i == R.id.group_ll) {
            intent.setClass(mContext, GroupActivity.class);
            mContext.startActivity(intent);
        } else if (i == R.id.search_title) {
            intent.setClass(mContext, SearchContactsActivity.class);
            mContext.startActivity(intent);
        }
    }


    public void initContacts() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mContactsView.showLoadingHeader();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                mContactsView.dismissLoadingHeader();
                if (responseCode == 0) {
                    if (userInfoList != null && userInfoList.size() != 0) {
                        mContactsView.dismissLine();
                        ActiveAndroid.beginTransaction();
                        try {
                            for (UserInfo userInfo : userInfoList) {
                                String displayName = userInfo.getDisplayName();
                                String letter;
                                if (!TextUtils.isEmpty(displayName.trim())) {
                                    ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                                            .get(displayName);
                                    StringBuilder sb = new StringBuilder();
                                    if (tokens != null && tokens.size() > 0) {
                                        for (HanziToPinyin.Token token : tokens) {
                                            if (token.type == HanziToPinyin.Token.PINYIN) {
                                                sb.append(token.target);
                                            } else {
                                                sb.append(token.source);
                                            }
                                        }
                                    }
                                    String sortString = sb.toString().substring(0, 1).toUpperCase();
                                    if (sortString.matches("[A-Z]")) {
                                        letter = sortString.toUpperCase();
                                    } else {
                                        letter = "#";
                                    }
                                } else {
                                    letter = "#";
                                }
                                //避免重复请求时导致数据重复A
                                FriendEntry friend = FriendEntry.getFriend(user,
                                        userInfo.getUserName(), userInfo.getAppKey());
                                if (null == friend) {
                                    if (TextUtils.isEmpty(userInfo.getAvatar())) {
                                        friend = new FriendEntry(userInfo.getUserID(), userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
                                                null, displayName, letter, user);
                                    } else {
                                        friend = new FriendEntry(userInfo.getUserID(), userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
                                                userInfo.getAvatarFile().getAbsolutePath(), displayName, letter, user);
                                    }
                                    friend.save();
                                    mList.add(friend);
                                }
                                forDelete.add(friend);
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                    } else {
                        mContactsView.showLine();
                    }
                    //其他端删除好友后,登陆时把数据库中的也删掉
                    List<FriendEntry> friends = JGApplication.getUserEntry().getFriends();
                    friends.removeAll(forDelete);
                    for (FriendEntry del : friends) {
                        del.delete();
                        mList.remove(del);
                    }
                    Collections.sort(mList, new PinyinComparator());
                    mAdapter = new StickyListAdapter(mContext, mList, false);
                    mContactsView.setAdapter(mAdapter);
                }
            }
        });

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        if (null != mAdapter) {
            int position = mAdapter.getSectionForLetter(s);
            if (position != -1 && position < mAdapter.getCount()) {
                mContactsView.setSelection(position);
            }
        }
    }

    public void refresh(FriendEntry entry) {
        mList.add(entry);
        if (null == mAdapter) {
            mAdapter = new StickyListAdapter(mContext, mList, false);
        } else {
            Collections.sort(mList, new PinyinComparator());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void refreshContact() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mList = user.getFriends();
        Collections.sort(mList, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mList, false);
        mContactsView.setAdapter(mAdapter);
    }

}
