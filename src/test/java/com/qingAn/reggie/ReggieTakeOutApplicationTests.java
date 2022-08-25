package com.qingAn.reggie;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingAn.reggie.entity.Employee;
import com.qingAn.reggie.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class ReggieTakeOutApplicationTests {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    void contextLoads() {
        //1. 设置当前页与页面大小 注意这两行代码一定要紧挨着
        PageHelper.startPage(1,2);
        //2. 查找数据
        List<Employee> employeeList  = employeeMapper.findByName("卿");

        //3. 得到分页对象
        PageInfo<Employee> pageInfo = new PageInfo<>(employeeList);
        System.out.println("总记录数："+ pageInfo.getTotal());
        System.out.println("总页数："+ pageInfo.getPages());
        System.out.println("当前页："+ pageInfo.getPageNum());
        System.out.println("页面大小："+ pageInfo.getPageSize());
    }

}
