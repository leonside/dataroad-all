package com.leonside.dataroad.common.script;

import java.io.Serializable;
import java.util.Map;

public interface ScriptEvaluator extends Serializable {

    Object evaluate(Object row,  Map<String, Object> var2) throws Exception;
}