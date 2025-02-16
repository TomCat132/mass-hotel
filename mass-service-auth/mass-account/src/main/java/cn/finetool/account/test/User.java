package com.example.fastexcel.entity;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class User {
    
    @ExcelProperty("编号")
    private Integer id;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("年龄")
    private Integer age;

}

