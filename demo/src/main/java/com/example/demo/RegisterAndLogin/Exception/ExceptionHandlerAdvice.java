package com.example.demo.RegisterAndLogin.Exception;

import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionHandlerAdvice {
/*
    @ExceptionHandler({Exception.class})
    public ResponseMessage exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        return new ResponseMessage(500,"error",null);
    }
*/
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseMessage exceptionHandler(ServiceException e, HttpServletRequest request, HttpServletResponse response) {
        return new ResponseMessage(500,e.getMessage(),null);
    }
}
