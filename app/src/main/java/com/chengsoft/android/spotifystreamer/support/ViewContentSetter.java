package com.chengsoft.android.spotifystreamer.support;

import com.chengsoft.android.spotifystreamer.support.BeanAdapter;

/**
 * Interface used by {@link BeanAdapter} to set the content of a view
 *
 * @param <T> The object to use to set the content of the view
 */
public interface ViewContentSetter<T> {
    public void setViewContent(Object view, T object);
}
