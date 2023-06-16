package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;


//스프링부트 업데이터 계속 되면서 이제 '@RunWith(SpringRunner.class)' 이것 안 적어줘도 알아서 다 작동시켜준다!
@Transactional //여러 옵션 어노테이션들 중 반드시 'org.springframework.transaction.annotation.Transactional'을 선택
@SpringBootTest
class MemberJpaRepositoryTest {



    //=========================================================================================================


    @Autowired
    MemberJpaRepository memberJpaRepository;


    //=========================================================================================================


    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 07:15~ ]. 실전! 스프링 데이터 JPA

    @Test
    public void testMember(){

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
        assertThat(findMember).isEqualTo(member);


    }

    //=========================================================================================================



}