package com.datn.doffice.exceptions;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 19497169550131057L;

    public BaseException(){
        super();
    }

    public BaseException(String msg, Exception e){
        super(msg, e);
    }
}
