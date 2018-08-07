package com.jiang.annotaiondemo.inject;

import android.app.Activity;
import android.view.View;

import com.jiang.annotaiondemo.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InjectUtil {

    public static void initViews(Object obj, View sourceView) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            ViewInject annotation = field.getAnnotation(ViewInject.class);
            if (annotation == null) {
                continue;
            }
            int viewId = annotation.id();
            boolean clickable = annotation.clickable();
            if (viewId != -1) {
                field.setAccessible(true);
                try {
                    field.set(obj, sourceView.findViewById(viewId));
                    if (clickable && obj instanceof View.OnClickListener) {
                        sourceView.findViewById(viewId).setOnClickListener((View.OnClickListener) obj);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void initLayout(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView annotation = clazz.getAnnotation(ContentView.class);
        if (annotation != null) {
            int layoutId = annotation.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.invoke(activity, layoutId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void init(Activity activity) {
        initLayout(activity);
        initViews(activity, activity.getWindow().getDecorView());
    }
}
