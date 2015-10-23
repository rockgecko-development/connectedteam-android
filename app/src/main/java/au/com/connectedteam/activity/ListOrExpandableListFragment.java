/*
 * Copyright (C) 2011 The Android Open Source Project
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

package au.com.connectedteam.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.util.StringUtils;


/**
 * Static library support version of the framework's {@link android.app.ListFragment}.
 * Used to write apps that run on platforms prior to Android 3.0.  When running
 * on Android 3.0 or above, this implementation is still used; it does not try
 * to switch to the framework's implementation.  See the framework SDK
 * documentation for a class overview.
 */
public abstract class ListOrExpandableListFragment extends BaseIonFragment {
	
	static final int INTERNAL_EXPANDABLE_LIST_ID = R.id.expandable_list;
	
	static final int INTERNAL_EMPTY_ID = R.id.internalEmpty;
	static final int INTERNAL_LOADING_TEXT_ID = R.id.internalLoading;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = R.id.progressContainer;
    static final int INTERNAL_LIST_CONTAINER_ID = R.id.listContainer;
    static final int INTERNAL_LIST_ROOT_CONTAINER_ID = R.id.listRootContainer;
    /*
	static final int INTERNAL_EMPTY_ID = 0x00ff0001;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003;
    */
    /**
	 * The current activated item position. Only used on tablets.
	 */
	protected int mActivatedPosition = ListView.INVALID_POSITION;
	protected long mPendingActivatedItemId = 0;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    public static final int TIME_UPDATE_INTERVAL = 1000;
    
    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };
    
    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        	if(isAdded() && getView()!=null)
    			onListItemClick((ListView)parent, v, position, id);
    		else{
    			String msg = "onListItemClick, added: "+isAdded()+" view: "+getView();
    			if(ConnectedApp.DEBUG) throw new IllegalStateException(msg);
    			else{    				
    				Log.e("ListOrExpandableListFragment", msg);    			
    			}
    		}
    	}
    };
    final private OnChildClickListener mOnChildClickListener
    = new OnChildClickListener() {
    	public boolean onChildClick(ExpandableListView parent, View v,
    			int groupPosition, int childPosition, long id) {
    		if(isAdded() && getView()!=null)
    			return onListChildClick(parent, v, groupPosition, childPosition, id);
    		else{
    			String msg = "OnChildClick, added: "+isAdded()+" view: "+getView();
    			if(ConnectedApp.DEBUG) throw new IllegalStateException(msg);
    			else Log.e("ListOrExpandableListFragment", msg);
    			return true;
    		}
    	}
    };
    final private OnGroupClickListener mOnGroupClickListener
    = new OnGroupClickListener() {
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			if(isAdded() && getView()!=null)
				return onListGroupClick(parent, v, groupPosition, id);
			else{
    			String msg = "onGroupClick, added: "+isAdded()+" view: "+getView();
    			if(ConnectedApp.DEBUG) throw new IllegalStateException(msg);
    			else Log.e("ListOrExpandableListFragment", msg);
    			return true;
    		}
		}
    	
    };

    ListAdapter mAdapter;
    ExpandableListAdapter mExpandableListAdapter;
    ListView mList;
    View mEmptyView;
    TextView mStandardEmptyView;
    TextView mLoadingTextView;
    View mSpinnerProgressView;
    ImageView mErrorIconView;
    View mProgressContainer;
    View mListContainer;
    CharSequence mEmptyText;
    boolean mListShown;
    boolean listEnabled=true;
    boolean isExpandableListView=false;

	

    public ListOrExpandableListFragment() {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	Log.d("ListOrExpandableListFragment", "onCreate: "+savedInstanceState);
    	if(savedInstanceState!=null){
    		isExpandableListView = savedInstanceState.getBoolean("isExpandableListView");
    	/*	if(isExpandableListView && mExpandableListState==null){
    			mExpandableListState = ExpandableSavedState.readFromBundle(savedInstanceState);
    		}*/
    	}
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    View root= getActivity().getLayoutInflater().inflate(R.layout.list_or_expandable_list_layout, container, false);
    return root;
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a ListView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     * 
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     *
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);

        // ------------------------------------------------------------------

        LinearLayout pframe = new LinearLayout(context);
        pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
        pframe.setOrientation(LinearLayout.VERTICAL);
        pframe.setVisibility(View.GONE);
        pframe.setGravity(Gravity.CENTER);

        ProgressBar progress = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        pframe.addView(progress, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(pframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        // ------------------------------------------------------------------

        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(INTERNAL_LIST_CONTAINER_ID);
        
        TextView tv = new TextView(getActivity());
        tv.setId(INTERNAL_EMPTY_ID);
        tv.setGravity(Gravity.CENTER);
        lframe.addView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        ListView lv = new ListView(getActivity());
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        lframe.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        // ------------------------------------------------------------------

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        return root;
    }
*/
    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureList();
        if(isExpandableListView){
        	/*if(mExpandableListAdapter!=null && mExpandableListState!=null){
        		mExpandableListState.restore(getExpandableListView());
        	}*/
        }
        if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
    }
    
   
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean("isExpandableListView", isExpandableListView);
    	if(isExpandableListView && mList instanceof ExpandableListView){    		
    		/*if(!getRetainInstance() && mExpandableListState!=null){
    			//only need to save this if we aren't persisting state
    			mExpandableListState.writeToBundle(outState);
    		}*/
    	}
    	if (mActivatedPosition != ListView.INVALID_POSITION) {
    		// Serialize and persist the activated item position.
    		outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    	}
    }
    
    /**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setPendingActivatedItemId(long id) {
		mPendingActivatedItemId = id;
		
	}
	protected void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
		mPendingActivatedItemId=0;
	}
	
	
	protected void setCardSeparatorAndBG(){
		
		getListView().setDivider(getResources().getDrawable(R.drawable.list_divider_card));
		if(isExpandableListView()){
			getExpandableListView().setChildDivider(getResources().getDrawable(R.drawable.list_divider_card));
		}
		getListView().setBackgroundResource(R.color.card_background);
		
	}
    /*
    protected Runnable timeUpdater = new Runnable(){

		@Override
		public void run() {			
			uiHandler.removeCallbacks(timeUpdater);
		
				if (timeUpdateTick()) uiHandler.postDelayed(timeUpdater, TIME_UPDATE_INTERVAL);
				
			
		}
		
	};
	/**
	 * Base implementation updates ListView children.
	 * Subclasses can override to update other views on the same tick
	 * @return true to continue, false to stop the timeUpdater.
	 *
	protected boolean timeUpdateTick(){
		if (ListOrExpandableListFragment.this.isResumed() &&!isStalled && getListView()!=null){
			//skip over uservisiblehint
			if(!ListOrExpandableListFragment.this.getUserVisibleHint()) return true;
			
			int count = ListOrExpandableListFragment.this.getListView().getChildCount();
			int updated=0;
			//TODO modified so return value from CellTimeUpdater interface is ignored, becuase if there
			//is only one cell which is counting, and it is scrolled away, the counter will turn off.
			int areAllStatic=0;
			
			Date now = new Date();
			for (int i=0;i<count;i++){
				View child =ListOrExpandableListFragment.this.getListView().getChildAt(i);
				if (child instanceof CellTimeUpdater){
					areAllStatic=areAllStatic+((CellTimeUpdater)child).updateTimeLeft(now);
					updated++;
				}
			}				
			
			if(TheDogsApplication.DEBUG && false) Log.d("TimeUpdater", "Children: "+count + " updated: "+updated);
			return true;
			
		}
		return false;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		uiHandler.removeCallbacks(timeUpdater);
		Log.d("ListOrExpandableListFragment", "onPause");
    	
    	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		uiHandler.postDelayed(timeUpdater, TIME_UPDATE_INTERVAL);
	}
	*/
	
	protected final void changeListView(boolean expandable){
    	ViewGroup parent;
    	if (mList!=null){
    		parent = (FrameLayout) mList.getParent();
    		parent.removeView(mList);
    	}
    	else{
    		parent =  (ViewGroup) getView();
    	}
    	
    	mList=null;
    	ListView lv;
    	
    	if (expandable){
    		lv = new ExpandableListView(getActivity());    		
    		((ExpandableListView) lv).setGroupIndicator(null);
			((ExpandableListView) lv).setChildIndicator(null);
			//getExpandableListView().setIndicatorBounds(0, 0);
			lv.setId(INTERNAL_EXPANDABLE_LIST_ID);
			//lv.setSelector(R.drawable.list_selector_expandable);
    	}
    	else{
    		lv = new ListView(getActivity());
    		lv.setId(android.R.id.list);
    	}
    	
    	
    	lv.setDrawSelectorOnTop(false);
    	parent.addView(lv);
    	
    	if (mProgressContainer == null) {
            throw new IllegalStateException("ExpandableListView can't be used with a custom content view");
        }
//    	try{
//    		//((FrameLayout)root.findViewById(INTERNAL_LIST_CONTAINER_ID)).addView(lv);
//    	}
//    	catch(NullPointerException e){
//    		throw new IllegalStateException("Could not find internal list container. " +
//    				"ExpandableListView can't be used with a custom content view");
//    	}
    	isExpandableListView=expandable;
    	ListAdapter lAdapter = mAdapter;
    	ExpandableListAdapter lExpandableAdapter = mExpandableListAdapter;
    	mAdapter=null;
    	mExpandableListAdapter=null;
    	ensureList();
    	mAdapter = lAdapter;
    	mExpandableListAdapter=lExpandableAdapter;
    	
    }
    
    /**
     * 
     * @return true if the attached list view is
     * an ExpandableListView
     */
    public boolean isExpandableListView(){
    	return mList instanceof ExpandableListView;
    }


    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        if(mList!=null && !ConnectedApp.DEBUG){
       	 mList.setOnItemClickListener(null);
            if (mList instanceof ExpandableListView){
            	((ExpandableListView) mList).setOnChildClickListener(null);
            	((ExpandableListView) mList).setOnGroupClickListener(null);
            }
       }
        mList = null;
        mListShown = false;
        mEmptyView = mProgressContainer = mListContainer = null;
        mStandardEmptyView = null;
        super.onDestroyView();
    }
    protected CharSequence getLoadingText(){
        return getString(R.string.requesting);
    }
    
    //@Override
	protected void showRequestFailedView(String msg) {
    	if(!listEnabled) return;
    //	if(listEnabled) setEmptyText(msg);
    	if(StringUtils.isNullOrEmpty(msg)){
    		mLoadingTextView.setText(getLoadingText());
    		mErrorIconView.setVisibility(View.GONE);
    		mSpinnerProgressView.setVisibility(View.VISIBLE);
    	}
    	else{    		
    		mLoadingTextView.setText(msg);
    		mErrorIconView.setVisibility(View.VISIBLE);
    		mSpinnerProgressView.setVisibility(View.GONE);
    		
    	}
    	
	}
    //@Override
    protected boolean shouldDisplayRequestFailedView(){
		//return lastModel==null;
		return isListEnabled()&& !isListVisible() && mLoadingTextView!=null;
	}

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }
    
    /**
     * This method will be called when a group item in the ExpandableList is selected.
     * Subclasses should override.
     * 
     *  @param parent The ExpandableListView where the click happened
     *	@param v The view within the expandable list/ListView that was clicked
     *	@param groupPosition The group position that contains the child that was clicked     	
     *	@param id The row id of the child that was clicked
     *	@return True if the click was handled
     */
    public boolean onListGroupClick(ExpandableListView parent, View v,
    		int groupPosition, long id) {
    	return false;
    }

    /**
     * This method will be called when a child item in the ExpandableList is selected.
     * Subclasses should override.
     * 
     *  @param parent The ExpandableListView where the click happened
     *	@param v The view within the expandable list/ListView that was clicked
     *	@param groupPosition The group position that contains the child that was clicked
     *	@param childPosition The child position within the group
     *	@param id The row id of the child that was clicked
     *	@return True if the click was handled
     */
    public boolean onListChildClick(ExpandableListView parent, View v,
    		int groupPosition, int childPosition, long id) {
    	return false;
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter adapter) {
    	mExpandableListAdapter = null;
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if(mList instanceof ExpandableListView){
        	changeListView(false);
        }
        if (mList != null) {
            mList.setAdapter(adapter);
            if (!mListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setListShown(true, getView().getWindowToken() != null);
            }
            if(mList.getChoiceMode()==ListView.CHOICE_MODE_SINGLE && mPendingActivatedItemId!=0){            	
            	for(int i=0;i<adapter.getCount(); i++){
            		if(adapter.getItemId(i)==mPendingActivatedItemId){
            			setActivatedPosition(i);
            			break;
            		}
            	}
            	
            	
            }
        }
    }
    
    public int setListAdapterKeepScroll(ListAdapter adapter){
    	int firstPos = -1;
		int top=0;
		if(getListAdapter()!=null){
			firstPos = getListView().getFirstVisiblePosition();
			View v = getListView().getChildAt(0);
			top = (v == null) ? 0 : v.getTop();
		}
		setListAdapter(adapter);		
		if(firstPos>=0 && firstPos<adapter.getCount()){
			getListView().setSelectionFromTop(firstPos, top);
		}
		return firstPos;
    }
    
    public void setExpandableListAdapter(ExpandableListAdapter adapter){
    	mAdapter=null;
    	boolean hadAdapter = mExpandableListAdapter != null;
    	mExpandableListAdapter = adapter;
    	if (mList!=null && !(mList instanceof ExpandableListView)){
    		changeListView(true);
    		
    	}
    	if (mList instanceof ExpandableListView) {
            ((ExpandableListView)mList).setAdapter(adapter);
            if (!mListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setListShown(true, getView().getWindowToken() != null);
            }
        }
       
    }
    
    
   
    
    public boolean isListVisible(){
    	if(!listEnabled){
    		return false;
    	}
    	return mListShown;
    }
    
    
    
    /**
     * Call this to permanently disable the list view components for this fragment.
     * Use this if eg you have overridden onCreateView and supplied a layout 
     * which does not include a listview.
     * After calling this, listView methods such as setListAdapter etc will throw exceptions.
     */    
    protected void enableListView(boolean enabled){
    	listEnabled=enabled;
    }
    
    protected boolean isListEnabled(){
    	return listEnabled;
    }

    /**
     * Set the currently selected list item to the specified
     * position with the adapter's data
     *
     * @param position
     */
    public void setSelection(int position) {
        ensureList();
        mList.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        ensureList();
        return mList.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        ensureList();
        return mList.getSelectedItemId();
    }

    /**
     * Get the activity's list view widget.
     */
    public ListView getListView() {
        ensureList();
        return mList;
    }
    
    public ExpandableListView getExpandableListView() {
        ensureList();
        if (mList instanceof ExpandableListView){
        	return (ExpandableListView) mList;
        }
        return null;
    }

    /**
     * The default content for a ListFragment has a TextView that can
     * be shown when the list is empty.  If you would like to have it
     * shown, call this method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {
        ensureList();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mStandardEmptyView.setText(text);
        if (mEmptyText == null && !StringUtils.isNullOrEmpty(text)) {
            mList.setEmptyView(mStandardEmptyView);
        }
        else if (StringUtils.isNullOrEmpty(text)){
        	mList.setEmptyView(null);
        }
        mEmptyText = text;
    }
    
    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     * 
     * <p>Applications do not normally need to use this themselves.  The default
     * behavior of ListFragment is to start with the list not being shown, only
     * showing it once an adapter is given with {@link #setListAdapter(android.widget.ListAdapter)}.
     * If the list at that point had not been shown, when it does get shown
     * it will be do without the user ever seeing the hidden state.
     * 
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     */
    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }
    
    /**
     * Like {@link #setListShown(boolean)}, but no animation is used when
     * transitioning from the previous state.
     */
    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }
    
    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     * 
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     * new state.
     */
    private void setListShown(boolean shown, boolean animate) {
        ensureList();
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }
    
    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }
    
    public ExpandableListAdapter getExpandableListAdapter(){
    	return mExpandableListAdapter;
    }

    private void ensureList() {
        if (mList != null || !listEnabled) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ListView) {
            mList = (ListView)root;
        } else {
            mStandardEmptyView = (TextView)root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mStandardEmptyView.setVisibility(View.GONE);
            }
            mLoadingTextView = (TextView) root.findViewById(INTERNAL_LOADING_TEXT_ID);
            mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            mSpinnerProgressView = root.findViewById(R.id.progress_spinner);
            mErrorIconView = (ImageView) root.findViewById(R.id.progress_error_icon);
            mListContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
            View rawListView = root.findViewById(android.R.id.list);
            if(rawListView==null){
            	rawListView = root.findViewById(INTERNAL_EXPANDABLE_LIST_ID);
            }
            if (!(rawListView instanceof ListView)) {
                if (rawListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ListView whose id attribute is " +
                            "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                        + "that is not a ListView class");
            }
            mList = (ListView)rawListView;
            if (mEmptyView != null) {
                mList.setEmptyView(mEmptyView);
            } else if (mEmptyText != null) {
                mStandardEmptyView.setText(mEmptyText);
                mList.setEmptyView(mStandardEmptyView);
            }
        }
        mListShown = true;
        mList.setOnItemClickListener(mOnClickListener);
        if (mList instanceof ExpandableListView){
        	((ExpandableListView) mList).setOnChildClickListener(mOnChildClickListener);
        	((ExpandableListView) mList).setOnGroupClickListener(mOnGroupClickListener);
        }
        if (mAdapter != null && !isExpandableListView) {
            ListAdapter adapter = mAdapter;
            mAdapter = null;
            setListAdapter(adapter);
        }
        else if (mExpandableListAdapter != null && isExpandableListView) {
            ExpandableListAdapter adapter = mExpandableListAdapter;
            mExpandableListAdapter = null;
            setExpandableListAdapter(adapter);
            
        }
        else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setListShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }
}