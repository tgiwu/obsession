package com.mine.obsession.mappers;

import com.mine.obsession.models.Chapter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChapterMapper {
    List<Chapter> getAllChapters();

    Chapter getChapterById(String id);

    int insertChapter(Chapter chapter);

    void updateChapter(Chapter chapter);

    void deleteChapter(String id);

    List<Chapter> getAllChaptersByWorksId(String works_id);

}
