package com.vaadin.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableDescriptor implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4241062150327079915L;
    private List<TableDescriptor.Column> columns = new ArrayList<TableDescriptor.Column>();
    private String sortColumnName;
    private Boolean isSortAscending;


    public String getSortColumnName() {
        return sortColumnName;
    }

    public void setSortColumnName(String sortColumnName) {
        this.sortColumnName = sortColumnName;
    }

    public Boolean getIsSortAscending() {
        return isSortAscending;
    }

    public void setIsSortAscending(Boolean isSortAscending) {
        this.isSortAscending = isSortAscending;
    }

    public List<TableDescriptor.Column> getColumns() {
        return columns;
    }

    public void setColumns(List<TableDescriptor.Column> columns) {
        this.columns = columns;
    }

    public void addColumn(TableDescriptor.Column column) {
        if (columns == null) {
            columns = new ArrayList<TableDescriptor.Column>();
        }
        columns.add(column);
    }

    public class Column implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -3453058967024445823L;

        private Object tableColumn;
        private String header;
        private Integer width;
        private Boolean collapsed;

        public Object getTableColumn() {
            return tableColumn;
        }

        public void setTableColumn(Object tableColumn) {
            this.tableColumn = tableColumn;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Boolean isCollapsed() {
            return collapsed;
        }

        public void setCollapsed(Boolean isCollapsed) {
            this.collapsed = isCollapsed;
        }

        @Override
        public String toString() {
            return "Column [tableColumn=" + tableColumn + ", header=" + header + ", width=" + width
                    + ", collapsed="
                    + collapsed + "]";
        }


    }

}
