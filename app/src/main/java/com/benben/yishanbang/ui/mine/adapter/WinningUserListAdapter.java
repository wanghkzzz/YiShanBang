package com.benben.yishanbang.ui.mine.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benben.commoncore.utils.ImageUtils;
import com.benben.commoncore.widget.CircleImageView;
import com.benben.yishanbang.R;
import com.benben.yishanbang.adapter.AFinalRecyclerViewAdapter;
import com.benben.yishanbang.adapter.BaseRecyclerViewHolder;
import com.benben.yishanbang.config.Constants;
import com.benben.yishanbang.ui.mine.bean.LotteryLuckyListBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by wanghk on 2019-08-07.
 * Describe:中奖用户
 */
public class WinningUserListAdapter extends AFinalRecyclerViewAdapter<LotteryLuckyListBean.LuckyUserBean> {

    public WinningUserListAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateCustomerViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(m_Inflater.inflate(R.layout.item_winning_user_list, parent, false));
    }

    @Override
    protected void onBindCustomerViewHolder(BaseRecyclerViewHolder holder, int position) {
        ((CommonViewHolder) holder).setContent(getItem(position), position);
    }

    public class CommonViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.civ_avatar)
        CircleImageView civAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;

        View itemView;

        public CommonViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView = view;
        }

        private void setContent(LotteryLuckyListBean.LuckyUserBean mLuckyUserBean, int position) {

            ImageUtils.getPic(Constants.IMAGE_BASE_URL +mLuckyUserBean.getAvatar(),civAvatar,m_Context,R.mipmap.icon_default_avatar);
            tvName.setText(mLuckyUserBean.getNickname());
        }
    }
}