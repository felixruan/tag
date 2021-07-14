package org.etocrm.tagManager.config;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

/**
 * @Author chengrong.yang
 * @date 2020/9/10 13:00
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandle {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseVO validExceptionHandler(BindException e) {
        return getResponseVO(e.getBindingResult());

    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseVO exceptionHandler(MethodArgumentNotValidException e) {
        return getResponseVO(e.getBindingResult());
    }

    private ResponseVO getResponseVO(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        String errorMsg = ResponseEnum.INCORRECT_PARAMS.getMessage();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Locale currentLocale = LocaleContextHolder.getLocale();
        int index = 0;
        for (FieldError fieldError : fieldErrors) {
            String errorMessage = messageSource.getMessage(fieldError, currentLocale);
            msg.append(fieldError.getField() + "：" + errorMessage + ",");

            if (index == 0) {
                errorMsg = errorMessage;
            }
        }
        String res = new String(msg);
        res = res.substring(0, res.length() - 1);
//        return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS, res);
        return new ResponseVO(ResponseEnum.INCORRECT_PARAMS.getCode(), errorMsg, res);

    }
}
