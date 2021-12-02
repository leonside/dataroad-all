package com.leonside.dataroad.core.component;


import com.leonside.dataroad.common.utils.IdGenerator;

/**
 * @author leon
 */
public abstract class ComponentNameAutoGenerator extends ComponentNameable {

    public ComponentNameAutoGenerator(String identityPrefix) {
        this.name = IdGenerator.generate(identityPrefix);
    }

}
