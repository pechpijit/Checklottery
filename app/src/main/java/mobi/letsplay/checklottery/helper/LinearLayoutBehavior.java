package mobi.letsplay.checklottery.helper;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class LinearLayoutBehavior extends CoordinatorLayout.Behavior<LinearLayout> {
    private int toolbarHeight;

    public LinearLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = 60;
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = (float)dependency.getY()/(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }
}
