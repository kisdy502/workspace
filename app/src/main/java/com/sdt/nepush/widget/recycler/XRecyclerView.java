package com.sdt.nepush.widget.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sdt13411 on 2019/7/29.
 */

public class XRecyclerView extends RecyclerView implements View.OnClickListener, View.OnLongClickListener {

    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    public XRecyclerView(@NonNull Context context) {
        super(context);
    }

    public XRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        if (child.isClickable()) {
            child.setOnClickListener(this);
            child.setOnLongClickListener(this);
        }
    }

    @Override
    public void onClick(View itemView) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(this, itemView, getChildLayoutPosition(itemView));
    }

    @Override
    public boolean onLongClick(View itemView) {
        if (onItemLongClickListener != null)
            return onItemLongClickListener.onItemLongClick(this, itemView, getChildLayoutPosition(itemView));
        return false;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
