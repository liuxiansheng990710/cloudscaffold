package com.example.checke.support;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.example.checke.bean.EntityDataSource;
import com.example.commons.core.mapstruct.CommonsStructConvert;

@Mapper(uses = {CommonsStructConvert.class}, typeConversionPolicy = ReportingPolicy.IGNORE)
public interface AbsentEntitiesStructConvert {

    AbsentEntitiesStructConvert INSTANCE = Mappers.getMapper(AbsentEntitiesStructConvert.class);

    @Mapping(source = "dbColumnNames", target = "columnNames")
    EntityDataSource afterComparisonConvertToAbsentEntities(List<String> dbColumnNames, EntityDataSource entityDataSource);

}
