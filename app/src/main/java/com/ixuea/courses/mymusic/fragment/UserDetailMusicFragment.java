package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.ListDetailActivity;
import com.ixuea.courses.mymusic.adapter.MeAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.List;
import com.ixuea.courses.mymusic.domain.MeUI;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.ToastUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserDetailMusicFragment extends BaseCommonFragment implements MeAdapter.OnMeListener, ExpandableListView.OnChildClickListener {
    private ExpandableListView elv;
    private MeAdapter adapter;
    //private DownloadManager downloadManager;

    public static UserDetailMusicFragment newInstance() {

        Bundle args = new Bundle();
        UserDetailMusicFragment fragment = new UserDetailMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        elv = (ExpandableListView)findViewById(R.id.elv);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        //downloadManager = DownloadService.getDownloadManager(getActivity().getApplicationContext());

        adapter = new MeAdapter(getActivity());
        adapter.setOnMeListener(this);
        elv.setAdapter(adapter);

        fetchData();
    }

    private void fetchData() {
        final ArrayList<MeUI> d = new ArrayList<>();

        Observable<ListResponse<List>> list = Api.getInstance().listsMyCreate();
        list.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<List>>(getMainActivity()){
                    @Override
                    public void onSucceeded(final ListResponse<List> data) {
                        super.onSucceeded(data);
                        d.add(new MeUI("Ta创建的歌单",data.getData()));

                        Api.getInstance().listsMyCollection().subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HttpListener<ListResponse<List>>(getMainActivity()){
                                    @Override
                                    public void onSucceeded(final ListResponse<List> data) {
                                        super.onSucceeded(data);
                                        d.add(new MeUI("Ta收藏的歌单",data.getData()));
                                        adapter.setData(d);
                                    }
                                });
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        elv.setOnChildClickListener(this);
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me,null);
    }

    /**
     * adapter.setOnMeListener(this);
     */
    @Override
    public void onListGroupSettingsClick() {

    }

    /**
     *
     * elv.setOnChildClickListener(this);
     *
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        List data = adapter.getChildData(groupPosition,childPosition);
        startActivityExtraId(ListDetailActivity.class,data.getId());
        return true;
    }
    private void createDialog(String text){
        List list = new List();
        //这里不爱传用户id，不然这是一个漏洞，就可以给任何人创建歌单
        //而是服务端根据token获取用户信息
        list.setTitle(text);

        Api.getInstance().createList(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<List>>(getMainActivity()){
                    @Override
                    public void onSucceeded(DetailResponse<List> data) {
                        super.onSucceeded(data);
                        ToastUtil.showSortToast(getMainActivity(),getString(R.string.list_create_susscess));
                        fetchData();
                    }
                });
    }
}
