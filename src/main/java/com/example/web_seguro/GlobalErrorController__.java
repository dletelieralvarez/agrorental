package com.example.web_seguro;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

public class GlobalErrorController__ implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int code = Integer.parseInt(status.toString());
            switch (code) {
                case 400: return "error/400";
                case 401: return "error/401";
                case 403: return "error/403";
                case 404: return "error/404";
                case 405: return "error/405";
                case 408: return "error/408";
                case 500: return "error/500";
                case 503: return "error/503";
                default:  return "error/generic";
            }
        }
        return "error/generic";
    }
}
