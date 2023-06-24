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


    //=========================================================================================================


    //[ '메소드 이름으로 쿼리 생성'강 01:00 ] 실전! 스프링 데이터 JPA. p29 pdf

    //< v1 '특정 회원의 나이가 ~살 보다 더 많은 경우'에 대해 레퍼지터리에 JPQL 쿼리를 직접 만들기 >


    public List<Member> findByUsernameAndAgeGreaterThan(String usernameeee, int ageeee){

        return em.createQuery("select m from Member m where m.username = :username1234 and m.age > :age1234")
                .setParameter("username1234", usernameeee)
                .setParameter("age1234", ageeee)
                .getResultList();
    }


    //< 파라미터 바인딩 >. p358 pdf

    //1.'메소드의 매개변수 usernameeee으로 들어온 인자값'이 'setParameter(..., usernameeeee)'의 'usernameeee'로 들어가고,
    //2.그 'setParameter(..., usernameeee)의 usernameeee'이 그 바로 옆의 현재 이 쿼리의 파라미터인 'username1234'와 바인딩되고,
    //3.그것이 최종적으로 where 조건식에서 'username1234'로 사용되어 'm.username'과 같은 경우의 데이터 레코드 조건을 구성하고 있음.


    //=========================================================================================================

    //[ '메소드 이름으로 쿼리 생성'강  08:40 ] 실전! 스프링 데이터 JPA. p29 pdf

    //< 메소드 이름으로 쿼리 생성하기 >


    //- 스프링 데이터 JPA 공식 도큐멘트 에서 예시 사용법 제시해줌.
    //  이 예시 안에 있는 거로 개발에서 필요한 웬만한 쿼리메소드는 다 작성 가능하다고 함.
    //  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    //- '내장 레펏 JpaRepository를 상속받은 후'에, 스프링 데이터 JPA가 자체적으로 내장 인식되어 있는 메소드의 이름을
    //  개발자가 입력하면, 그 메소드를 분석하여 그 메소드에 맞게 내장 인식되어 있는 JPQL 쿼리를 자동으로 만들어서 DB에 날려줌.
    //  필요에 따라 개발자가 추가적인 키워드를 메소드 이름에 추가하여 조건(where)을 설정할 수 있음.
    //- 단순 데이터 조회, 검색 기능을 구현할 때 주로 사용됨.
    //- 장점: 개발자가 직접 쿼리문을 작성하지 않고, 내장 메소드 이름만으로 쿼리를 생성하기에 빠른 개발이 가능함.
    //- 단점: 복잡한 쿼리, 조인을 작성해야 할 경우에는 한계가 있을 수 있음. 쿼레 메소드 만으로는 모든 데이터 조작 기능 구현할 수 없음.
    /*
    - 주의사항
    : 쿼리 메소드에 사용된 엔티티 객체의 '필드(속성)명'이 변경된 경우, 쿼리메소드가 참조하고 있는 필드명도 변경되어야 한다!
      왜냐하면, 이렇게 쿼리메소드명을 변경하지 않으면, 스프링 데이터 JPA가 개발자가 작성한 쿼리 메소드를 분석할 때,
      해당 필드가 없다고 판단하여 예외(에러)를 발생시킴.

    1. User 엔티티 객체 정의
    @Entity
    public class User{

        @Id
        private Long id;
        private String username;
        private int age;
     }


    2. 이 'User 엔티티 객체의 필드 username'을 참조하는 쿼리메소드를 작성함
    public interface UserRepository extends JpaRepository(User, Long){

        List<User> findByUsername(String username);
    }


    3. 여기서, 만약 '엔티티 객체 User의 필드(속성)명 username'이 'name'으로 바뀔 경우, 쿼리메소드의 해당 이름도 바뀌어야 함
    public interface UserRepository extends JpaRepository<User, Long>{

        List<User> findByName(String name);

    }

     */


    //=========================================================================================================


    //[ '순수 JPA 페이징과 정렬'강  00:00 ] 실전! 스프링 데이터 JPA. p35 pdf


    //< 순수 스프링 JPA로 페이징하기 >


    //< '한 페이지 당 보여줄 데이터(여기서는 '한 페이지 당' 보여줄, 주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트)를 만드는
    //  '메소드 findByPage'>


    //검색 조건: 나이가 10살
    //정렬 조건: 이름 으로 내림차순
    //페이징 조건: 첫 번째 페이지부터, 페이지 당 보여줄 데이터는 3개
    //- offset: db에서 검색 결과의 시작 위치
    //- limit: db로부터 한 번에 가져올 데이터의 최대 개수
    //- setParameter(): EntityManger의 메소드 createQuery로 생성된 JPQL 쿼리 객체에 호출되어,
    //                  '메소드의 매개변수로 들어온 값'을 'JPQL 쿼리의 파라미터에' '매핑, 바인딩시키는 역할'을 하는 메소드.


    public List<Member> findByPage(int age11, int offset, int limit){

        return em.createQuery("select m from Member m where m.age = :age22 order by m.username desc")
                .setParameter("age22", age11)
                .setFirstResult(offset) //어디서'부터' 가져올거야~. offset의 기본값은 0임.
                .setMaxResults(limit) //페이지 당 보여줄 데이터의 개수
                .getResultList();

    }

    //- '매개변수 int age11': 외부 클래스 어딘가에서 '메소드 findByPage를 호출할 때', 거기서 입력한 인자값이 이 매개변수 age11로 들어옴.

    //- 'setParameter("age22",..)의 age22': '파라미터의 이름'.
    //                                      '메소드 findByPage의 매개변수 username22으로 들어와서 그 내부에서
    //                                      사용되는 변수 age11'과
    //                                      'JPQL 쿼리의 파라미터인 ':age22'을 서로 '매핑, 바인딩해주는 역할'임.
    //                                      즉, 여기서 age22는 '내부에서 사용되는 변수 age11'이
    //                                      '현재 JPQL 쿼리 내부에서 사용되는 쿼리 인수 :age22'로 들어가기 위한 '통로'로 사용됨.

    //- 'setParameter(.., age11)의 age11': '매개변수 int age11'로 들어온 값을 사용하기 위해 내부에서 사용되는 변수 age11.
    //                                     '파라미터의 이름인 "age22"'에 실제 전달될 값을 담고 있음.

    //- ':age22': 현재 JPQL 쿼리 내부에서 사용되는 파라미터(인수)의 이름. 쿼리에서 사용될 파라미터의 위치를 나타냄.


    //- 매개변수 int age11과 내부에서 사용되는 변수 age11은 반드시 서로 일치하는 문자열명이어야 한다!
    //- ':age22'와 'setParameter("age22",..)의 age22'은 반드시 서로 일치하는 문자열명이어야 한다!


    //결론:
    //순서 1) 외부 클래스 어딘가에서 여기 '레펏 MemberJpaRepository의 메소드 findByPage'를 '호출'한다.
    //순서 2) '메소드의 매개변수 int age11'로 들어온 실제 인자값이
    //순서 3) 메소드 내부에서 사용되는 변수 age11('setParameter(.., age11)의 age11')로 들어가 사용되고,
    //순서 4) 'setParameter("age22",..)의 age22'을 통해 매핑(바인딩)되어,
    //순서 5) 'JPQL 쿼리'인 ':age22'로 쑥 들어가는 것임.



    //-----------------------------------------------------------------------------------------------


    //< 페이징 할 때 당연히 필요한 '한 페이지 당 보여줄, 주어진 조건에 해당되는 데이터(페이지 당 보여줄, 주어진 조건에 해당되는
    //  회원 Member 객체들의 목록 리스트')의 개수를 구하기 위한
    //  '한 페이지 당이 아닌, 주어진 조건에 해당하는 전체 총 데이터(주어진 조건에 해당되는 총 전체 회원 Member 객체들의 개수)
    //  를 가져오는 '메소드 totalCount' >

    //- '주어진 조건에 해당하는 총 데이터 개수'를 구한 후, 이를 기반으로 '필요한 페이지의 개수'를 계산할 수 있음.
    //  예를 들어, '주어진 조건에 해당하는 총 데이터의 개수'가 100개이고, '주어진 조건에 해당하는 페이지 당 보여줄 데이터의 개수'가
    //  10개라면, 100 나눈기 10을 해서, 총 10개의 페이지로 표시할 수 있음.
    //- 보통 페이징쿼리를 만들 때 카운팅하는 쿼리도 같이 만듦

    //- setParameter(): EntityManger의 메소드 createQuery로 생성된 JPQL 쿼리 객체에 호출되어,
    //                  '메소드의 매개변수로 들어온 값'을 'JPQL 쿼리의 파라미터에' '매핑, 바인딩시키는 역할'을 하는 메소드.

    public long totalCount(int age11){

        return em.createQuery("select count(m) from Member m where m.age = :age22", Long.class)
                //저 위에 사용된 '주어진 조건에 해당하는 페잊 당 보여줄 데이터의 개수'를 구하는 '메소드 findByPage'에서
                //사용된 JPQL 쿼리는 'select m from Member m where m.age = :age22 order by m.username desc'인데,
                //여기서는 당연히 '총 데이터의 개수'만 보여주면 되기 때문에,
                //'위 쿼리에서 정렬해주는 부분인 'order by m.username desc''는 성능최적화를 위해 삭제했다!
                //(당연...단순하게 '카운팅 결과'만 반환시키고 싶은데, 거기에 'order by 등의 쏘팅'하는 쿼리는 불필요하다!
                .setParameter("age22", age11)
                .getSingleResult(); //'카운팅의 결괏값은 당연히 '정수값''이므로, 그 '정수값 자체'는 오직 '숫자 1건'을 반환하기
                                    //때문에, 'Single Result'임.
    }


    //=========================================================================================================



}
