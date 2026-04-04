package com.UmirHack2026.diploma_service.mapper;

import com.UmirHack2026.diploma_service.dto.DiplomaPublicDto;
import com.UmirHack2026.diploma_service.dto.DiplomaRecordDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import org.apache.commons.csv.CSVRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DiplomaMapper {
    DiplomaRecordDto INSTANCE = Mappers.getMapper(DiplomaRecordDto.class);

//    @Mapping(target = "fio", expression = "java(record.get(\"ФИО\"))")
    @Mapping(target = "graduationYear", expression = "java(Integer.parseInt(record.get(\"Год выпуска\")))")
    @Mapping(target = "specialty", expression = "java(record.get(\"Специальность\"))")
    @Mapping(target = "diplomaNumber", expression = "java(record.get(\"Номер диплома\"))")
    @Mapping(target = "studentEmail", expression = "java(record.isMapped(\"Email\") ? record.get(\"Email\") : null)")
    DiplomaRecordDto toDto(CSVRecord record);



    DiplomaPublicDto toPublicDto(Diploma diploma);
}
