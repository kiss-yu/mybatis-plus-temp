package com.nix.plus.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.baomidou.mybatisplus.extension.injector.AbstractLogicMethod;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectCount;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectObjs;
import com.baomidou.mybatisplus.extension.injector.methods.LogicUpdate;
import com.baomidou.mybatisplus.extension.injector.methods.LogicUpdateById;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.nix.plus.model.BaseEntity;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author by keray
 * date:2019/7/25 16:33
 */
@Configuration
@Aspect
public class MybatisPlusSqlInjector {

    @Resource
    private MybatisPlusProperties mybatisPlusProperties;

    @PostConstruct
    public void init() {
        mybatisPlusProperties.getConfiguration().getTypeHandlerRegistry().register(LocalDateTime.class, LocalDateTimeTypeHandler.class);
        mybatisPlusProperties.getConfiguration().getTypeHandlerRegistry().register(LocalDate.class, LocalDateTypeHandler.class);
        mybatisPlusProperties.getConfiguration().getTypeHandlerRegistry().register(LocalTime.class, LocalTimeTypeHandler.class);
    }

    @Bean
    public ISqlInjector sqlInjector() {
        return new AbstractSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList() {
                return Stream.of(
                        new LogicUpdate(),
                        new LogicUpdateById(),
                        new ILogicSelectById(),
                        new ILogicSelectBatchByIds(),
                        new ILogicSelectByMap(),
                        new ILogicSelectOne(),
                        new ILogicSelectMaps(),
                        new ILogicSelectMapsPage(),
                        new ILogicSelectList(),
                        new ILogicSelectPage(),
                        new Insert(),
                        new LogicSelectCount(),
                        new LogicSelectObjs()
                ).collect(toList());
            }
        };
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Pointcut("execution(* com.nix.plus.mapper.BaseMapper.updateById(..))")
    public void updateById() {
    }

    @Pointcut("execution(* com.nix.plus.mapper.BaseMapper.update(..))")
    public void update() {
    }

    @Pointcut("execution(* com.nix.plus.mapper.BaseMapper.insert(..))")
    public void insert() {
    }

    @Before("updateById()")
    public void beforeUpdateById(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseEntity model = (BaseEntity) args[0];
        model.setModifyTime(LocalDateTime.now());
        if (StrUtil.isBlank(model.getUpdateBy())) {
//            model.setUpdateBy(userContext.currentUserId());
        }
    }

    @Before("update()")
    public void beforeUpdate(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseEntity model = (BaseEntity) args[0];
        Update update = (Update) args[1];
        if (model != null) {
            model.setModifyTime(LocalDateTime.now());
            if (StrUtil.isBlank(model.getUpdateBy())) {
//                model.setUpdateBy(userContext.currentUserId());
            }
            model.setCreateBy(null);
            model.setCreateTime(null);
        }
        if (update != null && model == null) {
            if (update instanceof LambdaUpdateWrapper) {
                LambdaUpdateWrapper<BaseEntity> updateWrapper = (LambdaUpdateWrapper) update;
                updateWrapper.set(BaseEntity::getModifyTime, LocalDateTime.now());
//                        .set(BaseEntity::getUpdateBy, userContext.currentUserId());
            } else if (update instanceof UpdateWrapper) {
                ((UpdateWrapper<BaseEntity>) update).lambda()
                        .set(BaseEntity::getModifyTime, LocalDateTime.now());
//                        .set(BaseEntity::getUpdateBy, userContext.currentUserId());
            }
        }
    }

    @Before("insert()")
    public void insert(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseEntity model = (BaseEntity) args[0];
        model.setModifyTime(LocalDateTime.now());
        model.setCreateTime(LocalDateTime.now());
        if (StrUtil.isBlank(model.getUpdateBy())) {
//            model.setUpdateBy(userContext.currentUserId());
        }
        if (StrUtil.isBlank(model.getCreateBy())) {
//            model.setCreateBy(userContext.currentUserId());
        }
    }

    private static final class ILogicSelectOne extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
                    this.sqlSelectColumns(tableInfo, true), tableInfo.getTableName(),
                    this.sqlWhereEntityWrapper(true, tableInfo)), modelClass);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectById extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.LOGIC_SELECT_BY_ID;
            SqlSource sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(),
                    sqlSelectColumns(tableInfo, false),
                    tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                    tableInfo.getLogicDeleteSql(true, false)), Object.class);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectBatchByIds extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.LOGIC_SELECT_BATCH_BY_IDS;
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
                    sqlSelectColumns(tableInfo, false), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                    SqlScriptUtils.convertForeach("#{item}", COLLECTION, null, "item", COMMA),
                    tableInfo.getLogicDeleteSql(true, false)), Object.class);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectByMap extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
            String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, false),
                    tableInfo.getTableName(), sqlWhereByMap(tableInfo));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectList extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_LIST;
            String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, true),
                    tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectPage extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_PAGE;
            String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, true),
                    tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
        }
    }

    private static final class ILogicSelectMaps extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_MAPS;
            String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, true),
                    tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Map.class, tableInfo);
        }
    }

    private static final class ILogicSelectMapsPage extends BaseAbstractLogicMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            SqlMethod sqlMethod = SqlMethod.SELECT_MAPS_PAGE;
            String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, true),
                    tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Map.class, tableInfo);
        }
    }
}

abstract class BaseAbstractLogicMethod extends AbstractLogicMethod {

    /**
     * SQL 查询所有表字段
     *
     * @param table        表信息
     * @param queryWrapper 是否为使用 queryWrapper 查询
     * @return sql 脚本
     */
    @Override
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        /* 假设存在 resultMap 映射返回 */
        String selectColumns = ASTERISK;
        if (!queryWrapper) {
            return selectColumns;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), selectColumns);
    }
}
