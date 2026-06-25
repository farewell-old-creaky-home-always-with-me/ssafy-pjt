package com.ssafy.home.board.service;

import static com.ssafy.home.global.exception.ErrorCode.BOARD_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.BOARD_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.board.dto.BoardCreateRequest;
import com.ssafy.home.board.dto.BoardUpdateRequest;
import com.ssafy.home.board.mapper.BoardMapper;
import com.ssafy.home.board.mapper.dto.BoardResult;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private MemberMapper memberMapper;

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardService(boardMapper, memberMapper);
    }

    @Test
    @DisplayName("페이지 조건이 올바르지 않으면 예외가 발생한다")
    void getBoardsThrowsWhenPageInvalid() {
        // when / then
        assertThatThrownBy(() -> boardService.getBoards(0, 20))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_PAGE));
    }

    @Test
    @DisplayName("게시글 목록을 조회한다")
    void getBoardsReturnsPage() {
        // given
        given(boardMapper.countAll()).willReturn(1L);
        given(boardMapper.findAll(0, 20)).willReturn(List.of(boardResult(1L, 1L)));

        // when
        var result = boardService.getBoards(1, 20);

        // then
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).boardId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 조회하면 예외가 발생한다")
    void getBoardThrowsWhenNotFound() {
        // given
        given(boardMapper.findById(99L)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> boardService.getBoard(99L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BOARD_NOT_FOUND));
    }

    @Test
    @DisplayName("게시글을 등록하면 제목과 내용을 trim해 저장한다")
    void createBoardTrimsRequest() {
        // when
        boardService.createBoard(1L, new BoardCreateRequest("  title  ", "  content  "));

        // then
        then(boardMapper).should().insert(org.mockito.ArgumentMatchers.argThat(param ->
                param.getMemberId().equals(1L)
                        && param.getTitle().equals("title")
                        && param.getContent().equals("content")
        ));
    }

    @Test
    @DisplayName("작성자가 아니면 게시글을 수정할 수 없다")
    void updateBoardThrowsWhenOwnerDiffers() {
        // given
        given(boardMapper.findById(1L)).willReturn(boardResult(1L, 2L));

        // when / then
        assertThatThrownBy(() -> boardService.updateBoard(1L, 1L, new BoardUpdateRequest("title", "content")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BOARD_FORBIDDEN));
    }

    @Test
    @DisplayName("작성자가 아니고 관리자도 아니면 게시글을 삭제할 수 없다")
    void deleteBoardThrowsWhenOwnerDiffersAndNotAdmin() {
        // given
        given(boardMapper.findById(1L)).willReturn(boardResult(1L, 2L));
        given(memberMapper.findById(1L)).willReturn(memberResult(1L, false));

        // when / then
        assertThatThrownBy(() -> boardService.deleteBoard(1L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BOARD_FORBIDDEN));
    }

    @Test
    @DisplayName("관리자는 다른 작성자의 게시글을 삭제할 수 있다")
    void deleteBoardAllowsAdmin() {
        // given
        given(boardMapper.findById(1L)).willReturn(boardResult(1L, 2L));
        given(memberMapper.findById(1L)).willReturn(memberResult(1L, true));

        // when
        boardService.deleteBoard(1L, 1L);

        // then
        then(boardMapper).should().deleteById(1L);
    }

    private BoardResult boardResult(Long boardId, Long memberId) {
        BoardResult board = new BoardResult();
        board.setId(boardId);
        board.setMemberId(memberId);
        board.setTitle("title");
        board.setContent("content");
        board.setAuthorName("User One");
        board.setCreatedAt(LocalDateTime.of(2026, 6, 1, 10, 0));
        return board;
    }

    private MemberDetailResult memberResult(Long memberId, boolean isAdmin) {
        MemberDetailResult member = new MemberDetailResult();
        member.setId(memberId);
        member.setAdmin(isAdmin);
        return member;
    }
}
