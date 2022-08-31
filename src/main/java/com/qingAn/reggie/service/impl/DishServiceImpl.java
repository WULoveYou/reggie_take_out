package com.qingAn.reggie.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingAn.reggie.entity.Dish;
import com.qingAn.reggie.entity.DishDto;
import com.qingAn.reggie.entity.DishFlavor;
import com.qingAn.reggie.entity.Page;
import com.qingAn.reggie.mapper.CategoryMapper;
import com.qingAn.reggie.mapper.DishFlavorMapper;
import com.qingAn.reggie.mapper.DishMapper;
import com.qingAn.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qingAn
 * @date 2022/08/28 20:34
 */
@Service
public class DishServiceImpl implements DishService {

    /**
     * 菜品mapper
     */
    @Autowired
    private DishMapper dishMapper;

    /**
     * 口味Mapper
     */
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 菜品保存方法
     *
     * @param dishDto 新增的菜品数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDto dishDto) {
        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        dishDto.setSort(0);
        dishDto.setStatus(1);
        //调用口味记录列表完善数据，要求获取到添加后菜单主键
        dishMapper.save(dishDto);

        //3. 补全口味的 dish_id 、 创建时间、修改时间、创建人、修改人、
        //获取所有的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历所有口味信息，补全dish_id 、 创建时间、修改时间、创建人、修改人、
      /* 传统用法
      for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            flavor.setCreateUser(dishDto.getCreateUser());
            flavor.setUpdateUser(dishDto.getUpdateUser());
        }*/
        //使用jdk8的新特性，stream, map映射
        List<DishFlavor> dishFlavorList = flavors.stream().map((flavor -> {
            //对每一个元素进行加工
            flavor.setDishId(dishDto.getId());
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            flavor.setCreateUser(dishDto.getCreateUser());
            flavor.setUpdateUser(dishDto.getUpdateUser());
            return flavor;
        })).collect(Collectors.toList());

        //4. 批量保存口味信息
        dishFlavorMapper.saveBatch(dishFlavorList);
    }

    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        PageHelper.startPage(page, pageSize);
        List<DishDto> byPage = dishMapper.findByPage(name);
        PageInfo<DishDto> dishDtoPageInfo = new PageInfo<>(byPage);

        Page<DishDto> dishDtoPage = new Page<>();
        dishDtoPage.setTotal(dishDtoPageInfo.getTotal());
        dishDtoPage.setRecords(dishDtoPageInfo.getList());
        return dishDtoPage;
    }

    /**
     * 作用：根据id查找菜品和菜品的口味
     *
     * @param id 菜品的id
     * @return
     */
    @Override
    public DishDto findById(Long id) {
        Dish byId = dishMapper.findById(id);
        List<DishFlavor> byDishId = dishFlavorMapper.findByDishId(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId, dishDto);
        dishDto.setFlavors(byDishId);
        return dishDto;
    }

    /**
     * 作用：修改菜品
     *
     * @param dishDto 页面传递过来的参数包含菜品与口味
     * @return
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //1. 补全修改时间
        dishDto.setUpdateTime(LocalDateTime.now());
        //2 修改菜品
        dishMapper.updateById(dishDto);

        //3. 删除该菜品的所有口味信息
        dishFlavorMapper.deleteByDishId(dishDto.getId());

        //4. 补全口味信息，然后重新插入
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> dishFlavorList = flavors.stream().map((flavor -> {
            //对每一个元素进行加工
            flavor.setDishId(dishDto.getId());
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            flavor.setCreateUser(dishDto.getCreateUser());
            flavor.setUpdateUser(dishDto.getUpdateUser());
            return flavor;
        })).collect(Collectors.toList());

        //4. 批量保存口味信息
        dishFlavorMapper.saveBatch(dishFlavorList);

    }

    /**
     * 根据id关闭售卖状态
     * @param ids
     */
    @Override
    public void updateStatus0(List ids ,Dish dish) {
        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.updateStatus0(ids,dish);
    }

    /**
     * 根据id开启售卖状态
     * @param ids
     */
    @Override
    public void updateStatus1(List ids ,Dish dish) {
        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.updateStatus1(ids,dish);
    }

    /**
     * 根据批量删除菜品
     * @param ids
     */
    @Override
    public void deleteDish(List ids) {
        dishMapper.deleteDish(ids);
    }

    /**
     * 方法作用： 根据菜品类别的id查找的菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> findByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.findByCategoryId(categoryId);
        return dishList;
    }
}
