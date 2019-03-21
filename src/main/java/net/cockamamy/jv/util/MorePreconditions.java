/*
 * Copyright 2018 John S. Burwell III
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cockamamy.jv.util;

import static com.google.common.base.Preconditions.checkArgument;

public final class MorePreconditions {

    private MorePreconditions() { }

    public static void checkArgumentNotNull(final Object argument) {
        checkArgument(argument != null);
    }

    public static void checkArgumentNotBlank(final String argument) {
        checkArgument(argument != null && argument.isEmpty() == false);
    }

}