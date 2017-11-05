      var vote = (function() {
          var opt = {
              getQueryString: function(param) {
                  var query = window.location.search;
                  var iLen = param.length;
                  var iStart = query.indexOf(param);
                  if (iStart == -1)
                      return "";
                  iStart += iLen + 1;
                  var iEnd = query.indexOf("&", iStart);
                  if (iEnd == -1)
                      return query.substring(iStart);
                  return query.substring(iStart, iEnd);
              },
              loading: {
                  show: function() { document.getElementById('divLoading').style.display = ''; },
                  hide: function() { document.getElementById('divLoading').style.display = 'none'; }
              },
              endTimeLoop: function(indexVm) {
                  if (opt.isOver()) {
                      indexVm.time.days = '00';
                      indexVm.time.hours = '00';
                      indexVm.time.minutes = '00';
                      indexVm.time.seconds = '00';
                      message.msg('活动已结束.')
                  } else {
                      var startTime = new Date();
                      var endTime = new Date(campaignEndTime);
                      var millisecond = (endTime - startTime);

                      var days = millisecond / 1000 / 60 / 60 / 24;
                      var daysRound = Math.floor(days);
                      var hours = millisecond / 1000 / 60 / 60 - (24 * daysRound);
                      var hoursRound = Math.floor(hours);
                      var minutes = millisecond / 1000 / 60 - (24 * 60 * daysRound) - (60 * hoursRound);
                      var minutesRound = Math.floor(minutes);
                      var seconds = millisecond / 1000 - (24 * 60 * 60 * daysRound) - (60 * 60 * hoursRound) - (60 * minutesRound);
                      var secondsRound = Math.floor(seconds);

                      indexVm.time.days = ('0' + daysRound).slice(-2);
                      indexVm.time.hours = ('0' + hoursRound).slice(-2);
                      indexVm.time.minutes = ('0' + minutesRound).slice(-2);
                      indexVm.time.seconds = ('0' + secondsRound).slice(-2);
                  }
              },
              isOver: function() {
                  return new Date().isAfter(new Date(campaignEndTime));
              },
              jqAjax: function(url, data, success, error, type, flage, noCache) {
                  if (!url) {
                      message.msg("ajax请求未发现请求地址.");
                      error && error();
                  } else {
                      var obj = {
                          type: (type || 'POST'),
                          url: url,
                          dataType: 'json',
                          async: true,
                          timeout: 0,
                          beforeSend: function(XMLHttpRequest) {},
                          complete: function() {},
                          success: function(json) {
                              if (json.status) {
                                  success && success(json);
                              } else {
                                  message.msg(json.message || json.msg);
                                  error && error(json);
                              }
                          },
                          error: function(jqXHR, textStatus, errorThrown) {
                              if (error) {
                                  error(jqXHR);
                              }
                              try {
                                  switch (jqXHR.status) {
                                      case 401:
                                          {
                                              var urls = [];
                                              urls.push('https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + appId + '&redirect_uri=');
                                              urls.push(encodeURIComponent('https://wx.jilunxing.cn/vote/auth/callback'));
                                              urls.push('&response_type=code&scope=snsapi_userinfo&state=' + encodeURIComponent(window.location.href) + '#wechat_redirect');
                                              window.location.href = urls.join('');
                                          }
                                          break;
                                      case 403:
                                          {
                                              window.location.href = '/vote/v_info';
                                          }
                                          break;
                                      default:
                                          {
                                              var xhr = jqXHR;
                                              if (xhr && xhr.responseText) {
                                                  var _json = JSON.parse(xhr.responseText);
                                                  if (!_json.res) {
                                                      _json.msg && message.msg(_json.msg);
                                                  } else {
                                                      message.msg(xhr.responseText);
                                                  }
                                              } else {
                                                  message.msg("后端错误.");
                                              }
                                          }
                                          break;

                                  }
                              } catch (ex) {
                                  message.msg("后端错误.");
                              }
                          }
                      };
                      if (noCache) {
                          obj.headers = {
                              "Cache-Control": "max-age=0",
                              "Pragma": "no-cache"
                          }
                          obj.cache = false;
                      }
                      if (flage) {
                          if (data) {
                              obj.data = data;
                          }
                      } else {
                          if ((typeof data) == 'object') {
                              obj.contentType = "application/json;charset=utf-8";
                              if (data) {
                                  obj.data = JSON.stringify(data);
                              }
                          } else if ((typeof data) == 'string') {
                              if (data) {
                                  obj.data = data;
                              }
                          }
                      }
                      $.ajax(obj);
                  }
              },
              wxShareCfg: function(onMenuShareTimeline, onMenuShareAppMessage) {
                  wx.onMenuShareTimeline({ //分享到朋友圈
                      title: onMenuShareTimeline.title, // 分享标题
                      link: onMenuShareTimeline.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                      imgUrl: onMenuShareTimeline.imgUrl, // 分享图标
                      success: function() {},
                      cancel: function() {}
                  });

                  wx.onMenuShareAppMessage({ //分享给朋友
                      title: onMenuShareAppMessage.title, // 分享标题
                      desc: onMenuShareAppMessage.desc, // 分享描述
                      link: onMenuShareAppMessage.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                      imgUrl: onMenuShareAppMessage.imgUrl, // 分享图标
                      success: function() {},
                      cancel: function() {}
                  });
              },
              getWxCfg: function(currentUrl, callback) {
                  var data = jsAPISignature;
                  if (data) {
                      wx.config({
                          debug: false,
                          appId: data.appId,
                          timestamp: Number(data.timestamp),
                          nonceStr: data.nonce,
                          signature: data.signature,
                          jsApiList: [
                              'onMenuShareTimeline',
                              'onMenuShareAppMessage',
                              'previewImage',
                          ]
                      });
                      wx.ready(function() {
                          callback && callback();
                      });
                      wx.error(function(res) {
                          console.error('err', res)
                      });
                  }
              },
              getWxCfgAjax: function(currentUrl, callback) {
                  var index = currentUrl.indexOf('#');
                  currentUrl = index > 0 ? currentUrl.substr(0, index) : currentUrl;
                  vote.jqAjax('/vote/' + chainId + '/jssdk_config', 'currentUrl=' + encodeURIComponent(currentUrl), function(json) {
                      var data = json.data;
                      if (data) {
                          wx.config({
                              debug: false,
                              appId: data.appId,
                              timestamp: Number(data.timestamp),
                              nonceStr: data.nonce,
                              signature: data.signature,
                              jsApiList: [
                                  'onMenuShareTimeline',
                                  'onMenuShareAppMessage',
                                  'previewImage',
                              ]
                          });
                          wx.ready(function() {
                              callback && callback();
                          });
                          wx.error(function(res) {
                              console.error('err', res)
                          });
                      }
                  }, function() {}, 'GET', '');
              }
          }

          return {
              loading: opt.loading,
              getQueryString: opt.getQueryString,
              endTimeLoop: opt.endTimeLoop,
              isOver: opt.isOver,
              jqAjax: opt.jqAjax,
              wxShareCfg: opt.wxShareCfg,
              getWxCfg: opt.getWxCfg
          }
      })();

      var message = (function() {
          var timer = null;
          var id = "$messageId";
          var cssId = "$cssId";
          var styleContent = ".prompt{width:50%;position:fixed;left:25%;height:auto;background:#000;border-radius:5px;top:200px;text-align:center;color:#fff;padding:15px;box-shadow:1px 1px 5px rgba(0,0,0,.5);z-index:99999}@media(min-width:992px){.prompt{top:20px}}";
          var createStyleElement = function() {
              if (isExist(cssId)) {
                  return false
              }
              var style = document.createElement("style");
              style.type = "text/css";
              style.id = cssId;
              style.innerHTML = styleContent;
              document.getElementsByTagName("HEAD").item(0).appendChild(style)
          };
          var createMsgElement = function(msg, isClose) {
              if (isExist(id)) {
                  clearTimeOut()
              }
              remove();
              var div = document.createElement("div");
              div.id = id;
              div.className = "prompt";
              div.innerHTML = msg;
              document.body.appendChild(div);
              if (!isClose) {
                  autoClose()
              }
          };
          var isExist = function(_id) {
              return document.getElementById(_id) ? true : false
          };
          var remove = function() {
              var node = document.getElementById(id);
              if (node) document.body.removeChild(node)
          };
          var clearTimeOut = function() {
              if (timer) window.clearTimeout(timer)
          };
          var autoClose = function() {
              timer = setTimeout(function() {
                  clearTimeOut();
                  remove()
              }, 3000)
          };
          createStyleElement();
          return {
              msg: function(msg, isClose) {
                  createMsgElement(msg, isClose)
              },
              remove: function() {
                  if (isExist(id)) {
                      clearTimeOut()
                  }
                  remove()
              }
          }
      })();

      // 对Date的扩展，将 Date 转化为指定格式的String
      // 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
      // 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
      // 例子： 
      // (new Date()).Format("yyyy-MM-dd HH:mm:ss.S") ==> 2006-07-02 08:09:04.423 
      // (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
      Date.prototype.Format = function(fmt) {
          var o = {
              "M+": this.getMonth() + 1, //月份 
              "d+": this.getDate(), //日 
              "H+": this.getHours(), //小时 
              "m+": this.getMinutes(), //分 
              "s+": this.getSeconds(), //秒 
              "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
              "S": this.getMilliseconds() //毫秒 
          };
          if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
          for (var k in o)
              if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
          return fmt;
      }

      Date.prototype.isAfter = function(checkDate) {
          return this > checkDate;
      }