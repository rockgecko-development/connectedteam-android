package au.com.connectedteam.ui.ribbonmenu;


	/*
	 * Copyright (C) 2010 The Android Open Source Project
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */



	import android.content.Context;
	import android.content.res.Resources;
	import android.content.res.TypedArray;
	import android.graphics.Rect;
	import android.graphics.drawable.Drawable;
	import au.com.connectedteam.R;

    import android.text.Spannable;
    import android.text.TextUtils;
	import android.text.method.TransformationMethod;
	import android.util.AttributeSet;
	import android.view.Gravity;
	import android.view.View;
	import android.widget.TextView;
	import android.widget.Toast;

import java.util.Locale;

    import au.com.connectedteam.util.UIUtil;


public class FooterMenuItemView extends TextView
	        implements  View.OnLongClickListener {

	    private static final String TAG = "FooterMenuItemView";

	    private RibbonMenuItem mItemData;
	    private CharSequence mTitle;
	    private Drawable mIcon;
	    

	    private boolean mAllowTextWithIcon;
	    private boolean mExpandedFormat;
	    private int mMinWidth;
	    private int mSavedPaddingLeft;

	    public FooterMenuItemView(Context context) {
	        this(context, null);
	    }

	    public FooterMenuItemView(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }

	    public FooterMenuItemView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        final Resources res = context.getResources();
	       // mAllowTextWithIcon = res.getBoolean(
	         //       android.support.v7.appcompat.R.bool.abc_config_allowActionMenuItemTextWithIcon);
	        mAllowTextWithIcon=true;
	        TypedArray a = context.obtainStyledAttributes(attrs,
	                android.support.v7.appcompat.R.styleable.ActionMenuItemView, 0, 0);
	        mMinWidth = a.getDimensionPixelSize(
	                R.styleable.ActionMenuItemView_android_minWidth, 0);
	        a.recycle();

	        setOnLongClickListener(this);

	       //setTransformationMethod(new AllCapsTransformationMethod());
	       setTransformationMethod(null);

	        mSavedPaddingLeft = -1;
	    }

	    @Override
	    public void setPadding(int l, int t, int r, int b) {
	        mSavedPaddingLeft = l;
	        super.setPadding(l, t, r, b);
	    }

	    public RibbonMenuItem getItemData() {
	        return mItemData;
	    }

	    public void initialize(RibbonMenuItem itemData) {
	        mItemData = itemData;

	        if(itemData.getIconDrawable()!=null) setIcon(itemData.getIconDrawable());
            CharSequence title = itemData.getText();
            if(!(title instanceof Spannable)){
                //spannable.toString will strip formatting, so don't do it
                title=title.toString().toUpperCase();
            }
	        setTitle(title); // Title only takes effect if there is no icon
	        setId(itemData.getId());

	        setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
	        setEnabled(itemData.isEnabled());

	    }

	   
	  

	    public boolean prefersCondensedTitle() {
	        return true;
	    }

	    public void setCheckable(boolean checkable) {
	        // TODO Support checkable action items
	    }

	    public void setChecked(boolean checked) {
	        // TODO Support checkable action items
	    }

	    public void setExpandedFormat(boolean expandedFormat) {
	        if (mExpandedFormat != expandedFormat) {
	            mExpandedFormat = expandedFormat;
	            if (mItemData != null) {
	               // mItemData.actionFormatChanged();
	            }
	        }
	    }

	    private void updateTextButtonVisibility() {
	        boolean visible = !TextUtils.isEmpty(mTitle);
	        visible &= mIcon == null ||
	                (mItemData.showsTextAsAction() && (mAllowTextWithIcon || mExpandedFormat));

	        setText(visible ? mTitle : null);
	    }

	    public void setIcon(Drawable icon) {
	        mIcon = icon;
	        setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
	        updateTextButtonVisibility();
	    }

	    public boolean hasText() {
	        return !TextUtils.isEmpty(getText());
	    }

	    public void setShortcut(boolean showShortcut, char shortcutKey) {
	        // Action buttons don't show text for shortcut keys.
	    }

	    public void setTitle(CharSequence title) {
	        mTitle = title;

	        setContentDescription(mTitle);
	        updateTextButtonVisibility();
	    }

	    public boolean showsIcon() {
	        return true;
	    }

	    public boolean needsDividerBefore() {
	        return hasText() && mItemData.getIconDrawable() == null;
	    }

	    public boolean needsDividerAfter() {
	        return hasText();
	    }

	    @Override
	    public boolean onLongClick(View v) {
	        if (hasText()) {
	            // Don't show the cheat sheet for items that already show text.
	            return false;
	        }

	        final int[] screenPos = new int[2];
	        final Rect displayFrame = new Rect();
	        getLocationOnScreen(screenPos);
	        getWindowVisibleDisplayFrame(displayFrame);

	        final Context context = getContext();
	        final int width = getWidth();
	        final int height = getHeight();
	        final int midy = screenPos[1] + height / 2;
	        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

	        Toast cheatSheet = Toast.makeText(context, mItemData.getText(), Toast.LENGTH_SHORT);
	        if (midy < displayFrame.height()) {
	            // Show along the top; follow action buttons
	            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
	                    screenWidth - screenPos[0] - width / 2, height);
	        } else {
	            // Show along the bottom center
	            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
	        }
	        cheatSheet.show();
	        return true;
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        final boolean textVisible = hasText();
	        if (textVisible && mIcon==null && mSavedPaddingLeft >= 0) {
	            super.setPadding(mSavedPaddingLeft, getPaddingTop(),
	                    getPaddingRight(), getPaddingBottom());
	        }

	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	        final int oldMeasuredWidth = getMeasuredWidth();
	        final int targetWidth = widthMode == MeasureSpec.AT_MOST ? Math.min(widthSize, mMinWidth)
	                : mMinWidth;

	        if (widthMode != MeasureSpec.EXACTLY && mMinWidth > 0 && oldMeasuredWidth < targetWidth) {
	            // Remeasure at exactly the minimum width.
	            super.onMeasure(MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY),
	                    heightMeasureSpec);
	        }

	        if (!textVisible && mIcon != null) {
	            // TextView won't center compound drawables in both dimensions without
	            // a little coercion. Pad in to center the icon after we've measured.
	            final int w = getMeasuredWidth();
	            final int dw = mIcon.getIntrinsicWidth();
	            super.setPadding((w - dw) / 2, getPaddingTop(), getPaddingRight(), getPaddingBottom());
	        }
            else if (textVisible && mIcon!=null){
                CharSequence text = getText();

                TransformationMethod meth = getTransformationMethod();
                if (meth!=null ){
                    //the text returned from getText() may be transformed before it is drawn. IE if textAllCaps is set in XML,
                    //The text will be subsequently transformed, changing its width. So apply the transformation if available.
                    text = meth.getTransformation(text, this);
                }
                float textWidth = getPaint().measureText(text.toString().split("\n")[0]);
                final int w = getMeasuredWidth();
                final int dw = mIcon.getIntrinsicWidth();
                float padding=(w-textWidth-dw-UIUtil.scale(8))/2f;
                super.setPadding((int)padding, getPaddingTop(), (int)padding, getPaddingBottom());
            }

	    }

	    private class AllCapsTransformationMethod implements TransformationMethod {
	        private Locale mLocale;

	        public AllCapsTransformationMethod() {
	            mLocale = getContext().getResources().getConfiguration().locale;
	        }

	        @Override
	        public CharSequence getTransformation(CharSequence source, View view) {
	            return source != null ? source.toString().toUpperCase(mLocale) : null;
	        }

	        @Override
	        public void onFocusChanged(View view, CharSequence sourceText, boolean focused,
	                int direction, Rect previouslyFocusedRect) {
	        }
	    }
	
}
