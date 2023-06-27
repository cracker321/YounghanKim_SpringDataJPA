package study.datajpa.repository;


import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;


//--------------------------------------------------------------------------------------------------


//[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 17:40~ ]. 실전! 스프링 데이터 JPA

//- '내장 레펏 JpaRepository'는 JPA로 개발할 때, 개발자가 상상할 수 있는 웬만한 공통 기능은 다 제공함. 이 정도면 충분하다고 함.


//--------------------------------------------------------------------------------------------------


//[ '사용자 정의 리포지토리 구현'강  03:00 ] 실전! 스프링 데이터 JPA. pdf p47

//- 1.'레퍼지터리(인터페이스) MemberRepository'가 '다중상속' 하고 있는 인터페이스 2개 중에 하나가
//  바로 이 '사용자 정의 인터페이스 MemberRepositoryCustom'이고,
//  (하나는 '스프링 데이터 JPA의 내장 인터페이스 JpaRepository'이고,
//   하나는 '사용자 정의 레퍼지터리를 만들기 위해 생성한 사용자 정의 인터페이스 MemberRepositoryCustom'임)
//  2.그 '사용자 정의 인터페이스 MemberRepositoryCustom' 내부의 추상 메소드들의 구현부들을 작성해주기 위해 존재하는
//  3.'구체화하는 클래스 MemberRepositoryCustom'의 내부에 '구체화 메소드 findMemberCustom'이 존재함.

//- 이를 통해,
//  1.외부 클래스 어딘가에서 '레퍼지터리(인터페이스) MemberRepsotory를 참조하여
//  2.'MemberRepositoryImpl의 내부 메소드 findMemberCustom을 호출('memberRepository.findMemberCustom' 이렇게 작성)
//  할 수 있게 된다!


//--------------------------------------------------------------------------------------------------


//@Repository : 이 어노테이션 작성 안해줘도 됨.
//              '내장 레펏 JpaRepository'를 상속받는 순간, 스프링 데이터 JPA가 아래 '레펏 TeamRepository'를
//              자동으로 '레퍼지토리'로 인식하기 때문이다.
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    //- '인터페이스'가 '2개의 인터페이스'들을 '다중 상속'받음.
    //  하나는 '스프링 데이터 JPA의 내장 인터페이스 JpaRepository'이고,
    //  하나는 '사용자 정의 레퍼지터리를 만들기 위해 생성한 인터페이스 MemberRepositoryCustom'임.
    //- 'Member': '회원 Member객체의 타입'
    //- 'Long': '회원 Memeber 객체'의 '필드 pk의 타입(=Long)'


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
    //  즉, 간단한 쿼리 정도 만들 때만 사용하고, 실무에서 자주 사용하는 것은 '@Query'를 사용하는 방법임.
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


    //[ '@Query'를 사용하여 리포지토리 메소드에 쿼리 정의하기'강  00:00 ] 실전! 스프링 데이터 JPA. p32 pdf

    //- 실무에서 정말 많이 사용하는 방법임.
    //- '메소드 이름으로 쿼리 생성하기' 방법 대신에, '개발자가 직접 본인이 원하는 쿼리를 작성 가능'함.
    //- 장점
    //1) 복잡한 쿼리 작성 가능.
    //2) 네이티브 SQL 쿼리를 작성할 수 있으며, JPA 쿼리 언어도 사용 가능.
    //3) 파라미터 바인딩, 페이징, 정렬 등 다양한 기능 지원.
    //4) 쿼리 결과를 엔티티 객체 또는 DTO로 매핑 가능.
    //5) 문법 오류(오타 등)가 있는 경우, 애플리케이션 서버 실행할 때(=쿼리 실행 전에) 문법 오류를 보여주어서 안정성이 높음.


    //여기서 'select m'의 'm'은 '회원 Member 객체의 '모든 필드(=컬럼)들'을 다 select해서 보여줘라!'라는 의미임
    @Query("select m from Member m where m.username = :username11 and m.age = :age11")
    List<Member> findUser(@Param("username11") String username22, @Param("age11") int age22);


    //- 'm.username': '회원 엔티티 Member 객체'의 '필드(속성) username'을 사용(참조)하여 검색 조건을 설정함

    //- ':username11': 여기 JPQL 쿼리의 파라미터임. '이 파라미터 :username11'은
    //                 '쿼리 메소드 findUser의 @Param("username11")'의 username11로 들어오게 되는 인자값'과 결합하여
    //                 이 쿼리의 실행 시점에 '바인딩'된다!

    //- '@Param("username11")': '어노테이션 @Param의 인자값(=파라미터의 실제값)이 들어오는 통로로 사용됨'.
    //                          '쿼리 메소드 findUser의 매개변수 username22으로 들어오는 실제 인자값'과
    //                          'JPQL 쿼리의 파라미터인 :username11'을 서로 '매핑하는 역할'임.
    //                          여기서의 'username11'은 '파라미터의 이름'이다.
    //                          'String username22'로 실제 전달되어 들어온 값을,
    //                          다시 ':username11'로 전달해주기 위해 서로 '매핑, 바인딩시켜주는 역할'임.

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


    //[ '@Query, 값, DTO 조회하기'강  00:00 ] 실전! 스프링 데이터 JPA. p33 pdf

    //< '전체 회원 리스트 목록 '문자열들''을 가져오기 >

    //- '회원 Member 객체의 필드(컬럼)들 중'에 '필드(컬럼) username'만 select 해서 보여줘라!' 라는 의미임.
    //  리턴타입이 '회원 이름(당연히 '문자열 타입')에 맞는 String 타입'이기 떄문에, '<String> 타입'으로 설정하였고,
    //  이렇게만 작성해도 '전체 회원 목록'을 가져올 수 있음.
    @Query("select m.username from Member m")
    List<String> findUsernameList();


    //- 만약, '전체 회원 리스트 '객체''를 가져오고 싶었다면,
    //  @Query("select m from Member m")
    //  List<Member> find...();
    //  이렇게 작성하면 된다!



    //=========================================================================================================

    //[ '@Query, 값, DTO 조회하기'강  02:10 ] 실전! 스프링 데이터 JPA. p33 pdf

    //- 실무에서 자주 쓰임.

    //< '전체 회원 리스트 목록(List) 'MemberDto 객체들''을 가져오기 >


    //- 조회해서 불러오고자 하는 DTO 객체를 아래 @Query(..)의 내부에 'new 연산자를 사용하여 직접 생성'하여 선택(select)함.
    //- 1.쿼리가 실행되면, '회원 엔티티 Member'와 '팀 엔티티 Team'을 조인하여 연관된 데이터를 가져오고,
    //  2.가져온 데이터는 'new ...MemberDto객체 생성'을 통해 'MemberDto 객체'로 '매핑'됨.
    //    (가져온 데이터 중에 '회원 엔티티 Member의 필드 id, 필드 username'과 '팀 엔티티 Team의 필드 name'을 추출하여
    //    그 필드들을 인자값으로 갖는 MemberDto 객체를 생성한 것임)
    //  3.그 필드들을 인자값으로 갖는 '전체 회원 리스트 목록(List) 'MemberDto 객체들'을 'List 객체'로 반환함.
    //- 형식: 'select new 해당DTO파일경로(필요한 인자값 전달) from 테이블 별칭...'
    //
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


    //=========================================================================================================


    //[ '반환 타입'강  00:00 ] 실전! 스프링 데이터 JPA. p35 pdf


    /*

    스프링 데이터 JPA는 유연한 반환 타입을 지원함

    1.컬렉션(리스트, 집합, 맵..) 타입
      : List<Member< findByUsername(String name);
    2.단건 객체 조회
      : Member findByUsername(String name);
    3.Optional<T> 타입
      : Optional<Member< findByUsername(String name);



    < 위 1~3의 데이터 조회 결과인 반환값의 건수(개수)가 그 타입의 특성에 맞지 않는 경우, 스프링 데이터 JPA가 이를 처리하는 방법 >


    1.데이터 조회 결과의 반환값 개수가 0건 인 경우
      (1) List<> 등 컬렉션인 경우(List객체 이기에 당연히 반환값 개수가 2건 이상인 게 정상임)
      : 빈 컬렉션 반환(반환값이 '0'임)
      (2) 단건 객체 조회의 경우(단건 객체만 조회하는 것이기에 당연히 반환값 개수가 1건이어야 함)
      : null 반환(원래라면 '특정 객체 1건'에 대한 정보를 반환하는 것임)

    *****결론*****
    - 따라서, '단건 객체 조회'의 경우, 그 데이터가 DB에 있는지 아닌지 불확실할 때는,
      그냥 'Optional<T> 타입'을 사용하는 것이 가장 무난하다!
      (.orElse 사용)



    2.데이터 조회 결과의 반환값 개수 2건 이상인 경우
      (1) 단건 객체 조회의 경우(단건 객체만 조회하는 것이기에 당연히 반환값 개수가 1건이어야 함)
      : NotUniqueResultException 반환.

     */


    //=========================================================================================================


    //[ '스프링 데이터 JPA 페이징과 정렬'강  00:00 ] 실전! 스프링 데이터 JPA. p37 pdf

    //< '순수 스프링 JPA가 아닌', '스프링 데이터 JPA를 사용한 페이징 메소드인 레펏 MemberRepository의 메소드 findByPage' 작성 >

    //< 순서 >

    //1. 스프링 데이터 JPA의 인터페이스 JpaRepository를 상속받는 레퍼지터리에 Page타입 페이징 메소드 findByAge를 작성한다.
    //2. 외부 클래스에서 '레펏 MemberRepository의 메소드 findByAge'를 호출한다.
    //   (여기서는 지금 '테스트클래스 MemberRepository' 내부에서 이 메소드 findByAge를 호출했고, 관련 설명들 다 거기에 있음.
    //3. 그 외부 클래스에 'PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));'
    //   와 같이 작성해줌.
    //4. 그 외부 클래스에
    //   int age11 = 10;
    //   Page<Member> resultMembersPerPage = memberRepository.findByAge(age11, pageRequest);
    //   와 같이 작성해줌.
    //5. 이제 거기서 내가 필요한 기능들에 해당하는 내장 메소드들을 사용하여 페이징을 사용하면 됨.
    /* 아래처럼 내가 필요한 기능들에 해당하는 내장 메소드들을 사용하면 됨.

            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getContent()'
            //  : 현재 페이지의 데이터를 가져옴
            List<Member> content = resultMembersPerPage.getContent();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalElements()'
            //  : '순수 스프링 JPA에서의' '주어진 조건에 해당하는 전체 총 데이터를 보여주는 메소드 totalCount'를 대신함.
            long totalElements = resultMembersPerPage.getTotalElements();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getNumber()'
            //  : 현재 페이지의 번호를 가져오는 메소드
            resultMembersPerPage.getNumber();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalPages()'
            //  : 총 페이지의 개수
            resultMembersPerPage.getTotalPages();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 isFirst()'
            //  :


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 hasNext()'
            //  : 다음 페이지가 있는지 여부를 boolean 타입으로 보여줌.

     */

    Page<Member> findByAge(int age, Pageable pageable);


    //=========================================================================================================


    //[ '스프링 데이터 JPA 페이징과 정렬'강  13:15 ] 실전! 스프링 데이터 JPA. p39 pdf

    //< 인터페이스 Slice >


    //- '..더보기'와 같은 것..?(?)
    //- Slice 인터페이스를 사용하면 얻어온 페이지의 데이터와 함께 다음 페이지 여부를 얻을 수 있어,
    //  일부 데이터를 요청하는 상황에서 성능 및 사용성을 개선하는데 도움을 줍니다.

    //< 순서 >

    //1. 스프링 데이터 JPA의 인터페이스 JpaRepository를 상속받는 레퍼지터리에 Slice타입 메소드 findByAge2를 작성한다.
    //2. 외부 클래스에서 '레펏 MemberRepository의 메소드 findByAge2'를 호출한다.
    //   (여기서는 지금 '테스트클래스 MemberRepository' 내부에서 이 메소드 findByAge를 호출했고, 관련 설명들 다 거기에 있음.
    //3. 그 외부 클래스에 'PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));'
    //   와 같이 작성해줌.
    //4. 그 외부 클래스에
    //   int age11 = 10;
    //   Slice<Member> resultMembersPerPage = memberRepository.findByAge(age11, pageRequest);
    //   와 같이 작성해줌.
    //5. 이제 거기서 내가 필요한 기능들에 해당하는 내장 메소드들을 사용하여 페이징을 사용하면 됨.

    /* 아래처럼 내가 필요한 기능들에 해당하는 내장 메소드들을 사용하면 됨.


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getContent()'
            //  : 현재 페이지의 데이터를 가져옴
            List<Member> content = resultMembersPerPage.getContent();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getNumber()'
            //  : 현재 페이지의 번호를 가져오는 메소드
            resultMembersPerPage.getNumber();


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 isFirst()'
            //  :


            //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 hasNext()'
            //  : 다음 페이지가 있는지 여부를 boolean 타입으로 보여줌.



# pdf p39
public interface Slice<T> extends Streamable<T> {
int getNumber(); //􀴅􀩤 􀲕􀩉􀫑
int getSize(); //􀲕􀩉􀫑 􀯼􀓝
int getNumberOfElements(); //􀴅􀩤 􀲕􀩉􀫑􀧀 􀕡􀧢 􀘘􀩉􀰠 􀣻
List<T> getContent(); //􀪑􀴥􀘻 􀘘􀩉􀰠
boolean hasContent(); //􀪑􀴥􀘻 􀘘􀩉􀰠 􀪓􀩤 􀧈􀠗
Sort getSort(); //􀩿􀛳 􀩿􀠁
boolean isFirst(); //􀴅􀩤 􀲕􀩉􀫑􀐾 􀭐 􀲕􀩉􀫑 􀩋􀫑 􀧈􀠗
boolean isLast(); //􀴅􀩤 􀲕􀩉􀫑􀐾 􀝃􀫑􀝄 􀲕􀩉􀫑 􀩋􀫑 􀧈􀠗
boolean hasNext(); //􀗮􀨺 􀲕􀩉􀫑 􀧈􀠗
boolean hasPrevious(); //􀩉􀩹 􀲕􀩉􀫑 􀧈􀠗
Pageable getPageable(); //􀲕􀩉􀫑 􀨃􀭒 􀩿􀠁
Pageable nextPageable(); //􀗮􀨺 􀲕􀩉􀫑 􀑑􀭓
Pageable previousPageable();//􀩉􀩹 􀲕􀩉􀫑 􀑑􀭓
<U> Slice<U> map(Function<? super T, ? extends U> converter); //􀟸􀴜􀓝
}



     */
    Slice<Member> findByAgeSlice(int age, Pageable pageable);



    //=========================================================================================================


    //[ '벌크성 수정 쿼리'강  05:00 ] 실전! 스프링 데이터 JPA. pdf p41


    //< '순수 스프링 JPA가 아닌 스프링 데이터 JPA'로 '엔티티 객체의 필드(속성)값을 변경 수정 Update 하는 쿼리 >
    //- @Modifying: '순수 스프링 JPA에서의 내장 메소드 em.createQuery 의 내장 메소드 executeUpdate()'를
    //              '스프링 데이터 JPA'에서는 이 어노테이션을 통해 대신해줌.
    //- 'clearAutomatically = true': 벌크 수정 Update 연산'을 호출하는 외부 클래스에서 반드시 작성해줘야 하는
    //                               em.flush(), em.clear() 중에서 'em.clear()'를 여기서 저렇게 사용하여 대체할 수 있음.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
    int bulkAgePlus(@Param("age") int age);




    //=========================================================================================================


    //[ '@EntityGraph'강  11:10 ] 실전! 스프링 데이터 JPA. pdf p42

    //< '@EntityGraph' >

    //- JPA에서 쿼리 실행 시 'EntityGraph'를 사용하여, 연관된 엔티티를 지연로딩(FetchType.LAZY)이 아닌
    //  즉시로딩(FetchType.EAGER)으로 '함께 로딩'할 수 있음.
    //- 사용하는 상황
    //  전제: 간단한 쿼리를 짜야 되긴 하는데, JPQL 쿼리로 작성하긴 귀찮을 경우에 사용함.
    //       복잡한 쿼리를 사용해야 하는 경우에는, 스프링 데이터 JPA의 @Entitygraph가 아닌
    //       JPQL의 fetch join을 사용해서 복잡한 쿼리를 짠다!
    //       특히, @EntityGraph를 사용할 경우, 저~ 아래의 '케이스 4' 경우를 실무에서 주로 사용함.
    //  (1) 외부 클래스에서 해당 메소드의 엔티티 객체를 조회하는 동시에, 그 엔티티와 연관된 엔티티의 조회도 함께 필요한 경우
    //  (2) N+1 문제를 방지하고자 할 때
    //  (3) 기존에 성능 최적화를 위해 해당 엔티티의 필드(속성)으로 설정된 연관된 특정 엔티티를 FetchType.LAZY로 설정했을 때,
    //      그 해당 엔티티를 조회할 때, 그 해당 엔티티의 필드(속성)으로 설정된 연관된 특정 엔티티를 FetchType.EAGER를 사용하여
    //      명시적으로 그 연관 엔티티를 로드하려는 경우
    //- 사용법 순서
    //  (1) '내장 레펏인 JpaRepository를 상속받는 레펏 MemberRepository의 메소드 findMemberFetchJoin()'의 위에
    //      '@EntityGraph'를 선언함. 즉, @EntityGraph를 사용할 메소드를 선언하는 것임.
    //  (2) @EntityGraph의 attributePaths 속성에 즉시로딩 EAGRER로 로딩하고 싶은 필드(속성)의 이름을 넣음
    //  (3) 외부 클래스 어딘가에서 해당 메소드를 호출하면, 설정한 attributtes 속성에 지정된 연관 엔티티를 같이 조회해서 가져옴
    //- 장점
    //  (1) 한 번의 쿼리로, 필요한 엔티티들을 모두 조회하여 최적의 성능을 낼 수 있음
    //  (2) FetchType.LAZY와 FetchType.EAGER 설정을 동적으로 변경 가능하여 코드 유연성 제공
    //  (3) 데이터 조회 속도가 개선됨
    //- 단점
    //  (1) 지나친 사용이 성능 저하를 초래할 수 있음. 조인으로 인해 데이터 건수가 증가하면 추가되는 레코드들 때문임.


    //--------------------------------------------------------------------------------------------------

    //cf) @EntityGraph(atrributes = "team")과
    //    @EntityGraph(atrributes = {"team"})과
    //    @EntityGraph(atrributes = ("team"))은 모두 동일하다. 단순 문법적 차이이며, 의미상 동일함.

    //--------------------------------------------------------------------------------------------------
    
    //< 케이스0: '@EntityGraph'를 사용하지 않고, 그냥 JPQL에서의 fetch join을 사용하는 경우 >
    //
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();


    //--------------------------------------------------------------------------------------------------
    

    //< 케이스1: '사용자 정의 메소드' 를 사용할 경우 >
    @EntityGraph(attributePaths = "team")
    List<Member> findMemberEntityGraph1();


    //--------------------------------------------------------------------------------------------------
    
    
    //< 케이스2: 'JpaRepository의 내장 메소드'를 사용할 경우 >
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();


    //--------------------------------------------------------------------------------------------------
    
    
    //< 케이스3: JPQL 사용자 쿼리를 먼저 작성한 후, @EntityGraph(..)을 추가 작성하여 '즉시로딩 EAGER'로 설정해주고 싶은 경우 >
    //- '케이스2'와 동일한 결과를 도출해냄.
    @EntityGraph(attributePaths = {"team"})
    @Query("SELECT m FROM Member m")
    List<Member> findMemberEntityGraph2();


    //--------------------------------------------------------------------------------------------------
    
    
    //< 케이스4: '쿼리메소드 방식'으로 사용하기 >
    //- '회원 Member 객체'를 조회할 때, 회원 엔티티와 연관 엔티티인 '팀 Team 엔티티 객체'를 동시에 조회해오기
    //- 김영한님은 케이스4를 자주 사용하신다 함.
    //  그리고, 쿼리가 복잡해지면, @EntityGraph를 사용하지 않고 그냥 바로 'JPQL의 fetch join'을 사용한다 함.
    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    //--------------------------------------------------------------------------------------------------


    //< 'JPQL쿼리에서 사용되는 fetch join'과 '스프링 데이터 JPA의 어노테이션 @EntityGraph'의 공통점 및 차이점 >

    //1. 공통점
    //- JPA에서 외부 클래스에서 특정 레퍼지터리를 통해 어떤 엔티티를 조회할 때, 그와 연관된 다른 엔티티도 함께 로딩하는 방법.
    //- 쿼리 실행 시, 관련된 엔티티를 즉시로딩(EAGER)할 수 있으며, N+1 문제를 방지하여 성능을 향상시킴.

    //2. 차이점
    //  (1) fetch join
    //   - 'JPQL 쿼리'에서 'JOIN FETCH'를 사용하여 로딩할 연관 엔티티를 직접 지정함.
    //     e.g) @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    //          List<Order> findAllOrdersWithCustomer();
    //  (2) @EntityGraph
    //   - 메소드 또는 쿼리 위에 어노테이션을 넣어서, 특정 그래프를 사용하도록 알려주는 방식.
    //     e.g) @EntityGraph(attributePaths = "customer")
    //          List<Order> findAll();


    //=========================================================================================================







    //=========================================================================================================
    
    
    
    

}
