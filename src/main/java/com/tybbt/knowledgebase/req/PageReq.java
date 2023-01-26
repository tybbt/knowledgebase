package com.tybbt.knowledgebase.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class PageReq {
    @NotNull(message = "[每条页码]不能为空")
    private int page;
    @NotNull(message = "[每条页数]不能为空")
    @Max(value = 1000, message = "【每条页数】不能超过1000")
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageReq{");
        sb.append("page=").append(page);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
