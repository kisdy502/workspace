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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.db.Conversation2Model;
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
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {

    private ILogger logger = ILoggerFactory.getLogger(getClass());
    private Context mContext;
    private List<Conversation2Model> conversation2ModelList;


    public ConversationAdapter(Context mContext, List<Conversation2Model> conversationList) {
        this.mContext = mContext;
        this.conversation2ModelList = conversationList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Conversation2Model conversation2Model = conversation2ModelList.get(position);
        holder.tvConversationName.setText(conversation2Model.getToObject());
    }

    @Override
    public int getItemCount() {
        return conversation2ModelList != null ? conversation2ModelList.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvConversationName;

        public VH(View itemView) {
            super(itemView);
            tvConversationName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}