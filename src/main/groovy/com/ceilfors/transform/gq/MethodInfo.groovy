package com.ceilfors.transform.gq

import groovy.transform.ToString

/**
 * @author ceilfors
 */
@ToString
class MethodInfo {

    String name
    List<String> parameters
    List<Object> args

    MethodInfo(String name, List<String> parameters, List<Object> args) {
        this.name = name
        this.parameters = parameters
        this.args = args
    }
}
