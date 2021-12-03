package com.leonside.dataroad.core.component;

import com.leonside.dataroad.common.extension.ExtensionLoader;
import com.leonside.dataroad.common.spi.Component;

/**
 * @author leon
 */
public class JobExtensionLoader {

    public static <T> T getExtensionLoader(Class<T> clazz, String componentName){
        ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(clazz);
        return (T) extensionLoader.getExtension(componentName);
    }

    public static <T extends Component> T getComponent(ComponentType componentType, String componentName){
        return (T) getExtensionLoader(componentType.getSpi(),componentName);
    }

    public static <T extends Component> T getSingleComponent(ComponentType componentType){

        ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(componentType.getSpi());

        return (T) extensionLoader.getFirstExtension();
    }


}
