//package cn.finetool.mass.config;
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@MapperScan("cn.finetool.mass.mapper*")
//public class MybatisPlusConfig {
//
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor();
//        innerInterceptor.setDbType(DbType.MYSQL);
//        innerInterceptor.setMaxLimit(100000L);
//        interceptor.addInnerInterceptor(innerInterceptor);
//        return interceptor;
//    }
//}
