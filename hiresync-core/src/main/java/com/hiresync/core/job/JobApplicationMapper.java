package com.hiresync.core.job;

import com.hiresync.core.entity.JobApplication;
import com.hiresync.core.job.dto.JobApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface JobApplicationMapper {
    JobApplicationDTO toDTO(JobApplication entity);
    List<JobApplicationDTO> toDTOList(List<JobApplication> entities);
}
