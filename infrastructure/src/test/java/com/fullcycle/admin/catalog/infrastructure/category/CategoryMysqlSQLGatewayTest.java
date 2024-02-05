package com.fullcycle.admin.catalog.infrastructure.category;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;

@MySQLGatewayTest
public class CategoryMysqlSQLGatewayTest {
    @Autowired
    private CategoryMysqlGateway categoryMysqlGateway;
    @Autowired
    private CategoryRepository categoryRepository;

    
}
