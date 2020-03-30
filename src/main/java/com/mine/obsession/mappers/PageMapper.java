package com.mine.obsession.mappers;

import com.mine.obsession.models.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PageMapper {
    List<Page> getAllPages();

    Page getPageById(String id);

    int insertPage(Page page);

    void updatePage(Page page);

    void deletePage(String id);

    List<Page> getAllPagesByChapterId(String chapter_id);

}
