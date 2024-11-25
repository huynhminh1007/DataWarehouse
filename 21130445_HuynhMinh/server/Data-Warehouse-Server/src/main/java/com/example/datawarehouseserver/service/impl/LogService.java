package com.example.datawarehouseserver.service.impl;

import com.example.datawarehouseserver.entity.Log;
import com.example.datawarehouseserver.exception.CodeException;
import com.example.datawarehouseserver.repository.LogRepository;
import com.example.datawarehouseserver.service.ILogService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogService implements ILogService {

    LogRepository logRepository;

    @Transactional
    public Log updateStatusById(Integer id, String status) {
        logRepository.updateStatusById(id, status);

        return logRepository.findById(id)
                .orElseThrow(CodeException.LOG_NOT_FOUND::throwException);
    }

    public Log findById(Integer id) {
        return logRepository.findById(id)
                .orElseThrow(CodeException.LOG_NOT_FOUND::throwException);
    }

    public Log save(Log log) {
        return logRepository.save(log);
    }

    public Long count() {
        return logRepository.count();
    }
}
