;
(function($) {
    var payVm = avalon.define({
        $id: "pay",
        top: {
            signCount: 0,
            viewCount: 0,
            voteCount: 0,
            sponsorPic: ''
        },
        giftList: [],
        giftId: 0,
        amount: 1,
        curIndex: -1,
        isover: false, //是否结束
        campaignScroll: campaignScroll,
        remark: '',
        info: '',
        methods: {
            itemClick: function(index) {
                try {
                    payVm.curIndex = index;
                    var cur = payVm.giftList[index];
                    if (cur) {
                        payVm.giftId = cur.giftId;
                    }
                    payVm.info = "单价" + cur.giftPoint + '元,抵' + cur.voteCount + '票';
                } catch (error) {
                    message.msg(error.message);
                }
            },
            pay: function() {
                if (payVm.curIndex >= 0) {
                    var cur = payVm.giftList[payVm.curIndex];
                    if (cur) {
                        vote.loading.show();
                        var param = {
                            chainId: chainId,
                            userId: userDetail.userId,
                            giftId: cur.giftId,
                            giftCount: payVm.amount,
                            openid: openId
                        }
                        var remark = $.trim(payVm.remark);
                        if (remark) {
                            param.remark = remark;
                        }
                        vote.jqAjax('/vote/' + chainId + '/pay/prepay', param, function(res) {
                            if (res.status) {
                                var item = res.data;
                                var payResult = JSON.parse(item.payResult);
                                var _appid = payResult.appId;
                                var _timeStamp = payResult.timeStamp;
                                var _nonceStr = payResult.nonceStr;
                                var _package = payResult.package;
                                var _signType = payResult.signType;
                                var _paySign = payResult.paySign;

                                WeixinJSBridge.invoke('getBrandWCPayRequest', {
                                        "appId": _appid,
                                        "timeStamp": _timeStamp,
                                        "nonceStr": _nonceStr,
                                        "package": _package,
                                        "signType": _signType,
                                        "paySign": _paySign
                                    },
                                    function(res) {
                                        vote.loading.hide();
                                        if (res.err_msg == "get_brand_wcpay_request:ok") {
                                            message.msg('支付成功');
                                            location.reload();
                                        } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                                            message.msg("交易已取消");
                                            orderId = -1;
                                        } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                                            message.msg("支付失败");
                                            orderId = -1;
                                        } else {
                                            message.msg(res.err_msg || res.errMsg);
                                            orderId = -1;
                                        }
                                    });
                            } else {
                                message.msg(data.msg);
                                vote.loading.hide();
                            }
                        }, function(err) {
                            vote.loading.hide();
                        }, 'POST', false);
                    }
                } else {
                    message.msg('请选择要购买的礼物.');
                }
            }
        }
    });

    var payOpt = (function() {
        var opt = {
            queryGiftList: function() {
                vote.loading.show();
                vote.jqAjax('/vote/' + chainId + '/gift', '', function(res) {
                    if (res.status) {
                        payVm.giftList = res.data;
                    } else {
                        message.msg(res.msg);
                    }
                    vote.loading.hide();
                }, function(err) { vote.loading.hide(); }, 'GET', false);
            },
            build: function() {
                if (vote.isOver()) {
                    payVm.campaignScroll = '活动已结束';
                    message.msg('活动已结束.');
                }
                payVm.isover = vote.isOver();
                payVm.top = userDetail;
                opt.queryGiftList();
                var shareUrl = "https://" + window.location.host + '/vote/' + chainId + '/v_user/' + userDetail.userId;
                vote.getWxCfg(window.location.href, function() {
                    vote.wxShareCfg({
                        title: '<' + userDetail.name + '>参加了' + campaignName + '投票活动，等待你的支持，快去给Ta投票吧～',
                        link: shareUrl,
                        imgUrl: userDetail.headPic
                    }, {
                        title: '投Ta一票',
                        desc: '<' + userDetail.name + '>参加了' + campaignName + '投票活动，等待你的支持，快去给Ta投票吧～',
                        link: shareUrl,
                        imgUrl: userDetail.headPic,
                    });
                });
            }
        };
        return {
            build: opt.build,
        }
    })();

    payOpt.build();
})(Zepto)