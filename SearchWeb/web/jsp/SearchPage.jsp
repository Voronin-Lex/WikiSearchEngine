<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: lex
  Date: 07.03.17
  Time: 20:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search.WikiSearch</title>
    <style>
        body {
            background-color: burlywood;
        }

        #mainForm {
            text-align: center;
            margin-top: 10%

        }
        #tfIdf{
            margin-left: 107px;
        }

        #head{
            margin-left: 80px;
        }

        #quote{
            margin-left: 5px;
        }

        #resultInfo {
            text-align: center
        }

    </style>
</head>
<body>
<div id="mainForm">
    <form action="../search" method="get">
        <input type="text" size="90" name="query">
        <input type="submit" value="Найти"> <br>
        <div id="searchType">
            <input id="boolean" type="radio" name="search" value="boolean">    Булев поиск<br>
            <input id="tfIdf" type="radio" name="search" value="TF_IDF">Поиск с ранжированием<br>
            <input id="head" type="radio" name="search" value="Header">Поиск по заголовкам<br>
            <input id="quote" type="radio" name="search" value="Quote">Поиск цитат<br>
        </div>
    </form>
</div>
<div id="resultInfo">
    <c:set var="articles" value='${requestScope["answer"]}'/>
    <c:set var="docCount" value='${requestScope["docCount"]}'/>
    <c:choose>

        <c:when test="${articles==null}">
            <p>Давайте что нибудь поищем...</p>
        </c:when>


        <c:when test="${articles.size()>0}">
            Найдено:<c:out value="${docCount}"/>
            Показано:<c:out value="${articles.size()}"/>
        </c:when>

        <c:otherwise>
            <p>ничего не найдено:(</p>
        </c:otherwise>
    </c:choose>

    <table border="0">
        <c:set var="count" value="0" scope="page"/>
        <c:forEach items="${articles}" var="article">
            <c:set var="count" value="${count+1}" scope="page"/>
            <tr>
                <td>${count}.</td>
                <td><a href=https://ru.wikipedia.org/wiki?curid=${article.getKey()}>${article.getValue()}</a></td>
            </tr>
        </c:forEach>
    </table>
</div>

<div id="resultDocs">

</div>

</body>
</html>
