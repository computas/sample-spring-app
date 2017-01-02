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

import static org.asciidoctor.Asciidoctor.Factory.create;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import org.asciidoctor.Asciidoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

  private DocumentRepository documentRepository;

  private final Asciidoctor asciidoctor = create();

  @Autowired
  public DocumentController(DocumentRepository documentRepository) {
    this.documentRepository = documentRepository;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Document> getDocument(@PathVariable("id") String id) {
    final Document found = documentRepository.findOne(id);
    return found == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(found, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getHtmlDocument(@PathVariable("id") String id) {
    final Document found = documentRepository.findOne(id);
    return found == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(toHtml(found), HttpStatus.OK);
  }

  // Example request body:
  // {"id":"abcd","context":{"date":"2016-05-21","author":"john"},"content":"= Titel"}
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Document> addDocument(@RequestBody Document document) throws URISyntaxException {
    if (document.getId() == null) {
      document.setId(UUID.randomUUID().toString());
    }
    final Document saved = documentRepository.save(document);
    URI uri = new URI("/documents/" + saved.getId());
    return ResponseEntity.created(uri).body(null);
  }

  private String toHtml(final Document document) {
    return asciidoctor.convert("= Title\na name\n\n== A subtitle\n\nWriting AsciiDoc is _easy_!", new HashMap<>());
  }
}
