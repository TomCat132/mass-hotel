package cn.finetool.common.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FunUtil {
    /**
     * 组装生成Predicate 断言对象的工具类
     *
     * @param <T> 参与比较的对象类型
     * @author LiuLuhao
     */
    public static class EqualsBuilder<T> {

        private final List<Predicate<T>> queryList = new ArrayList<>();

        public static <T> EqualsBuilder<T> equalsBuilder(Class<T> tClass) {
            return new EqualsBuilder<>();
        }

        /**
         * 直接组装加入一个T类型的Predicate对象
         *
         * @param predicate
         * @return
         */
        public EqualsBuilder<T> addPredicate(Predicate<T> predicate) {
            if (Objects.isNull(predicate)) {
                return this;
            }
            queryList.add(predicate);
            return this;
        }

        /**
         * 比较值的属性是否满足预期Predicate
         *
         * @param getter
         * @param predicate
         * @param <R>       预期比较值的类型
         * @return
         */
        public <R> EqualsBuilder<T> addPredicate(Function<T, R> getter, Predicate<R> predicate) {
            Predicate<T> targetPredicate = FunUtil.predicateAttr(getter, predicate);
            queryList.add(targetPredicate);
            return this;
        }


        /**
         * 比较集合每个原始值是否与给定字段值一致
         *
         * @param getter      获取字段function
         * @param equalsValue 预期值
         * @param <R>         预期比较值的类型
         * @return thisBuilder
         */
        public <R> EqualsBuilder<T> addEquals(Function<T, R> getter, R equalsValue) {
            if (validateParamNotIllegal(equalsValue)) {
                return this;
            }
            Predicate<T> predicate = item -> {
                R apply = getter.apply(item);
                return Objects.equals(apply, equalsValue);
            };
            queryList.add(predicate);
            return this;
        }

        /**
         * 比较集合每个原始值是否与给定字段值一致，或者是满足补充断言orThen即可
         *
         * @param getter      获取字段function
         * @param equalsValue 预期值
         * @param orThen      补充断言
         * @param <R>         预期比较值的类型
         * @return thisBuilder
         */
        public <R> EqualsBuilder<T> addEqualsOrThen(Function<T, R> getter, R equalsValue, Predicate<T> orThen) {
            if (validateParamNotIllegal(equalsValue)) {
                return this;
            }
            Predicate<T> predicate = item -> {
                R apply = getter.apply(item);
                return orThen.test(item) || Objects.equals(apply, equalsValue);
            };
            queryList.add(predicate);
            return this;
        }

        /**
         * 比较集合每个值是否在预期的集合范围内，或者是满足补充断言orThen即可
         *
         * @param getter          获取字段function
         * @param collectionValue 预期集合的值范围
         * @param <R>             预期比较值的类型
         * @return thisBuilder
         */
        public <R> EqualsBuilder<T> andContainsIn(Function<T, R> getter, Collection<R> collectionValue) {
            if (Strings.isEmpty(collectionValue)) {
                return this;
            }
            Predicate<T> predicate = FunUtil.containsIn(getter, collectionValue);
            queryList.add(predicate);
            return this;
        }

        /**
         * 比较集合每个值是否在预期的集合范围内
         *
         * @param getter          获取字段function
         * @param collectionValue 预期集合的值范围
         * @param orThen          补充断言
         * @param <R>             预期比较值的类型
         * @return thisBuilder
         */
        public <R> EqualsBuilder<T> andContainsInOrThen(
                Function<T, R> getter, Collection<R> collectionValue, Predicate<T> orThen) {
            if (Strings.isEmpty(collectionValue)) {
                return this;
            }
            Predicate<T> predicate = item -> {
                R apply = getter.apply(item);
                return orThen.test(item) || Objects.nonNull(apply) && collectionValue.contains(apply);
            };
            queryList.add(predicate);
            return this;
        }

        /**
         * 相等时值校验，部分值不需要做比较
         *
         * @param equalsValue 预期值
         * @param <R>         预期比较值的类型
         * @return boolean true:不合法；false：合法
         */
        private <R> boolean validateParamNotIllegal(R equalsValue) {
            if (Objects.isNull(equalsValue)) {
                return true;
            }
            return equalsValue instanceof String && Strings.isBlank((String) equalsValue);
        }

        /**
         * 合并所有查询为一个断言预测，合并逻辑是任意满足
         *
         * @return Predicate<T>
         */
        public Predicate<T> matchAny() {
            return item -> queryList.stream().anyMatch(predicate -> predicate.test(item));
        }

        /**
         * 合并所有查询为一个断言预测，合并逻辑是全部满足
         *
         * @return Predicate<T>
         */
        public Predicate<T> matchAll() {
            return item -> queryList.stream().allMatch(predicate -> predicate.test(item));
        }

        /**
         * 执行清空的方法
         *
         * @return this builder
         */
        public EqualsBuilder<T> clear() {
            queryList.clear();
            return this;
        }
    }


    /**
     * 预期属性满足条件
     *
     * @param getter    原始属性获取方法
     * @param predicate 预期属性需要满足的条件
     * @param <T>       原始属性所属类型T
     * @param <R>       预期比较的类型R
     * @return T类型的Predicate对象
     */
    public static <T, R> Predicate<T> predicateAttr(Function<T, R> getter, Predicate<R> predicate) {
        return item -> {
            R apply = getter.apply(item);
            return predicate.test(apply);
        };
    }

    /**
     * 判断T类型的对象属性是否在预期范围内，
     * 范围是属性的类型的集合
     *
     * @param getter        原始属性获取方法
     * @param expectedRange 预期范围集合
     * @param <T>           原始属性所属类型T
     * @param <R>           预期比较的类型R
     * @return T类型对象的Predicate对象
     * @author LiuLuhao
     */
    public static <T, R> Predicate<T> containsIn(Function<T, R> getter, Collection<R> expectedRange) {
        return item -> {
            R apply = getter.apply(item);
            return Objects.nonNull(apply) && expectedRange.contains(apply);
        };
    }

    /**
     * 判断T类型的对象属性是否在预期范围内
     * 范围不是属性的类型的集合，但能通过映射获取
     *
     * @param getter           原始属性获取方法
     * @param rangeValueGetter 范围集合的R类型数据获取映射
     * @param expectedRange    预期范围集合
     * @param <T>              原始属性所属类型T
     * @param <R>              预期比较的类型R
     * @param <E>              预期范围的类型E
     * @return T类型对象的Predicate对象
     * @author LiuLuhao
     */
    public static <T, R, E> Predicate<T> containsIn(
            Function<T, R> getter, Function<E, R> rangeValueGetter, Collection<E> expectedRange) {
        return item -> {
            R apply = getter.apply(item);
            return Objects.nonNull(apply)
                    && expectedRange.stream().map(rangeValueGetter).anyMatch(value -> Objects.equals(apply, value));
        };
    }

    /**
     * stream group by 提取
     *
     * @param list        数据列表
     * @param keyFunction a function map T to K
     * @param <K>         group by K 字段
     * @param <T>         列表数据类型
     * @return Map<K, List < T>>
     */
    public static <K, T> Map<K, List<T>> groupBy(List<T> list, Function<T, K> keyFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(keyFunction));
    }

    /**
     * stream list to Map
     *
     * @param list        数据列表
     * @param keyFunction a function map T to K
     * @param <K>         group by K 字段
     * @param <T>         列表数据类型
     * @return Map<K, T>
     */
    public static <K, T> Map<K, T> toMap(List<T> list, Function<T, K> keyFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Collector<T, ?, Map<K, T>> mapCollector = getMapCollector(keyFunction);
        return list.stream().collect(mapCollector);
    }

    /**
     * stream list to Map
     *
     * @param list        数据列表
     * @param keyFunction a function map T to K
     * @param supplier    Map的Supplier
     * @param <K>         group by K 字段
     * @param <T>         列表数据类型
     * @param <M>         提供Map的类型
     * @return Map<K, T>
     */
    @SuppressWarnings("unchecked")
    public static <K, T, M extends Map<K, T>> M toMap(List<T> list, Function<T, K> keyFunction, Supplier<M> supplier) {
        if (CollectionUtils.isEmpty(list)) {
            return (M) Collections.emptyMap();
        }
        Collector<T, ?, M> mapCollector = getMapCollector(keyFunction, supplier);
        return list.stream().collect(mapCollector);
    }

    /**
     * stream list to Map
     *
     * @param list             数据列表
     * @param keyFunction      a function map T to K
     * @param keyRepeatHandler 处理重复key的处理方式，缺省方式为(o1, o2) -> o2，覆盖处理
     * @param <K>              group by K 字段
     * @param <T>              列表数据类型
     * @return Map<K, T>
     */
    public static <K, T> Map<K, T> toMap(List<T> list, Function<T, K> keyFunction, BinaryOperator<T> keyRepeatHandler) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Collector<T, ?, Map<K, T>> mapCollector = getMapCollector(keyFunction, keyRepeatHandler, HashMap::new);
        return list.stream().collect(mapCollector);
    }


    private static <K, T> Collector<T, ?, Map<K, T>> getMapCollector(Function<T, K> keyFunction) {
        return getCollector(keyFunction, (o1, o2) -> o2, HashMap::new);
    }

    private static <K, T, M extends Map<K, T>> Collector<T, ?, M> getMapCollector(
            Function<T, K> keyFunction, Supplier<M> supplier) {
        return getCollector(keyFunction, (o1, o2) -> o2, supplier);
    }

    private static <K, T, M extends Map<K, T>> Collector<T, ?, M> getMapCollector(
            Function<T, K> keyFunction, BinaryOperator<T> keyRepeatHandler, Supplier<M> supplier) {
        return getCollector(keyFunction, keyRepeatHandler, supplier);
    }

    private static <K, T, M extends Map<K, T>> Collector<T, ?, M> getCollector(
            Function<T, K> keyFunction, BinaryOperator<T> keyRepeatHandler, Supplier<M> supplier) {
        return Collectors.toMap(keyFunction, Function.identity(), keyRepeatHandler, supplier);
    }


    private FunUtil() {
    }
}
