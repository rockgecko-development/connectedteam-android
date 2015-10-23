package au.com.connectedteam.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



public class ListUtils {

    public static <T> int indexOf(T[] array, T value){
        if(array!=null) for(int i=0;i<array.length; i++){
            T item = array[i];
            if((value==null && item==null) || (value!=null && value.equals(item))) return i;
        }
        return -1;
    }
/*
    public static int indexOfKey(List<? extends IKeyValuePair> list, String key){
        if(list!=null) for(int i=0;i<list.size(); i++){
            IKeyValuePair item = list.get(i);
            if((item.getKey()==null && key==null)||(item.getKey()!=null && item.getKey().equals(key))) return i;
        }
        return -1;
    }

    public static <T extends IKeyValuePair> T keyValueSearch(List<T> list, String key){
        int index = indexOfKey(list, key);
        if(index>=0) return list.get(index);
        return null;
    }
*/
    public static <T> boolean arrayContains(T[] array, T value){
        if(array!=null) for(T item : array){
            if(value.equals(item)) return true;
        }
        return false;
    }

    public static boolean arrayContains(int[] array, int value) {
        if(array!=null) for(int item : array){
            if(value==item) return true;
        }
        return false;
    }

    public static List<Integer> toIntegerList(int[] in){
        List<Integer> out = new ArrayList<Integer>(in.length);
        for(int v : in){
            out.add(v);
        }
        return out;
    }

    public static int[] toIntArray(List<Integer> in){
        int[] out = new int[in.size()];
        int i=0;
        for(int v : in){
            out[i]=v;
            i++;
        }
        return out;
    }

    public static <T> ArrayList<T> asArrayList(T ... elements) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> ofType(List<? super T> items, Class<T> type){
        List<T> results = new ArrayList<T>();
        for(Object item : items){
            if(type.isAssignableFrom(item.getClass())) results.add((T) item);
        }
        return results;

    }
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> List<T> ofTypes(List<? super T> items, Class<? extends T>... types){
        List<T> results = new ArrayList<T>();
        for(Object item : items){
            for(Class<? extends T> type : types){
                if(type.isAssignableFrom(item.getClass())){
                    results.add((T) item);
                    break;
                }
            }
        }
        return results;

    }

    /**
     * Converts a List of Lists to a 2x2 matrix
     * @param in
     * @param clazz the class of T
     * @return 2x2 matrix, the width of the matrix is equal to the widest row in the inner lists.
     * If the inner lists have different lengths, shorter lists are padded with nulls.
     */
    public static <T> T[][] toMatrix(List<List<T>> in, Class<T> clazz){
        int maxWidth = 0;
        for(int i=0;i<in.size();i++){
            maxWidth=Math.max(maxWidth, in.get(i).size());
        }

        //below is equivalent to
        //T[][] result = new T[in.size()][maxWidth];

        T[][] result = (T[][]) Array.newInstance(clazz, in.size(), maxWidth);
        for(int i=0;i<in.size();i++){
            result[i]=in.get(i).toArray((T[])Array.newInstance(clazz, maxWidth));
        }
        return result;
    }
    /**
     * Swaps the rows and columns of a 2x2 matrix
     * @param in
     * @return
     */
    public static <T> T[][] rotate(T[][] in){
        T[][] result = (T[][]) Array.newInstance(in.getClass().getComponentType().getComponentType(), in[0].length, in.length);
        for(int i=0;i<in.length;i++){
            for(int j=0;j<in[i].length;j++){
                result[j][i] = in[i][j];
            }

        }
        return result;
    }
}
