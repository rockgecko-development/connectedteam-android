package au.com.connectedteam.adapter;

import android.database.DataSetObserver;

/**
 * Created by bramleyt on 7/08/2015.
 */
public class DataCallbacks {
    public interface IDataObservable{
        void registerDataObserver(DataSetObserver observer);
        void unregisterDataObserver(DataSetObserver observer);
    }
}
