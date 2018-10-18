package com.karachee.lms.repository;

import com.karachee.lms.models.OverviewItem;
import com.karachee.lms.models.ValueCount;
import com.karachee.lms.repository.base.ExtendedCrudRepository;
import com.karachee.lms.models.LogItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends ExtendedCrudRepository<LogItem, Integer> {

    @Query(value= "SELECT DISTINCT logItem.service FROM log.log_item logItem")
    List<String> getDistinctServices();

    @Query(value= "SELECT new com.karachee.lms.models.ValueCount(logItem.level AS value, COUNT(logItem)) " +
            "FROM log.log_item logItem " +
            "GROUP BY logItem.level")
    List<ValueCount> getLogLevelCounts();

    @Query(value= "SELECT new com.karachee.lms.models.OverviewItem(logItem.service, logItem.level, COUNT(logItem)) " +
            "FROM log.log_item logItem " +
            "WHERE (:service IS NULL OR logItem.service = :service) " +
            "GROUP BY logItem.service,  logItem.level")
    List<OverviewItem> getOverview(@Param("service") String service);

}

