package com.ixuea.courses.mymusic.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.ConversationAdapter;
import com.ixuea.courses.mymusic.domain.event.OnMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MessageActivity extends BaseTitleActivity {
    private static final String TAG = "MessageActivity";
    private RecyclerView rv;
    private ConversationAdapter adapter;
    private RongIMClient imClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        imClient = RongIMClient.getInstance();

        adapter = new ConversationAdapter(getActivity(),R.layout.item_conversation);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                Conversation data = adapter.getData(position);
                String targetId = data.getTargetId();
                ConversationActivity.start(getActivity(),targetId);
            }
        });
        rv.setAdapter(adapter);
        fetchData();
    }
    private void fetchData(){
     //获取会话，单聊
        imClient.getConversationList(new RongIMClient.ResultCallback<List<Conversation>>(){
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations!=null&&conversations.size()>0){
                    Log.d(TAG,"onSuccess: "+conversations.size());
                    adapter.setData(conversations);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        },Conversation.ConversationType.PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        fetchData();
    }
    //在这里解注册的好处是，如果在聊天界面发送消息，会话界面就不用每次都刷新
    //只有用户返回，再刷新。

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnMessageEvent event){
        fetchData();
    }
}
