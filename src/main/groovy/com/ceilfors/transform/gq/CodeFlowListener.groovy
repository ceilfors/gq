package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
interface CodeFlowListener {

    /**
     * Method is about to be executed
     * @param methodInfo method information
     */
    void methodStarted(MethodInfo methodInfo)

    /**
     * Called when a method returns a value i.e. method return type is not void.
     * @param result that will be returned by the method
     */
    void methodEnded(Object result)

    /**
     * Called when a method is ended and the method return type is void.
     */
    void methodEnded()
}
