package ru.itmo.web.lesson4.web;

import freemarker.template.*;
import ru.itmo.web.lesson4.util.DataUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerServlet extends HttpServlet {
    private static final String UTF_8 = StandardCharsets.UTF_8.name();
    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);

    private final Handler getPageHandler = (request, response) -> {
        String uri = request.getRequestURI();
        if (uri.equals("") || uri.equals("/")) {
            response.sendRedirect("/index");
        } else {
            Map<String, Object> data = getData(request);
            addDataFromParameters(data, request);
            setResponseContent(request, response, uri + ".ftlh", data);
        }
    };

    private final Handler pageNotFoundHandler = (request, response) ->
            setResponseContent(request, response, "not_found.ftlh", getData(request));

    @Override
    public void init() throws ServletException {
        super.init();
        File dir = new File(getServletContext().getRealPath("."), "../../src/main/webapp/WEB-INF/templates");
        try {
            cfg.setDirectoryForTemplateLoading(dir);
        } catch (IOException e) {
            throw new ServletException("Unable to set template directory [dir=" + dir + "].", e);
        }
        cfg.setDefaultEncoding(UTF_8);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);
        try {
            getPageHandler.handle(request, response);
        } catch (Exception ignored) {
            pageNotFoundHandler.handle(request, response);
        }
    }

    private Map<String, Object> getData(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, Object> data = new HashMap<>();
        DataUtil.addData(request, data);
        data.put("currentPage", URLDecoder.decode(request.getRequestURI(), UTF_8));
        return data;
    }

    private void addDataFromParameters(Map<String, Object> data, HttpServletRequest request) throws UnsupportedEncodingException {
        for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            if (e.getValue() != null && e.getValue().length == 1) {
                Object value = e.getKey().endsWith("_id") && !e.getKey().equals("logged_user_id") ? Long.parseLong(e.getValue()[0]) : e.getValue()[0];
                data.put(e.getKey(), value);
            }
        }
    }

    private void setResponseContent(HttpServletRequest request, HttpServletResponse response,
                                    String templateFile, Map<String, Object> data) throws IOException {
        Template template = cfg.getTemplate(templateFile);
        response.setContentType("text/html");
        try {
            template.process(data, response.getWriter());
        } catch (TemplateException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @FunctionalInterface
    private interface Handler {
        void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }
}
