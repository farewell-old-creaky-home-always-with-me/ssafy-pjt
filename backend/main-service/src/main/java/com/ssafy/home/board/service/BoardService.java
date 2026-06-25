package com.ssafy.home.board.service;

import static com.ssafy.home.global.exception.ErrorCode.BOARD_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.BOARD_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.ssafy.home.board.dto.BoardCreateRequest;
import com.ssafy.home.board.dto.BoardDetailResponse;
import com.ssafy.home.board.dto.BoardIdResponse;
import com.ssafy.home.board.dto.BoardListItemResponse;
import com.ssafy.home.board.dto.BoardUpdateRequest;
import com.ssafy.home.board.mapper.BoardMapper;
import com.ssafy.home.board.mapper.dto.BoardCreateParam;
import com.ssafy.home.board.mapper.dto.BoardResult;
import com.ssafy.home.board.mapper.dto.BoardUpdateParam;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public PageResponse<BoardListItemResponse> getBoards(int page, int size) {
        validatePage(page, size);
        int offset = calculateOffset(page, size);
        long total = boardMapper.countAll();
        List<BoardListItemResponse> items = boardMapper.findAll(offset, size)
                .stream()
                .map(BoardListItemResponse::from)
                .toList();
        return PageResponse.of(items, total, page, size);
    }

    private int calculateOffset(int page, int size) {
        long offset = ((long) page - 1) * size;
        if (offset > Integer.MAX_VALUE) {
            throw new CustomException(COMMON_INVALID_PAGE);
        }
        return (int) offset;
    }

    @Transactional(readOnly = true)
    public BoardDetailResponse getBoard(Long boardId) {
        return BoardDetailResponse.from(requireBoard(boardId));
    }

    @Transactional
    public BoardIdResponse createBoard(Long memberId, BoardCreateRequest request) {
        BoardCreateParam board = new BoardCreateParam();
        board.setMemberId(memberId);
        board.setTitle(request.title().trim());
        board.setContent(request.content().trim());
        boardMapper.insert(board);
        return BoardIdResponse.of(board.getId());
    }

    @Transactional
    public BoardIdResponse updateBoard(Long memberId, Long boardId, BoardUpdateRequest request) {
        BoardResult existing = requireOwnedBoard(memberId, boardId);
        BoardUpdateParam board = new BoardUpdateParam();
        board.setId(existing.getId());
        board.setTitle(request.title().trim());
        board.setContent(request.content().trim());
        int updatedCount = boardMapper.updateById(board);
        if (updatedCount == 0) {
            throw new CustomException(BOARD_NOT_FOUND);
        }
        return BoardIdResponse.of(boardId);
    }

    @Transactional
    public void deleteBoard(Long memberId, Long boardId) {
        BoardResult board = requireBoard(boardId);
        if (!memberId.equals(board.getMemberId()) && !isAdmin(memberId)) {
            throw new CustomException(BOARD_FORBIDDEN);
        }
        int deletedCount = boardMapper.deleteById(boardId);
        if (deletedCount == 0) {
            throw new CustomException(BOARD_NOT_FOUND);
        }
    }

    private boolean isAdmin(Long memberId) {
        MemberDetailResult member = memberMapper.findById(memberId);
        if (member == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return member.isAdmin();
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(COMMON_INVALID_PAGE);
        }
    }

    private BoardResult requireBoard(Long boardId) {
        BoardResult board = boardMapper.findById(boardId);
        if (board == null) {
            throw new CustomException(BOARD_NOT_FOUND);
        }
        return board;
    }

    private BoardResult requireOwnedBoard(Long memberId, Long boardId) {
        BoardResult board = requireBoard(boardId);
        if (!memberId.equals(board.getMemberId())) {
            throw new CustomException(BOARD_FORBIDDEN);
        }
        return board;
    }
}
