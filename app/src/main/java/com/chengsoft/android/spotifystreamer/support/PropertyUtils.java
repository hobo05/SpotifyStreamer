package com.chengsoft.android.spotifystreamer.support;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.PropertyDescriptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A very simplified PropertyUtils to read and write bean properties
 *
 * @author Tim Cheng
 *
 */
public class PropertyUtils {

    private PropertyUtils() {}

    private static class PropertyUtilsHolder {
        private static final PropertyUtils INSTANCE = new PropertyUtils();
    }

    public static PropertyUtils getInstance() {
        return PropertyUtilsHolder.INSTANCE;
    }

    // Holds any descriptors that have been loaded before
    private Map<String, PropertyDescriptor> descriptorCache = new HashMap<>();

    /**
     * Read bean property.
     *
     * @param bean the bean
     * @param property the property
     * @return the bean property value
     */
    public Object readProperty(Object bean, String property) {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, property);
        Method readMethod = descriptor.getReadMethod();
        try {
            return readMethod.invoke(bean);
        } catch (Exception e) {
            String errorMsg = String.format("Error while writing property [%s] in bean [%s]",
                    property, bean);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Write bean property.
     *
     * @param bean the bean
     * @param property the property to modify
     * @param value the value to write
     */
    public void writeProperty(Object bean, String property, Object value) {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, property);
        Method writeMethod = descriptor.getWriteMethod();
        try {
            writeMethod.invoke(bean, value);
        } catch (Exception e) {
            String errorMsg = String.format("Error while writing property [%s] in bean [%s]",
                    property, bean);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the {@link PropertyDescriptor}
     *
     * @param bean the bean
     * @param property the property
     * @return the {@link PropertyDescriptor}
     */
    public PropertyDescriptor getPropertyDescriptor(Object bean, String property) {
        // Try to retrieve it from cache, otherwise create one
        Class<? extends Object> beanClass = bean.getClass();
        String cacheKey = createCacheKey(beanClass, property);
        PropertyDescriptor descriptor = descriptorCache.get(cacheKey);
        if (descriptor != null) {
            return descriptor;
        }

        try {
            // Create new property descriptor and save it to the cache
            descriptor = new PropertyDescriptor(property, beanClass);
            descriptorCache.put(cacheKey, descriptor);
            return descriptor;
        } catch (IntrospectionException e) {
            throw new RuntimeException(String.format("Error retrieving PropertyDescriptor for [%s]", beanClass));
        }
    }


    /**
     * Creates the cache key.
     *
     * @param beanClass the bean class
     * @param property the property
     * @return the cache key
     */
    private String createCacheKey(Class<? extends Object> beanClass,
                                  String property) {
        return String.format("%s_%s", beanClass.getName(), property);
    }
}
