package com.kk.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {

    void updataViewCount(@Param("id") Long id);

    void updataVoteCount(@Param("id")Long id);
}