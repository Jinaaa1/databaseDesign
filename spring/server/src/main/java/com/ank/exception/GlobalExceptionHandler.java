package com.ank.exception;

import com.ank.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     *日期： 8.1
     *
     *如果抛出的的是BaseException或者其子类错误，则调用该方法
     *
     * @param exception 业务异常
     * @return Result
     *
     *
     * 在解决登录问题时，发现 impl 层的抛出的异常不能传回到 Controller 层，此类用于解决该问题
     * */
    //下处可以直接写 "BaseException.class" Java 异常处理机制会匹配继承链上的所有父类异常
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public Result baseException(BaseException exception) {return new Result(exception.getMessage());}
}
