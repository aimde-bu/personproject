package com.ssm;

import com.ssm.domain.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectException {
    //处理所有异常
    @ExceptionHandler(Exception.class)
    public Result doException(Exception e){
        e.printStackTrace();
        return new Result(500,"服务器故障，请稍后再试");
    }
}
