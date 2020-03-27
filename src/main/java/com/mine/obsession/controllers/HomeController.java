package com.mine.obsession.controllers;

import com.mine.obsession.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    private Props mProps;

    public HomeController(Props props) {
        this.mProps = props;
    }

    @GetMapping("/")
    public String home() {
        log.warn(mProps.getImageDir());
        return "home";
    }
}
