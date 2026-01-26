package turip.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import turip.common.exception.custom.ForbiddenException;

@Slf4j
@ControllerAdvice(basePackages = "turip.admin.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AdminExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbiddenException(ForbiddenException e) {
        log.warn("어드민 기능에 USER 접근 시도: {}", e.getMessage());
        return new ModelAndView("error/403");
    }
}
