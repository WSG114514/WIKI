package com.kk.wiki.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class EbookQueryReq extends PageReq{
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EbookReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}