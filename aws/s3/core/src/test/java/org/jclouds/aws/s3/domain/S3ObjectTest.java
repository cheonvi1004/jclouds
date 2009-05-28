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
package org.jclouds.aws.s3.domain;

import org.jclouds.http.ContentTypes;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import org.testng.annotations.Test;

import java.io.File;

@Test(groups = "unit", testName = "s3.S3ObjectTest")
public class S3ObjectTest {

    @Test
    void testSetNoContentType() {
        S3Object object = new S3Object("test");
        File file = new File("hello.txt");
        object.setData(file);
        assertEquals(object.getMetadata().getContentType(),
                ContentTypes.BINARY);
    }
    
    @Test
    void testMd5CopyingNotReference() {
    	byte[] md5 = new byte[12];
        S3Object object = new S3Object("test");
    	object.getMetadata().setMd5(md5);
    	byte[] returnedMd5 = object.getMetadata().getMd5();
    	assertNotSame(md5, returnedMd5);
    }
    
}
