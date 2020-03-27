package com.mine.obsession.models;

import lombok.Data;

import java.util.List;

@Data
public class Chapter {
    String chapter_id,
     works_id,
     chapter_title,
     chapter_content,
     chapter_add_date,
     chapter_remark;
    List<Page> pages;
    int chapter_index;
    int page_count;
}
