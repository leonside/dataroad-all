package com.leonside.dataroad.dashboard.domian;

import com.leonside.dataroad.common.exception.JobFlowException;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
@Data
public class JobJarsReponse {

    private String address;
    private String status;

    private List<FileJarInfo> files;

    public FileJarInfo findFirstJarNotNull(){
        if(CollectionUtils.isEmpty(files)){
            throw new JobFlowException("请先上传dataroad Jar");
        }

        Optional<FileJarInfo> jarInfoOptional = files.stream().filter(fileJarInfo -> fileJarInfo.getName().startsWith("dataroad")).findFirst();
        if(!jarInfoOptional.isPresent()){
            throw new JobFlowException("请先上传dataroad Jar");
        }
        return jarInfoOptional.get();
    }

    @Data
    public static class FileJarInfo{
        private String id;
        private String name;
        private String uploaded;
    }

}
