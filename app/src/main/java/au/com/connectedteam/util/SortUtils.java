package au.com.connectedteam.util;

import net.servicestack.func.Func;
import net.servicestack.func.Function;

import java.io.Serializable;
import java.util.*;
/**
 * Created by Bramley on 4/08/2015.
 */
public class SortUtils {

    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
    public static <T> ArrayList<T> orderBy(Collection<T> items, Comparator<? super T> comparator){
        ArrayList<T> result = new ArrayList<>(items);
        Collections.sort(result, comparator);
        return result;
    }
    public static <T> ArrayList<T> orderByThenBy(Collection<T> items, final Comparator<? super T>... comparators){
        ArrayList<T> result = new ArrayList<>(items);
        Collections.sort(result, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                int compare=0;
                for(Comparator<? super T> comparator : comparators){
                    compare = comparator.compare(lhs,rhs);
                    if(compare!=0) return compare;
                }
                return compare;
            }
        });
        return result;
    }

    public static <T, R extends Comparable<? super R>> ArrayList<T> orderByThenByComparable(Collection<T> xs, final Function<T, R>... fs) {
        ArrayList<T> cloned = Func.toList(xs);
        Collections.sort(cloned, new Comparator<T>() {
            @Override
            public int compare(T a, T b) {
                for(Function<T, R> f : fs) {
                    R aVal = f.apply(a);
                    R bVal = f.apply(b);
                    int compare;
                    if (aVal == null && bVal == null)
                        compare=0;
                    else if (aVal == null)
                        compare= -1;
                    else if (bVal == null)
                        compare = 1;
                    else
                        compare= aVal.compareTo(bVal);
                    if(compare!=0) return compare;
                }
                return 0;
            }
        });

        return cloned;
    }

    public static <U extends Comparable<? super U>> U min(U a, U b){
        if(a==null) return b;
        if(b==null) return a;
        int compare = a.compareTo(b);
        if(compare<=0) return a;
        return b;
    }
    public static <U extends Comparable<? super U>> U max(U a, U b){
        if(a==null) return b;
        if(b==null) return a;
        int compare = a.compareTo(b);
        if(compare>=0) return a;
        return b;
    }

    /**
     * Returns true if two possibly-null objects are equal.
     */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }
}
