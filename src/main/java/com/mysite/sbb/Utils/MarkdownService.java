package com.mysite.sbb.Utils;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

@Service
public class MarkdownService {
    public String renderMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return ""; // 빈 문자열 처리
        }
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document); // 마크다운을 HTML로 변환
    }
}