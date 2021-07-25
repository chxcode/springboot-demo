package com.cxcoder.services;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.cxcoder.utils.ExUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: ChangXuan
 * @Decription: 图书服务
 * @Date: 18:37 2021/7/25
 **/
@Service
public class BookService {

    private ExUtil exUtil;

    @Autowired
    public void setExUtil(ExUtil exUtil) {
        this.exUtil = exUtil;
    }

    public void importBooks(MultipartFile file, HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter();
        StyleSet oldStyle = writer.getStyleSet();
        StyleSet green = exUtil.getColorStyle(writer, IndexedColors.LIGHT_GREEN.getIndex());
        StyleSet yellow = exUtil.getColorStyle(writer, IndexedColors.YELLOW.getIndex());
        // 根据业务情况自行修改
        ExcelUtil.read03BySax(file.getInputStream(), 0, new RowHandler() {
            @Override
            public void handle(int sheetIndex, int rowIndex, List<Object> list) {
                switch (rowIndex) {
                    case 0:
                        // 默认第一行为标题列不做处理
                        if (list.size() > 0) {
                            writer.writeRow(list);
                        }
                        break;
                    case 1:
                        // 可以增加多个 case 针对特定行进行处理
                    default:
                        if (Double.parseDouble(list.get(3).toString()) < 0) {
                            list.add("价格不允许小于0");
                            exUtil.changeRowGroundColor(writer, list, oldStyle, yellow);
                            break;
                        }
                        list.add("新增成功");
                        System.out.println(Arrays.toString(list.toArray()));
                        exUtil.changeRowGroundColor(writer, list, oldStyle, green);
                }
            }
        });
        // 自动宽度
        writer.autoSizeColumnAll();
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //import_result.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        String fileName = "attachment;filename=import_result-" + System.currentTimeMillis() + ".xls";
        response.setHeader("Content-Disposition", fileName);
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

}
