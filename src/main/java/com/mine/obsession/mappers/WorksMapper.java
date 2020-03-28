package com.mine.obsession.mappers;

import com.mine.obsession.models.Works;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface WorksMapper {
    List<Works> getAllWorks();

    Works getWorksById(String id);

    int insertWorks(Works works);

    void updateWorks(Works works);

    void deleteWorks(String id);

    List<Works> getWorksByName(@Param("content") String content);
}