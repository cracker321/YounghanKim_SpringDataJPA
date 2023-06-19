package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContexts;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Rollback(false)
class MemberTest {


    //[ '예제 도메인 모델과 동작확인'강. 10:30~ ]. 실전! 스프링 데이터 JPA

    //=========================================================================================================

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity(){

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);



        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);


        //초기화
        em.flush();
        em.clear();


        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();


        for (Member memberrr : members) { //- '회원 목록 리스트 List<Member>'를 순회하는 것임.
                                          //- 'iter'를 입력하고 'tab' 누르면, 이렇게 for-each문 작성됨.

            System.out.println(memberrr);
            System.out.println(memberrr.getTeam());
            
        }



    }


    //=========================================================================================================



}