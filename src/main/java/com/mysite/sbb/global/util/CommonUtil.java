package com.mysite.sbb.global.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CommonUtil {

    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static void validateUserPermission(String currentUsername, String authorUsername, String actionMessage) {
        if (!currentUsername.equals(authorUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, actionMessage + "이(가) 없습니다.");
        }
    }

}
