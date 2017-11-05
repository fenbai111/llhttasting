<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <div class="loading-bg" id="divLoading" style="display:none;">
        <div class="loading">
            <div style="text-align: center;">
                <div class="v-pulse v-pulse1" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 1.25s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.12s;">
                </div>
                <div class="v-pulse v-pulse2" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 1.35s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.24s;">
                </div>
                <div class="v-pulse v-pulse3" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 1.45s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.36s;">
                </div>
            </div>
        </div>
    </div>

    <!-- build:lib -->
    <script src="/res/lib/js/avalon.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/zepto.min.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/imagesloaded.pkgd.min.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/masonry.pkgd.min.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/jweixin-1.0.0.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/swipe.js<%=CDN_VERSION%>"></script>
    <script src="/res/lib/js/vote_helper.js<%=CDN_VERSION%>"></script>
    <!-- endbuild -->