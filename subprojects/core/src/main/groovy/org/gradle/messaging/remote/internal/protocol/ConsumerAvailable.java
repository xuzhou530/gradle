/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.messaging.remote.internal.protocol;

import org.gradle.messaging.remote.internal.Message;

public class ConsumerAvailable extends Message implements ReplyRoutableMessage, RouteAvailableMessage {
    private final Object id;
    private final String displayName;

    public ConsumerAvailable(Object id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Object getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Object getSource() {
        return id;
    }

    public Object getDestination() {
        return null;
    }

    public RouteUnavailableMessage getUnavailableMessage() {
        return new ConsumerUnavailable(id);
    }

    @Override
    public String toString() {
        return String.format("[ConsumerAvailable id: %s, displayName: %s]", id, displayName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ConsumerAvailable other = (ConsumerAvailable) o;
        return id.equals(other.id) && displayName.equals(other.displayName);
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ displayName.hashCode();
    }
}
