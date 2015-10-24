package au.com.connectedteam.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.locks.Lock;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class ParseUtils {

    public static Gson getGson(){
        Gson gson = new GsonBuilder().
                setExclusionStrategies(new ParseExclusion()).
                create();
        return gson;
    }
    private static class ParseExclusion implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getDeclaredClass() == Lock.class);
        }

    }
}
