package com.cxcoder.utils;

import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ChangXuan
 * @Decription: Excel 工具类
 * @Date: 18:49 2021/7/25
 **/
@Component
public class ExUtil {

    /**
     * 更改写入行背景色
     *
     * @param writer   excelWriter
     * @param list     当前行数据
     * @param oldStyle 原来样式
     * @param newStyle 更改样式
     */
    public void changeRowGroundColor(ExcelWriter writer, List<Object> list, StyleSet oldStyle, StyleSet newStyle) {
        writer.setStyleSet(newStyle);
        writer.writeRow(list);
        writer.setStyleSet(oldStyle);
    }

    /**
     * 获取指定背景色样式
     *
     * @param writer excelWriter
     * @param color  颜色
     * @return StyleSet
     */
    public StyleSet getColorStyle(ExcelWriter writer, short color) {
        StyleSet target = new StyleSet(writer.getWorkbook());
        target.getCellStyle().setFillForegroundColor(color);
        target.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return target;
    }
}
