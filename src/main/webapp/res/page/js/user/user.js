;
(function($) {
    var userVm = avalon.define({
        $id: "user",
        top: {},
        pagecfg: {
            pageNo: 1,
            pageSize: 10
        },
        userId: userDetail.userId,
        giftList: [],
        isover: false, //是否结束
        isMore: false, //是否加载更多
        campaignScroll: campaignScroll,
        modal: {
            title: '投票成功',
            content: '恭喜您为支持的Ta贡献了一票~',
        },
        methods: {
            send: function() {
                userOpt.send();
            },
            more: function() {
                userOpt.queryGifts();
            },
            close: function() {
                userOpt.modal.hide();
            }
        }
    });
    avalon.filters.titleFilter = function(item) {
        return item.nickName + "，给TA送了" + item.giftCount + "份" + item.giftName + '！';
    };
    //未达到 10条  隐藏 加载更多
    var userOpt = (function() {
        var opt = {
            modal: {
                show: function() { document.getElementById('divResultModal').style.display = ''; },
                hide: function() { document.getElementById('divResultModal').style.display = 'none'; }
            },
            queryGifts: function() {
                vote.loading.show();
                var param = {
                    pageNo: userVm.pagecfg.pageNo,
                    pageSize: userVm.pagecfg.pageSize,
                    userId: userDetail.userId
                }
                if (userVm.pagecfg.pageNo > 1) {
                    param.voteTimestamp = userVm.giftList[userVm.giftList.length - 1].voteTimestamp;
                }
                vote.jqAjax('../gifts', param, function(res) {

                    if (userVm.pagecfg.pageNo == 1) {
                        userVm.giftList.length > 0 && (userVm.giftList = []);
                    }
                    userVm.pagecfg.pageNo++;
                    if (res.data.list.length === 0) {
                        userVm.pagecfg.pageNo--;
                    }


                    var list = res.data.list;
                    var len = list.length;
                    for (var i = 0; i < len; i++) {
                        var item = list[i];
                        var st = new Date(item.voteTime).Format('yyyy-MM-dd HH:mm');
                        item.voteTimeStr = item.remark ? ("留言: " + item.remark + '<br/>' + st) : st;
                    }
                    userVm.giftList = userVm.giftList.concat(res.data.list);

                    userVm.isMore = !(res.data.totalCount == userVm.giftList.length);
                    if (res.data.list.length < userVm.pagecfg.pageSize) {
                        userVm.isMore = false;
                    }
                    vote.loading.hide();
                }, function(err) {
                    console.log(err)
                    vote.loading.hide();
                }, 'GET', true, true);
            },
            send: function() {
                vote.loading.show();
                vote.jqAjax('../common_vote', 'userId=' + userDetail.userId, function(res) {
                    var key = Number(res.data.result);
                    switch (key) {
                        case 0:
                            {
                                userVm.modal.title = '投票失败';
                                userVm.modal.content = '你今天投票次数已用完,请明天继续投票吧';
                                opt.modal.show();
                                opt.queryUserDetail();
                            }
                            break;
                        case 1:
                            {
                                userVm.modal.title = '投票失败';
                                userVm.modal.content = '你已给ta投票了,请明天继续投票吧';
                                opt.modal.show();
                                opt.queryUserDetail();
                            }
                            break;
                        case 2:
                            {
                                userVm.modal.title = '投票成功';
                                userVm.modal.content = '恭喜您为支持的Ta贡献了一票~';
                                opt.modal.show();
                                opt.queryUserDetail();
                            }
                            break;
                        case -1:
                            {
                                message.msg('活动已结束.');
                            }
                            break;
                    }
                    vote.loading.hide();
                }, function(err) {
                    console.log(err)
                    vote.loading.hide();
                }, 'POST', false);
            },
            queryUserDetail: function() {
                vote.jqAjax('../user/' + userDetail.userId, '', function(res) {
                    var data = res.data;
                    userVm.top.voteCount = data.voteCount || 0;
                    userVm.top.viewCount = data.viewCount || 0;
                    userVm.top.giftPoint = data.giftPoint || 0;
                }, function(err) {}, 'GET', false, true);
            },
            build: function() {
                var shareUrl = window.location.href;
                var fromIndex = shareUrl.indexOf('&from');
                if (fromIndex > 0) {
                    shareUrl = shareUrl.substr(0, fromIndex);
                    window.location.href = shareUrl;
                } else {
                    if (vote.isOver()) {
                        message.msg('活动已结束.');
                        userVm.campaignScroll = '活动已结束';
                    }
                    userVm.isover = vote.isOver();
                    userVm.top = userDetail;
                    userVm.pagecfg.pageNo = 1;
                    opt.queryGifts();

                    vote.getWxCfg(shareUrl, function() {
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

                        var bullets = document.getElementById('scroll_position').getElementsByTagName('li');
                        var slider = Swipe(document.getElementById('scroll_img'), {
                            auto: 3000,
                            continuous: true,
                            callback: function(pos) {
                                $('#scroll_position li').removeClass('on');
                                if (bullets[pos]) {
                                    $(bullets[pos]).addClass('on');
                                } else {
                                    $(bullets[0]).addClass('on');
                                }
                            }
                        });
                        $(function() {
                            $('.scroll_position_bg').css({
                                width: $('#scroll_position').width()
                            });
                        });

                    });
                    opt.queryUserDetail();
                }
            }
        };
        return {
            build: opt.build,
            queryGifts: opt.queryGifts,
            send: opt.send,
            modal: opt.modal,
        }
    })();

    userOpt.build();
})(Zepto)