package au.com.connectedteam.ui.ribbonmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.connectedteam.R;




/**
 * Drop down RibbonMenuView. Not currently used.
 * @author bramleyt
 *
 */
public class MainDropDownMenu extends RibbonMenuView{

	private View listContainer;
	protected View rbmOutsideView;
	
	public MainDropDownMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public MainDropDownMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@SuppressLint("NewApi")
	@Override
	protected void inflateLayout(){

		
			LayoutInflater.from(getContext()).inflate(R.layout.rbm_menu, this, true);
			
		rbmListView = (ListView) findViewById(R.id.rbm_listview);
		initListView();
				
		rbmOutsideView = (View) findViewById(R.id.rbm_outside_view);		
		initOutsideView();
		

	}
	
	@Override
	protected void initListView(){
		rbmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(callback != null){		
					boolean hide = callback.onRibbonMenuItemClick(MainDropDownMenu.this, (RibbonMenuItem) rbmListView.getAdapter().getItem(position));
					if(hide) hideMenu();
				}
				else hideMenu();			

			}

		});

	}
	
	protected void initOutsideView(){
		//rbmOutsideView.setBackgroundColor(0xaaff0000);
		rbmOutsideView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(v.getVisibility()==VISIBLE) hideMenu();
				return true;
				
			}
		});/*
		rbmOutsideView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMenu();

			}
		});*/
	}


	
	
	public View getListContainer(){
		if(listContainer!=null) return listContainer;
		return rbmListView;
	}
	
	
	
	public void showMenu(){
		notifyMenuItemChanges();
		
		rbmOutsideView.setVisibility(View.VISIBLE);	
				
		getListContainer().setVisibility(View.VISIBLE);	
		getListContainer().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.in_from_left));
		
	}
	
	
	public void hideMenu(){
		
		rbmOutsideView.setVisibility(View.GONE);
		getListContainer().setVisibility(View.GONE);	
		
		getListContainer().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.out_to_left));
		
	}
	
	
	public boolean isMenuVisible(){
		return !(rbmOutsideView.getVisibility() == View.GONE);
	}
	

	
	
}
