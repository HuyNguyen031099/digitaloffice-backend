package com.datn.doffice.exceptions;

public class TokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 7608157964961848795L;

    public TokenExpiredException() {
        super("TokenExpiredException");
    }
}
