package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CaptchaFilter extends HttpFilter {

    @FunctionalInterface
    private interface Handler {
        void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;
    }

    private final Map<String, Handler> captchaHandlers = Map.of(
            "/favicon.ico", (request, response, chain) -> chain.doFilter(request, response),
            "/captcha/getSecretCode", (request, response, chain) -> {
                setSecretCode(request.getSession());
                response.sendRedirect("/captcha.html");
            },
            "/captcha.html", (request, response, chain) -> {
                HttpSession session = request.getSession();
                if (accepted(session)) {
                    response.sendRedirect("/index.html");
                    return;
                }
                chain.doFilter(request, response);
            },
            "/captcha/img", (request, response, chain) -> {
                response.setContentType("image/png");
                response.getOutputStream()
                        .write(ImageUtils.toPng((String) request.getSession().getAttribute("secret-code")));
                response.getOutputStream().flush();
            },
            "/captcha", (request, response, chain) -> {
                HttpSession session = request.getSession();
                if (accepted(session)) {
                    response.sendRedirect("/index.html");
                    return;
                }
                session.setAttribute("received-code", request.getParameter("code"));
                response.sendRedirect(accepted(session) ?
                        (String) session.getAttribute("last-request") : "/captcha/getSecretCode");
            }
    );

    private final Handler defaultHandler = (request, response, chain) -> {
        HttpSession session = request.getSession();
        if (accepted(session) || !request.getMethod().equals("GET")) {
            chain.doFilter(request, response);
            return;
        }
        session.setAttribute("last-request", request.getRequestURI());
        response.sendRedirect(secretCodeExists(session) ? "/captcha.html" : "/captcha/getSecretCode");
    };

    private void setSecretCode(HttpSession session) {
        String code = Integer.toString(ThreadLocalRandom.current().nextInt(100, 1000));
        session.setAttribute("secret-code", code);
    }

    private boolean secretCodeExists(HttpSession session) {
        return session.getAttribute("secret-code") != null;
    }

    private boolean accepted(HttpSession session) {
        return session.getAttribute("secret-code") != null &&
                session.getAttribute("secret-code").equals(session.getAttribute("received-code"));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilter(request, response, chain);
        captchaHandlers.getOrDefault(request.getRequestURI(), defaultHandler).handle(request, response, chain);
    }
}