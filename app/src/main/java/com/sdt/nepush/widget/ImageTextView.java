package com.sdt.nepush.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdt.nepush.R;


public class ImageTextView extends LinearLayout {

    private Context mContext;

    private ImageView mImageView;
    private TextView mTextView;

    private int default_icon;
    private String title;

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
            default_icon = a.getResourceId(R.styleable.ImageTextView_default_icon, R.mipmap.ic_launcher);
            title = a.getString(R.styleable.ImageTextView_menu_title);
            a.recycle();
        }
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setClickable(true);
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View parentView;
        parentView = inflater.inflate(R.layout.view_image_text, this, true);
        mImageView = (ImageView) parentView.findViewById(R.id.imagetext_icon);
        mTextView = (TextView) parentView.findViewById(R.id.imagetext_title);
        mImageView.setImageResource(default_icon);
        mTextView.setText(title);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setClickable(!selected);
        mImageView.setSelected(selected);
        mTextView.setSelected(selected);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        mImageView.setPressed(pressed);
        mTextView.setPressed(pressed);
    }

}
