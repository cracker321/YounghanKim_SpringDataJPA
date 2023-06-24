package study.datajpa.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
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


}
