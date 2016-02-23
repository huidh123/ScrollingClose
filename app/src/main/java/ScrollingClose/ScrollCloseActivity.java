package ScrollingClose;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.icephone.scrollingclose.R;

import Utils.LogUtils;
import Utils.ViewUtils;

/**
 * Created by 晨晖 on 2016-02-22.
 */
public class ScrollCloseActivity extends Activity {
    private ViewDragHelper mViewDragHelper;
    private View mRootView;
    private Rect mRootViewRect;

    private boolean isInitOK = false;
    private int curLeft = 0;
    private int curTop = 0;
    //初始状态下的DragView的位置，用于重置的时候使用
    private Point mAutoBackPoint = new Point();
    private int ScreenHeight;
    private int ScreenWidth;

    //ScrollActivity相关配置信息
    private ScrollCloseConfig mCloseConfig;

    private ObjectAnimator closeAnima;
    private ObjectAnimator resetAnima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean initScrollView(){
        //获取Activity的父ViewGroup
        this.mRootView = ((ViewGroup)this.findViewById(android.R.id.content));
        if(!(this.mRootView instanceof ViewGroup)){
            return false;
        }
        //初始化配置信息
        mCloseConfig = new ScrollCloseConfig();
        mAutoBackPoint.set(mRootView.getLeft(),mRootView.getTop());
        ScreenHeight = ViewUtils.getScreenHeight(this);
        ScreenWidth = ViewUtils.getScreenWidth(this);

        mViewDragHelper = ViewDragHelper.create((ViewGroup) mRootView,1.0f,new DragHelperCallback());
        //设置边缘允许滚动
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
        //获取边界范围,会进行处理使得区域变小，可以触发边缘拖动
        mRootViewRect = ViewUtils.getViewRect(mRootView,mCloseConfig.EGDE_TRIGGER_WIDTH);
        mRootView.setTouchDelegate(new DecorDelegate(mRootViewRect,mRootView));

        initAnimation(mRootView);
        isInitOK = true;
        return true;
    }

    private void initAnimation(View animaView){
        closeAnima = ObjectAnimator.ofFloat(animaView,"rate",0.0f,1.0f);
        closeAnima.setDuration(mCloseConfig.ANIME_DURATION);
        closeAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //在关闭动画完成之后，调用realCloseActivity关闭Activity
                realCloseActivity();
            }
        });
        resetAnima = ObjectAnimator.ofFloat(animaView,"rate",1.0f,0.0f);
        resetAnima.setDuration(mCloseConfig.ANIME_DURATION);
    }

    /**
     * 执行Activity的关闭操作
     */
    private void realCloseActivity(){
        this.finish();
    }

    /**
     * 重置Activity位移，当未移动超过阈值的时候
     * @param left 发生位移的距离
     * @param top 发生位移的距离
     */
    private void resetRootView(final int left, int top){
        if(isInitOK){
            resetAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = (float) animation.getAnimatedValue("rate");
                    ((View)((ObjectAnimator)animation).getTarget()).setLeft((int) (left * rate));
                }
            });
            resetAnima.start();
            //重置View的位置
            mViewDragHelper.getCapturedView().offsetLeftAndRight(-left);
        }
    }

    /**
     * 关闭Activity
     * @param left 发生位移的距离
     * @param top 发生位移的距离
     */
    private void closeActivity(final int left, int top){
        if(isInitOK){
            closeAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = (float) animation.getAnimatedValue("rate");
                    ((View)((ObjectAnimator)animation).getTarget()).setLeft((int) ((ScreenWidth - left) * rate));
                }
            });
            closeAnima.start();
        }
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        //是否边缘被点击，只有当点击过之后才会触发滑动
        private boolean isEdgeTouched = false;
        //记录滑动距离,以向左向下为正方向
        private int mScrollX = 0;
        private int mScrollY = 0;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //只有当边缘滑动时才触发事件
            if(!isEdgeTouched){
                return 0;
            }
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            if(edgeFlags == ViewDragHelper.EDGE_LEFT){
                isEdgeTouched = true;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //当未点击的时候直接返回
            if(!isEdgeTouched){
                return;
            }
            //当滑动手势结束之后，清除是否点击标志位
            isEdgeTouched = false;
            //判断是否超过位移边界，然后进行进行状态处理
            int moveLeft = mViewDragHelper.getCapturedView().getLeft();
            int moveTop = mViewDragHelper.getCapturedView().getTop();
            //判断当前RootView位移是否超过了阈值
            if(moveLeft >= mCloseConfig.AUTO_SWITCH_VALUE){
                closeActivity(moveLeft,moveTop);
            }else{
                resetRootView(moveLeft,moveTop);
            }
        }
    }

    private class DecorDelegate extends TouchDelegate {

        public DecorDelegate(Rect bounds, View delegateView) {
            super(bounds, delegateView);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mViewDragHelper.processTouchEvent(event);
            return true;
        }
    }
}
