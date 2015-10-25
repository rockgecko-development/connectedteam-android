package au.com.connectedteam.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AbstractAQuery;
import com.androidquery.util.AQUtility;

import java.math.BigDecimal;

/**
 * Created by bramleyt on 12/10/2015.
 */
public class AQueryEx extends AbstractAQuery<AQueryEx> {
    public AQueryEx(Activity act) {
        super(act);
    }

    public AQueryEx(View root) {
        super(root);
    }

    public AQueryEx(Activity act, View root) {
        super(act, root);
    }

    public AQueryEx(Context context) {
        super(context);
    }

    public AQueryEx checked(boolean checked) {
        if (this.view instanceof Checkable) {
            Checkable cb = (Checkable) this.view;
            cb.setChecked(checked);
        }

        return this.self();
    }

    public boolean isChecked() {
        boolean checked = false;
        if (this.view instanceof Checkable) {
            Checkable cb = (Checkable) this.view;
            checked = cb.isChecked();
        }

        return checked;
    }

    public AQueryEx weight(float weight) {
        if (this.view != null && this.view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.view.getLayoutParams();
            lp.weight = weight;
            this.view.setLayoutParams(lp);
        }
        return this.self();
    }

    public AQueryEx bindString(final Object model, final String property) {
        Object value = Reflect.getValue(model, property, true);
        if (value == null || value instanceof String) {
            text(value != null ? value.toString() : null).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String value = ((EditText) v).getText().toString().trim();
                        Reflect.setValue(model, property, value, true);
                    }
                }

            });
        }
        return this.self();
    }
    public AQueryEx bindInteger(final Object model, final String property) {
        Object value = Reflect.getValue(model, property, true);
        if (value == null || value instanceof Integer) {
            text(value != null ? value.toString() : null).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        Integer value=null;
                        try{
                            value = Integer.parseInt(((EditText) v).getText().toString().trim());
                        }
                        catch(Exception e){}

                        Reflect.setValue(model, property, value, true);
                    }
                }

            });
        }
        return this.self();
    }
    public AQueryEx bindBigDecimal(final Object model, final String property) {
        Object value = Reflect.getValue(model, property, true);
        if (value == null || value instanceof BigDecimal) {
            text(value != null ? ((BigDecimal)value).toPlainString() : null).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        BigDecimal value=null;
                        try{
                            value = new BigDecimal(((EditText) v).getText().toString().trim());
                        }
                        catch(Exception e){}

                        Reflect.setValue(model, property, value, true);
                    }
                }

            });
        }
        return this.self();
    }

}