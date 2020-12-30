package studio.rockpile.devtools.registry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class DynamicBeanRegister implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @SuppressWarnings(value = "static-access")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext == null) {
            this.applicationContext = applicationContext;
        }
    }

    public static <T> void registry(String beanName, Class<T> clazz, Map<String, Object> properties) {
        // 获取BeanFactory
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        // 创建bean对象
        BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            beanDefinition.addPropertyValue(next.getKey(), next.getValue());
        }
        // 动态注册bean
        // 多次注入同一Class的Bean对象，如果beanName不一样，那么会产生两个Bean；如果beanName一样，后面注入的会覆盖前面
        beanFactory.registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
    }

    public static <T> void remove(String beanName, Class<T> clazz) {
        T bean = applicationContext.getBean(beanName, clazz);
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.removeBeanDefinition(beanName);
        if (bean != null) {
            bean = null;
        }
    }

    // 获取动态注册的bean
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }
}
