/*
 * Copyright 2016 Wisen Tanasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * Called the method throws an exception.
     */
    void exceptionThrown(ExceptionInfo exceptionInfo)

    /**
     * Called when an expression has just been processed
     * @param methodName the method name where this expression is currently in
     * @param expressionInfos the expression information
     * @return the first expression result, useful for method chaining
     */
    Object expressionProcessed(String methodName, ExpressionInfo... expressionInfos)
}
