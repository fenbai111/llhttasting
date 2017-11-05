<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <title>系统错误</title>
                <%@ include file="./common/header.jsp" %>
            </head>

            <body>
                <div class="empty-box">
                    <div class="empty-img"><img src="/res/page/img/error.png"></div>
                    <div class="empty-title">${errorMessage}</div>
                </div>
            </body>

            </html>