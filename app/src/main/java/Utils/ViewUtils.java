package Utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 晨晖 on 2016-02-22.
 */
public class ViewUtils {
    /**
     * 获取View的RECT区域范围
     * @param view
     * @param egdeWidth 边缘偏差 会使获取的RECT区域变小
     * @return
     */
    public static Rect getViewRect(View view,int egdeWidth){
        int witdh  = view.getWidth();
        int height = view.getHeight();
        Rect res = new Rect();
        res.left = view.getLeft() - egdeWidth;
        res.right = view.getRight()- egdeWidth;
        res.top = view.getTop()- egdeWidth;
        res.bottom = view.getBottom()- egdeWidth;
        return res;
    }

    public static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
}
