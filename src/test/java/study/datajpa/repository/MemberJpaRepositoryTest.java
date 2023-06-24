package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


//스프링부트 업데이트 계속 되면서 이제 '@RunWith(SpringRunner.class)' 이것 안 적어줘도 알아서 다 작동시켜준다!
@Transactional //여러 옵션 어노테이션들 중 반드시 'org.springframework.transaction.annotation.Transactional'을 선택
@SpringBootTest
@Rollback(false) //@Transactional 은 원래 실행 후 다 롤백시키는데, 테스트용에서는 이거를 입력해줌으로써 롤백 안시킴.
class MemberJpaRepositoryTest {

    //테스트 클래스 생성 단축키: ctrl + shift + T.
    //                      '클래스 MemberJpaRepository'의 화면에서 위 단축키 누르면 이 클래스 생성됨

    //=========================================================================================================


    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberRepository memberRepository;


    //=========================================================================================================


    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 07:15~ ]. 실전! 스프링 데이터 JPA

    @Test
    public void testMember(){ //'테스트'는 메소드 단위!!

        //'회원 Member 객체'에서 '아래에 해당하는 사용자 생성자'를 만들었기에, 여기 테스트에서 이렇게
        //내가 원하는 '테스트용 회원 Member 객체'를 만들 수 있음.
        Member member = new Member("memberA");

        //db에 '테스트용 새로운 회원 memberA'를 '저장 save'하고,
        //그 memberA를 '변수 savedMember'에 담음
        Member savedMember = memberJpaRepository.save(member);


        Member findMember = memberJpaRepository.find(savedMember.getId());
        //- 'savedMember.getId()': 'DB에 저장한 해당 회원 정보'중 '그 회원의 id'
        //- 'find(savedMember.getId())': 'DB에 저장한 그 회원의 id'를 통해 DB에 그 회원을 조회해서 그 데이터를
        //                               '자바 Member 객체'로 변환해서 가져옴.


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


    //< 레펏 MemberJpaRepository의 CRUD 메소드 기능들 테스트 >

    @Test
    public void basicCRUD(){

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);


        //=========================================================================================================

        //< 회원 단건 조회 검증 >

        //DB로부터 Member1을 꺼내와서 조회함
        Optional<Member> byId1 = memberJpaRepository.findById(member1.getId());
        Member findMember1 = byId1.get();


        //DB로부터 Member2를 꺼내와서 조회함
        Optional<Member> byId2 = memberJpaRepository.findById(member2.getId());
        Member findMember2 = byId2.get();


        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);


        //=========================================================================================================


        //< 전체 회원 조회 검증 >

        List<Member> members = memberJpaRepository.findAll();

        Assertions.assertThat(members.size()).isEqualTo(2);


        //=========================================================================================================


        //< 전체 회원 카운팅 검증 >

        long memberCount = memberJpaRepository.count();

        Assertions.assertThat(memberCount).isEqualTo(2);


        //=========================================================================================================


        //< 회원 삭제 검증 >

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedMemberCount = memberJpaRepository.count();
        Assertions.assertThat(deletedMemberCount).isEqualTo(0);


        //=========================================================================================================


        //< '특정 회원의 나이가 ~살 보다 더 많은 경우'에 대해 레퍼지터리에 JPQL 쿼리를 직접 만들기'를 검증 >.
        //'레펏 MemberJpaRepository' 내부 메소드임.

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);


        List<Member> greaterMembers = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);


        Assertions.assertThat(greaterMembers.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(greaterMembers.get(0).getAge()).isEqualTo("10");
        //- 'get(n)': 'List 객체의 내장 메소드 get()'. n번 인덱스 원소를 불러옴.
        Assertions.assertThat(greaterMembers.size()).isEqualTo(1);


        //=========================================================================================================

        }


    //=========================================================================================================


    //[ '순수 JPA 페이징과 정렬'강 ] 실전! 스프링 데이터 JPA. p35 pdf

    //< '순수 스프링 JPA'로 사용된 '레펏 MemberJpaRepository의 페이징 메소드 findByPage'의 기능 테스트 >

    @Test
    public void paging() {

        //given
        //테스트용 새로운 회원 객체 5개 생성함.
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));



        //when
        //< '한 페이지 당 보여줄 데이터(여기서는 '한 페이지 당' 보여줄, 주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트)를 만드는
        //  '메소드 findByPage'>

        //'순수 스프링 JPA의 페이징 메소드인 레펏 MemberJpaRepository의 메소드 findByPage'를 호출함.

        int age11 = 10;
        int offset = 0;
        int limit = 3;


        List<Member> resultMembersPerPage = memberJpaRepository.findByPage(age11, offset, limit);



        //< '순수 스프링 JPA'에서의 페이징 할 때 당연히 필요한 '한 페이지 당 보여줄, 주어진 조건에 해당되는 데이터(한 페이지 당
        // 보여줄,주어진 조건에 해당되는 회원 Member 객체들의 목록 리스트')의 개수를 구하기 위한
        // '한 페이지 당이 아닌, 주어진 조건에 해당하는 전체 총 데이터(주어진 조건에 해당되는 총 전체 회원 Member 객체들의 개수')
        // 를 가져오는 '메소드 totalCount' >

        long resultTotalCount = memberJpaRepository.totalCount(age11);


        Assertions.assertThat(resultMembersPerPage.size()).isEqualTo(3);
        Assertions.assertThat(resultTotalCount).isEqualTo(5);

    }


    //=========================================================================================================



    //=========================================================================================================




}