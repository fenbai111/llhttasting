<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="zh-CN">

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <title>购买礼物</title>
                <%@ include file="./common/header.jsp" %>
            </head>

            <body ms-controller="pay">
                <div class="content">
                    <div class="remind-info" ms-if="campaignScroll" style="position:inherit;">
                        <marquee scrollamount="6" scrolldelay="30" direction="left" ms-text="campaignScroll"></marquee>
                    </div>
                    <div class="gift-list">
                        <div class="gift-list-content">
                            <ul>
                                <li>
                                    <a href="javascript:;;" class="b-b-line">
                                        <span class="icon-box"> 
                                            <img  class="user-img" ms-attr="{src:top.headPic+'?x-oss-process=style/q_60'}"> 
                                        </span>
                                        <div class="inner">
                                            <div class="inner-title" ms-text="top.name"></div>
                                            <div class="inner-content">给Ta送上一份礼物吧</div>
                                        </div>
                                        <div class=""><span class="iconfont icon-jiantou"></span> </div>
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="show-info">
                        <ul>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-geren"></span>编号 </div>
                                <div class="show-info-num" ms-text="top.number"></div>
                            </li>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-like"></span>票数 </div>
                                <div class="show-info-num" ms-text="top.voteCount"></div>
                            </li>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-fangwenliang"></span>热度 </div>
                                <div class="show-info-num" ms-text="top.viewCount"></div>
                            </li>
                        </ul>
                    </div>
                    <div class="buy-list">
                        <ul>
                            <li class="gift-li-cls" style="cursor: pointer;" ms-class="{active:item.giftId==giftId}" ms-for="($index,item) in giftList" ms-click="methods.itemClick($index)">
                                <div class="product-img"><img ms-attr="{src:item.giftpic +'?x-oss-process=style/q_60'}"> </div>
                                <div class="product-title" ms-text="item.giftName"></div>
                                <div class="product-price" ms-if="item.giftId!=giftId">
                                    <span class="cl-red" ms-text="item.giftPoint + '点'"></span>
                                </div>
                                <div class="product-price" ms-if="item.giftId==giftId" ms-text="item.giftPoint + '点抵' + item.voteCount +'票' ">
                                    <!-- {{item.giftPoint}}点抵<span class="cl-red" ms-text="item.voteCount"></span>票 -->
                                </div>
                            </li>
                        </ul>
                        <div class="product-intro">
                            <span ms-text="info"></span>
                            <div class="pull-right">数量：
                                <select ms-duplex="amount">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="input-list">
                        <ul>
                            <li>
                                <div class="input-label">留言</div>
                                <div class="input-box">
                                    <input ms-duplex="remark" class="form-control1" placeholder="想对Ta说些什么吗？" maxlength="140">
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div style="cursor:pointer;" ms-click="methods.pay()" ms-if="!isover" class="pay-btn">去支付</div>
                <div ms-if="isover" onclick="message.msg('活动已结束.')" class="pay-btn" style="cursor:pointer; ">去支付</div>


                <%@ include file="./common/footer.jsp" %>
                    <script src="/res/page/js/pay/pay.js<%=CDN_VERSION%>"></script>

            </body>

            </html>