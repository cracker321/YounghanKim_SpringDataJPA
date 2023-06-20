package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;
/*
< DB에서의 레코드와 필드 >
- '레코드'는 '행(row)'에 해당하는 정보를 포함하는 데이터 단위.
- '필드'는 '열(column)'에 해당하는 정보를 포함하는 데이터 단위.

 */
@Repository
public class MemberJpaRepository {




    //=========================================================================================================

    @PersistenceContext //영속성 컨텍스트. 'EntityManger'를 통해  'em.persist()', 'em.find()' 등등을 사용하여
                        //JPA가 알아서 db와 교류하여 저장, 조회, 수정, 삭제 등을 다 알아서 해준다.
    private EntityManager em;

    //=========================================================================================================

    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 00:00~ ]. 실전! 스프링 데이터 JPA


    //< 신규회원 저장 Create >

    public Member save(Member member){

        //< 신규 회원 정보 Member 객체를 DB에 새롭게 저장(Create)시킴  >

        em.persist(member);

        return  member;

    }

    //=========================================================================================================


    //< v1. 회원 단건 조회 Read >: Optional<> 사용 X

    public Member find(Long id){ //매개변수의 타입이 중요하지, 그 매개변수의 이름은 아~무 상관 없다!

        //< 사용자로부터 들어온 id를 바탕으로 db로부터 그 id를 통해 '해당 회원 정보인 Member 객체'를 조회(Read)해옴 >

        return em.find(Member.class, id);
        //- 'em.find() 메소드': 'EntityManager'를 사용하여, DB로부터 '특정 데이터타입의 객체'를 조회하는 역할
        //- 'id': DB는 '주어진 회원 id'를 사용하여 '회원 Member 테이블을 검색'하고, '일치하는 해당 회원의 모든 정보'룰
        //        가져옴. 즉, 자바 객체로 변환시키면, '회원 Member 객체'가 이에 대응되는 것이라 보면 됨.
        //- 'Member.class': id를 통해 가져온 그 해당 회원의 정보를 이제 자바의 'Member 객체 데이터타입'으로 변환시킴.
        //                  Member객체는 '해당 회원 정보를 담고 있는 자바 Member 객체'임.
        //즉, 'em.find(Member.class, id)'는, '주어진 회원 id'를 사용하여 DB로부터 해당 회원 테이블을 검색하여 일치하는 회원 데이터를
        //가져오고, 그 데이터를 이제 자바 객체로 변환시킨 후, 반환시키는 것임.

    }


    //=========================================================================================================


    //< v2. 회원 단건 조회 Read >: Optional 사용 O

    public Optional<Member> findById(Long id){


        Member member = em.find(Member.class, id);

        return Optional.ofNullable(member); //- DB에 해당 회원 id값을 갖는 회원이 있을 수도 있고, 없을 수도 있다는 뜻
                                            //- Optional 타입으로 감싸는 것임.

        //- 'Optional<Member>': DB에 그 해당 회원의 id가 존재할 수도 있고, 없을 수도 있기 때문에,
        //                      '내장 JpaRepository의 내장 메소드 findById()의 반환값은 Optional<>로 설정되어 있음'.
        //cf) 다만, 여기 클래스에서는 아직 내장 레펏 JpaRepository를 사용한 것이 아니고, 일단 이름만 비슷하게 저렇게 해둔 것임.

    }

    //=========================================================================================================

    
    //< 기존 회원 정보 수정 Update >






    //=========================================================================================================

    //< 모든 회원 조회 Read >

    public List<Member> findAll(){

        //- '모든 회원 조회'는 'EntityManager의 createQuery를 통해 JPQL로 DB로부터 데이터를 불러와야 하는 것임'.
        //*****중요*****
        //- JPQL은 모양은 SQL과 비슷하게 생겼으나(SQL은 실제 DB에 쿼리 날리는 것),
        //  '자바 객체'를 대상으로 쿼리를 날려서 'EntityManager'를 통해 정보를 가져오는 것이다!!!
        //  따라서, 'select m from Member m'에서 'Member'는, 절대 DB에서의 'Member 테이블'이 아니고,
        //  여기 프로젝트에서의 '회원 Member 객체'에 쿼리를 날리는 것임.
        //- JPQL 쿼리를 작성하면, 그 쿼리가 실행될 때 SQL 쿼리문으로 자동 번역되어, 이제 그 SQL 쿼리문으로 DB에서 정보를 가져오는 것임.
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return members;

        //- '메소드 getResultList()'
        //: JPQL에서 쿼리를 실행한 후, 그 결과값들을 'List 객체 형태로 반환'함. 주로 여러 개의 결과가 반환될 때 사용함.
        //그리고, 그 반환된 결과는 자바 객체로 매핑됨.

    }



    //=========================================================================================================


    //< 전체 회원의 수 카운트 Count >

    //- 'JPQL에서의 함수 count()'는, DB에서 '특정 조건에 부합하는 레코드('행' 데이터)의 개수를 반환'하는 함수임.
    //- '정수 값'을 반환하며, 결괏값은 항상 0 이상임.
    //- 주로 SELECT문과 함께 사용되어 조건에 맞는 레코드('행' 데이터)를 가져옴.
    //- 사용법: SELECT count(e) FROM 엔티티명 e(=별명) WHERE 조건
    //         일반적으로 '메소드 getSingleResult()'를 사용하여 그 결과를 가져옴.
    //         함수 count()는 항상 '단일 결괏값'을 반환하며, 해당 결과는 '항상 단일한 하나의 값(=개수)를 가져오기 때문'에
    //         '메소드 getSingleResult()'를 사용하는 것임.
    //- 'Long.class': count()함수 쿼리는 정수 형태 결괏값을 반환하는데, 그것을 여기서 내가 '임의로' 그냥
    //                정수 데이터를 나타내는 데이터타입 중 가장 넓은 범위를 정수 값을 저장할 수 있는 Long 타입으로 지정한 것임.
    //                (Long 대신 Integer로 해도 정상적으로 작동 가능한 듯..?)

    public long count(){

        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();

    }


    //=========================================================================================================


    //< 삭제 Delete >

    public void delete(Member member){

        em.remove(member);
    }
}
