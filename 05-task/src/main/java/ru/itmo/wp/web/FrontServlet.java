package ru.itmo.wp.web;

import com.google.common.base.Strings;
import freemarker.template.*;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FrontServlet extends HttpServlet {
    private static final String BASE_PACKAGE = FrontServlet.class.getPackage().getName() + ".page";
    private static final String DEFAULT_ACTION = "action";

    private Configuration sourceConfiguration;
    private Configuration targetConfiguration;

    private Configuration newFreemarkerConfiguration(String templateDirName, boolean debug) throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir=" + templateDir + "]");
        }
        configuration.setLocalizedLookup(false);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER :
                TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }

    @Override
    public void init() throws ServletException {
        sourceConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("/") + "../../src/main/webapp/WEB-INF/templates", true);
        targetConfiguration = newFreemarkerConfiguration(getServletContext().getRealPath("WEB-INF/templates"), false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Route route = Route.newRoute(request);
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException notFoundException) {
                throw new ServletException(notFoundException);
            }
        }
    }

    private void process(Route route, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, ServletException, IOException {
        updateLang(request);
        Class<?> pageClass = findPageClass(route);
        Method method = findMethod(pageClass, route);
        Object page = newInstance(pageClass);
        Map<String, Object> view = new HashMap<>();
        try {
            substituteParameters(page, method, view, request);
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't invoke action method [pageClass=" + pageClass + ", method=" + method + "]");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                RedirectException redirectException = (RedirectException) cause;
                response.sendRedirect(redirectException.getTarget());
                return;
            } else {
                throw new ServletException("Can't invoke action method [pageClass=" + pageClass + ", method=" + method + "]", cause);
            }
        }
        Template template = newTemplateBySession(request.getSession(), pageClass.getSimpleName());
        try {
            apply(template, view, response);
        } catch (TemplateException e) {
            throw new ServletException("Can't render template [pageClass=" + pageClass + ", action=" + method + "]", e);
        }
    }

    private Class<?> findPageClass(Route route) throws NotFoundException {
        try {
            return Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();
        }
    }

    private Method findMethod(Class<?> pageClass, Route route) throws NotFoundException {
        Method method = null;
        for (Class<?> clazz = pageClass;
             method == null && clazz != null;
             clazz = clazz.getSuperclass()) {
            method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(route.getAction()))
                    .filter(m -> Arrays.stream(m.getParameterTypes())
                            .allMatch(type -> Map.class.equals(type) || HttpServletRequest.class.equals(type)))
                    .findFirst()
                    .orElse(null);
        }
        if (method == null) {
            throw new NotFoundException();
        }
        return method;
    }

    private Object newInstance(Class<?> pageClass) throws ServletException {
        try {
            return pageClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("Can't create page [pageClass=" + pageClass + "]");
        }
    }

    private void substituteParameters(Object page, Method method, Map<String, Object> view, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        Object[] args = Arrays.stream(method.getParameters()).map(p -> {
            if (HttpServletRequest.class.equals(p.getType())) {
                return request;
            } else {
                return view;
            }
        }).toArray(Object[]::new);
        method.invoke(page, args);
    }

    private void updateLang(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        if (lang != null && lang.matches("[a-z]{2}")) {
            request.getSession().setAttribute("lang", request.getParameter("lang"));
        }
    }

    private Template newTemplateBySession(HttpSession session, String simpleName) throws ServletException {
        Template template;
        String lang = (String) session.getAttribute("lang");
        if (lang != null) {
            try {
                template = newTemplate(String.format("%s_%s.ftlh", simpleName, lang));
            } catch (ServletException e) {
                template = newTemplate(simpleName + ".ftlh");
            }
        } else {
            template = newTemplate(simpleName + ".ftlh");
        }
        return template;
    }

    private Template newTemplate(String templateName) throws ServletException {
        Template template = null;
        if (sourceConfiguration != null) {
            try {
                template = sourceConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }
        if (template == null && targetConfiguration != null) {
            try {
                template = targetConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }
        if (template == null) {
            throw new ServletException("Can't find template [templateName=" + templateName + "]");
        }
        return template;
    }

    private void apply(Template template, Map<String, Object> view, HttpServletResponse response) throws IOException, TemplateException {
        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        template.process(view, response.getWriter());
    }

    private static class Route {
        private final String className;
        private final String action;

        private static final Route NOT_FOUND_ROUTE = new Route(
                NotFoundPage.class.getName(),
                DEFAULT_ACTION
        );

        private static final Route INDEX_ROOT = new Route(
                IndexPage.class.getName(),
                DEFAULT_ACTION
        );

        private Route(String className, String action) {
            this.className = className;
            this.action = action;
        }

        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newNotFoundRoute() {
            return NOT_FOUND_ROUTE;
        }

        private static Route newIndexRoute() {
            return INDEX_ROOT;
        }

        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();

            List<String> classNameParts = Arrays.stream(uri.split("/"))
                    .filter(part -> !Strings.isNullOrEmpty(part))
                    .collect(Collectors.toList());

            if (classNameParts.isEmpty()) {
                return newIndexRoute();
            }

            StringBuilder simpleClassName = new StringBuilder(classNameParts.get(classNameParts.size() - 1));
            int lastDotIndex = simpleClassName.lastIndexOf(".");
            simpleClassName.setCharAt(lastDotIndex + 1,
                    Character.toUpperCase(simpleClassName.charAt(lastDotIndex + 1)));
            classNameParts.set(classNameParts.size() - 1, simpleClassName.toString());

            String className = BASE_PACKAGE + "." + String.join(".", classNameParts) + "Page";

            String action = request.getParameter("action");
            if (Strings.isNullOrEmpty(action)) {
                action = DEFAULT_ACTION;
            }

            return new Route(className, action);
        }
    }
}
