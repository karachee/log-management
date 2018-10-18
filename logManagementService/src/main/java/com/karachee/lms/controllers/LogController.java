package com.karachee.lms.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.karachee.lms.repository.specifications.GenericSpecification;
import com.karachee.lms.controllers.base.ControllerBase;
import com.karachee.lms.daos.LogDao;
import com.karachee.lms.models.LogItem;
import com.karachee.lms.models.Plural;
import com.karachee.lms.models.SearchCriteria;
import io.swagger.annotations.*;
import javafx.util.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/log")
@Api(value="IoTransaction Controller", description="Rest interface to perform crud operations on the Log resource.")
public class LogController extends ControllerBase {
    public static final String FILTER_NAME = "LogItem";

    @Autowired
    private Logger logger;

    @Autowired
    private LogDao logDao;

    @RequestMapping(value={"", "/{id}"}, method=RequestMethod.OPTIONS)
    @ApiOperation(value = "Get IoTransaction Controller Options", notes = "Returns available controller options.", response = Void.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 200, message = "Ok", response = Void.class)
    })
    public ResponseEntity<String> getOptions() {
        return buildOptionsResponse(new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
            add(HttpMethod.OPTIONS);
        }});
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "Get", notes = "Get", response = Plural.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class),
            @ApiResponse(code = 200, message = "Ok", response = Plural.class)
    })
    public ResponseEntity<String> get(
            @ApiParam(value = "Service", allowMultiple = false) @RequestParam(value = "service", required = false) String service,
            @ApiParam(value = "Hostname", allowMultiple = false) @RequestParam(value = "hostname", required = false) String hostname,
            @ApiParam(value = "Level", allowMultiple = false) @RequestParam(value = "level", required = false) String level,
            @ApiParam(value = "DateTime Start (MM/dd/yyyy)", allowMultiple = false) @RequestParam(value = "datetimestart", required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") Date dateTimeStart,
            @ApiParam(value = "DateTime End (MM/dd/yyyy)", allowMultiple = false) @RequestParam(value = "datetimeend", required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") Date dateTimeEnd,
            @ApiParam(value = "Package and Class (LIKE)", allowMultiple = false) @RequestParam(value = "packageandclass", required = false) String packageAndClass,
            @ApiParam(value = "Message (LIKE)", allowMultiple = false) @RequestParam(value = "message", required = false) String message,

            @ApiParam(value = "Comma delimited list of log item columns to return. If null, all columns are returned.", allowMultiple = false) @RequestParam(value = "logitemfilter", required = false) String filter,
            @ApiParam(value = "Perform and return count query", allowMultiple = false) @RequestParam(value = "countquery", defaultValue = "false", required = false) Boolean countQuery,

            @ApiParam(value = "Paging response offset - zero based", allowMultiple = false) @RequestParam(value = "offset", defaultValue = "0", required = false) Integer offset,
            @ApiParam(value = "Paging max page size", allowMultiple = false) @RequestParam(value = "limit", defaultValue = "25", required = false) Integer limit,
            @ApiParam(value = "Sorting column - only one column supported", allowMultiple = false) @RequestParam(value = "sortcol", defaultValue = "id", required = false) String sortCol,
            @ApiParam(value = "Sort in ascending order", allowMultiple = false) @RequestParam(value = "sortasc", defaultValue = "true", required = false) Boolean sortAsc
    ) {
        ResponseEntity response = responseEntityGenerator.getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            response = responseEntityGenerator.formatJsonResponse(true, logDao.get(buildSpecifications(
                    new GenericSpecification(new SearchCriteria("service", "=", service)),
                    new GenericSpecification(new SearchCriteria("hostname", "=", hostname)),
                    new GenericSpecification(new SearchCriteria("level", "=", level)),
                    new GenericSpecification(new SearchCriteria("dateTime", ">=", dateTimeStart)),
                    new GenericSpecification(new SearchCriteria("dateTime", "<=", dateTimeEnd)),
                    new GenericSpecification(new SearchCriteria("packageAndClass", "LIKE", packageAndClass)),
                    new GenericSpecification(new SearchCriteria("message", "LIKE", message))
                    ), countQuery, buildPageable(offset, limit, sortAsc, sortCol)
            ), HttpStatus.OK, null, handleFilters(new Pair<>(FILTER_NAME, filter)));
        } catch (JsonProcessingException e) {
            logger.error("Error Parsing JSON, ", e);
        }

        return response;
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    @ApiOperation(value = "Log Level Count", notes = "Log Level Count", response = Plural.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class),
            @ApiResponse(code = 200, message = "Ok", response = List.class)
    })
    public ResponseEntity<String> getServices() {
        ResponseEntity response = responseEntityGenerator.getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            response = responseEntityGenerator.formatJsonResponse(true, logDao.getDistinctServices(), HttpStatus.OK, null, handleFilters(new Pair<>(FILTER_NAME, null)));
        } catch (JsonProcessingException e) {
            logger.error("Error Parsing JSON, ", e);
        }

        return response;
    }

    @RequestMapping(value = "/loglevelcount", method = RequestMethod.GET)
    @ApiOperation(value = "Log Level Count", notes = "Log Level Count", response = Plural.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class),
            @ApiResponse(code = 200, message = "Ok", response = List.class)
    })
    public ResponseEntity<String> getLogLevelCount() {
        ResponseEntity response = responseEntityGenerator.getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            response = responseEntityGenerator.formatJsonResponse(true, logDao.getLogLevelCounts(), HttpStatus.OK, null, handleFilters(new Pair<>(FILTER_NAME, null)));
        } catch (JsonProcessingException e) {
            logger.error("Error Parsing JSON, ", e);
        }

        return response;
    }

    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @ApiOperation(value = "Overview", notes = "Overview", response = Plural.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class),
            @ApiResponse(code = 200, message = "Ok", response = Plural.class)
    })
    public ResponseEntity<String> getOverview(
            @ApiParam(value = "Service", allowMultiple = false) @RequestParam(value = "service", required = false) String service
    ) {
        ResponseEntity response = responseEntityGenerator.getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            response = responseEntityGenerator.formatJsonResponse(true, logDao.getOverview(service), HttpStatus.OK, null, handleFilters(new Pair<>(FILTER_NAME, null)));
        } catch (JsonProcessingException e) {
            logger.error("Error Parsing JSON, ", e);
        }

        return response;
    }

    @RequestMapping(method=RequestMethod.POST)
    @ApiOperation(value = "Create new log item", notes = "Attempt to add a reservation if space is available.", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "Internal Server Error - Return body contains error", response = String.class),
            @ApiResponse(code = 409, message = "CONFLICT", response = Void.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = String.class),
            @ApiResponse(code = 201, message = "CREATED", response = String.class)
    })
    public ResponseEntity<String> createLogItem(@RequestBody LogItem logItem)
    {
        ResponseEntity<String> response = responseEntityGenerator.getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            LogItem savedLogItem = logDao.save(logItem);
            HttpStatus httpStatus = (savedLogItem!=null) ? HttpStatus.CREATED : HttpStatus.NOT_ACCEPTABLE;
            response = responseEntityGenerator.formatJsonResponse(true, httpStatus , httpStatus, null,null);
        } catch (JsonProcessingException e) {
            logger.error("Error Parsing JSON, ", e);
        }

        return response;
    }
}
