package Utils;

import android.util.Log;

/**
 * Created by 晨晖 on 2016-02-22.
 */
public class LogUtils {
    public static boolean isDebug = true;

    public static void log(Object obj , String msg){
        if(isDebug){
            Log.e(obj.getClass().getSimpleName() , "输出信息："+msg);
        }
    }
}
