package study.datajpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;



//[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 17:40~ ]. 실전! 스프링 데이터 JPA


//@Repository : 이 어노테이션 작성 안해줘도 됨.
//              '내장 레펏 JpaRepository'를 상속받는 순간, 스프링 데이터 JPA가 아래 '레펏 TeamRepository'를
//              자동으로 '레퍼지토리'로 인식하기 때문이다.
public interface MemberRepository extends JpaRepository<Member, Long> { //'인터페이스'가 '인터페이스'를 '상속'받음
    //- 'Member': '회원 Member객체의 타입'
    //- 'Long': '회원 Memeber 객체'의 '필드 pk의 타입(=Long)'







}
