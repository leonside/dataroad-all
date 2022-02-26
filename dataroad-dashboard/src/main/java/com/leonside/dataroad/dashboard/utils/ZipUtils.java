package com.leonside.dataroad.dashboard.utils;

import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author leon
 */
@Slf4j
public class ZipUtils {

    public static File zip (String[] fileIds, String zipFileName) {

        String zipFile = HomeFolderUtils.getHomeFolder() + File.separator + File.separator + zipFileName;

        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))){

            Arrays.stream(fileIds).forEach(fileName->{
                String schemaFilePathInHomeFolder = HomeFolderUtils.getSchemaFilePathInHomeFolder(fileName);
                try {
                    File file = new File(schemaFilePathInHomeFolder);
                    out.putNextEntry(new ZipEntry(file.getName()));
                    try(FileInputStream inputStream = new FileInputStream(file)){
                        IOUtils.copy(inputStream, out);
                    }
                } catch (IOException e) {
                    log.error("download file exception",e);
                }
            });
            return new File(zipFile);
        } catch (IOException e) {
            log.error("download file exception",e);
            throw new JobConfigException("download file exception",e);
        }
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        zip(new String[]{"es_filter_es.json","mysql_aggcount_es.json"}, "dataroad-"+ DateUtil.dateToStoreDateTimeString(new Date()) + ".zip");
//    }
}
