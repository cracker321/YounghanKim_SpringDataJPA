package study.datajpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;



//[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 17:40~ ]. 실전! 스프링 데이터 JPA
public interface MemberRepository extends JpaRepository<Member, Long> {
    //- 'Member': Member객체의 타입
    //- 'Long': '회원 Memeber 객체'의 '필드 pk의 타입(=Long)'





}
