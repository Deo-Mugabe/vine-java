package vine.vine.service.Impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vine.vine.domain.dto.response.BookingNamePair;

@Service
@RequiredArgsConstructor
public class BookingFetcher {

    private final JdbcTemplate jdbcTemplate;

    public List<BookingNamePair> fetchBookingAndNameIds(LocalDateTime lastRunTime) {
        String sql = """
    SELECT DISTINCT 
        jmmain.book_id AS book_id, 
        jmmain.name_id AS name_id
    FROM 
        jmmain
        LEFT OUTER JOIN jrelease 
            ON jmmain.book_id = jrelease.book_id
    WHERE 
        jmmain.bkstatus = 'A'
        OR jrelease.releasetime >= ?
    ORDER BY 
        jmmain.book_id
""";

        return jdbcTemplate.query(
                sql,
                ps -> ps.setTimestamp(1, Timestamp.valueOf(lastRunTime)),
                (rs, rowNum) -> new BookingNamePair(
                        rs.getLong("book_id"),
                        rs.getLong("name_id")
                )
        );
    }

    public Page<BookingNamePair> fetchBookingAndNameIdsPaginated(
            LocalDateTime lastRunTime, Pageable pageable) {
        
        String countSql = """
            SELECT COUNT(DISTINCT jmmain.book_id)
            FROM jmmain
            LEFT OUTER JOIN jrelease ON jmmain.book_id = jrelease.book_id
            WHERE jmmain.bkstatus = 'A'
               OR jrelease.releasetime >= ?
            """;
        
        Integer totalCount = jdbcTemplate.queryForObject(
            countSql, Integer.class, Timestamp.valueOf(lastRunTime));
        
        String dataSql = """
            SELECT DISTINCT 
                jmmain.book_id AS book_id, 
                jmmain.name_id AS name_id
            FROM jmmain
            LEFT OUTER JOIN jrelease ON jmmain.book_id = jrelease.book_id
            WHERE jmmain.bkstatus = 'A'
               OR jrelease.releasetime >= ?
            ORDER BY jmmain.book_id
            LIMIT ? OFFSET ?
            """;
        
        List<BookingNamePair> content = jdbcTemplate.query(
            dataSql,
            ps -> {
                ps.setTimestamp(1, Timestamp.valueOf(lastRunTime));
                ps.setInt(2, pageable.getPageSize());
                ps.setLong(3, pageable.getOffset());
            },
            (rs, rowNum) -> new BookingNamePair(
                rs.getLong("book_id"),
                rs.getLong("name_id")
            )
        );
        
        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
    }
}