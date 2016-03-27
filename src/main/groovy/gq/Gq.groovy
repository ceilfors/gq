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

package gq

import com.github.yihtserns.groovy.decorator.MethodDecorator
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import static com.ceilfors.groovy.gq.GqTraceDecorator.trace

/**
 * @author ceilfors
 */
@MethodDecorator({ func ->
    return { args -> trace(func, args) }
})
@GroovyASTTransformationClass('com.github.yihtserns.groovy.decorator.DecoratorASTTransformation')
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
public @interface Gq {

}