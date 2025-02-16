package com.example.fastexcel.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;

import cn.idev.excel.exception.ExcelDataConvertException;
import cn.idev.excel.metadata.data.ReadCellData;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BaseExcelListener<T> extends AnalysisEventListener<T> {

    // 定义一个数据列表，用于存储读取到的每一行数据
    private List<T> dataList = new ArrayList<>();

    //定义一个计数器
    private int count = 0;
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        // 添加读取到的每一行数据到数据列表
        dataList.add(t);

        //计数器自增
        count++;
        if (count % 10000 == 0) {
            System.out.println("已读取 " + count + " 条数据");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 读取完成后，打印数据列表的大小
        System.out.println("读取完成，共读取了 " + dataList.size() + " 条数据");
    }

    public List<T> getDataList() {
        return dataList;
    }

    /**
     * 自定义 ReadListener（处理异常）
     * @param exception
     * @param context
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析异常: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("第 {} 行, 第 {} 列解析错误，数据: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    /**
     * 自定义 ReadListener（获取表头）
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到表头: {}", JSON.toJSONString(headMap));
    }
    
}
