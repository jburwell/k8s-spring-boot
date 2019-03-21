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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.MoreObjects.toStringHelper;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotBlank;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotNull;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(Endpoint.BASE_URI)
public class Endpoint {

    private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

    static final String BASE_URI = "/objects/";

    private final ConcurrentMap<String, Value> kvStore;

    public Endpoint() {
        kvStore = new ConcurrentHashMap<>();
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> getValue(@PathVariable final String key) {

        checkArgumentNotBlank(key);

        LOG.debug("Getting object with key {}", key);
        final Value value = kvStore.get(key);
        return value == null ? ResponseEntity.notFound().build()
            : ResponseEntity.ok().contentType(value.getContentType()).body(value.getObject());

    }

    @PutMapping("/{key}")
    @ResponseStatus(CREATED)
    public void putValue(@PathVariable final String key,
        @RequestHeader("Content-Type") final MediaType contentType,
        @RequestBody final String value) {

        checkArgumentNotBlank(key);
        checkArgumentNotNull(contentType);
        checkArgumentNotBlank(value);

        LOG.debug("Putting key {} with content type {} and a value of {}", key, contentType, value);
        kvStore.put(key, new Value(contentType, value));

    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteValue(@PathVariable final String key) {

        checkArgumentNotBlank(key);

        LOG.debug("Deleting key {}", key);
        final Value deletedValue = kvStore.remove(key);
        return deletedValue == null ? ResponseEntity.notFound().build()
            : ResponseEntity.ok().contentType(deletedValue.getContentType())
                .body(deletedValue.getObject());

    }

    @Immutable
    private static final class Value implements Serializable {

        private final MediaType contentType;
        private final String object;

        private Value(@Nonnull final MediaType contentType, @Nonnull final String object) {

            checkArgumentNotNull(contentType);
            checkArgumentNotBlank(object);

            this.contentType = contentType;
            this.object = object;

        }

        @Nonnull
        public MediaType getContentType() {
            return contentType;
        }

        @Nonnull
        public String getObject() {
            return object;
        }

        @Override
        public boolean equals(final Object thatObject) {

            if (this == thatObject) {
                return true;
            }

            if (thatObject == null) {
                return false;
            }

            if (!getClass().equals(thatObject.getClass())) {
                return false;
            }

            final Value thatValue = (Value) thatObject;
            return Objects.equal(contentType, thatValue.contentType) &&
                Objects.equal(object, thatValue.object);

        }

        @Override
        public int hashCode() {
            return Objects.hashCode(contentType, object);
        }

        @Override
        public String toString() {
            return toStringHelper(this)
                .add("contentType", contentType)
                .add("object", object)
                .toString();
        }

    }

}
