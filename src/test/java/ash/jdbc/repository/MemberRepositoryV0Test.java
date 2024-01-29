package ash.jdbc.repository;

import ash.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

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

        // 3. update : money 100000 -> 200000
        repository.update(member.getMemberId(), 200000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(200000);

        // 4. delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}