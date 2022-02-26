package com.leonside.dataroad.dashboard.domian;

import com.leonside.dataroad.common.exception.JobFlowException;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Data
public class JobJarsReponse {

    private String address;
    private String status;

    private List<FileJarInfo> files;

    public List<FileJarInfo> findDataroadJars(){
        List<FileJarInfo> dataroadJars = files.stream().filter(fileJarInfo -> fileJarInfo.getName().startsWith("dataroad")).collect(Collectors.toList());
        return dataroadJars;
    }

    public FileJarInfo findFirstJarNotNull(){
        if(CollectionUtils.isEmpty(files)){
            throw new JobFlowException("请先上传dataroad Jar");
        }

        List<FileJarInfo> dataroadJars = findDataroadJars();
        if(CollectionUtils.isEmpty(dataroadJars)){
            throw new JobFlowException("请先上传dataroad Jar");
        }
        return dataroadJars.get(0);
    }

    @Data
    public static class FileJarInfo{
        private String id;
        private String name;
        private String uploaded;
    }

}
