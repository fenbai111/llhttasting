package com.jlt.vote.bis.wx.sdk.common.message.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.jlt.vote.bis.wx.sdk.common.message.MsgType;
import com.jlt.vote.bis.wx.sdk.common.message.News;
import com.jlt.vote.bis.wx.sdk.common.message.XmlMessageHeader;

import java.util.Date;

/**
 * Created by exizhai on 9/26/2015.
 */
@JacksonXmlRootElement(localName = "xml")
public class NewsXmlMessage extends XmlMessageHeader {

    @JsonProperty("ArticleCount")
    private int articleCount;
    @JsonProperty("Articles")
    private News news;

    public NewsXmlMessage() {
        this.msgType = MsgType.news;
        this.setCreateTime(new Date());
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public int getArticleCount() {
        return news.getArticles().size();
    }
}
