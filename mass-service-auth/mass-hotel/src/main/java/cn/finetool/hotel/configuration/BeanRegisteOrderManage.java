package cn.finetool.hotel.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/*
TODO:
  Bean生命周期 :Bean 定义 -> ---> BeanDefinitionRegistryPostProcessor ---> Bean 实例化 
  -> Bean 属性填充 -> Bean 依赖注入 -> Bean 初始化 -> Bean 销毁
  △: BeanDefinitionRegistryPostProcessor -》 BeanFactoryPostProcessor 子类
 */
/**
 * Bean实例化前置操作 / Bean注册后置操作
 */
@Component
public class BeanRegisteOrderManage implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //以对 BeanDefinition 进行修改或注册新的 BeanDefinition
        List<String> beanNames = new ArrayList<>(Arrays.asList(registry.getBeanDefinitionNames()));
        BeanDefinition appContext = registry.getBeanDefinition("appContext");
        BeanDefinition serveHandler = registry.getBeanDefinition("serveHandler");
        beanNames.remove(appContext);
        beanNames.remove(serveHandler);
        beanNames.add(0, "appContext");
        beanNames.add(1, "serveHandler");

        beanNames.forEach( beanName -> {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            registry.removeBeanDefinition(beanName);
            registry.registerBeanDefinition(beanName, beanDefinition);
        });
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 可以在这里进行 BeanFactory 的其他配置
    }
}
