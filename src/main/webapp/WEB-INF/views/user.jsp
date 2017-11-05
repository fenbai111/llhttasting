<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

                <title>个人中心</title>

                <%@ include file="./common/header.jsp" %>

            </head>

            <body ms-controller="user">
                <div class="content">
                    <div class="remind-info" ms-if="campaignScroll" style="position:inherit;">
                        <marquee scrollamount="6" scrolldelay="30" direction="left" ms-text="campaignScroll"></marquee>
                    </div>
                    <div class="show-info">
                        <div class="user-info">
                            <span class="icon-box "> 
                                <img ms-attr="{src:top.headPic +'?x-oss-process=style/q_60'}" class="img-circle" style="width:30px;">
                            </span>
                            <div class="inner">
                                <div class="inner-title" ms-text="top.name"> </div>
                                <div class="inner-content" ms-text="top.declaration"></div>
                            </div>
                        </div>
                        <ul>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-geren"></span>编号 </div>
                                <div class="show-info-num" ms-text="top.number"> </div>
                            </li>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-like"></span>票数 </div>
                                <div class="show-info-num" ms-text="top.voteCount"></div>
                            </li>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-fangwenliang"></span>访问量 </div>
                                <div class="show-info-num" ms-text="top.viewCount"></div>
                            </li>
                            <li>
                                <div class="show-info-title"> <span class="iconfont icon-jiangpin"></span>礼物 </div>
                                <div class="show-info-num" ms-text="top.giftPoint"> </div>
                            </li>
                        </ul>
                    </div>
                    <div class="show-img">
                        <article>
                            <div class="scroll relative">
                                <div class="scroll_box" id="scroll_img">
                                    <ul class="scroll_wrap">
                                        <li ms-for="(index,item) in top.userPicVos">
                                            <a href="javascript:;;"><img ms-attr="{src: item.picUrl +'?x-oss-process=style/q_60'}" /></a>
                                        </li>
                                    </ul>
                                </div>
                                <ul class="scroll_position" id="scroll_position">
                                    <li ms-class="{on:index==0}" ms-for="(index,item) in top.userPicVos">
                                        <a href="javascript:void(0);" ms-text="index + 1"></a>
                                    </li>
                                </ul>
                            </div>
                        </article>
                    </div>
                    <div class="gift-list">
                        <div class="gift-list-title">
                            <span class="iconfont icon-jiangpin"></span>礼物列表
                        </div>
                        <div class="gift-list-content">
                            <ul>
                                <li ms-for="item in giftList">
                                    <a href="javascript:;;" class="b-b-line"> <span class="icon-box "> 
                                        <img ms-attr="{src: @item.headImgUrl +'?x-oss-process=style/q_60'}"> </span>
                                        <div class="inner">
                                            <div class="inner-title" ms-text="item|titleFilter"></div>
                                            <div class="inner-content" ms-html="item.voteTimeStr"></div>
                                        </div>
                                    </a>
                                </li>
                                <li ms-if="isMore">
                                    <button type="button" class="btn btn-default btn-block" ms-click="methods.more()">加载更多</button>
                                </li>
                                <li ms-if="giftList.length==0">
                                    <div class="empty-box">
                                        <div class="empty-img"><img ms-attr="{src:'/res/page/img/no_records.png'}"></div>
                                        <div class="empty-title">
                                            Ta还有获得过礼物，快去给Ta送个礼物吧！
                                        </div>
                                        <a ms-attr="{href: '/vote/pay/v_pay?chainId=${chainId}&userId='+@userId}" ms-if="!isover">
                                            <div class="empty-btn">挑选礼物</div>
                                        </a>
                                        <a href="javascript:;;" ms-if="isover" onclick="message.msg('活动已结束.')">
                                            <div class="empty-btn">挑选礼物</div>
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="bottom-bar">
                    <a href="../home" class="back"><span class="iconfont icon-fanhui"></span>首页</a>
                    <a class="top-bar-title" ms-click="methods.send()" ms-if="!isover" href="javascript:;;" style="cursor:pointer;">
                        <span class="btn-vote"><i class="iconfont icon-like"></i><br>投票</span>
                    </a>
                    <div class="top-bar-title" ms-if="isover" onclick="message.msg('活动已结束.')">
                        <span class="btn-vote"><i class="iconfont icon-like"></i><br>投票</span>
                    </div>
                    <div class="pull-right" ms-if="!isover">
                        <a ms-attr="{href: '/vote/pay/v_pay?chainId=${chainId}&userId='+@userId}" class="gift"><span class="iconfont icon-jiangpin"></span>礼物</a>
                    </div>
                    <div class="pull-right" ms-if="isover">
                        <a href="javascript:;;" onclick="message.msg('活动已结束.')" class="gift"><span class="iconfont icon-jiangpin"></span>礼物</a>
                    </div>
                </div>

                <div class="modal-box" id="divResultModal" style="display:none;">
                    <div class="modal">
                        <div class="modal-body">
                            <div class="modal-img">
                                <a ms-attr="{href: '/vote/pay/v_pay?chainId=${chainId}&userId='+@userId}">
                                    <img src="/res/page/img/send-gift.gif">
                                </a>
                            </div>
                            <div class="title" ms-text="modal.title"></div>
                            <div class="intro" ms-text="modal.content"></div>
                        </div>
                        <div class="modal-footer">
                            <ul style="font-size: 1.4rem;">
                                <li><a href="javascript:;;" ms-click="methods.close()">关闭</a></li>
                                <li><a ms-attr="{href: '/vote/pay/v_pay?chainId=${chainId}&userId='+@userId}">赠送礼物</a></li>
                            </ul>
                        </div>
                    </div>
                </div>

                <%@ include file="./common/footer.jsp" %>

                    <script src="/res/page/js/user/user.js<%=CDN_VERSION%>"></script>

            </body>

            </html>