package org.example.docmeet.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;


public class BadRequestException extends HttpClientErrorException {
    public BadRequestException(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
        super(statusText, HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
    }
}
