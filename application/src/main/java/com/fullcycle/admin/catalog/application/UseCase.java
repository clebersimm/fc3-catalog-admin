package com.fullcycle.admin.catalog.application;

import com.fullcycle.admin.catalog.domain.category.Category;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public abstract class UseCase<IN, OUT> {
    public abstract OUT execute(IN aIn);
}