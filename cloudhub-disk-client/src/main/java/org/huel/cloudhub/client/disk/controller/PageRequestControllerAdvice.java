package org.huel.cloudhub.client.disk.controller;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.web.data.page.PageRequest;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
@ControllerAdvice
public class PageRequestControllerAdvice {

    @ModelAttribute
    public PageRequest fromRequest(HttpServletRequest request) {
        String page = request.getParameter("page");
        String size = request.getParameter("size");

        try {
            int pageInt = Strings.isNullOrEmpty(page) ? 1 : Integer.parseInt(page);
            int sizeInt = Strings.isNullOrEmpty(size) ? 10 : Integer.parseInt(size);
            return new PageRequest(pageInt, sizeInt);
        } catch (NumberFormatException e) {
            throw new ParameterFailedException(e.getMessage());
        }
    }

    @ModelAttribute
    public Pageable pageableFromRequest(HttpServletRequest request) {
        return fromRequest(request);
    }
}
