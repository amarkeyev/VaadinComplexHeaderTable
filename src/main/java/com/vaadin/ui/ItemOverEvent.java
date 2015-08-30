package com.vaadin.ui;

import com.vaadin.data.Item;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.util.ReflectTools;

import java.lang.reflect.Method;

/**
 * Created by andrey.sorokin on 21.04.2014.
 */
public class ItemOverEvent extends Component.Event {
    public static final Method ITEM_OVER_METHOD =
            ReflectTools.findMethod(ItemOverListener.class, "onItemOverEvent", ItemOverEvent.class);

    private Item item;
    private Object itemId;
    private Object propertyId;
    private MouseEventDetails details;


    /**
     * Constructs a new event with the specified source component.
     *
     * @param source the source component of the event
     */
    public ItemOverEvent(Component source, Item item, Object itemId, Object propertyId,
            MouseEventDetails details) {
        super(source);

        this.item = item;
        this.itemId = itemId;
        this.propertyId = propertyId;
        this.details = details;
    }

    public Item getItem() {
        return item;
    }

    public Object getItemId() {
        return itemId;
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public MouseEventDetails getDetails() {
        return details;
    }
}
