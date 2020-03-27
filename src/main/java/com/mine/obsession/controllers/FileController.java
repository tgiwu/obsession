package com.mine.obsession.controllers;

import com.mine.obsession.Props;
import com.mine.obsession.Utils;
import com.mine.obsession.services.ZipHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping(path = "/upload")
public class FileController {

    private final Props props;

    private final ZipHandleService zipHandleService;

    public FileController(Props props, ZipHandleService zipHandleService) {
        this.props = props;
        this.zipHandleService = zipHandleService;
    }

    @RequestMapping(value = "/multi", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadMulti(HttpServletRequest request) {

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("files");

        log.info("files size " + files.size());
        String works = request.getParameter("works");

        if (null == works || works.isEmpty()) return ResponseEntity.badRequest().body("works is empty");

        String chapter = request.getParameter("chapter");
        if (null == chapter || chapter.isEmpty()) chapter = "unknown";

        File folder = new File(props.getImageDir() + File.separator + works, chapter);
        Utils.createDirIfNeed(folder);

        BufferedOutputStream stream;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    File dest = new File(folder, file.getOriginalFilename());
                    if (dest.isFile() && dest.exists()) dest.delete();
                    log.info("dest file " + dest.getAbsolutePath());

                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(dest));
                    stream.write(bytes);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    stream = null;
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("file is empty");
            }
        }
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/single", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                   @RequestParam String works,
                                                   @RequestParam(defaultValue = "unknown") String chapter) {
        try {
            if (null == file || file.isEmpty()) return ResponseEntity.badRequest().body("works is empty");
            File folder = new File(props.getImageDir() + File.separator + works, chapter);
            log.info("folder path " + folder.getAbsolutePath());
            Utils.createDirIfNeed(folder);
            File dest = new File(folder, file.getOriginalFilename());
            if (dest.isFile() && dest.exists()) dest.delete();
            log.info("dest file " + dest.getAbsolutePath());
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(dest));
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/zip", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> handleZipUpload(@RequestParam("zip") MultipartFile zipFile, @RequestParam String works) {
        try {
            if (null == zipFile || zipFile.isEmpty()) return ResponseEntity.badRequest().body("works is empty");
            if (null == works || works.isEmpty()) works = String.valueOf(System.currentTimeMillis());

            log.info("catch zip Original File " + zipFile.getOriginalFilename());

            File zipTempFolder = new File(props.getImageDir() + File.separator + "zipTemp", works);
            if (!(zipTempFolder.exists() && zipTempFolder.isDirectory())) zipTempFolder.mkdirs();

            File zip = new File(zipTempFolder, Objects.requireNonNull(zipFile.getOriginalFilename()));
            if (zip.exists() && zip.isFile()) zip.delete();
            log.info("zip file name " + zip.getAbsolutePath());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(zip));
            stream.write(zipFile.getBytes());
            stream.flush();
            stream.close();
            zipHandleService.scheduleTask(zip, works);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("ok");
    }
}
