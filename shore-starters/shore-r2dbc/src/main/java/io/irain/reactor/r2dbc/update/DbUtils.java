package io.irain.reactor.r2dbc.update;

import io.irain.reactor.commons.exception.ServerException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Mono;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author youta
 **/
@SuppressWarnings("unused")
public interface DbUtils {

    /**
     * 忽略的属性
     */
    List<String> ignoreDescriptors = List.of("class");

    /**
     * 更新
     * @param template 模板
     * @param entity 实体
     * @return 更新结果
     * @param <T> 实体类型
     */
    static <T> Mono<Long> update(R2dbcEntityTemplate template, T entity) {
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
                return Mono.error(new ServerException("Can not generate valid Sql statement"));
            }
        }

        if (query == null || update == null) {
            return Mono.error(new ServerException("Can not generate valid Sql statement"));
        }
        return template.update(entity.getClass())
                .matching(query)
                .apply(update);
    }

}
