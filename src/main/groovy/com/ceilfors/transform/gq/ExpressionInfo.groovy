package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
class ExpressionInfo {

    String methodName
    String text
    Object value

    ExpressionInfo(String methodName, String text, Object value) {
        this.methodName = methodName
        this.text = text
        this.value = value
    }
}
