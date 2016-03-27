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

package com.ceilfors.groovy.gq

import com.ceilfors.groovy.gq.codeflow.ExceptionInfo
import com.ceilfors.groovy.gq.codeflow.MethodInfo
import com.github.yihtserns.groovy.decorator.Function

/**
 * @author ceilfors
 */
class GqTraceDecorator {

    @SuppressWarnings('CatchThrowable')
    static trace(Function func, args) {
        boolean voidReturnType = func.returnType == void
        SingletonCodeFlowManager.INSTANCE.methodStarted(new MethodInfo(func.name, args))

        try {
            Object result = func(args)
            if (voidReturnType) {
                SingletonCodeFlowManager.INSTANCE.methodEnded()
            } else {
                SingletonCodeFlowManager.INSTANCE.methodEnded(result)
            }
            return result
        } catch (Throwable e) {
            SingletonCodeFlowManager.INSTANCE.exceptionThrown(new ExceptionInfo(func.name, e))
            throw e
        }
    }
}
