/**
 * The MIT License
 * Copyright (c) 2016 Michael Gfeller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.workshop;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.example.workshop.test.IntegrationTestCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorkshopApplication.class)
@WebIntegrationTest("server.port=0")
@Category(IntegrationTestCategory.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentRestTest {

  private RestTemplate restTemplate = new TestRestTemplate();

  @Value("${local.server.port}")
  private int port;

  private static String id;

  @BeforeClass
  public static void createId() {
    id = UUID.randomUUID().toString();
  }

  @Test
  public void canAPostDocument() {
    Document document = new Document();
    document.setId(id);
    Map<String, String> context = new HashMap<>();
    context.put("author", "John Doe");
    document.setContent("= Title");
    ResponseEntity<Document> response = restTemplate.postForEntity("http://localhost:" + port + "/documents/",
                                                                   document,
                                                                   Document.class);
    assertThat(response.getHeaders().getLocation().toString(), containsString("/documents/" + document.getId()));
    assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    assertNull(response.getBody());
  }

  @Test
  public void canBGetDocument() {
    ResponseEntity<Document> response = restTemplate.getForEntity("http://localhost:" + port + "/documents/" + id,
                                                                  Document.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertNotNull(response.getBody());
    Document document = response.getBody();
    assertEquals("= Title", document.getContent());
  }

  @Test
  public void canCGetHtmlDocument() {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/documents/" + id + ".html",
                                                                String.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertTrue("Media type should be compatible with " + MediaType.TEXT_HTML_VALUE,
               response.getHeaders().getContentType().isCompatibleWith(MediaType.TEXT_HTML));
    String body = response.getBody();
    assertNotNull(body);
    assertTrue("Response body should contain <html>, but is '" + body + "'", body.contains("<html>"));
  }
}
