package net.cockamamy.jv;

import com.google.common.base.Objects;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.MoreObjects.toStringHelper;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotBlank;
import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotNull;

@Immutable
final class KvStore {

    private final ConcurrentMap<String, Value> values;

    KvStore() {
        values = new ConcurrentHashMap<>();
    }

    @Nonnull
    Optional<Value> get(@Nonnull final String key) {
        checkArgumentNotBlank(key);

        return Optional.ofNullable(values.get(key));
    }

    void put(@Nonnull final String key,
             @Nonnull final MediaType mediaType,
             @Nonnull final String value) {

        checkArgumentNotBlank(key);
        checkArgumentNotNull(mediaType);
        checkArgumentNotNull(value);

        values.put(key, new Value(mediaType, value));
    }

    Optional<Value> remove(@Nonnull final String key) {
        checkArgumentNotBlank(key);

        return Optional.ofNullable(values.remove(key));
    }

    @Immutable
    static final class Value implements Serializable {

        private final MediaType contentType;
        private final String object;

        private Value(@Nonnull final MediaType contentType,
                      @Nonnull final String object) {

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
