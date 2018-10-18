package com.karachee.lms.controllers.utils.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ResponseEntityGenerator implements IResponseEntityGenerator {

    private static final String JSON_FORMAT = "JSON";

    @Resource(name = "objectMapper")
    private ObjectMapper JSON_MAPPER;

    @Resource(name = "prettyObjectMapper")
    private ObjectMapper JSON_PRETTY_MAPPER;

    @Resource(name = "xmlMapper")
    private ObjectMapper XML_MAPPER;

    @Resource(name = "prettyXmlMapper")
    private ObjectMapper XML_PRETTY_MAPPER;

    @Override
    public ResponseEntity<String> getResponseEntity(final HttpStatus httpStatus) {
        return new ResponseEntity<>("", null, httpStatus);
    }

    @Override
    public ResponseEntity<String> getTextResponseEntity(final String bodyText, final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        return new ResponseEntity<>(bodyText, buildResponseHttpHeaders(httpHeaders, MediaType.TEXT_PLAIN), httpStatus);
    }

    @Override
    public ResponseEntity<String> getJsonResponseEntity(final String ret, final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        return new ResponseEntity<>(ret, buildResponseHttpHeaders(httpHeaders, MediaType.APPLICATION_JSON), httpStatus);
    }

    @Override
    public <T> ResponseEntity<String> formatJsonResponse(Boolean pretty, T jsonObject, HttpStatus httpStatus, HttpHeaders httpHeaders, Map<String, String> filters) throws JsonProcessingException {
        return formatResponse(JSON_FORMAT, pretty, jsonObject, httpStatus, httpHeaders, filters);
    }


    private ResponseEntity<String> formatResponse(final String format, final Boolean pretty, final Object responseObject, final HttpStatus httpStatus, final HttpHeaders httpHeaders, final String filter, final String filterName) throws JsonProcessingException {
        return formatResponse(format, pretty, responseObject, httpStatus, httpHeaders, (StringUtils.isEmpty(filter)) ? null : new HashMap<String, String>() {{
            put(filterName, filter);
        }});
    }

    private ResponseEntity<String> formatResponse(final String format, final Boolean pretty, final Object responseObject, final HttpStatus httpStatus, final HttpHeaders httpHeaders, final Map<String, String> filters) throws JsonProcessingException {
        SimpleFilterProvider sfp = new SimpleFilterProvider();
        sfp.setFailOnUnknownId(false);

        if (MapUtils.isNotEmpty(filters)) {
            filters.entrySet().stream().forEach(filter -> sfp.addFilter(filter.getKey(), SimpleBeanPropertyFilter.filterOutAllExcept(convertFilterStringToSet(filter.getValue()))));
        }

        return JSON_FORMAT.equalsIgnoreCase(format) ?
                getJsonResponseEntity(BooleanUtils.isTrue(pretty) ? JSON_PRETTY_MAPPER.writer(sfp).writeValueAsString(responseObject) : JSON_MAPPER.writer(sfp).writeValueAsString(responseObject), httpStatus, httpHeaders) :
                getTextResponseEntity(BooleanUtils.isTrue(pretty) ? XML_PRETTY_MAPPER.writer(sfp).writeValueAsString(responseObject) : XML_MAPPER.writer(sfp).writeValueAsString(responseObject), httpStatus, httpHeaders);

    }

    private static Set<String> convertFilterStringToSet(final String filter) {
        return new HashSet<>(Arrays.asList(StringUtils.split(filter, ",")));
    }

    private HttpHeaders buildResponseHttpHeaders(HttpHeaders httpHeaders, MediaType mediaType) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (httpHeaders != null) {
            responseHeaders.putAll(httpHeaders);
        }
        responseHeaders.setContentType(mediaType);

        return responseHeaders;
    }
}
