 ;
 (function($) {
     var _timer = null;
     var indexVm = avalon.define({
         $id: "vote",
         top: {
             signCount: 0,
             viewCount: 0,
             voteCount: 0,
             sponsorPic: ''
         },
         time: {
             days: "00",
             hours: "00",
             minutes: "00",
             seconds: "00",
             text: '活动时间倒计时'
         },
         bottom: {
             sponsorIntro: '',
             sponsorPicUrls: [],
         },
         pagecfg: {
             pageNo: 1,
             pageSize: 10
         },
         queryKey: '',
         userList: [],
         isShowMore: false,
         isLoadImg: false,
         campaignScroll: campaignScroll,
         methods: {
             more: function() {
                 indexOpt.more();
             },
             rearch: function() {
                 indexOpt.rearch();
             },
             rendered: function() {
                 $("#masonry").show();
                 indexVm.isLoadImg = true;

                 vote.loading.hide();
                 setTimeout(function() {
                     imagesLoaded('#masonry', function() {
                         var msnry = new Masonry('#masonry', {
                             itemSelector: '.item',
                             columnWidth: 0
                         });
                     });
                     vote.loading.hide();
                 }, 800);
             }
         }
     });

     var indexOpt = (function() {
         var opt = {
             queryUsers: function() {
                 vote.loading.show();
                 var param = {
                     pageNo: indexVm.pagecfg.pageNo,
                     pageSize: indexVm.pagecfg.pageSize
                 }
                 indexVm.queryKey && (param.queryKey = indexVm.queryKey);

                 vote.jqAjax('users', param, function(data) {
                     if (indexVm.pagecfg.pageNo == 1) {
                         indexVm.userList.length > 0 && (indexVm.userList = []);
                     }
                     indexVm.pagecfg.pageNo++;
                     if (data.data.list.length === 0) {
                         indexVm.pagecfg.pageNo--;
                     }
                     indexVm.userList = indexVm.userList.concat(data.data.list);
                     // !(res.data.totalCount == rankVm.list.length);
                     indexVm.isShowMore = !(indexVm.userList.length == data.data.totalCount);
                     if (data.data.list.length < indexVm.pagecfg.pageSize) {
                         indexVm.isShowMore = false;
                     }
                 }, function(err) {
                     console.log(err)
                     vote.loading.hide();
                 }, 'GET', true, true);
             },
             timer: {
                 creat: function() {
                     if (!vote.isOver()) {
                         _timer = window.setInterval(function() {
                             !vote.isOver() && vote.endTimeLoop(indexVm);
                         }, 1000);
                     } else {
                         if (_timer) {
                             window.clearInterval(_timer);
                         }
                     }
                 }
             },
             queryVoteDetail: function() {
                 vote.jqAjax('detail', '', function(res) {
                     var data = res.data;
                     indexVm.top.signCount = data.signCount || 0;
                     indexVm.top.viewCount = data.viewCount || 0;
                     indexVm.top.voteCount = data.voteCount || 0;
                     indexVm.top.sponsorPic = data.sponsorPic || '';

                     indexVm.bottom.sponsorIntro = data.sponsorIntro || '';
                     indexVm.bottom.sponsorPicUrls = data.sponsorPicUrls || [];

                 }, function(err) {}, 'GET', false, true);
             },
             build: function() {
                 indexVm.userList = [];
                 var shareUrl = window.location.href;
                 var fromIndex = shareUrl.indexOf('?from');
                 if (fromIndex > 0) {
                     shareUrl = shareUrl.substr(0, fromIndex);
                     window.location.href = shareUrl;
                 } else {
                     if (vote.isOver()) {
                         indexVm.time.text = '活动已结束';
                         message.msg('活动已结束.');
                         indexVm.campaignScroll = '活动已结束';
                     }

                     opt.queryVoteDetail();

                     opt.timer.creat();

                     indexVm.pagecfg.pageNo = 1;
                     opt.queryUsers();

                     vote.getWxCfg(shareUrl, function() {
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
             }
         };
         return {
             build: opt.build,
             more: function() {
                 opt.queryUsers();
             },
             rearch: function() {
                 indexVm.pagecfg.pageNo = 1;
                 opt.queryUsers();
             }
         }
     })();

     indexOpt.build();

 })(Zepto)