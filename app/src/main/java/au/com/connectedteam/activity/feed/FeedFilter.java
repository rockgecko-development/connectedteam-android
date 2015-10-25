package au.com.connectedteam.activity.feed;

import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class FeedFilter implements Serializable{

    public List<String> tags;
    public FeedFilter(){
        reset();
    }
    public void reset(){
        tags = new ArrayList<>();
        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null){
            List<String> tags = user.<String>getList("tags");
            if(tags!=null) this.tags.addAll(tags);
        }
    }
}
