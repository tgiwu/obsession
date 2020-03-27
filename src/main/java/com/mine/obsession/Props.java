package com.mine.obsession;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "props")
@Data
@Validated
public class Props {

    String imageDir = "../source";

    String zipDir = "/zipTemp";

    int bufferSize = 1024;
}
