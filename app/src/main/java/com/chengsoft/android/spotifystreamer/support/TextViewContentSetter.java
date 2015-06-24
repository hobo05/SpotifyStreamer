package com.chengsoft.android.spotifystreamer.support;

import android.widget.TextView;

/**
 * Convenience subclass that uses a bean property to set a {@link TextView}
 * @param <T> the bean type
 */
public class TextViewContentSetter<T> implements ViewContentSetter<T> {

    private String beanProperty;
    private PropertyUtils propertyUtils = PropertyUtils.getInstance();

    public TextViewContentSetter(String beanProperty) {
        this.beanProperty = beanProperty;
    }

    @Override
    public void setViewContent(Object view, T object) {
        // retrieve the property and set the text of the text view
        TextView textView = (TextView) view;
        Object property = propertyUtils.readProperty(object, beanProperty);
        if (property != null) {
            textView.setText(property.toString());
        }
    }
}
