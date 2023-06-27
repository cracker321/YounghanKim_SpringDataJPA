package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;




@SpringBootTest
@Transactional
@Rollback(false) //@Transactional 은 원래 실행 후 다 롤백시키는데, 테스트용에서는 이거를 입력해줌으로써 롤백 안시킴.
class MemberRepositoryTest {

    //테스트 클래스 생성 단축키: ctrl + shift + T.
    //                      '클래스 MemberRepository'의 화면에서 위 단축키 누르면 이 클래스 생성됨

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;



    @Test
    public void testMember() { //'테스트'는 메소드 단위!!


        //=========================================================================================================

        //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 18:50~ ]. 실전! 스프링 데이터 JPA

        //'회원 Member 객체'에서 '아래에 해당하는 사용자 생성자'를 만들었기에, 여기 테스트에서 이렇게
        //내가 원하는 '테스트용 회원 Member 객체'를 만들 수 있음.
        Member member = new Member("memberA");


        //db에 '테스트용 새로운 회원 memberA'를 '저장 save'하고,
        //그 memberA를 '변수 savedMember'에 담음
        Member savedMember = memberRepository.save(member);


        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get(); //이렇게 하는 건 좋은 방법이 아닌데 그냥 간단한 예제 테스트코드이니 그냥 이렇게 한다고 함.
        //제대로 작성해야 할 땐 'orElse..' 이렇게 해줘야 함.
        //- 'savedMember.getId()': 'DB에 저장한 해당 회원 정보'중 '그 회원의 id'
        //- 'findById(savedMember.getId())': 'DB에 저장한 그 회원의 id'를 통해 DB에 그 회원을 조회해서 그 데이터를
        //                                   '자바 Member 객체'로 변환해서 가져옴.
        //- 'Optional<Member>': DB에 그 해당 회원의 id가 존재할 수도 있고, 없을 수도 있기 때문에,
        //                      '내장 JpaRepository의 내장 메소드 findById()의 반환값은 Optional<>로 설정되어 있음'.

        /*
        //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 20:50~ ]. 실전! 스프링 데이터 JPA

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        - 원래 Optional<>로 받아오기 때문에, 여기서 orElse().. 이런거 작성해줘야 하는데,
          여기서는 간단한 예제이고 테스트코드이기에 그런 것 안 하고, 아래처럼 맨 끝에 'get()' 붙여주면,
          Optional<Member>를 알아서 까줘서 그냥 'Member findMember' 이렇게 작성이 가능해진다.
          뭐 별로 좋은 방법은 아님.

         */


        //< 테스트 1 >
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        //'Assertions'는 '생략해도 된다!'. 즉, 그냥 'asserThat~'으로 시작해도 됨.


        //< 테스트 2 >
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());


        //< 테스트 3 >
        //- 'findMember'와 'member'가 같은지 여부 검사
        //  findMember == memer
        Assertions.assertThat(findMember).isEqualTo(member);
    }


    //=========================================================================================================


    //[ '스프링 데이터 JPA 페이징과 정렬'강 04:00 ] 실전! 스프링 데이터 JPA. p37 pdf


    //< '순수 스프링 JPA가 아닌', '스프링 데이터 JPA의 페이징 메소드인 레펏 MemberRepository의 메소드 findByAge' 기능 테스트 >

    @Test
    public void paging() {


        //--------------------------------------------------------------------------------------------------

        //given
        //테스트용 새로운 회원 객체 5개 생성함.
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        //--------------------------------------------------------------------------------------------------


        //순서1)
        //< Pagination 설정을 위한 'PageRequest 객체' 생성: 페이지 세팅 작업 >

        //- 'PageRequest 객체'를 생성하여 '페이지 번호(page), 한 페이지 당 보여줄 회원 객체 개수(size), 정렬 방식'을 설정함.
        //- 아래에서는 '첫 번째 페이지('인덱스 0')를 요청(page)하며, 한 페이지에 3개의 회원 개수(size),
        //  'DB의 컬럼 username'을 기준으로 내림차순 정렬'을 설정


        //
        //- 'Sort.by(Sort.Direction.DESC, "username"): 이 정렬은 내가 만약 정렬키는 기능을 원하지 않으면, 제거해도 됨.
        //- 'username': DB의 '컬럼(자바객체의 필드(속성)) username'을 기준으로 '내림차순 DESC' 정렬시키는 것임.
        //- 첫 번째 파라미터 'page': 페이지의 인덱스, 즉 페이지의 번호임. 참고로 페이지가 '0'부터 시작하니깐,
        //                        페이징을 1부터 시작하고 싶으면, '1'로 변경해줘야 함.
        //- 두 번째 파라미터 'size': 한 페이징 당 보여줄 게시글의 수. '순수 JPA에서의 인수 limit'과 동일함.
        PageRequest pageRequest = PageRequest
                .of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //--------------------------------------------------------------------------------------------------

        //순서2)
        //< '메소드 findByAge'를 사용하여 주어진 조건('age11 = 10')에 해당되는 데이터(회원 목록 '페이지')를 DB로부터 가져오기 >
        //- 이렇게 하면, 두 개의 매개변수인 1)'주어진 조건('age11 = 10')'과 2)'페이지 세팅('PageRequest 객체)'을 기준으로
        //  '조회 조건(해당 나이 조건 & 페이지 세팅 조건)에 맞는 회원 목록 페이지'를 담은 결과를 반환해줌.

        //
        //when
        //< '순수 스프링 JPA가 아닌' '스프링 데이터 JPA의 페이징 인터페이스 Paegable'에서의
        //'한 페이지 당 보여줄 데이터(여기서는 '한 페이지 당' 보여줄, 주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트)를
        //만드는 '메소드 findByAge'>

        //'순수 스프링 JPA가 아닌', '스프링 데이터 JPA의 페이징 메소드인 레펏 MemberRepository의 메소드 findByAge'를 호출함.

        int age11 = 10;

        Page<Member> resultMembersPerPage = memberRepository.findByAge(age11, pageRequest);

        //--------------------------------------------------------------------------------------------------

        //순서3)
        //< '조회 조건에 맞는 회원 목록 페이지'의 '회원 객체'들을 '회원 DTO 객체'로 변환시키기 >

        //< 페이지를 유지하면서 'Member 엔티티를 DTO로 변환하기 >. 25:40~
        //- 'Member 엔티티'를 그대로 넘겨주면 안되기 때문에, 당연히 'Member DTO로 변환해줌!
        //- 'map()'을 사용하여 Dto를 변환시켜 주는 것임.
        //- 여기서는 '회원 엔티티 Member 객체의 필드(속성)들 중 필드 id와, 필드 username'만 포함시켜 DTO객체로 변환시킴.

        Page<MemberDto> toResulttMembersPerPageDto = resultMembersPerPage
                .map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        //--------------------------------------------------------------------------------------------------

        //순서4)
        //< 페이징 결과 활용 >


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getContent()'
        //  : 현재 페이지의 데이터(= 회원 Member 객체 목록 리스트)를 가져옴
        List<Member> content = resultMembersPerPage.getContent();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalElements()'
        //  : 주어진 조건(age11 = 10)을 만족하는 회원들이 카운팅된 '숫자'를 가져옴.
        //  '순수 스프링 JPA에서의' '주어진 조건에 해당하는 전체 총 데이터를 보여주는 메소드 totalCount'를 대신함.
        long totalElements = resultMembersPerPage.getTotalElements();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getNumber()'
        //  : 현재 페이지의 인덱스(=페이지 번호)를 가져오는 메소드
        resultMembersPerPage.getNumber();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalPages()'
        //  : 총 페이지의 개수
        resultMembersPerPage.getTotalPages();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 isFirst()'
        //  : 현재 페이지가 첫 번째 페이지인지 여부를 boolean 타입으로  확인한 결과(T or F)를 반환함
        resultMembersPerPage.isFirst();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 hasNext()'
        //  : 다음 페이지가 있는지 여부를 boolean 타입으로 확인한 결과(T or F)를 반환함.
        resultMembersPerPage.hasNext();

        //--------------------------------------------------------------------------------------------------


        //[ '스프링 데이터 JPA 페이징과 정렬'강 08:00 ] 실전! 스프링 데이터 JPA. p37 pdf

        // < '스프링 데이터 JPA에서의 페이징'에서는 '주어진 조건에 해당하는 전체 총 데이터를 보여주는 메소드 totalCount' 등을
        // 작성해줄 필요 없다!! 메소드 totalCount와 같은 카운팅 메소드는 오직 '순수 스프링 JPA에서의 페이징'할 때나 필요함.

        //< '순수 스프링 JPA'에서의 페이징 할 때 당연히 필요한 '한 페이지 당 보여줄, 주어진 조건에 해당되는 데이터(한 페이지 당
        // 보여줄,주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트')의 개수를 구하기 위한
        // '한 페이지 당이 아닌, 주어진 조건에 해당하는 전체 총 데이터(주어진 조건에 해당되는 총 전체 회원 Member 객체들의 개수')
        // 를 가져오는 '메소드 totalCount' >

        //long resultTotalCount = memberRepository.totalCount(age11);


    }

    //=========================================================================================================

    //[ '스프링 데이터 JPA 페이징과 정렬'강 14:00 ] 실전! 스프링 데이터 JPA. p37 pdf

    //< 'Slice' 기능 >
    //- '스프링 데이터 JPA의 페이징 메소드인 레펏 MemberRepository의 메소드 findByAgeSlice' 기능 테스트 >

    @Test
    public void slice() {


        //--------------------------------------------------------------------------------------------------

        //given
        //테스트용 새로운 회원 객체 5개 생성함.
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        //--------------------------------------------------------------------------------------------------


        //순서1)
        //< Pagination 설정을 위한 'PageRequest 객체' 생성: 페이지 세팅 작업 >

        //- 'PageRequest 객체'를 생성하여 '페이지 번호(page), 한 페이지 당 보여줄 회원 객체 개수(size), 정렬 방식'을 설정함.
        //- 아래에서는 '첫 번째 페이지('인덱스 0')를 요청(page)하며, 한 페이지에 3개의 회원 개수(size),
        //  'DB의 컬럼 username'을 기준으로 내림차순 정렬'을 설정

        //- 'Sort.by(Sort.Direction.DESC, "username"): 이 정렬은 내가 만약 정렬키는 기능을 원하지 않으면, 제거해도 됨.
        //- 'username': 'DB'의 '컬럼(자바객체의 필드(속성)) username'을 기준으로 '내림차순 DESC' 정렬시키는 것임.
        //- 첫 번째 파라미터 'page': 페이지의 인덱스, 즉 페이지의 번호임
        //- 두 번째 파라미터 'size': 한 페이징 당 보여줄 게시글의 수. '순수 JPA에서의 인수 limit'과 동일함.
        PageRequest pageRequest = PageRequest
                .of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //--------------------------------------------------------------------------------------------------

        //순서2)
        //< '메소드 findByAgeSlice'를 사용하여 주어진 조건('age11 = 10')에 해당되는 데이터(회원 목록 '페이지')를 DB로부터 가져오기 >
        //- 이렇게 하면, 두 개의 매개변수인 1)'주어진 조건('age11 = 10')'과 2)'페이지 세팅('PageRequest 객체)'을 기준으로
        //  '조회 조건(해당 나이 조건 & 페이지 세팅 조건)에 맞는 회원 목록 페이지'를 담은 결과를 반환해줌.

        //
        //when
        //< '순수 스프링 JPA가 아닌' '스프링 데이터 JPA의 페이징 인터페이스 Paegable'에서의
        //'한 페이지 당 보여줄 데이터(여기서는 '한 페이지 당' 보여줄, 주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트)를
        //만드는 '메소드 findByPage'>

        //'순수 스프링 JPA가 아닌', '스프링 데이터 JPA의 페이징 메소드인 레펏 MemberRepository의 메소드 findByPage'를 호출함.

        int age11 = 10;

        Slice<Member> resultMembersPerPage = memberRepository.findByAgeSlice(age11, pageRequest);


        //--------------------------------------------------------------------------------------------------


        //순서3)
        //< '조회 조건에 맞는 회원 목록 페이지'의 '회원 객체'들을 '회원 DTO 객체'로 변환시키기 >

        Slice<Member> resultSliceMembersPerPage = memberRepository.findByAgeSlice(age11, pageRequest);


        Slice<MemberDto> toResultSliceMembersPerPageDto = resultSliceMembersPerPage
                .map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        //--------------------------------------------------------------------------------------------------

        //순서4) < 페이지 결과 활용 >

        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getContent()'
        //  : 현재 페이지의 데이터를 가져옴
        List<Member> content = resultSliceMembersPerPage.getContent();


        //< 'Slice'에서는 '내장 메소드 getTotalElements' 사용할 필요 X >
        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalElements()'
        //  : '순수 스프링 JPA에서의' '주어진 조건에 해당하는 전체 총 데이터를 보여주는 메소드 totalCount'를 대신함.
        //     long totalElements = resultMembersPerPage.getTotalElements();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getNumber()'
        //  : 현재 페이지의 번호를 가져오는 메소드
        resultSliceMembersPerPage.getNumber();


        //< 'Slice'에서는 '내장 메소드 getTotalPages' 사용할 필요 X >
        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 getTotalPages()'
        //  : 총 페이지의 개수
        //resultSliceMembersPerPage.getTotalPages();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 isFirst()'
        //  : 현재 페이지가 첫 번째 페이지인지 여부를 boolean 타입으로  확인한 결과(T or F)를 반환함
        resultSliceMembersPerPage.isFirst();


        //# '스프링 데이터 JPA의 페이징 인터페이스 Pageable의 내장 메소드 hasNext()'
        //  : 다음 페이지가 있는지 여부를 boolean 타입으로 확인한 결과(T or F)를 반환함.
        resultSliceMembersPerPage.hasNext();

        }
        //=========================================================================================================


        //[ '벌크성 수정 쿼리'강  05:00 ] 실전! 스프링 데이터 JPA. pdf p41

        @Test
        public void bulkUpdate(){

            memberRepository.save(new Member("member1", 10));
            memberRepository.save(new Member("member2", 19));
            memberRepository.save(new Member("member3", 20));
            memberRepository.save(new Member("member4", 21));
            memberRepository.save(new Member("member5", 40));




            int resultCount = memberRepository.bulkAgePlus(20);
            //*****중요*****
            //'스프링 데이터 JPA'에서 'update 수정 벌크 연산'을 할 때에는, 반드시 em.flush를 해줘야 한다!!
            //이런 '벌크 연산' 이후에는 반드시 강제로 em.flush, em.clear()를 작성해줘야 함.
            //em.flush(); //flush를 해줌으로써, 혹시라도 변경 쿼리 날렸는데 db에 적용되지 않는 부분이 있는 경우, 확실히 다시 db에
                        //쿼리 날려주는 것임.
            //em.clear(); //이후, 영속성 컨텍스트에 혹시라도 남아 있는 데이터를 다 없애버림.
                        //이후 다른 곳에서 다른 쿼리 다른 작업을 문제 없이 할 수 있도록 해주는 것임.
            //(단, '레펏 MemberRepository의 메소드 bulkAgePlus'의 위에 '어노테이션 @Modifying(clearAutomatically = true)'를
            // 작성해줌으로써, 여기서 'em.clear()' 작성을 생략해줘도 됨.)
            //- @Modifying: '순수 스프링 JPA에서의 내장 메소드 em.createQuery 의 내장 메소드 executeUpdate()'를
            //              '스프링 데이터 JPA'에서는 이 어노테이션을 통해 대신해줌.
            //- 'clearAutomatically = true': 벌크 수정 Update 연산'을 호출하는 외부 클래스에서 반드시 작성해줘야 하는
            //                               em.flush(), em.clear() 중에서 'em.clear()'를 여기서 저렇게 사용하여 대체할 수 있음.






            Assertions.assertThat(resultCount).isEqualTo(3);

        }

        //=========================================================================================================


        //[ '@EntityGraph'강  00:00 ] 실전! 스프링 데이터 JPA. pdf p42
        //'클래스 MemberRepository'의 내부에 설명 다 해놓음.

        @Test
        public void findMemberLazy(){

        //given
        //member1은 teamA를 참조하고, member2는 teamB를 참조한다.

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA); //'레펏 TeamRepository는 extends JpaRepository<'Team', Long> 에서
                                    //'Team 엔티티'를 기본으로 하고 있기 때문에, '내장 메소드 save()'가 teamA 를 받기 가능함.
        teamRepository.save(teamB);


        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush(); //위에 '내장 메소드 save'로 작성한 것들이 먼저는 영속성 컨텍스트에 보관되어 있다가,
                    //em.flush()를 작성하면 이제 db에 다 쿼리로 날려버림.
        em.clear(); //그런 후, 영속성 컨텍스트에 혹시 모르게 남아 있는 것들을 다음 영속성 컨텍스트 작업을 위해 다 비워줌.


        //------------------------------------------------------------------------------------------------


        //when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {

            System.out.println(member.getUsername());
                
        }

    }


    //=========================================================================================================


    //[ '사용자 정의 리포지토리 구현'강  03:00 ] 실전! 스프링 데이터 JPA. pdf p47


    //- 순서
    //  1.'레퍼지터리(인터페이스) MemberRepository'가 '다중상속' 하고 있는 인터페이스 2개 중에 하나가
    //  바로 이 '사용자 정의 인터페이스 MemberRepositoryCustom'이고,
    //  2.그 '사용자 정의 인터페이스 MemberRepositoryCustom' 내부의 추상 메소드들의 구현부들을 작성해주기 위해
    //  '클래스 MemberRepositoryCustom'을 작성해줌.
    //  3.'구체화하는 클래스 MemberRepositoryCustom'의 내부에 '구체화 메소드 findMemberCustom'을 작성해줌.

    //- 이를 통해,
    //  1.외부 클래스 어딘가(여기서는 '클래스 MemberRepsoitoryTest')에서 '레퍼지터리(인터페이스) MemberRepsotory'를 참조하여
    //  2.'MemberRepositoryImpl의 내부 메소드 findMemberCustom을
    //  호출('memberRepository.findMemberCustom' 이렇게 작성)할 수 있게 된다!

    //- 주로 복잡한 쿼리르 사용할 때 활용하는 'Query DSL'을 사용할 때, 이 '사용저 정의 인터페이스 ~~Custom'을 실무에서 자주 사용함.


    //- 여기가 바로 그 '외부 클래스 MemberRepositoryTest'이고,
    //  여기서 memberRepository.findMemberCustom 을 호출할 것임.


    @Test
    public void callCustom(){


        List<Member> memberCustom = memberRepository.findMemberCustom();


    }


    //=========================================================================================================


}

