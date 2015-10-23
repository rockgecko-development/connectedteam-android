package au.com.connectedteam.adapter;

import android.support.v7.widget.RecyclerView;
import java.util.List;
/**
 * Created by Bramley on 8/08/2015.
 */
public abstract class CellRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    public abstract List<T> getItems();

    @Override
    public int getItemCount() {
        List<T> items = getItems();
        if(items==null) return 0;
        return items.size();
    }

    public T removeItem(int position) {
        final T model = getItems().remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, T model) {
        getItems().add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final T model = getItems().remove(fromPosition);
        getItems().add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    public void animateTo(List<T> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }
    private void applyAndAnimateRemovals(List<T> newModels) {
        for (int i = getItems().size() - 1; i >= 0; i--) {
            final T model = getItems().get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }
    private void applyAndAnimateAdditions(List<T> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final T model = newModels.get(i);
            if (!getItems().contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<T> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newModels.get(toPosition);
            final int fromPosition = getItems().indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
