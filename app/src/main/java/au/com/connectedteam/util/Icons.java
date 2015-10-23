package au.com.connectedteam.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.StateSet;

import au.com.connectedteam.R;
import au.com.connectedteam.appsapi.generated.dto;

/**
 * Created by bramleyt on 23/06/2015.
 */
public class Icons {

    /**
     *
     * @param leagueID
     * @param type one of ic, contest, stats or filter
     * @return
     */
    public static int getIconForLeague(int leagueID, String type){
        //if(type.equals("filter"))
        int resId=0;
        Object league=null;
        if(league!=null){
            resId = Reflect.getImageResId(type+"_"+league.toString());
        }
        if(resId==0){
            switch (type){
                case "filter":
                    return getFilterIcon(leagueID);
                case "contest":
                case "ic":
                case "stats":
                default:
                    return 0;
            }
        }
        return resId;
    }

    public static int getFilterIcon(int leagueID){

       return 0;
    }

    public static Drawable getFilterIcon(int leagueID, Context c){
        int baseRes = getFilterIcon(leagueID);
        return buildStateList(c, baseRes, c.getResources().getColor(R.color.button_activated));
        //return buildStateList(c, baseRes, Color.RED);
        //return c.getResources().getDrawable(baseRes);
    }

    private static StateListDrawable buildStateList(Context c, int onRes, int offColour){
        Drawable onDrawable = c.getResources().getDrawable(onRes);
        StateListDrawable result = new StateListDrawable();
        int[] onSets = new int[]{android.R.attr.state_pressed,
                android.R.attr.state_checked, android.R.attr.state_activated, android.R.attr.state_selected};
        for(int id : onSets){
            result.addState(new int[]{id}, onDrawable);
        }
        Drawable offDrawable = DrawableCompat.wrap(c.getResources().getDrawable(onRes)).mutate();
        DrawableCompat.setTint(offDrawable, offColour);
        result.addState(StateSet.WILD_CARD, offDrawable);
        //result.addState(StateSet.WILD_CARD, c.getResources().getDrawable(R.drawable.filter_multi_off));
        return result;
    }


}
