package com.nix.plus.mapper;

import cn.hutool.core.util.TypeUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nix.plus.model.BaseEntity;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by keray
 * date:2019/7/25 16:56
 */
public interface BaseMapper<T extends BaseEntity> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    Map<Class<?>, BaseEntity> DELETE_CACHE = new ConcurrentHashMap<>(128);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     * @return
     */
    @Override
    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return
     */
    @Override
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     * @return
     */
    @Override
    int insert(T entity);

    @Override
    T selectById(Serializable id);

    default boolean contains(String id) {
        BaseEntity entityV3 = this.getDeleteEntity();
        entityV3.setUpdateBy(null);
        entityV3.setModifyTime(null);
        entityV3.setDeleteTime(null);
        entityV3.setDeleted(null);
        return selectCount(
                Wrappers.lambdaQuery((T) entityV3)
                        .eq(BaseEntity::getId, id)
        ) == 1;
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param id 主键ID列表(不能为 null 以及 empty)
     */
    @Override
    default int deleteById(Serializable id) {
        BaseEntity entityV3 = this.getDeleteEntity();
        return this.update((T) entityV3,
                Wrappers.lambdaUpdate((T) null)
                        .set(BaseEntity::getDeleted, true)
                        .eq(BaseEntity::getId, id)
        );
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    @Override
    default int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList) {
        BaseEntity entityV3 = this.getDeleteEntity();
        entityV3.setId(null);
        return this.update((T) entityV3,
                Wrappers.lambdaUpdate((T) null)
                        .set(BaseEntity::getDeleted, true)
                        .in(BaseEntity::getId, idList)
        );
    }


    @Override
    default int delete(Wrapper<T> wrapper) {
        if (wrapper instanceof LambdaUpdateWrapper) {
            ((LambdaUpdateWrapper<T>) wrapper).set(BaseEntity::getDeleted, true);
        } else if (wrapper instanceof UpdateWrapper) {
            ((UpdateWrapper<T>) wrapper).lambda().set(BaseEntity::getDeleted, true);
        } else {
            throw new IllegalArgumentException("delete只允许传入UpdateWrapper类型 now= " + wrapper.getClass());
        }
        BaseEntity entityV3 = this.getDeleteEntity();
        return this.update((T) entityV3, wrapper);
    }

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    @Override
    default int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap) {
        throw new RuntimeException("不支持");
    }

    /**
     * <p>
     * <h3>作者 keray</h3>
     * <h3>时间： 2019/9/4 21:20</h3>
     * 获取mapper下删除对象
     * </p>
     *
     * @param
     * @return <p> {@link BaseEntity} </p>
     * @throws
     */
    default BaseEntity getDeleteEntity() {
        BaseEntity entityV3 = DELETE_CACHE.get(this.getClass());
        if (entityV3 == null) {
            Class<T> modelClazz = (Class<T>) TypeUtil.getTypeArgument(this.getClass().getInterfaces()[0].getGenericInterfaces()[0]);
            try {
                entityV3 = modelClazz.newInstance();
                entityV3.setDeleted(true);
                BaseEntity v = DELETE_CACHE.putIfAbsent(this.getClass(), entityV3);
                if (v != null) {
                    entityV3 = v;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (entityV3 == null) {
            throw new NullPointerException();
        }
        LocalDateTime now = LocalDateTime.now();
        entityV3.setDeleteTime(now);
        entityV3.setModifyTime(now);
        return entityV3;
    }

}
