package ash.jdbc.repository;

import ash.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // 1. save
        Member member = new Member("우즈", 100000);
        repository.save(member);
        
        // 2. findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member == findMember {}", member == findMember); // 객체 주소 값 비교 -> false
        log.info("member equals findMember {}", member.equals(findMember)); // 객체 내부 값 비교 -> true
        assertThat(findMember).isEqualTo(member);
    }
}