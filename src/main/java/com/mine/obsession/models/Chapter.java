package com.mine.obsession.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Chapter implements Serializable {
    String chapter_id,
     works_id,
     chapter_title,
     chapter_content,
     chapter_add_date,
     chapter_remark;
    int chapter_index;
    int page_count;

    public Chapter(Map<String, String> param) {
        this.chapter_id = param.get("chapter_id");
        this.works_id = param.get("works_id");
        this.chapter_title = param.get("chapter_title");
        this.chapter_content = param.get("chapter_content");
        this.chapter_add_date = param.get("chapter_add_date");
        this.chapter_remark = param.get("chapter_remark");
        this.chapter_index = Integer.parseInt(param.get("chapter_index"));
        this.page_count = Integer.parseInt(param.get("page_count"));
    }
}
