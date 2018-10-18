package com.karachee.lms.controllers.utils.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IResponseEntityGenerator {
    ResponseEntity<String> getResponseEntity(HttpStatus httpStatus);

    ResponseEntity<String> getTextResponseEntity(String bodyText, HttpStatus httpStatus, HttpHeaders httpHeaders);

    ResponseEntity<String> getJsonResponseEntity(String bodyText, HttpStatus httpStatus, HttpHeaders httpHeaders);

    <T> ResponseEntity<String> formatJsonResponse(Boolean pretty, T jsonObject, HttpStatus httpStatus, HttpHeaders httpHeaders, final Map<String, String> filters) throws JsonProcessingException;
}
