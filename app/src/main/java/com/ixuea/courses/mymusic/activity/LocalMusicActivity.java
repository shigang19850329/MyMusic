package com.ixuea.courses.mymusic.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.SongAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.ScanMusicCompleteEvent;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.fragment.SelectListDialogFragment;
import com.ixuea.courses.mymusic.fragment.SortDialogFragment;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocalMusicActivity extends BaseMusicPlayerActivity implements SongAdapter.OnSongListener {

    private static final String TAG = "TAG";

    private RecyclerView rv;
    private View ll_play_all_container;
    private TextView tv_count;

    private java.util.List<Song> songs;

    private SongAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        downloadManager = DownloadService.getDownloadManager(getApplicationContext());

        EventBus.getDefault().register(this);

        adapter = new SongAdapter(getActivity(), R.layout.item_song_detail, getSupportFragmentManager(), playListManager,downloadManager);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                play(position);
            }
        });
        adapter.setOnSongListener(this);
        adapterWrapper = new LRecyclerViewAdapter(adapter);
        adapterWrapper.addHeaderView(createHeaderView());

        rv.setAdapter(adapterWrapper);

        fetchData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ll_play_all_container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_all_container:
                play(0);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void fetchData() {
        List<Song> songs = getData();
        if (songs != null && songs.size() > 0) {
            setData(songs);
        } else {
            toScanLocalMusic();
        }
    }

    private void toScanLocalMusic() {
        //去扫描本地歌曲
        startActivity(ScanLocalMusicActivity.class);
    }

    private void setData(List<Song> songs) {
        this.songs = songs;
        adapter.setData(songs);
        tv_count.setText(getResources().getString(R.string.music_count, songs.size()));
    }

    private List<Song> getData() {
        return orm.queryLocalMusic(sp.getUserId(), Song.SORT_KEYS[sp.getLocalMusicSortKey()]);
    }

    //扫描完成的事件。
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanMusicCompleteEvent(ScanMusicCompleteEvent event) {
        List<Song> songs = getData();
        if (songs != null && songs.size() > 0) {
            setData(songs);
        }
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_local_song, (ViewGroup) rv.getParent(), false);
        ll_play_all_container = top.findViewById(R.id.ll_play_all_container);
        tv_count = top.findViewById(R.id.tv_count);

        return top;
    }

    private void play(int position) {
        if (adapter.getDatas().size() > 0) {
            Song data = adapter.getData(position);
            playListManager.setPlayList(adapter.getDatas());
            playListManager.play(data);
            adapter.notifyDataSetChanged();
            startActivity(MusicPlayerActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_scan_local_music:
                toScanLocalMusic();
                break;
            case R.id.action_select_sort:
                showSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        SortDialogFragment sortDialogFragment = new SortDialogFragment();
        sortDialogFragment.show(getSupportFragmentManager(), sp.getLocalMusicSortKey(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: " + which);
                dialog.dismiss();
                sp.setLocalMusicSortKey(which);
                fetchData();
            }
        });
    }
    @Override
    public void onCollectionClick(final Song song) {
        //ToastUtil.showSortToast(getActivity(),song.getTitle());
        //获取我创建的歌单，然后显示选择歌单Fragment，选择完成后调用接口收藏
        Observable<ListResponse<com.ixuea.courses.mymusic.domain.List>> list = Api.getInstance().listsMyCreate();
        list.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<com.ixuea.courses.mymusic.domain.List>>((BaseActivity)getActivity()){
                    @Override
                    public void onSucceeded(final ListResponse<com.ixuea.courses.mymusic.domain.List> data) {
                        super.onSucceeded(data);
                        SelectListDialogFragment.show(getSupportFragmentManager(), data.getData(), new SelectListDialogFragment.OnSelectListListener() {
                            @Override
                            public void onSelectListClick(com.ixuea.courses.mymusic.domain.List list) {
                                collectionSong(song,list);
                            }
                        });
                    }
                });
    }

    @Override
    public void onDownloadClick(Song song) {

    }

    @Override
    public void onDeleteClick(Song song) {

    }
    private void collectionSong(Song song, com.ixuea.courses.mymusic.domain.List list) {
        //将song收藏到list
        Api.getInstance().addSongInSheet(song.getId(),list.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<com.ixuea.courses.mymusic.domain.List>>(getActivity()){
                    @Override
                    public void onSucceeded(DetailResponse<com.ixuea.courses.mymusic.domain.List> data) {
                        super.onSucceeded(data);
                        ToastUtil.showSortToast(getActivity(),getString(R.string.song_like_success));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
