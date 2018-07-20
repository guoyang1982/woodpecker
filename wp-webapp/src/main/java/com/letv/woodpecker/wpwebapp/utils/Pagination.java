package com.letv.woodpecker.wpwebapp.utils;

import java.net.URLEncoder;
import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.codec.Charsets;

/**
 * Pagination of data access
 *
 * @author liangguorong
 * @date 2015-07-14
 */
public class Pagination {

    protected String uri = null;
    protected int pn = 1;
    protected int ps = 14;
    protected int total = 0;
    protected int pageTotal = 0;
    protected String currentPage = null;
    protected Map<String, Object> paramMap = new HashMap<String, Object>();
    protected OrderByPattern order = OrderByPattern.ASC;
    protected String orderBy = null;

    private int showPageCount = 9;

    protected String pager = null;

    public Pagination() {

    }

    public Pagination(int ps) {
        this.ps = ps;
    }

    public Pagination(int pn, int ps) {
        this.pn = pn;
        this.ps = ps;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getTotal() {
        return total;
    }

    public Pagination setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void addParam(String paramKey, Object paramValue) {

        this.paramMap.put(paramKey, paramValue);
    }

    public void removeParam(String paramKey) {

        this.paramMap.remove(paramKey);
    }

    public Object getParam(String paramKey) {
        return this.paramMap.get(paramKey);
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public int getShowPageCount() {
        return showPageCount;
    }

    public void setShowPageCount(int showPageCount) {
        this.showPageCount = showPageCount;
    }

    public OrderByPattern getOrder() {
        return order;
    }

    public void setOrder(OrderByPattern order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getPager() {
        if (this.pager == null) {
            this.pager = this.buildPage();
        }

        return pager;
    }

    public void setPager(String pager) {
        this.pager = pager;
    }

    public int getStart() {
        return (pn - 1) * this.ps;
    }

    public String getCurrentPage() {
        if (this.pager == null) {
            this.pager = this.buildPage();
        }

        return currentPage == null ? null : Utils.encode64(currentPage);
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    private String buildPage() {
        pageTotal = (int) Math.ceil(this.total * 1.0 / this.ps);

        String url = uri + "?";
        if (paramMap != null && paramMap.size() > 0) {
            Set<String> paramKeys = paramMap.keySet();
            for (String paramKey : paramKeys) {
                if ("pn".equals(paramKey) || "ps".equals(paramKey)) {
                    continue;
                }

                Object paramValue = paramMap.get(paramKey);
                if (paramValue == null || "".equals(paramValue)) {
                    continue;
                }

                String encodeValue = paramValue.toString();
                try {
                    encodeValue = URLEncoder.encode(encodeValue, Charsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    ;
                }

                url += (paramKey + "=" + encodeValue + "&");
            }
        }
        url += ("ps=" + ps + "&");

        // 开始生成分页条
        StringBuffer pagerBuffer = new StringBuffer();
        pagerBuffer.append("<ul class='pagination'>");

        if (pn == 1) {
            pagerBuffer.append("<li class='disabled'><a href='#'>首页</a></li>");
        } else if (pn > 1) {
            pagerBuffer.append("<li><a href='").append(url + "&pn=" + 1).append("'>首页</a></li>");
        }

        int showNoStart = pn - (getShowPageCount() / 2);
        showNoStart = showNoStart <= 0 ? 1 : showNoStart;
        int showNoEnd = showNoStart + getShowPageCount() - 1;
        showNoEnd = showNoEnd > pageTotal ? pageTotal : showNoEnd;
        showNoStart = showNoEnd - getShowPageCount() + 1;
        showNoStart = showNoStart <= 0 ? 1 : showNoStart;

        for (int i = showNoStart; i <= showNoEnd; i++) {
            if (pn == i) {
                // 生成当前页URL (带参数)
                currentPage = url + "pn=" + i;
            }

            pagerBuffer.append(pn == i ? "<li class='active'" : "<li").append("><a href='" + url + "pn=" + i + "'")
                    .append(">" + i + "</a></li>");
        }

        if (pn >= pageTotal) {
            pagerBuffer.append("<li class='disabled'><a href='#'>末页</a></li>");
        } else {
            pagerBuffer.append("<li><a href='").append(url).append("&pn=" + pageTotal).append("'>末页</a></li>");
        }

        pagerBuffer.append("</ul>");

        return pagerBuffer.toString();
    }

    public static Pagination duplicate(Pagination pgn) {
        Pagination pagination = new Pagination();
        pagination.setUri(pgn.getUri());
        pagination.setPn(pgn.getPn());
        pagination.setPs(pgn.getPs());
        pagination.setOrder(pgn.getOrder());
        pagination.setOrderBy(pgn.getOrderBy());
        pagination.setTotal(0);
        return pagination;
    }

    public static Pagination create(int pn, int ps) {
        Pagination p = new Pagination();
        p.setPn(pn);
        p.setPs(ps);
        return p;
    }

}
