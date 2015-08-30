package com.vaadin.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * TableCellStyleBuilder is used to build sequence of CSS-styles.
 */
public class TableCellStyleBuilder {
    public static final String SPACE_SEPARATOR = " ";
    private List<String> styles = new ArrayList<>();

    /**
     * Return string of css-styles separated by space
     *
     * @return string of css-styles
     */
    public String build() {
        String cellStyle = "";
        for(String style: styles) {
            cellStyle = cellStyle + (cellStyle.equals("")?"":SPACE_SEPARATOR) + style;
        }
        return cellStyle.startsWith("v-table-cell-content-") ? cellStyle.replaceFirst("v-table-cell-content-", "") : cellStyle;
    }

    /**
     * @param condition if true style will be added to result sequence
     * @param style     css-style name
     * @return this builder.
     */
    public TableCellStyleBuilder setStyle(boolean condition, final String style) {
        if (condition) {
            styles.add(style);
        }
        return this;
    }
}
