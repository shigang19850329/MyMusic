package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.manager.impl.UserManagerImpl;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.MessageUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * 会话列表
 * Created by kaka
 * On 2019/7/27
 */
public class ConversationAdapter extends BaseQuickRecyclerViewAdapter<Conversation> {
    private final UserManager userManager;

    //private String userId;
    public ConversationAdapter(Context context, int layoutId) {
        super(context, layoutId);
        userManager = UserManagerImpl.getInstance(context);
    }

    @Override
    protected void bindData(final ViewHolder holder, int position, final Conversation data) {
        //int messageId = data.getLatestMessageId();
        final MessageContent messageContent = data.getLatestMessage();
        String targetId = data.getTargetId();
        /**
         *  由于消息上没有用户信息，只有id，所以还需要从用户管理器中
         *  获取用户信息，当然也可以每条消息上带上用户信息，但这肯定
         *  会更耗资源。
         */
        userManager.getUser(targetId, new UserManager.OnUserListener() {
            @Override
            public void onUser(User user) {
                ImageView iv_avatar = (ImageView) holder.findViewById(R.id.iv_avatar);
                ImageUtil.showCircle(context, iv_avatar, user.getAvatar());
                holder.setText(R.id.tv_nickname, user.getNickname());
                holder.setText(R.id.tv_info, MessageUtil.getContent(messageContent));
                holder.setText(R.id.tv_time, TimeUtil.toLocalDate(data.getReceivedTime()));

                int unreadMessageCount = data.getUnreadMessageCount();
                if (unreadMessageCount > 0) {
                    if (unreadMessageCount > 99) {
                        //显示99+
                        holder.setText(R.id.tv_count, R.string.message_count_99);
                    } else {
                        holder.setText(R.id.tv_count, String.valueOf(unreadMessageCount));
                    }
                    holder.setVisibility(R.id.tv_count, View.VISIBLE);
                } else {
                    holder.setVisibility(R.id.tv_count, View.GONE);
                }
            }
        });

    }
}
