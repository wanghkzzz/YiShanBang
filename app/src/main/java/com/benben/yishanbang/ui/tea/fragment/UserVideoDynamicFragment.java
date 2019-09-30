package com.benben.yishanbang.ui.tea.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benben.commoncore.utils.JSONUtils;
import com.benben.commoncore.utils.RxBus;
import com.benben.commoncore.utils.ToastUtils;
import com.benben.yishanbang.MyApplication;
import com.benben.yishanbang.R;
import com.benben.yishanbang.api.NetUrlUtils;
import com.benben.yishanbang.base.LazyBaseFragments;
import com.benben.yishanbang.config.Constants;
import com.benben.yishanbang.http.BaseCallBack;
import com.benben.yishanbang.http.BaseOkHttpClient;
import com.benben.yishanbang.ui.tea.adapter.UserAllDynamicAdapter;
import com.benben.yishanbang.ui.tea.bean.UserDynamicBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;

/**
 * Created by Administrator on 2019/8/13 0013
 * Describe:他的动态页面（视频动态）
 */
public class UserVideoDynamicFragment extends LazyBaseFragments {
    @BindView(R.id.rlv_user_video_dynamic)
    RecyclerView rlvUserVideoDynamic;
    @BindView(R.id.llyt_no_data)
    LinearLayout llytNoData;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    private UserAllDynamicAdapter mUserVideoDynamicAdapter;//视频动态适配器
    private int mPage = 1;
    private int mPageSize = 15;
    private boolean isRefresh = true;
    private boolean isMine;
    private int mType;

    @Override
    public View bindLayout(LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.frag_user_video_ynamic, null);
        return mRootView;
    }

    @Override
    public void initView() {
        isMine = getArguments().getBoolean("isMine");
        mType = getArguments().getInt("type", 0);
        //视频动态
        mUserVideoDynamicAdapter = new UserAllDynamicAdapter(mContext, isMine, mType);
        rlvUserVideoDynamic.setLayoutManager(new LinearLayoutManager(mContext));
        rlvUserVideoDynamic.setAdapter(mUserVideoDynamicAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                mPage = 1;
                getList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isRefresh = false;
                mPage++;
                getList();
            }
        });

        mUserVideoDynamicAdapter.setOnDeleteDynamicListener(new UserAllDynamicAdapter.OnDeleteDynamicListener() {
            @Override
            public void deleteDynamicListener(int position, UserDynamicBean mUserDynamicBean) {
                deleteDynamic(mUserDynamicBean);
            }
        });

        //刷新动态
        RxBus.getInstance().toObservable(Integer.class)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer == Constants.REFRESH_USER_DETAILS_INFO) {
                            getList();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //删除动态
    private void deleteDynamic(UserDynamicBean mUserDynamicBean) {

        BaseOkHttpClient.newBuilder()
                .addParam("id", mUserDynamicBean.getId())
                .url(NetUrlUtils.DELETE_USER_DYNAMIC)
                .post()
                .build().enqueue(mContext, new BaseCallBack<String>() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtils.show(mContext, msg);
                getList();
            }

            @Override
            public void onError(int code, String msg) {
                ToastUtils.show(mContext, msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });


    }

    @Override
    public void initData() {
        getList();
    }

    //获取订单列表
    private void getList() {
        BaseOkHttpClient.newBuilder()
                .addParam("userId", MyApplication.mPreferenceProvider.getUId())
                .addParam("type", "1")
                .url(NetUrlUtils.GET_USER_DYNAMIC)
                .post()
                .build().enqueue(mContext, new BaseCallBack<String>() {
            @Override
            public void onSuccess(String json, String msg) {
                List<UserDynamicBean> userDynamicBeans = JSONUtils.jsonString2Beans(json, UserDynamicBean.class);
                mUserVideoDynamicAdapter.refreshList(userDynamicBeans);
                if (mUserVideoDynamicAdapter.getItemCount() > 0) {
                    llytNoData.setVisibility(View.GONE);
                    rlvUserVideoDynamic.setVisibility(View.VISIBLE);
                } else {
                    llytNoData.setVisibility(View.VISIBLE);
                    rlvUserVideoDynamic.setVisibility(View.GONE);
                }

                refreshLayout.finishRefresh(true);
                refreshLayout.finishLoadMore(true);
            }

            @Override
            public void onError(int code, String msg) {
                refreshLayout.finishRefresh(true);
                refreshLayout.finishLoadMore(true);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh(true);
                refreshLayout.finishLoadMore(true);
            }
        });

    }
}
