package com.leonside.dataroad.config.domain;

import com.leonside.dataroad.common.context.JobSetting;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * @author leon
 */
@Data
public class JobConfig {

    private JobSetting setting;

    public List<Map<String, GenericComponentConfig>> content;

    public void buildJobFlowRelation(){
        this.getContent().forEach(contents->{

            Map<String, GenericComponentConfig> componentConfigMap = new LinkedHashMap<>();
            contents.forEach((key,value)->{
                componentConfigMap.put(key, value);
            });

            Iterator<Map.Entry<String, GenericComponentConfig>> iterator = contents.entrySet().iterator();
            Map.Entry<String, GenericComponentConfig> latestComponent = null;
            while (iterator.hasNext()){
                Map.Entry<String, GenericComponentConfig> next = iterator.next();

                next.getValue().setName(next.getKey());

                if(latestComponent != null){
                    List<String> parentCmpName = new ArrayList<>();
                    if(ArrayUtils.isEmpty(next.getValue().getDependencies())){
                        parentCmpName.add(latestComponent.getKey());
                    }else{
                        parentCmpName.addAll(Arrays.asList(next.getValue().getDependencies()));
                    }

                    parentCmpName.stream().forEach(cmpName ->{
                        componentConfigMap.get(cmpName).getChilds().add(next.getValue());
                    });
                }

                latestComponent = next;
            }
        });
    }


}
