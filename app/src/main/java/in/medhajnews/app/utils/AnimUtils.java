package in.medhajnews.app.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.transition.Transition;
import android.util.ArrayMap;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import java.util.ArrayList;

/**
 * Utility methods for working with animations.
 */
public class AnimUtils {

    private AnimUtils() { }

    /**
     * Linear interpolate between a and b with parameter t.
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }


    /**
     * An implementation of {@link android.util.Property} to be used specifically with fields of
     * type
     * <code>float</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Float) set()} function that takes the primitive
     * <code>float</code> type and avoids autoboxing and other overhead associated with the
     * <code>Float</code> class.
     *
     * @param <T> The class on which the Property is declared.
     **/
    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Float)} that is faster when dealing
         * with fields of type <code>float</code>.
         */
        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    /**
     * An implementation of {@link android.util.Property} to be used specifically with fields of
     * type
     * <code>int</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Integer) set()} function that takes the primitive
     * <code>int</code> type and avoids autoboxing and other overhead associated with the
     * <code>Integer</code> class.
     *
     * @param <T> The class on which the Property is declared.
     */
    public static abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Integer)} that is faster when dealing
         * with fields of type <code>int</code>.
         */
        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value.intValue());
        }

    }
}

