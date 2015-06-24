package com.chengsoft.android.spotifystreamer.support;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Adapter that allows beans to set views
 */
public class BeanAdapter<T> extends ArrayAdapter<T> {

    private int resource;
    private Map<Integer, ViewContentSetter<T>> resourceContentSetterMap;
    private final String LOG_TAG = BeanAdapter.class.getSimpleName();

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     * @param resourceContentSetterMap  The map that ties the resource and a way to set the view
     */
    public BeanAdapter(Context context, int resource, List<T> objects, Map<Integer, ViewContentSetter<T>> resourceContentSetterMap) {
        super(context, resource, objects);
        this.resource = resource;
        this.resourceContentSetterMap = resourceContentSetterMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            T item = getItem(position);
            for (Map.Entry<Integer, ViewContentSetter<T>> entry : resourceContentSetterMap.entrySet()) {
                // Find view to set content
                View curView = view.findViewById(entry.getKey());
                // Set the content of the view with the item
                ViewContentSetter<T> contentSetter = entry.getValue();
                contentSetter.setViewContent(curView, item);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error setting fields in BeanAdapter");
            throw new IllegalStateException(
                    "Error setting fields in BeanAdapter", e);
        }

        return view;
    }

}
