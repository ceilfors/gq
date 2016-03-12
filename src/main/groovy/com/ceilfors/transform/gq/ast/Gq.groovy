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

package com.ceilfors.transform.gq.ast

import com.github.yihtserns.groovy.decorator.MethodDecorator
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import com.ceilfors.transform.gq.ExceptionInfo
import com.ceilfors.transform.gq.MethodInfo
import com.ceilfors.transform.gq.SingletonCodeFlowManager

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author ceilfors
 */
@MethodDecorator({ func ->
    boolean voidReturnType = func.returnType == void

    return { args ->
        SingletonCodeFlowManager.INSTANCE.methodStarted(new MethodInfo(func.name, args))

        def result = null
        try {
            result = func(args)
        } catch (Exception e) {
            SingletonCodeFlowManager.INSTANCE.exceptionThrown(new ExceptionInfo(e))

            throw e
        }

        if (voidReturnType) {
            SingletonCodeFlowManager.INSTANCE.methodEnded()
        } else {
            SingletonCodeFlowManager.INSTANCE.methodEnded(result)
        }

        return result
    }
})
@GroovyASTTransformationClass ("com.github.yihtserns.groovy.decorator.DecoratorASTTransformation")
@Retention (RetentionPolicy.SOURCE)
@Target ([ElementType.METHOD])
public @interface Gq {

}