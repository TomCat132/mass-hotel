package cn.finetool.common.util;




import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.*;

public class CommonsUtils {


    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(8, 7);

    private static final Random random = new Random();

    static {
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setGroupingUsed(false);
    }



    public static String getWorkerID() {
        return String.valueOf(idWorker.nextId());
    }

    /**
     * md5 进行加密
     *
     * @param orginStr
     * @return
     */
    public static String encodeMD5(String orginStr) {
        return DigestUtils.md5Hex(orginStr);
    }


    /**
     * 计算文件签名
     *
     * @param file
     * @return
     */
    public static String fileSignature(byte[] file) {
        return DigestUtils.md5Hex(file);
    }


    public static String floatToStr(float ft) {
        return numberFormat.format(ft);
    }

    /**
     * 输入流转换为xml字符串
     *
     * @param inputStream
     * @return
     */
    public static String streamToString(InputStream inputStream) throws
            IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inputStream.close();
        return new String(outSteam.toByteArray());
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }


    /**
     * 延时
     *
     * @param delay
     */
    public static void delay(long delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
