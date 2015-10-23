package au.com.connectedteam.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.widget.LinearLayout;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

	public interface IWantDivider{
		boolean showDivider(int position);
	}

	private Drawable mDivider;
	private IWantDivider mDummyDividerCallback = new IWantDivider() {
		@Override
		public boolean showDivider(int position) {
			return position>0;
		}
	};

	public DividerItemDecoration(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, new int [] { android.R.attr.listDivider });
		mDivider = a.getDrawable(0);
		a.recycle();
	}

	public DividerItemDecoration(Drawable divider) { mDivider = divider; }

	@Override
	public void getItemOffsets (Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (mDivider == null) return;
		int adapterPosition = parent.getChildAdapterPosition(view);
		IWantDivider dividerCallback= getShowDividerCallback(parent);
		if(!dividerCallback.showDivider(adapterPosition) || (adapterPosition>1 && !dividerCallback.showDivider(adapterPosition-1))) return;
		int orientation = getOrientation(parent);
		if (orientation == LinearLayoutManager.VERTICAL || orientation== ORIENTATION_GRID) {
			outRect.top = mDivider.getIntrinsicHeight();
		}
		if (orientation == LinearLayoutManager.HORIZONTAL || orientation== ORIENTATION_GRID) {
			outRect.left = mDivider.getIntrinsicWidth();
		}

	}

	private IWantDivider getShowDividerCallback(RecyclerView parent){
		if(parent.getAdapter() instanceof IWantDivider) return (IWantDivider) parent.getAdapter();
		return mDummyDividerCallback;
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		if (mDivider == null) { super.onDrawOver(c, parent, state); return; }
		IWantDivider dividerCallback= getShowDividerCallback(parent);
		int orientation = getOrientation(parent);
		if (orientation == LinearLayoutManager.VERTICAL || orientation== ORIENTATION_GRID) {
			final int left = parent.getPaddingLeft();
			final int right = parent.getWidth() - parent.getPaddingRight();
			final int childCount = parent.getChildCount();
			final int size = mDivider.getIntrinsicHeight();

			for (int i=1; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				int adapterPosition = parent.getChildAdapterPosition(child);
				if(!dividerCallback.showDivider(adapterPosition) || (adapterPosition>1 && !dividerCallback.showDivider(adapterPosition-1))) continue;
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

				final int top = child.getTop() - params.topMargin;
				final int bottom = top + size;
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
		if (orientation== LinearLayoutManager.HORIZONTAL || orientation==ORIENTATION_GRID){ //horizontal
			final int top = parent.getPaddingTop();
			final int bottom = parent.getHeight() - parent.getPaddingBottom();
 			final int childCount = parent.getChildCount();
			final int size = mDivider.getIntrinsicWidth();

			for (int i=1; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

				final int left = child.getLeft() - params.leftMargin;
				final int right = left + size;
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
	}

	static final int ORIENTATION_GRID = 3;
	private int getOrientation(RecyclerView parent) {
		if(parent.getLayoutManager() instanceof GridLayoutManager){
			return ORIENTATION_GRID;
		}
		else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
			return layoutManager.getOrientation();
		} else throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
	}

}