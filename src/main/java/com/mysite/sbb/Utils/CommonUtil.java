package com.mysite.sbb.Utils;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class CommonUtil {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonUtil() {
        // AutolinkExtension 추가
        List<Extension> extensions = Collections.singletonList(AutolinkExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String markdown(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}