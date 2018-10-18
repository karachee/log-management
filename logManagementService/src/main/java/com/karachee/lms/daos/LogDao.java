package com.karachee.lms.daos;

import com.karachee.lms.repository.base.ExtendedCrudRepository;
import com.karachee.lms.daos.base.DaoBase;
import com.karachee.lms.models.LogItem;
import com.karachee.lms.models.OverviewItem;
import com.karachee.lms.models.ValueCount;
import com.karachee.lms.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Component
public class LogDao extends DaoBase<LogItem, LogItem> {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Autowired
    private LogRepository logRepository;

    public LogItem save(LogItem logItem){
        return (logItem!=null) ? logRepository.save(logItem) : null;
    }

    public List<ValueCount> getLogLevelCounts(){
        return this.logRepository.getLogLevelCounts();
    }

    public List<String> getDistinctServices(){
        return this.logRepository.getDistinctServices();
    }

    public List<OverviewItem> getOverview(String service){
        return  this.logRepository.getOverview(service);
    }

    public static LogItem buildFromDto(final LogItem logItemRef) {
        return logItemRef;
    }

    @Override
    public LogItem buildFromDtoWrapper(LogItem internal) {
        return buildFromDto(internal);
    }

    @Override
    public ExtendedCrudRepository getRepository() {
        return this.logRepository;
    }

}
