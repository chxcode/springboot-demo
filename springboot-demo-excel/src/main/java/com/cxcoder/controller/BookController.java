package com.cxcoder.controller;

import com.cxcoder.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: ChangXuan
 * @Decription: Book API
 * @Date: 18:37 2021/7/25
 **/
@RestController
public class BookController {

    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/import")
    public void importData(MultipartFile file, HttpServletResponse response) throws IOException {
        bookService.importBooks(file, response);
    }
}
