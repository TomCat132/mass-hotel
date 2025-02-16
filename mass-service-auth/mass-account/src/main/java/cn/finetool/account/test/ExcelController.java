package cn.finetool.account.test;

import cn.idev.excel.FastExcel;
import com.example.fastexcel.entity.User;
import com.example.fastexcel.listener.BaseExcelListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class ExcelController {


    /**
     * 导出Excel
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 桌面路径
        String pathName = System.getProperty("user.home") + "/Desktop/student.xlsx";
        Long start = System.currentTimeMillis();
        FastExcel.write(pathName)
                .sheet("student")
                .head(User.class).doWrite(buildData());
        Long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start));
    }


    /**
     * 导入Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择一个文件上传！");
        }
        try {
            Long start = System.currentTimeMillis();
            BaseExcelListener<User> baseExcelListener = new BaseExcelListener<>();
            FastExcel.read(file.getInputStream(), User.class, baseExcelListener).sheet().doRead();
            List<User> dataList = baseExcelListener.getDataList();
            System.out.println(dataList.size());
            Long end = System.currentTimeMillis();
            return ResponseEntity.ok("文件上传并处理成功！耗时：" + (end - start) + "ms");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件处理失败！");
        }
    }

    private List<User> buildData() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            User user = new User();
            user.setId(i);
            user.setName("张三" + i);
            user.setAge(88);
            list.add(user);
            System.out.println(user);
        }
        return list;
    }
}

