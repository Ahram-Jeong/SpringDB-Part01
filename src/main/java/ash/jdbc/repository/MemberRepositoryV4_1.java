package ash.jdbc.repository;

import ash.jdbc.domain.Member;
import ash.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * Checked Exception을 RuntimeException으로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {
    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member (member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 데이터 변경 실행
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null); // 항상 수행되도록 finally에 작성
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

       try {
           con = getConnection();
           pstmt = con.prepareStatement(sql);
           pstmt.setString(1, memberId);
           rs = pstmt.executeQuery();// 데이터 조회 실행

           if (rs.next()) {
               Member member = new Member();
               member.setMemberId(rs.getString("member_id"));
               member.setMoney(rs.getInt("money"));
               return member;
           } else { // 데이터가 없을 경우
               throw new NoSuchElementException("member not found memberId = " + memberId);
           }
       } catch (SQLException e) {
           throw new MyDbException(e);
       } finally {
           close(con, pstmt, rs);
       }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();// 데이터 변경 실행
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();// 데이터 변경 실행
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        // 리소스 정리 : 사용 자원들을 역순으로 close
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // Caution! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource); // 커넥션 닫기
    }

    private Connection getConnection() throws SQLException {
        // Caution! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource); // 커넥션 획득
        log.info("get connection = {} , class = {}", con, con.getClass());
        return con;
    }
}
