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

import static java.lang.String.join;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllBytes;
import static net.cockamamy.jv.Endpoint.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.cockamamy.jv.ManagedDiagnosticContextFilter.REQUEST_ID_HEADER;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = MOCK)
@ExtendWith(SpringExtension.class)
public class EndpointTest {

    private String jsonObject;

    @Nonnull
    private static String randomKey() {
        return UUID.randomUUID().toString();
    }

    @CanIgnoreReturnValue
    @Nonnull
    private static URI putObject(@Nonnull final MockMvc client, @Nonnull final MediaType mediaType,
        @Nonnull final String content) throws Exception {

        final URI uri = URI.create(join("/", BASE_URI, randomKey()));

        client.perform(put(uri)
            .contentType(mediaType)
            .content(content))
            .andExpect(status().isCreated())
            .andExpect(header().string(REQUEST_ID_HEADER, anything()));

        return uri;

    }

    @BeforeEach
    private void loadJsonObject() throws IOException, URISyntaxException {

        final URL url = EndpointTest.class.getClassLoader().getResource("myobject.json");
        assertThat(url).isNotNull();

        final Path testResourcePath = Paths.get(url.toURI());
        jsonObject = new String(readAllBytes(testResourcePath), defaultCharset());
        assertThat(jsonObject).isNotBlank();

    }

    @Test
    public void testPutObject(@Autowired @Nonnull final MockMvc client) throws Exception {
        putObject(client, APPLICATION_JSON_UTF8, jsonObject);
    }

    @Test
    public void testSuccessfulGetObject(@Autowired @Nonnull final MockMvc client) throws Exception {

        final MediaType mediaType = APPLICATION_JSON_UTF8;
        final URI objectURI = putObject(client, mediaType, jsonObject);
        assertThat(objectURI).isNotNull();

        client.perform(get(objectURI))
            .andExpect(status().isOk())
            .andExpect(header().string(REQUEST_ID_HEADER, anything()))
            .andExpect(content().contentType(mediaType))
            .andExpect(content().string(jsonObject));

    }

    @Test
    public void testUnsuccessfulGetObject(@Autowired @Nonnull final MockMvc client)
        throws Exception {
        client.perform(get(join("/", BASE_URI, randomKey())))
            .andExpect(header().string(REQUEST_ID_HEADER, anything()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testSuccessfulDeleteObject(@Autowired @Nonnull final MockMvc client)
        throws Exception {

        final MediaType mediaType = APPLICATION_JSON_UTF8;
        final URI objectURI = putObject(client, mediaType, jsonObject);
        assertThat(objectURI).isNotNull();

        client.perform(delete(objectURI))
            .andExpect(status().isOk())
            .andExpect(header().string(REQUEST_ID_HEADER, anything()))
            .andExpect(content().contentType(mediaType))
            .andExpect(content().string(jsonObject));

    }

    @Test
    public void testUnsuccessfulDeleteObject(@Autowired @Nonnull final MockMvc client)
        throws Exception {
        client.perform(delete(join("/", BASE_URI, randomKey())))
            .andExpect(header().string(REQUEST_ID_HEADER, anything()))
            .andExpect(status().isNotFound());
    }

}
