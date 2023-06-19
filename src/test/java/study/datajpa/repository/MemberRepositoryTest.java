package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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


    @Test
    public void testMember(){ //'테스트'는 메소드 단위!!

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


}