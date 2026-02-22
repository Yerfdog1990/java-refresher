package com.baeldung.rwsb.web.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.TransientObjectException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionsHandler {

    /*
    // 1. Creating the Global Exception Handler
    @ExceptionHandler({ EntityNotFoundException.class })
    public ModelAndView resolveException(JpaObjectRetrievalFailureException ex,
                                         ServletRequest request, HttpServletResponse response) {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Associated entity not found: "
                + ex.getMessage());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/error");
        return mav;
    }
*/

    // 2. Handling duplicate data
    @ExceptionHandler({ DataIntegrityViolationException.class })
    public String resolveDuplicatedKey(ServletRequest request) {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Duplicated key");
        return "/error";
    }

/*
    // 3. Handling Different Errors in the Same Method Handler
    @ExceptionHandler({ EntityNotFoundException.class, TransientObjectException.class })
    public ModelAndView resolveEntityNotFoundException(Exception ex, ServletRequest request,
                                                       HttpServletResponse response) {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Invalid associated entity: "
                + ex.getMessage());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/error");
        return mav;
    }
*/

    /*
        // 4. Retrieving a Custom Error Object by explicitly adding @ResponseBody
        @ResponseBody
        @ExceptionHandler({ EntityNotFoundException.class, TransientObjectException.class })
        public CustomErrorBody resolveEntityNotFoundException(
                Exception ex,
                ServletRequest request,
                HttpServletResponse response) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new CustomErrorBody("Invalid associated entity: " + ex.getMessage(), "INVALID_CAMPAIGN_ID");
        }
    */

    // 5. Retrieving a Custom Error Object without explicitly adding @ResponseBody
    @ExceptionHandler({ EntityNotFoundException.class, TransientObjectException.class })
    public ResponseEntity<CustomErrorBody> resolveEntityNotFoundException(Exception ex) {
        return ResponseEntity.badRequest()
                .header("Custom-Header", "Value")
                .body(new CustomErrorBody("Invalid associated entity: " + ex.getMessage(), "INVALID_CAMPAIGN_ID"));
    }

}
