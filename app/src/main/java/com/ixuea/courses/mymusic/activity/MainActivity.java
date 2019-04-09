package com.ixuea.courses.mymusic.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.HomeAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.event.LoginSuccessEvent;
import com.ixuea.courses.mymusic.domain.event.LogoutSuccessEvent;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseTitleActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
   private DrawerLayout drawer_layout;
    ImageView iv_avatar;
    TextView tv_nickname;
    TextView tv_description;
    private ViewPager vp;
    private HomeAdapter adapter;
    private ImageView iv_music;
    private ImageView iv_recommend;
    private ImageView iv_video;
    private LinearLayout ll_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //也可以延迟注册，比如：当前用户点击到设置界面是才注册
        EventBus.getDefault().register(this);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_description =(TextView) findViewById(R.id.tv_description);
        //初始化三个按钮,我的音乐，我的推荐，我的视频
        iv_music = (ImageView) findViewById(R.id.iv_music);
        iv_recommend = (ImageView) findViewById(R.id.iv_recommend);
        iv_video = (ImageView) findViewById(R.id.iv_video);

        ll_settings = (LinearLayout) findViewById(R.id.ll_settings);

        vp =(ViewPager)findViewById(R.id.vp);

        //这个类要导入v7里面的。参数要设置一个字符串，再设置一个关闭的字符串。
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //添加一个监听器
        drawer_layout.addDrawerListener(toggle);
        //同步
        toggle.syncState();

        //缓存三个页面
        vp.setOffscreenPageLimit(3);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        adapter = new HomeAdapter(getActivity(),getSupportFragmentManager());
        vp.setAdapter(adapter);

        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(0);
        datas.add(1);
        datas.add(2);
        adapter.setDatas(datas);

        showUserInfo();
    }

    private void showData(User data) {
        //将显示用户信息放到单独的类中，是为了重用，因为在用户详情界面会用到
        UserUtil.showUser(getActivity(),data,iv_avatar,tv_nickname,tv_description);
    }
    @OnClick(R.id.iv_avatar)
    public void avatarClick(){
        closeDrawer();
        if (sp.isLogin()){
            startActivityExtraId(UserDetailActivity.class,sp.getUserId());
            //Log.e("MainActivity","进入用户详情页面");
        }else{
            startActivity(LoginActivity.class);
        }
    }

    /**
     * 设置点击事件
     */
    @Override
    protected void initListener() {
        super.initListener();
        iv_music.setOnClickListener(this);
        iv_recommend.setOnClickListener(this);
        iv_video.setOnClickListener(this);
        //给ViewPager添加监听器
        vp.addOnPageChangeListener(this);

        ll_settings.setOnClickListener(this);
        //默认选中第二个页面，设置监听器在选择就会调用监听器
        vp.setCurrentItem(1);
    }

    /**
     * 点击图标，切换fragment
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_music:
                //第二个参数是滚动效果，平滑滚动
                vp.setCurrentItem(0,true);
                break;
            case R.id.iv_recommend:
                vp.setCurrentItem(1,true);
                break;
            case R.id.iv_video:
                vp.setCurrentItem(2,true);
                break;
            case R.id.ll_settings:
                startActivity(SettingsActivity.class);
                closeDrawer();
                break;
                default:
                    break;
        }
    }

    /**
     * 关闭DrawerLayout
     */
    private void closeDrawer() {
        drawer_layout.closeDrawer(Gravity.START);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            iv_music.setImageResource(R.drawable.ic_play_selected);
            iv_recommend.setImageResource(R.drawable.ic_music);
            iv_video.setImageResource(R.drawable.ic_video);
        } else if (position == 1) {
            iv_music.setImageResource(R.drawable.ic_play);
            iv_recommend.setImageResource(R.drawable.ic_music_selected);
            iv_video.setImageResource(R.drawable.ic_video);
        } else {
            iv_music.setImageResource(R.drawable.ic_play);
            iv_recommend.setImageResource(R.drawable.ic_music);
            iv_video.setImageResource(R.drawable.ic_video_selected);
        }
    }

    /**
     * 拖动状态和滑动状态
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutSuccessEvent(LogoutSuccessEvent event) {
        showUserInfo();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSuccessEvent(LoginSuccessEvent event){
        showUserInfo();
    }

    private void showUserInfo() {
        //用户信息这部分，进来是看不到的，所以可以延后初始化
        if (sp.isLogin()) {
            //调用用户信息接口
            Api.getInstance().userDetail(sp.getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<User> data) {
                            super.onSucceeded(data);
                            showData(data.getData());
                        }
                    });

        } else {
            UserUtil.showNotLoginUser(getActivity(), iv_avatar, tv_nickname, tv_description);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}