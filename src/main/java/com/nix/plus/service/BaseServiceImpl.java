package com.nix.plus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nix.plus.model.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author by keray
 * date:2019/7/25 16:19
 */
@Slf4j
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Override
    public Boolean insert(T entity) {
        return getMapper().insert(entity) == 1;
    }

    @Override
    public Boolean update(T entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("update必须拥有Id");
        }
        return getMapper().updateById(entity) == 1;
    }

    @Override
    public Boolean delete(String id) {
        return getMapper().deleteById(id) == 1;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean delete(Collection<? extends Serializable> ids) {
        if (getMapper().deleteBatchIds(ids) != ids.size()) {
            throw new RuntimeException("批量删除未完全删除异常");
        }
        return true;
    }

    @Override
    public T getById(String id) {
        return getMapper().selectById(id);
    }

    @Override
    public IPage<T> page(Page<T> pager) {
        return getMapper().selectPage(pager, null);
    }

    @Override
    public IPage<T> page(Page<T> pager, Wrapper<T> queryWrapper) {
        return getMapper().selectPage(pager, queryWrapper);
    }

    @Override
    public List<T> selectList(Wrapper<T> queryWrapper) {
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public T selectOne(Wrapper<T> queryWrapper) {
        return getMapper().selectOne(queryWrapper);
    }

    @Override
    public Integer selectCount(Wrapper<T> queryWrapper) {
        return getMapper().selectCount(queryWrapper);
    }

    @Override
    public boolean contains(String id) {
        return getMapper().contains(id);
    }
}
