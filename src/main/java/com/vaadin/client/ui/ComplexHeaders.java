package com.vaadin.client.ui;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Stanislav.Matveev on 24.03.2014.
 */
public class ComplexHeaders {

    public static final String CC_ATTR = "complexcaption";
    public static final String CCR_ATTR = "complexcaptionrows";
    public static final String VC_ATTR = "visiblecolumns";
    public static final String CID_ATTR = "cid";


    private List<ComplexHeaders> nodes = new ArrayList<ComplexHeaders>();
    private String caption = null;
    private Object property = null;
    private int rows = 0;
    private LinkedHashSet keySet = new LinkedHashSet();

    public ComplexHeaders() {
    }

    private ComplexHeaders(String caption) {
        this.caption = caption;
    }

    public static ComplexHeaders createFromUIDL(ComplexHeaders captions,
            ApplicationConnection client, UIDL uidl) {
        if (captions != null) {
            captions.clear();
        }
        return create(captions, new ParametersContainer(fillArrayFromUIDL(client, uidl)));
    }

    private static List<List> fillArrayFromUIDL(ApplicationConnection client, UIDL uidl) {
        int rowsInHeader = uidl.hasAttribute(CCR_ATTR) ? uidl.getIntAttribute(CCR_ATTR) : 1;
        UIDL vcUIDL = uidl.getChildByTagName(VC_ATTR);

        List<List> complexHeaders = new ArrayList<List>();
        for (int i = 0; i < rowsInHeader; i++) {
            complexHeaders.add(new ArrayList<String>());
        }
        complexHeaders.add(new ArrayList<String>());

        Iterator<?> it = vcUIDL.getChildIterator();
        int colIndex = 0;
        while (it.hasNext()) {
            UIDL col = (UIDL) it.next();
            fillHTMLCaptionsColumn(client, complexHeaders, col, colIndex++, rowsInHeader);
            complexHeaders.get(rowsInHeader).add(col.getStringAttribute(CID_ATTR));
        }
        return complexHeaders;
    }

    private static void fillHTMLCaptionsColumn(ApplicationConnection client,
            List<List> complexHeaders, UIDL columnUIDL, int colIndex, int rowsCount) {
        if (rowsCount == 0) {
            return;
        }
        for (int i = 0; i < rowsCount; i++) {
            String st = getCaptionFromUIDL(columnUIDL, i);
            if (i == rowsCount - 1) {
                boolean hasIcon = columnUIDL.hasAttribute("icon"), hasIconTitle =
                        columnUIDL.hasAttribute("icon-title");
                String iconSt =
                        hasIcon ?
                                "<img src=\"" + Util.escapeAttribute(client.translateVaadinUri(
                                        columnUIDL.getStringAttribute("icon"))) + "\"" +
                                        (hasIconTitle ?
                                                " title=\"" + columnUIDL
                                                        .getStringAttribute("icon-title") + "\"" :
                                                "") +
                                        " class=\"v-icon\">" /* TODO + st*/ :
                                hasIconTitle ?
                                        "<div title=\"" + columnUIDL
                                                .getStringAttribute("icon-title") + "\">" + st
                                                + "</div>" : st;
                complexHeaders.get(i).add(iconSt);
                for (int j = rowsCount - 2; j >= 0; j--) {
                    String newSt = getCaptionFromUIDL(columnUIDL, j);
                    if (newSt.equals(st)) {
                        complexHeaders.get(j).set(colIndex, iconSt);
                    }
                }
            } else {
                complexHeaders.get(i).add(st);
            }
        }
    }

    private static String getCaptionFromUIDL(UIDL uidl, int index) {
        String columnAttr = CC_ATTR + String.valueOf(index);
        return uidl.hasAttribute(columnAttr) ? uidl
                .getStringAttribute(columnAttr) : "";
    }

    public static ComplexHeaders createFromArray(ComplexHeaders chRoot, String[][] complexHeaders,
            Object[] visibleFields) {
        if (chRoot != null) {
            chRoot.clear();
        }
        if (complexHeaders == null) {
            return chRoot;
        }
        return recurseCreate(chRoot, new ParametersContainer(complexHeaders, visibleFields), 0,
                complexHeaders[0].length, 0);
    }

    private static ComplexHeaders create(ParametersContainer complexHeaders) {
        return create(new ComplexHeaders(), complexHeaders);
    }

    private static ComplexHeaders create(ComplexHeaders chRoot,
            ParametersContainer complexHeaders) {
        if (complexHeaders == null) {
            return chRoot;
        }
        return recurseCreate(chRoot, complexHeaders, 0, complexHeaders.sizeLength(), 0);
    }

    private static ComplexHeaders recurseCreate(ComplexHeaders level,
            ParametersContainer complexHeaders, int start, int end, int deep) {
        level.rows = complexHeaders.sizeDeep() - deep;
        for (int i = start; i < end; i++) {
            level.keySet.add(complexHeaders.getField(i));
        }
        if (level.rows == 0) {
            level.property = complexHeaders.getField(start);
            return level;
        }
        String caption = complexHeaders.get(deep, start);
        for (int i = start; i < end; i++) {
            if (!complexHeaders.get(deep, i).equals(caption) ||
                    (level.rows == 1 && !equals(complexHeaders.getField(i),
                            complexHeaders.getField(start)))) { /* divide columns (start new) when either captions different either on lowest level properties are different */
                level.nodes.add(recurseCreate(new ComplexHeaders(caption), complexHeaders, start, i,
                        deep + 1));
                start = i;
                caption = complexHeaders.get(deep, start);
            }
        }
        level.nodes.add(recurseCreate(new ComplexHeaders(caption), complexHeaders, start, end,
                deep + 1));
        return level;
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s);
    }

    public String getCaption() {
        return caption == null ? "" : caption;
    }

    public LinkedHashSet keySet() {
        return keySet;
    }

    public List<Object> keyList() {
        return new ArrayList(keySet);
    }

    public int size() {
        return rows;
    }

    public ComplexHeaders getTopByPropertyId(Object propertyId) {
        for (ComplexHeaders node : nodes) {
            if (node.keySet().contains(propertyId)) {
                return node;
            }
        }
        return null;
    }

    public ComplexHeaders getTopByCaption(String caption) {
        for (ComplexHeaders node : nodes) {
            if (equals(node.getCaption(), caption)) {
                return node;
            }
        }
        return null;
    }

    public ComplexHeaders getPropertyNode(Object propertyId) {
        return findByPropertyId(propertyId);
    }

    public ComplexHeaders getCaptionNode(String caption) {
        return findByCaption(caption);
    }

    /**
     * Return array of not null strings, considering menuCaption field is available,
     * otherwise caption, otherwise empty string.
     *
     * @param propertyId
     * @return
     */
    public String[] getCaptions(Object propertyId, boolean isFolded) {
        String[] complexHeader = get(propertyId);
        if (complexHeader == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        String prevHeader = null;
        for (String header : complexHeader) {
            if (isFolded) {
                if (prevHeader == null || !prevHeader.equals(header)) {
                    prevHeader = header;
                    list.add(header);
                }
            } else {
                list.add(header);
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] get(Object propertyId) {
        if (propertyId == null) {
            return null;
        }
        String[] chRow = new String[rows];
        return fill(chRow, propertyId) ? chRow : null;
    }

    private ComplexHeaders findByPropertyId(Object propertyId) {
        if (rows > 0) {
            for (ComplexHeaders node : nodes) {
                if (node.keySet().contains(propertyId)) {
                    return node.findByPropertyId(propertyId);
                }
            }
        } else {
            if (propertyId.equals(property)) {
                return this;
            }
        }
        return null;
    }

    /**
     * Search for first occurence of caption inside tree and returns its properties
     *
     * @param caption
     * @return
     */

    private ComplexHeaders findByCaption(String caption) {
        if (rows > 0) {
            for (ComplexHeaders node : nodes) {
                if ((">" + node.caption + "<").indexOf(">" + caption + "<") != -1) {
                    return node;
                } else {
                    ComplexHeaders ch = node.findByCaption(caption);
                    if (ch == null) {
                        continue;
                    } else {
                        return ch;
                    }
                }
            }
        } else {
            if ((">" + this.caption + "<").indexOf(">" + caption + "<") != -1) {
                return this;
            }
        }
        return null;
    }

    private boolean fill(String[] chRow, Object propertyId) {
        if (rows > 0) {
            for (ComplexHeaders node : nodes) {
                if (node.keySet().contains(propertyId)) {
                    chRow[chRow.length - rows] = node.getCaption();
                    return node.fill(chRow, propertyId);
                }
            }
        } else {
            return propertyId.equals(property);
        }
        return false;
    }

    public void clear() {
        for (ComplexHeaders ch : nodes) {
            ch.clear();
        }
        nodes = new ArrayList<ComplexHeaders>();
        caption = null;
        property = null;
        rows = 0;
        keySet = new LinkedHashSet();
    }

    public String toString() {
        if (property != null) {
            return caption + "[" + property + "]";
        } else {
            StringBuilder sb = new StringBuilder(caption + ":( ");
            for (ComplexHeaders node : nodes) {
                sb.append(node + " ");
            }
            return sb.append(")").toString();
        }
    }

    /**
     * Пробегает по всей иерархии заголовков таблицы и возвращает самый нижний (самый дочерний), имя которого соответствует caption.
     * Метод применяется для выделения колонок только филиала без вклюния регионов. Для этого необходимо найти столбец только филиала.
     *
     * @param caption
     * @return
     */
    public ComplexHeaders getDeepestByCaption(String caption) {
        for (ComplexHeaders node : nodes) {
            ComplexHeaders deepestByCaption = node.getDeepestByCaption(caption);
            if (deepestByCaption != null) {
                return deepestByCaption;
            }
        }

        if (this.caption.contains(caption)) {
            return this;
        }

        return null;
    }

    protected static class ParametersContainer {
        private final Object storage;
        private final Object[] visibleFields;

        protected ParametersContainer(String[][] headersArray, Object[] visibleFields) {
            storage = headersArray;
            this.visibleFields = visibleFields;
        }

        protected ParametersContainer(List<List> headersList) {
            storage = headersList;
            visibleFields = headersList.get(headersList.size() - 1).toArray();
        }

        public String get(int i, int j) {
            return storage instanceof List ?
                    (String) ((List<List>) storage).get(i).get(j) :
                    ((String[][]) storage)[i][j];
        }

        public int sizeLength() {
            return storage instanceof List ?
                    ((List<List>) storage).get(0).size() :
                    ((String[][]) storage)[0].length;
        }

        public int sizeDeep() {
            return storage instanceof List ?
                    ((List<List>) storage).size() - 1 :
                    ((String[][]) storage).length;
        }

        public Object getField(int fieldIndex) {
            return visibleFields[fieldIndex];
        }
    }

    public static void main(String[] args) {

        String[][] cm = new String[][] {
                {"a", "a", "a", "a", "h", "k", "k"},
                {"b", "b", "c", "c", "h", "p", "r"},
                {"d", "e", "f", "g", "j", "p", "r"}
        };
        String[] vf = new String[] {"D", "E", "F", "G", "J", "N", "O"};

        ComplexHeaders ch = createFromArray(new ComplexHeaders(), cm, vf);

        //for (ComplexHeaders chi : ch.get("E")) System.out.println(chi);

        System.out.println(ch);

        System.out.println(Arrays.asList(ch.getCaptions("J", true)));
        System.out.println(Arrays.asList(ch.getCaptions("J", false)));

        System.out.println(ch.getCaptionNode("h").keySet());
    }

}

