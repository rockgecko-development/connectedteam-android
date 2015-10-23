package au.com.connectedteam.util;

import static net.servicestack.func.Func.*;
import net.servicestack.func.Function;
import net.servicestack.func.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Collection;

/**
 * Created by Bramley on 11/08/2015.
 */
public class FuncEx {
    
    public static <T> ArrayList<T> distinct(Iterable<T> xs, Comparator<? super T> equality) {
        ArrayList<T> distincts = new ArrayList<T>();
        for (T x : xs) {
            for(T ex : distincts){
                if(equality.compare(x, ex)==0)
                    break;
                distincts.add(x);
            }
        }
        return distincts;
    }

    public static <T, U> ArrayList<U> select(Iterable<T> xs, Function<T,U> selector){
       return map(xs, selector);
    }

    public static <T, U> ArrayList<U> selectMany(Iterable<T> xs, Function<T,Collection<U>> selector){

        ArrayList<U> result = new ArrayList<>();
        for(T x : xs){
            result.addAll(selector.apply(x));
        }
        return result;
        //return expand(map(xs, selector));
    }
}
