package me.youm.reactor.r2dbc.util;

import org.springframework.beans.BeanUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Mono;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author youta
 **/
public interface DbUtils {

    List<String> ignoreDescriptors = Arrays.asList("class");

    static <T> Mono<Integer> update(R2dbcEntityTemplate template, T entity) {
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(entity.getClass());
        Update update = null;
        Query query = null;
        for (PropertyDescriptor descriptor: descriptors) {
            try {
                String name = descriptor.getName();

                if (ignoreDescriptors.contains(name)) {
                    continue;
                }

                Object invoke = descriptor.getReadMethod().invoke(entity);
                if (invoke == null) {
                    continue;
                }
                if ("id".equals(name)) {
                    query = query(Criteria.where(name).is(invoke));
                } else {
                    update = update == null? Update.update(name, invoke):
                            update.set(name, invoke);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (query == null || update == null) {
            return Mono.error(new RuntimeException("无法生成有效的Sql语句！"));
        }

        return template.update(entity.getClass())
                .matching(query)
                .apply(update);
    }

}
