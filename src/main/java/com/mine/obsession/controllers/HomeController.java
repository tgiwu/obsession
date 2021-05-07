package com.mine.obsession.controllers;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.mine.obsession.Props;
import com.mine.obsession.mappers.WorksMapper;
import com.mine.obsession.models.Works;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private Props mProps;

    @Autowired
    WorksMapper worksMapper;

    @NacosValue(value = "${imgDir:images_contents}", autoRefreshed = true)
    private String imgDir;

    @NacosInjected
    private NamingService namingService;

    @GetMapping("/")
    public String home() {
        log.warn(mProps.getImageDir());
        return "home";
    }

    @PostMapping(path = "/imgPath")
    public ResponseEntity<String> showImgPath() {
        return ResponseEntity.ok(imgDir);
    }

    @RequestMapping(value = "/instances", method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getNacosInstances(@RequestBody Map<String, String> params) {
        try {
            return ResponseEntity.ok(namingService.getAllInstances(params.get("serviceName")));
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }


    @RequestMapping(value = "/insertW", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> insertW(@RequestParam Map<String, String> param) {

        for (String key : param.keySet()) {
            log.info(key + " = " + param.get(key));
        }

        Works works;
        try {
            works = new Works(param);
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of(e.getMessage()));
        }

        log.info("insert result " + worksMapper.insertWorks(works));
        log.info("insert after " + works);

        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/queryW", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Works>> queryW(@RequestParam Map<String, String> param, HttpServletRequest request) {
        String content = param.get("title");
        log.info("search content = " + content);

        List<Works> worksList = worksMapper.getWorksByName("%" + content + "%");
        log.info("search result = " + worksList);
        if (null == worksList) worksList = new ArrayList<>();

        return ResponseEntity.ok(worksList);
    }

    @RequestMapping(value = "/queryAll", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<Works>> queryAll() {
        List<Works> worksList = worksMapper.getAllWorks();
        log.info("result = " + worksList);

        return ResponseEntity.ok(worksList);
    }
}
