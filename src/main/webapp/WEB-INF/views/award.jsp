<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <title>活动奖品</title>
                <%@ include file="./common/header.jsp" %>
            </head>

            <body ms-controller="award">
                <input id="campaignRule" value='${campaignRule}' type="hidden">
                <div class="content">
                    <div class="remind-info" ms-if="campaignScroll" style="position:inherit;">
                        <marquee scrollamount="6" scrolldelay="30" direction="left" ms-text="campaignScroll"></marquee>
                    </div>
                    <div class="gift-item">
                        <div ms-for="gift in list">
                            <div class="gift-title" ms-text="gift.summary"></div>
                            <div class="gift-content">
                                <div ms-html="gift.detail"></div>
                                <img ms-attr="{src: gift.awardpic+'?x-oss-process=style/q_60'}">
                            </div>
                        </div>

                        <div class="gift-content">
                            <span class="cl-red" ms-html="campaignRule"></span>
                        </div>
                    </div>
                </div>

                <div class="bar-tab">
                    <ul>
                        <li>
                            <a href="home">
                                <div class="tab-icon"> <span class="iconfont icon-shouye"></span> </div>
                                <div class="tab-title"> 首页 </div>
                            </a>
                        </li>
                        <li class="active">
                            <a href="javascript:;;">
                                <div class="tab-icon"> <span class="iconfont icon-jiangpin"></span> </div>
                                <div class="tab-title"> 奖品 </div>
                            </a>
                        </li>
                        <li>
                            <a href="v_rank">
                                <div class="tab-icon"> <span class="iconfont icon-tubiao-"></span> </div>
                                <div class="tab-title"> 榜单 </div>
                            </a>
                        </li>
                    </ul>
                </div>

                <%@ include file="./common/footer.jsp" %>


                    <script src="/res/page/js/award/award.js<%=CDN_VERSION%>"></script>

            </body>

            </html>