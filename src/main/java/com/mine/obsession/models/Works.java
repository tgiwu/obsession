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
public class Works implements Serializable {
    String works_id,
            works_title,
            works_content,
            create_date,
            update_date,
            works_path;
    int chapter_count;
    //        chapters: List<Chapter> = ArrayList(),


    public Works(Map<String, String> param) {
        this.works_title = param.get("works_title");
        this.works_path = param.get("works_path");
        this.works_content = param.get("works_content");
        String count = param.get("chapter_count");
        if (null != count)
        this.chapter_count = Integer.parseInt(count);
    }
}
