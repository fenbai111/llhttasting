;
(function($) {
    var timer = null;
    var awardVm = avalon.define({
        $id: "award",
        list: [],
        campaignRule: '',
        campaignScroll: campaignScroll
    });

    var awardOpt = (function() {
        var opt = {
            build: function() {

                if (vote.isOver()) {
                    message.msg('活动已结束.');
                    awardVm.campaignScroll = '活动已结束';
                }

                awardVm.campaignRule = $("#campaignRule").val();
                awardVm.list = campaignAwards.campaignAwards;

                var shareUrl = "https://" + window.location.host + '/vote/' + chainId + '/home';
                vote.getWxCfg(window.location.href, function() {
                    vote.wxShareCfg({
                        title: '<' + campaignName + '>' + '发布了投票活动，等待你的支持，快去给Ta投票吧～',
                        link: shareUrl,
                        imgUrl: sponsorPic
                    }, {
                        title: '邀您投票',
                        desc: '<' + campaignName + '>' + '发布了投票活动，等待你的支持，快去给Ta投票吧～',
                        link: shareUrl,
                        imgUrl: sponsorPic,
                    });
                });
            }
        };
        return {
            build: opt.build
        }
    })();

    awardOpt.build();

})(Zepto)