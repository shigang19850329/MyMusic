package com.ixuea.courses.mymusic.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Session;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.event.LoginSuccessEvent;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseTitleActivity {
    @BindView(R.id.et_nickname)
    EditText et_nickname;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.et_phone)
    EditText et_phone;

    private String phone;
    private String nickname;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //开启返回菜单
        enableBackMenu();
    }

    @OnClick(R.id.bt_register)
    public void bt_register(){
         nickname = et_nickname.getText().toString();
        if (StringUtils.isBlank(nickname)){
            ToastUtil.showSortToast(getActivity(),R.string.enter_nickname);
            return;
        }
        if (nickname.contains(" ")){
            //更复杂的，建议用正则表达式
            ToastUtil.showSortToast(getActivity(),R.string.nickname_space);
            return;
        }

        phone = et_phone.getText().toString();
        /**
         * 如果是空的，我们就给一个提示
         */
        if (StringUtils.isBlank(phone)){
            ToastUtil.showSortToast(getActivity(),R.string.hint_phone);
            return;
        }
        if (!StringUtil.isPhone(phone)){
            ToastUtil.showSortToast(getActivity(),R.string.hint_error_phone);
            return;
        }
        password=et_password.getText().toString();
        if (StringUtils.isBlank(password)){
            ToastUtil.showSortToast(getActivity(), R.string.hint_password);
            return;
        }
        if (!StringUtil.isPassword(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_password_format);
            return;
        }
        //以上这些是对用户名，手机号和密码格式是否正确的检测。
        User user = new User();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(password);
        user.setType(User.TYPE_PHONE);

        Api.getInstance().register(user)
                .subscribeOn(Schedulers.io())
                //在Android主线程中运行
                .observeOn(AndroidSchedulers.mainThread())
                //设置一个监听器，RxJava这是一个Observer，Observer是一个接口，这里改成了一个适配器。
                .subscribe(new HttpListener<DetailResponse<Session>>(getActivity()){
                    @Override
                    public void onSucceeded(DetailResponse<Session> data){
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }
    public void next(Session session){
        //登录完了以后我们把用户名和信息保存起来。
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setType(User.TYPE_PHONE);

        Api.getInstance().login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Session>>(getActivity()){
                    @Override
                    public void onSucceeded(DetailResponse<Session> data) {
                        super.onSucceeded(data);
                        sp.setToken(data.getData().getToken());
                        sp.setUserId(data.getData().getId());
                        sp.setIMToken(data.getData().getIm_token());
                        //开启MainActivity并且将次界面关闭掉。
                        startActivityAfterFinishThis(MainActivity.class);

                        //发布登录成功消息，登录界面好自动关闭
                        EventBus.getDefault().post(new LoginSuccessEvent());
                    }
                });
    }
}
