
package com.leonside.dataroad.common.domain;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.utils.DateUtil;
import com.leonside.dataroad.common.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MetaColumn implements Serializable {

    private String name;
    private String type;
    private Integer index;  //?
    private String value;
    private SimpleDateFormat timeFormat;
    private String splitter;  //?
//    private Boolean isPart; //?

    public Object convertToColumn(String value){
        return StringUtil.string2col(value, getType(), getTimeFormat());
    }

    public static List<MetaColumn> getMetaColumns(List columns, boolean generateIndex){
        List<MetaColumn> metaColumns = new ArrayList<>();
        if(columns != null && columns.size() > 0) {
            if (columns.get(0) instanceof Map) {
                for (int i = 0; i < columns.size(); i++) {
                    Map sm = (Map) columns.get(i);
                    MetaColumn mc = new MetaColumn();

                    Object colIndex = sm.get("index");
                    if(colIndex != null) {
                        if(colIndex instanceof Integer) {
                            mc.setIndex((Integer) colIndex);
                        } else if(colIndex instanceof Double) {
                            Double doubleColIndex = (Double) colIndex;
                            mc.setIndex(doubleColIndex.intValue());
                        }
                    } else {
                        if (generateIndex) {
                            mc.setIndex(i);
                        } else {
                            mc.setIndex(-1);
                        }
                    }

                    mc.setName(sm.get("name") != null ? String.valueOf(sm.get("name")) : null);
                    mc.setType(sm.get("type") != null ? String.valueOf(sm.get("type")) : null);
                    mc.setValue(sm.get("value") != null ? String.valueOf(sm.get("value")) : null);
                    mc.setSplitter(sm.get("splitter") != null ? String.valueOf(sm.get("splitter")) : null);
//                    mc.setPart(sm.get("isPart") != null ? (Boolean) sm.get("isPart") : false);

                    if(sm.get("format") != null && String.valueOf(sm.get("format")).trim().length() > 0){
                        mc.setTimeFormat(DateUtil.buildDateFormatter(String.valueOf(sm.get("format"))));
                    }

                    metaColumns.add(mc);
                }
            } else if (columns.get(0) instanceof String) {
                if(columns.size() == 1 && JobCommonConstant.STAR_SYMBOL.equals(columns.get(0))){
                    MetaColumn mc = new MetaColumn();
                    mc.setName(JobCommonConstant.STAR_SYMBOL);
                    metaColumns.add(mc);
                } else {
                    for (int i = 0; i < columns.size(); i++) {
                        MetaColumn mc = new MetaColumn();
                        mc.setName(String.valueOf(columns.get(i)));
                        if (generateIndex) {
                            mc.setIndex(i);
                        } else {
                            mc.setIndex(-1);
                        }
                        metaColumns.add(mc);
                    }
                }
            } else {
                throw new IllegalArgumentException("column argument error");
            }
        }

        return metaColumns;
    }

    public static List<MetaColumn> getMetaColumns(List columns){
        return getMetaColumns(columns, true);
    }

    public static List<String> getColumnNames(List columns){
        List<String> columnNames = new ArrayList<>();

        List<MetaColumn> metaColumns = getMetaColumns(columns);
        for (MetaColumn metaColumn : metaColumns) {
            columnNames.add(metaColumn.getName());
        }

        return columnNames;
    }

    public static MetaColumn getMetaColumn(List columns, String name){
        List<MetaColumn> metaColumns = getMetaColumns(columns);
        for (MetaColumn metaColumn : metaColumns) {
            if(StringUtils.isNotEmpty(metaColumn.getName()) && metaColumn.getName().equalsIgnoreCase(name)){
                return metaColumn;
            }
        }

        return null;
    }

    public String getSplitter() {
        return splitter;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(SimpleDateFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

//    public Boolean getPart() {
//        return isPart;
//    }
//
//    public void setPart(Boolean part) {
//        isPart = part;
//    }
}
