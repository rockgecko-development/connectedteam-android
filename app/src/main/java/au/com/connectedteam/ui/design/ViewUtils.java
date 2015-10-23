//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package au.com.connectedteam.ui.design;

import android.os.Build.VERSION;

import android.view.View;

class ViewUtils {
    static final ValueAnimatorCompat.Creator DEFAULT_ANIMATOR_CREATOR = new ValueAnimatorCompat.Creator() {
        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat((ValueAnimatorCompat.Impl)(VERSION.SDK_INT >= 12?new ValueAnimatorCompatImplHoneycombMr1():new ValueAnimatorCompatImplEclairMr1()));
        }
    };
    private static final ViewUtils.ViewUtilsImpl IMPL;

    ViewUtils() {
    }

    static void setBoundsViewOutlineProvider(View view) {
        IMPL.setBoundsViewOutlineProvider(view);
    }

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    static {
        int version = VERSION.SDK_INT;
        if(version >= 21) {
            IMPL = new ViewUtils.ViewUtilsImplLollipop();
        } else {
            IMPL = new ViewUtils.ViewUtilsImplBase();
        }

    }

    private static class ViewUtilsImplLollipop implements ViewUtils.ViewUtilsImpl {
        private ViewUtilsImplLollipop() {
        }

        public void setBoundsViewOutlineProvider(View view) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(view);
        }
    }

    private static class ViewUtilsImplBase implements ViewUtils.ViewUtilsImpl {
        private ViewUtilsImplBase() {
        }

        public void setBoundsViewOutlineProvider(View view) {
        }
    }

    private interface ViewUtilsImpl {
        void setBoundsViewOutlineProvider(View var1);
    }
}
