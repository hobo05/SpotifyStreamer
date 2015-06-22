package com.chengsoft.android.spotifystreamer;

/**
 * Interface used by {@link BeanAdapter} to set the content of a view
 *
 * @param <V> The View class
 * @param <T> The object to use to set the content of the view
 */
public interface ViewContentSetter<V, T> {
    public void setViewContent(V view, T object);
}
