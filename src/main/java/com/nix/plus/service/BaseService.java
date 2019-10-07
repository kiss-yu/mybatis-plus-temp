package com.nix.plus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nix.plus.mapper.BaseMapper;
import com.nix.plus.model.BaseEntity;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author by keray
 * date:2019/7/25 16:03
 */
public interface BaseService<T extends BaseEntity> {
    /**
     * 获取基础模块操作mapper
     *
     * @return
     */
    BaseMapper<T> getMapper();

    /**
     * 添加
     *
     * @param entity insert实体
     * @return
     */
    Boolean insert(T entity);

    /**
     * 修改
     *
     * @param entity update实体
     * @return
     */
    Boolean update(T entity);

    /**
     * 删除
     *
     * @param id 实体id
     * @return
     */
    Boolean delete(String id);

    /**
     * 批量逻辑删除
     *
     * @param ids 实体id
     * @return
     */
    Boolean delete(Collection<? extends Serializable> ids);

    /**
     * 通过id查询数据
     *
     * @param id 实体id
     * @return T
     */
    T getById(String id);

    /**
     * 分页查询
     *
     * @param pager 分页参数
     * @return 分页数据
     */
    IPage<T> page(Page<T> pager);


    /**
     * 分页查询
     *
     * @param pager        分页参数
     * @param queryWrapper
     * @return 分页数据
     */
    IPage<T> page(Page<T> pager, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return
     */
    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return
     */
    T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Integer selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     *<p>
     *   <h3>作者 keray</h3>
     *   <h3>时间： 2019/9/16 14:14</h3>
     *   是否存在id
     *</p>
     * @param id
     * @return <p> {@link boolean} </p>
     * @throws
     */
    boolean contains(String id);
}
