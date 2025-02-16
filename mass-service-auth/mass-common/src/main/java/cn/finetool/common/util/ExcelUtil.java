package cn.finetool.common.util;

import cn.finetool.common.constant.Constant;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.idev.excel.FastExcel;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Excel 工具包
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 导出Excel
     *
     * @param res:
     * @param clazz:
     * @param data：  sheet数据
     */
    public static void exportExcel(HttpServletResponse res, String fileName, Class<?> clazz, Collection<?> data) throws IOException {
        // 设置内容类型和字符编码
     
        try (OutputStream os = res.getOutputStream()) {
            Long start = System.currentTimeMillis();
            // 使用输出流将数据写入响应
            FastExcel.write(os)
                    .sheet("OrderRecords") 
                    .head(clazz)
                    .doWrite(data);
            Long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start) + " ms");
        }
    }
}
