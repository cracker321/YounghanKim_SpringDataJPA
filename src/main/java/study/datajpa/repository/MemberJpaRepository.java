package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {




    //=========================================================================================================

    @PersistenceContext //영속성 컨텍스트. 'EntityManger'를 통해  'em.persist()', 'em.find()' 등등을 사용하여
                        //JPA가 알아서 db와 교류하여 저장, 조회, 수정, 삭제 등을 다 알아서 해준다.
    private EntityManager em;

    //=========================================================================================================

    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 00:00~ ]. 실전! 스프링 데이터 JPA
    public Member save(Member member){

        //< 신규 회원 정보 Member 객체를 DB에 새롭게 저장(Create)시킴  >

        em.persist(member);

        return  member;

    }

    //=========================================================================================================

    public Member find(Long id){

        //< 사용자로부터 들어온 id를 바탕으로 db로부터 그 id를 통해 '해당 회원 정보인 Member 객체'를 조회(Read)해옴 >

        return em.find(Member.class, id);
        //- 'em.find() 메소드': 'EntityManager'를 사용하여, DB로부터 '특정 데이터타입의 객체'를 조회하는 역할
        //- 'id': DB는 '주어진 회원 id'를 사용하여 '회원 Member 테이블을 검색'하고, '일치하는 해당 회원의 정보 모든 정보;룰
        //        가져옴.
        //- 'Member.class': id를 통해 가져온 그 해당 회원의 정보를 이제 자바의 'Member 객체 데이터타입'으로 변환시킴.
        //                  Member객체는 '해당 회원 정보를 담고 있는 자바 Member 객체'임.
        //즉, 'em.find(Member.class, id)'는, '주어진 회원 id'를 사용하여 DB로부터 해당 회원 테이블을 검색하여 일치하는 회원 데이터를
        //가져오고, 그 데이터를 이제 자바 객체로 변환시킨 후, 반환시키는 것임.

    }

    //=========================================================================================================




    //=========================================================================================================



}
