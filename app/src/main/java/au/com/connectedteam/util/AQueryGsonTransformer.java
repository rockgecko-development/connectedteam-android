package au.com.connectedteam.util;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;

/**
 * Created by bramleyt on 22/12/2014.
 */
public class AQueryGsonTransformer implements Transformer {
    private Gson gson;
    private TypeToken typeToken;
    private Class<?> clazz;
    public AQueryGsonTransformer(Gson gson, TypeToken typeToken){
        this.gson=gson;
        this.typeToken=typeToken;
    }
    public AQueryGsonTransformer(Gson gson, Class<?> clazz){
        this.gson=gson;
        this.clazz=clazz;
    }
    @Override
    public <T> T transform(String url, Class<T> type, String encoding,
                           byte[] data, AjaxStatus status) {

        try {
            if(typeToken!=null)
                return gson.fromJson(new String(data, encoding), typeToken.getType());
            if(!clazz.isAssignableFrom(type)) throw new RuntimeException("Unknown type "+type);
            return gson.fromJson(new String(data, encoding), (Class<T>)clazz);
        } catch ( UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
