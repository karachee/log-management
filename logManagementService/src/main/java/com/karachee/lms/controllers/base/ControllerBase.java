package com.karachee.lms.controllers.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.karachee.lms.repository.specifications.GenericSpecification;
import com.karachee.lms.controllers.utils.response.IResponseEntityGenerator;
import javafx.util.Pair;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.*;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public abstract class ControllerBase {

    @Autowired
    private Logger logger;

    @Autowired
    protected IResponseEntityGenerator responseEntityGenerator;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleThrowable(final Throwable ex) {
        logger.error("Unexpected Exception: " + ex.getMessage(), ex);
        return "Unknown error";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        logger.info(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleTypeMismatchException(final TypeMismatchException ex) {
        logger.info(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleIllegalArgumentException(final IllegalArgumentException ex) {
        logger.info(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleJsonProcessingException(final JsonProcessingException ex) {
        logger.error("Encountered JsonProcessingException: " + ex.getMessage(), ex);
        return "Unknown error";
    }

    @ExceptionHandler(MalformedURLException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleMalformedURLException(final MalformedURLException ex) {
        logger.error("Encountered MalformedURLException: " + ex.getMessage(), ex);
        return "Unknown error";
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleIOException(final IOException ex) {
        logger.error("Encountered IOException: " + ex.getMessage(), ex);
        return "Unknown error";
    }

    protected ResponseEntity<String> buildOptionsResponse(Set<HttpMethod> allows) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAllow(allows);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(mediaTypes);

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLength(0);

        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }

    protected <T extends Specification> Specification<T> buildSpecifications(GenericSpecification<T>... specifications) {
        Specification<T> specification = null;
        if (ArrayUtils.isNotEmpty(specifications)) {
            specification = specifications[0];

            for (int i = 1; i < specifications.length; i++) {
                specification = Specifications.where(specification).and(specifications[i]);
            }
        }

        return specification;
    }

    protected Pageable buildPageable(int page, int size, boolean sortAsc, String sortCol) {
        Sort sort = new Sort(new Sort.Order((sortAsc) ? Sort.Direction.ASC : Sort.Direction.DESC, sortCol));
        return new PageRequest(page, size, sort);
    }

    protected Map<String, String> handleFilters(Pair<String, String> ... filters){
        Map<String, String> filterMap = new HashMap<>();
        if(ArrayUtils.isNotEmpty(filters)){
            for(Pair<String, String> filter :  filters){
                if(filter!=null && StringUtils.isNotEmpty(filter.getKey()) && StringUtils.isNotEmpty(filter.getValue())) {
                    filterMap.put(filter.getKey(), filter.getValue());
                }
            }
        }
        return filterMap;
    }
}
