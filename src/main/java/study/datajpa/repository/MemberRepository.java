package study.datajpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;


//[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 17:40~ ]. 실전! 스프링 데이터 JPA


//- '내장 레펏 JpaRepository'는 JPA로 개발할 때, 개발자가 상상할 수 있는 웬만한 공통 기능은 다 제공함. 이 정도면 충분하다고 함.



//@Repository : 이 어노테이션 작성 안해줘도 됨.
//              '내장 레펏 JpaRepository'를 상속받는 순간, 스프링 데이터 JPA가 아래 '레펏 TeamRepository'를
//              자동으로 '레퍼지토리'로 인식하기 때문이다.
public interface MemberRepository extends JpaRepository<Member, Long> { //'인터페이스'가 '인터페이스'를 '상속'받음
    //- 'Member': '회원 Member객체의 타입'
    //- 'Long': '회원 Memeber 객체'의 '필드 pk의 타입(=Long)'


    //=========================================================================================================

    //[ '메소드 이름으로 쿼리 생성'강 05:05 ] 실전! 스프링 데이터 JPA. p30 pdf
    //< v2 '특정 회원의 나이가 ~살 보다 더 많은 경우'에 대해 레퍼지터리에 JPQL 쿼리를 직접 만들기 >


    //=========================================================================================================


    //[ ''@Query'를 사용하여 리포지토리 메소드에 쿼리 정의하기'강  00:00 ] 실전! 스프링 데이터 JPA. p32 pdf

    //- 실무에서 정말 많이 사용하는 방법임.
    //- '메소드 이름으로 쿼리 생성하기' 방법 대신에, '개발자가 직접 본인이 원하는 쿼리를 작성 가능'함.
    //- 장점
    //1) 복잡한 쿼리 작성 가능.
    //2) 네이티브 SQL 쿼리를 작성할 수 있으며, JPA 쿼리 언어도 사용 가능.
    //3) 파라미터 바인딩, 페이징, 정렬 등 다양한 기능 지원.
    //4) 쿼리 결과를 엔티티 객체 또는 DTO로 매핑 가능.
    //5) 쿼리 실행 전에 문법 오류를 찾을 수 잇어 안정성이 높음.


    @Query("select m from Member m where m.username = :username11 and m.age = :age11")
    List<Member> findUser(@Param("username11") String username22, @Param("age11") int age22);

    //- 'm.username': '회원 엔티티 Member 객체'의 '필드(속성) username'을 사용(참조)하여 검색 조건을 설정함

    //- ':username11': 여기 JPQL 쿼리의 파라미터임. '이 파라미터 :username11'은
    //                 '쿼리 메소드 findUser의 @Param("username11")'의 username11로 들어오게 되는 인자값'과 결합하여
    //                 이 쿼리의 실행 시점에 '바인딩'된다!

    //- '@Param("username11")': '어노테이션 @Param의 인자값(=파라미터의 실제값)이 들어오는 통로로 사용됨'.
    //                          '쿼리 메소드 findUser의 매개변수 username22으로 들어오는 실제 인자값'과
    //                          'JPQL 쿼리의 파라미터인 :username11'을 서로 '매핑하는 역할'임.

    //- 'String username22': 이건 그냥 아무렇게나 작성해줘도 됨. 'username'으로 해도 되고, 'username22'로 해도 되고 무관함.
    //                       (정확히 알아보기..!)
    
    //- ':username11'과 '@Param("username11")'의 'username11'은 반드시 서로 일치하는 문자열이어야 한다!

    //결론:
    //순서 1) 외부 클래스 어딘가에서 여기 '레펏 MemberRepository의 메소드 findUser'를 '호출'한다.
    //순서 2) '쿼리메소드의 String username22'로 들어온 실제 인자값이
    //순서 3) '쿼리메소드의 @Param("username11")'을 통해서
    //순서 3) 'JPQL 쿼리'인 ':username11'로 쑥 들어가는 것임.

    /*
    < e.g >

    외부 클래스 어딘가에서 '메소드 findUser를 호출('memberRespository.findUser("유종", 32)'처럼 이렇게 호출)할 경우',
    실행되는 JPQL 쿼리는
    : select m from Member m where m.username = '유종' and m.age = 32
    이렇게 되는 것이다!
     */
    //=========================================================================================================








}
