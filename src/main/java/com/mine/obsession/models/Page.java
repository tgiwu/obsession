package com.mine.obsession.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Page implements Serializable {
    String page_id;
    String page_title;
    String page_add_date;
    String page_remark;
    String works_id;
    String page_content;
    int page_index;
    String chapter_id;

    public Page(Map<String, String> param) {
        this.chapter_id = param.get("chapter_id");
        this.page_index = Integer.parseInt(param.get("page_index"));
        this.page_add_date = param.get("page_add_date");
        this.page_content = param.get("page_content");
        this.page_id = param.get("page_id");
        this.page_remark = param.get("page_remark");
        this.page_title = param.get("page_title");
        this.works_id = param.get("works_id");
    }
}
