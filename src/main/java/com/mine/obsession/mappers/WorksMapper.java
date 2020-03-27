package com.mine.obsession.mappers;

import com.mine.obsession.models.Works;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
interface WorksMapper {
    Collection<Works> getAllWorks();

    Works getWorksById(String id);

     void insertWorks(Works works);

     void updateWorks(Works works);

     void deleteWorks(String id);
}