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

package net.cockamamy.jv;

import com.google.common.net.HttpHeaders;
import net.cockamamy.jv.KvStore.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotBlank;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotNull;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(Endpoint.BASE_URI)
public class Endpoint {

    private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

    static final String BASE_URI = "/objects/";

    private final KvStore kvStore;

    public Endpoint() {
        kvStore = new KvStore();
    }

    @GetMapping("/{key}")
    public Optional<Value> getValue(@PathVariable final String key) {

        checkArgumentNotBlank(key);

        LOG.debug("Getting object with key {}", key);
        return kvStore.get(key);

    }

    @PutMapping("/{key}")
    @ResponseStatus(CREATED)
    public void putValue(@PathVariable final String key,
        @RequestHeader(CONTENT_TYPE) final MediaType contentType,
        @RequestBody final String value) {

        checkArgumentNotBlank(key);
        checkArgumentNotNull(contentType);
        checkArgumentNotBlank(value);

        LOG.debug("Putting key {} with content type {} and a value of {}", key, contentType, value);
        kvStore.put(key, contentType, value);

    }

    @DeleteMapping("/{key}")
    public Optional<Value> deleteValue(@PathVariable final String key) {

        checkArgumentNotBlank(key);

        LOG.debug("Deleting key {}", key);
        return kvStore.remove(key);

    }

}
