package com.fullcycle.admin.catalog.infrastructure.genre;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infrastructure.category.CategoryMysqlGateway;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class GatewayMySQLGatewayTest {
    @Autowired
    private CategoryMysqlGateway categoryMysqlGateway;
    @Autowired
    private GenreMySQLGateway genreMySQLGateway;
    @Autowired
    private GenreRepository genreRepository;

}
