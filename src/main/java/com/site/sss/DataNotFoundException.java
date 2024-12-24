package com.site.sss;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//runtionexception -> 언체크 예외
//컴파일러가 체크하지 않기 때문에 try catch 작성 할 필요 x
@ResponseStatus(value = HttpStatus.NOT_FOUND,reason ="en")
public class DataNotFoundException extends RuntimeException {
    private static final long serialVersionUID=1L;
    public DataNotFoundException(String message) {
        super(message);
    }
}
