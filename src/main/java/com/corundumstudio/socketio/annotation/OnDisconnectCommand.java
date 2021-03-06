/**
 * Copyright (c) 2012-2019 Nikita Koksharov
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
package com.corundumstudio.socketio.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.corundumstudio.socketio.namespace.Namespace;

public class OnDisconnectCommand implements Command {
    private OnDisconnectScanner theOnDisconnectScanner;

    private Namespace namespace;
    private Object object;
    private Method method;
    private Annotation annotation;
    private Class<?> clazz;

    public OnDisconnectCommand(OnDisconnectScanner theOnDisconnectScanner, Namespace namespace, Object object, Method method, Annotation annotation) {
        this.theOnDisconnectScanner = theOnDisconnectScanner;
        this.namespace = namespace;
        this.object = object;
        this.method = method;
        this.annotation = annotation;
    }
    public OnDisconnectCommand(OnDisconnectScanner theOnDisconnectScanner, Method method, Class<?> clazz) {
        this.theOnDisconnectScanner = theOnDisconnectScanner;
        this.method = method;
        this.clazz = clazz;
    }

    public void addExecution() {
        theOnDisconnectScanner.addListener(namespace, object, method, annotation);
    }
    public void validateExecution() {
        theOnDisconnectScanner.validateListener(method, clazz);
    }
}
