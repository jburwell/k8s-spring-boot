package net.cockamamy.jv;

import static org.springframework.http.HttpStatus.CREATED;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
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

@RestController
@RequestMapping("/api/objects")
public class Endpoint {

    private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

    private final ConcurrentMap<String, Value> kvStore;

    public Endpoint() {
        // TODO Calculate parallel factor?
        kvStore = new ConcurrentHashMap<>();
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> getValue(@PathVariable final String key) {

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

        LOG.debug("Putting key {} with content type {} and a value of {}", key, contentType, value);
        kvStore.put(key, new Value(contentType, value));

    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteValue(@PathVariable final String key) {

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

            if (!(thatObject instanceof Value)) {
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
            return MoreObjects.toStringHelper(this)
                .add("contentType", contentType)
                .add("object", object)
                .toString();
        }

    }

}