package com.mine.obsession.models;

import lombok.Data;

@Data
public class Works {
    String works_id,
            works_title,
            works_content,
            create_date,
            update_date,
            works_path;
    int chapter_count;
    //        chapters: List<Chapter> = ArrayList(),


}
