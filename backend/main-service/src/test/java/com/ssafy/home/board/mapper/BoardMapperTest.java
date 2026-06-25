package com.ssafy.home.board.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.board.mapper.dto.BoardCreateParam;
import com.ssafy.home.board.mapper.dto.BoardResult;
import com.ssafy.home.board.mapper.dto.BoardUpdateParam;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/board-data.sql")
class BoardMapperTest {

    @Autowired
    private BoardMapper boardMapper;

    @Test
    @DisplayName("게시글 개수를 조회한다")
    void countAll() {
        // when / then
        assertThat(boardMapper.countAll()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 목록을 최신순으로 조회한다")
    void findAll() {
        // when
        List<BoardResult> boards = boardMapper.findAll(0, 20);

        // then
        assertThat(boards).hasSize(2);
        assertThat(boards.get(0).getId()).isEqualTo(2L);
        assertThat(boards.get(0).getAuthorName()).isEqualTo("User Two");
    }

    @Test
    @DisplayName("게시글을 상세 조회한다")
    void findById() {
        // when
        BoardResult board = boardMapper.findById(1L);

        // then
        assertThat(board.getTitle()).isEqualTo("First board");
        assertThat(board.getContent()).isEqualTo("First board content");
        assertThat(board.getAuthorName()).isEqualTo("User One");
    }

    @Test
    @DisplayName("게시글을 등록하고 수정하고 삭제한다")
    void insertUpdateAndDelete() {
        // given
        BoardCreateParam createParam = new BoardCreateParam();
        createParam.setMemberId(1L);
        createParam.setTitle("New board");
        createParam.setContent("New board content");

        // when
        boardMapper.insert(createParam);

        // then
        assertThat(createParam.getId()).isNotNull();

        // given
        BoardUpdateParam updateParam = new BoardUpdateParam();
        updateParam.setId(createParam.getId());
        updateParam.setTitle("Updated board");
        updateParam.setContent("Updated board content");

        // when
        int updated = boardMapper.updateById(updateParam);

        // then
        assertThat(updated).isEqualTo(1);
        assertThat(boardMapper.findById(createParam.getId()).getTitle()).isEqualTo("Updated board");

        // when
        int deleted = boardMapper.deleteById(createParam.getId());

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(boardMapper.findById(createParam.getId())).isNull();
    }
}
