/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.multimap.impl.operations.client;

import com.hazelcast.client.impl.client.RetryableRequest;
import com.hazelcast.multimap.impl.MultiMapPortableHook;
import com.hazelcast.multimap.impl.operations.CountOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

public class CountRequest extends MultiMapKeyBasedRequest implements RetryableRequest {

    public CountRequest() {
    }

    public CountRequest(String name, Data key) {
        super(name, key);
    }

    protected Operation prepareOperation() {
        return new CountOperation(name, key);
    }

    public int getClassId() {
        return MultiMapPortableHook.COUNT;
    }

    @Override
    public String getMethodName() {
        return "valueCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{key};
    }
}
