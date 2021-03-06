/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.tasks.execution;

import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.tasks.TaskExecuter;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskStateInternal;

/**
 * Executes any delayed configuration actions for {@link org.gradle.api.tasks.TaskInputs} and {@link org.gradle.api.tasks.TaskOutputs}.
 */
public class InputOutputEnsuringTaskExecuter implements TaskExecuter {
    private final TaskExecuter delegate;

    public InputOutputEnsuringTaskExecuter(TaskExecuter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(TaskInternal task, TaskStateInternal state, TaskExecutionContext context) {
        task.getInputs().ensureConfigured();
        task.getOutputs().ensureConfigured();
        delegate.execute(task, state, context);
    }
}
