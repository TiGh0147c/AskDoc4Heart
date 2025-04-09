package com.example.demo.Message.DTO;

import lombok.Data;

@Data
public class PageQuery {
    private int page = 1;
    private int size = 10;

    public PageQuery(int page, int size) {
        this.page = page;
        this.size = size;
    }
}