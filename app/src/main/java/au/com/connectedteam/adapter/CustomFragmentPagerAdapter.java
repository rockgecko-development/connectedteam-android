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

package au.com.connectedteam.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;



/**
 * Custom implementation of android.support.v4.view.PagerAdapter, with new method 
 * getExistingFragment
 */
public abstract class CustomFragmentPagerAdapter extends PagerAdapter implements MultiPagerAdapter{
    private static final String TAG = "CFragmentPagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    
    public int mContainerId;

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        mFragmentManager = fm; 
    }

    /**
     * Create the Fragment associated with a specified position.
     */
    public abstract Fragment makeFragment(int position);
    
    /**
     * Get a fragment that has already been created, if it exists. Else null.
     * @param position
     * @return
     */
    public Fragment getExistingFragment(int position){
    	final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(mContainerId, itemId);
        return mFragmentManager.findFragmentByTag(name);
    	
    }
    
    @Override
    public int getViewsPerPage(){
		
		return 1;
	}
	
	@Override
	public float getPageWidth(int position) {
		
		//Margins are 1% wide,			
		int numMargins = getViewsPerPage()-1;
		if(numMargins==0) return 1;
		return (1f/getViewsPerPage())*(1f-(numMargins/100f));
	}

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	mContainerId = container.getId();
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            if (DEBUG) Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
            mCurTransaction.attach(fragment);
        } else {
            fragment = makeFragment(position);
            if (DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }
    
    public void duck(boolean hide){
    	
    	if(mCurrentPrimaryItem!=null){
    		
    		mCurrentPrimaryItem.setMenuVisibility(!hide);
    		mCurrentPrimaryItem.setUserVisibleHint(!hide);
    	}
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                + " v=" + ((Fragment)object).getView());
        mCurTransaction.detach((Fragment)object);
    }
    
    /**
     * DestroyItem only detatches the fragment, it stays in the fragment manager. This fully removes any fragments it has previously added.
     */
    public void removeAllFragments(){
    	boolean needToFinish = mCurTransaction==null;
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
    	for(int i=0;i<getCount();i++){
    		Fragment fragment = getExistingFragment(i);
            if(fragment!=null){
            	mCurTransaction.remove(fragment);
            }
    	}
    	
        if(needToFinish){
        	mCurTransaction.commitAllowingStateLoss();
        	//mCurTransaction=null;
        }
           
           

    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }
    
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    /**
     * Return a unique identifier for the item at the given position.
     *
     * <p>The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.</p>
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    public long getItemId(int position) {
        return position;
    }

    protected  String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + this.getClass().getSimpleName()+viewId + ":" + id;
    }
}
