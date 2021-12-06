package com.leonside.dataroad.core.component;

import com.leonside.dataroad.common.spi.Component;

/**
 * @author leon
 */
public abstract class ComponentNameSupport implements Component {

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
