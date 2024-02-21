package com.fullcycle.admin.catalog.infrastructure.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fullcycle.admin.catalog.ControllerTest;

@ControllerTest(controllers = CategoryApi.class)
public class CategoryAPITest {
    @Autowired
    private MockMvc mvc;

    
}
