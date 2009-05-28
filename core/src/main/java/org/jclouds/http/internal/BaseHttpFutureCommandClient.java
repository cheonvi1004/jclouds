/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http.internal;

import com.google.inject.Inject;
import org.jclouds.http.*;
import org.jclouds.http.annotation.ClientErrorHandler;
import org.jclouds.http.annotation.RedirectHandler;
import org.jclouds.http.annotation.RetryHandler;
import org.jclouds.http.annotation.ServerErrorHandler;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.CloseContentAndSetExceptionHandler;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public abstract class BaseHttpFutureCommandClient implements HttpFutureCommandClient {

    protected final URL target;

    @Resource
    protected Logger logger = Logger.NULL;

    @Inject(optional = true)
    protected List<HttpRequestFilter> requestFilters = Collections.emptyList();
    @RedirectHandler
    @Inject(optional = true)
    protected HttpResponseHandler redirectHandler = new CloseContentAndSetExceptionHandler();
    @ClientErrorHandler
    @Inject(optional = true)
    protected HttpResponseHandler clientErrorHandler = new CloseContentAndSetExceptionHandler();
    @ServerErrorHandler
    @Inject(optional = true)
    protected HttpResponseHandler serverErrorHandler = new CloseContentAndSetExceptionHandler();
    
    @RetryHandler
    @Inject(optional = true)
    protected HttpRetryHandler httpRetryHandler = new BackoffLimitedRetryHandler(5);   

    @Inject
    public BaseHttpFutureCommandClient(URL target) {
        this.target = target;
    }

    protected void handleResponse(HttpFutureCommand<?> command, HttpResponse response) {
        int code = response.getStatusCode();
        if (code >= 500) {
            this.serverErrorHandler.handle(command, response);
        } else if (code >= 400 && code < 500) {
            this.clientErrorHandler.handle(command, response);
        } else if (code >= 300 && code < 400) {
            this.redirectHandler.handle(command, response);
        } else {
            command.getResponseFuture().setResponse(response);
            command.getResponseFuture().run();
        }
    }

}