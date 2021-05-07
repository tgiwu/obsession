package com.mine.obsession.services;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.mine.obsession.Props;
import com.mine.obsession.Utils;
import com.mine.obsession.models.SimpleZipFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;

@Slf4j
@Service
public class ZipHandleService {

    @Autowired
    @Qualifier("zip")
    private ExecutorService mExecutorService;

    @Autowired
    private Props props;

    public void scheduleTask(File zipFile, String works) {
        if (zipFile.getName().endsWith(".zip")) {
            try {
                mExecutorService.submit(new UnzipTask(new SimpleZipFile(zipFile.getAbsoluteFile()), works));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (zipFile.getName().endsWith(".tar.gz")) {
            mExecutorService.submit(new UnzipTask(zipFile, works));
        }
    }

    public void scheduleTask(String zipFilePath, String works) {
        scheduleTask(new File(zipFilePath), works);
    }

    class UnzipTask implements Callable<Integer> {

        private SimpleZipFile zipFile;
        private File gzipFile;
        private String works;

        UnzipTask(SimpleZipFile zipFile, String works) {
            this.zipFile = zipFile;
            this.works = works;
        }

        UnzipTask(File gzipFile, String works) {
            this.gzipFile = gzipFile;
            this.works = works;
        }

        @Override
        public Integer call() throws Exception {

            if (null != zipFile && zipFile.getName().endsWith(".zip")) {
                unzip();

                Utils.delFileOrDir(new File(props.getImageDir() + File.separator +
                        props.getZipDir() + File.separator +
                        zipFile.getName().replace(".zip", "")));

                Utils.delFileOrDir(new File(props.getImageDir() + File.separator +
                        props.getZipDir() + File.separator +
                        zipFile.getName() + ".zip"));
            } else if (null != gzipFile && gzipFile.getName().endsWith(".tar.gz")) {
                unCompressArchiveGz(gzipFile.getAbsolutePath());
                Utils.delFileOrDir(gzipFile);
                Utils.delFileOrDir(new File(gzipFile.getParent(), gzipFile.getName()));
            }
            return 0;
        }

        private synchronized void unzip() {
            Enumeration<?> entries = zipFile.entries();
            Map<String, List<String>> chapters  = new HashMap<>();
            try {

                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    log.info("decompress " + entry.getName());
                    String[] keys = entry.getName().split(File.separator);
                    String chapter = keys[1];
                    if (entry.isDirectory()) {
                        File folder = new File(props.getImageDir() + File.separator +
                                props.getZipDir() + File.separator +
                                works);
                        if (folder.exists() && folder.isDirectory()) {
                            Utils.delFileOrDir(folder);
                        }
                        folder.mkdirs();
                        chapters.computeIfAbsent(chapter, k -> new ArrayList<>());
                    } else {
                        // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                        File targetFile = new File(props.getImageDir() + File.separator +
                                props.getZipDir() + File.separator +
                                works + File.separator +
                                chapter + File.separator +
                                keys[2]);
                        // 保证这个文件的父文件夹必须要存在
                        if(!targetFile.getParentFile().exists()){
                            targetFile.getParentFile().mkdirs();
                        }
                        targetFile.createNewFile();
                        // 将压缩文件内容写入到这个文件中
                        if (chapters.containsKey(chapter)) {
                            chapters.get(chapter).add(targetFile.getAbsolutePath());
                        } else {
                            log.error("not find chapter " + chapter);
                        }
                        InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        int len;
                        byte[] buf = new byte[props.getBufferSize()];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.close();
                        is.close();
                    }
                }
                copyToPersistent(chapters, works);
            } catch (Exception e) {
                throw new RuntimeException("unzip error from ZipUtils", e);
            } finally {
                if(zipFile != null){
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    log.warn("delete zipFile = " +zipFile.getFile().getAbsolutePath());
                    Utils.delFileOrDir(zipFile.getFile());
                }
            }
        }

        private synchronized void unCompressArchiveGz(String archive) throws IOException {

            File file = new File(archive);

            BufferedInputStream bis =
                    new BufferedInputStream(new FileInputStream(file));

            String fileName =
                    file.getName().substring(0, file.getName().lastIndexOf("."));

            String finalName = file.getParent() + File.separator + fileName;

            BufferedOutputStream bos =
                    new BufferedOutputStream(new FileOutputStream(finalName));

            GzipCompressorInputStream gcis =
                    new GzipCompressorInputStream(bis);

            byte[] buffer = new byte[1024];
            int read = -1;
            while((read = gcis.read(buffer)) != -1){
                bos.write(buffer, 0, read);
            }
            gcis.close();
            bos.close();

            unCompressTar(finalName);
        }

        private synchronized void unCompressTar(String finalName) throws IOException {
            File file = new File(finalName);
            String parentPath = file.getParent();
            TarArchiveInputStream tais =
                    new TarArchiveInputStream(new FileInputStream(file));

            TarArchiveEntry tarArchiveEntry = null;
            Map<String, List<String>> chapters = new HashMap<>();
            while((tarArchiveEntry = tais.getNextTarEntry()) != null){
                String name = tarArchiveEntry.getName();
                File tarFile = new File(parentPath, name);
                if(!tarFile.getParentFile().exists()){
                    tarFile.getParentFile().mkdirs();
                    log.info("parent file name = " + tarFile.getParentFile().getName());
                }

                if (tarArchiveEntry.isDirectory()) {
                    tarFile.mkdir();
                    log.info("tar file dir name = " + tarFile.getName());
                    continue;
                }

                if (tarFile.getName().startsWith(".")) continue;

                String[] keys = tarFile.getCanonicalPath().split(File.separator);
                String chapter = keys[keys.length - 2];
                if (!chapters.containsKey(chapter)) {
                    chapters.put(chapter, new ArrayList<>());
                }
                chapters.get(chapter).add(tarFile.getCanonicalPath());

                BufferedOutputStream bos =
                        new BufferedOutputStream(new FileOutputStream(tarFile));

                int read = -1;
                byte[] buffer = new byte[1024];
                while((read = tais.read(buffer)) != -1){
                    bos.write(buffer, 0, read);
                }
                bos.close();
            }
            tais.close();
            copyToPersistent(chapters, works);
            Utils.delFileOrDir(parentPath);
            Utils.delFileOrDir(file);//删除tar文件
        }

        private synchronized void copyToPersistent(Map<String, List<String>> param, String works) {
            if (null == param || param.isEmpty()) return;

            try {
                Set<String> chapterName = param.keySet();
                for (String chapter : chapterName) {
                    List<String> tempFilePaths = param.get(chapter);
                    File folder = new File(props.getImageDir() + File.separator +
                            works + File.separator +
                            chapter);
                    if (!folder.exists() || !folder.isDirectory()) {
                        folder.mkdirs();
                    }

                    for (String path : tempFilePaths) {
                        BufferedInputStream inputStream = FileUtil.getInputStream(path);
                        File to = new File(folder, path.substring(path.lastIndexOf(File.separator) + 1));
                        if (to.exists()) FileUtil.del(to);
                        BufferedOutputStream outputStream = FileUtil.getOutputStream(to);
                        IoUtil.copy(inputStream, outputStream, IoUtil.DEFAULT_BUFFER_SIZE);

                        log.info(path + " => " + to.getAbsolutePath());
                        IoUtil.close(inputStream);
                        IoUtil.close(outputStream);
                        persistentToDB(works, chapter, to.getAbsolutePath());
                    }

                    Utils.delFileOrDir(new File(props.getImageDir() + File.separator +
                            props.getZipDir() + File.separator + works + File.separator + chapter));
                }
            } catch (Exception e) {
                log.error("error on copy ", e);
            }
        }

        private synchronized void persistentToDB(String works, String chapter, String fileName) {
            log.warn("works = " + works + "; chapter = " + chapter + "; fileName = " + fileName);
        }
    }
}
