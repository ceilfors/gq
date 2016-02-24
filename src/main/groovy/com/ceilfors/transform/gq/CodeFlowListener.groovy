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

    /**
     * Called when an expression has just been processed
     * @param expressionInfo the expression information
     * @return the expression result, useful for method chaining
     */
    Object expressionProcessed(ExpressionInfo expressionInfo)
}
