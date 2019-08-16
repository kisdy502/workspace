/**
 * Copyright (C), 2015-2019, 锋芒科技有限公司
 * FileName: ChatMessageAdapter
 * Author: sdt13411
 * Date: 2019/8/13 17:10
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.sdt.nepush.adapter;
/**
 * @ClassName: ChatMessageAdapter
 * @Description: class is do
 * @Author: sdt13411
 * @Date: 2019/8/13 17:10
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.util.DateUtil;

import java.util.List;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.VH> {

    private ILogger logger = ILoggerFactory.getLogger(getClass());
    private Context mContext;
    private List<Message2Model> chatMessageList;

    private int viewTypeSend = 0;
    private int viewTypeReceiver = 1;
    private String myId;
    private String toId;

    public ChatMessageAdapter(Context mContext, List<Message2Model> chatMessageList, String myId, String toId) {
        this.mContext = mContext;
        this.chatMessageList = chatMessageList;
        this.myId = myId;
        this.toId = toId;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == viewTypeSend) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_send, parent, false);
            return new VH(view);
        } else if (viewType == viewTypeReceiver) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_receiver, parent, false);
            return new VH(view);
        } else
            throw new IllegalArgumentException("unsupport viewType" + viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Message2Model message2Model = chatMessageList.get(position);
        holder.tvMessageTime.setText(DateUtil.timestampToDateString(message2Model.getTimestamp()));
        holder.tvMessageText.setText(message2Model.getContent());
        if (message2Model.getStatusReport() == 0 && message2Model.getFromId().equalsIgnoreCase(myId)) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
        if (needShowMessageTime(position)) {
            holder.tvMessageTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessageTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message2Model message2Model = chatMessageList.get(position);
        if (message2Model.getFromId().equalsIgnoreCase(myId)) {
            return viewTypeSend;
        } else if (message2Model.getFromId().equalsIgnoreCase(toId)) {
            return viewTypeReceiver;
        } else
            return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return chatMessageList != null ? chatMessageList.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvMessageTime;
        private TextView tvMessageText;
        private ProgressBar progressBar;

        public VH(View itemView) {
            super(itemView);
            tvMessageTime = (TextView) itemView.findViewById(R.id.datetime);
            tvMessageText = (TextView) itemView.findViewById(R.id.textView2);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public boolean needShowMessageTime(int position) {
        if (position == 0) {
            return true;
        }
        Message2Model message2Model = chatMessageList.get(position);
        Message2Model prevMessage2Model = chatMessageList.get(position - 1);
        if (message2Model.getTimestamp() - prevMessage2Model.getTimestamp() > 1000 * 60) {
            return true;  //大于一分钟
        }

        return false;
    }
}