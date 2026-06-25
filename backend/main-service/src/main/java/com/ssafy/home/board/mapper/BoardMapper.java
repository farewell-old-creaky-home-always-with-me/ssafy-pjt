package com.ssafy.home.board.mapper;

import com.ssafy.home.board.mapper.dto.BoardCreateParam;
import com.ssafy.home.board.mapper.dto.BoardResult;
import com.ssafy.home.board.mapper.dto.BoardUpdateParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardMapper {

    long countAll();

    List<BoardResult> findAll(@Param("offset") int offset, @Param("size") int size);

    BoardResult findById(@Param("boardId") Long boardId);

    void insert(BoardCreateParam board);

    int updateById(BoardUpdateParam board);

    int deleteById(@Param("boardId") Long boardId);
}
