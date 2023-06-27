package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;


//--------------------------------------------------------------------------------------------------


//[ '사용자 정의 리포지토리 구현'강  03:00 ] 실전! 스프링 데이터 JPA. pdf p47


//- 순서
//  1.'레퍼지터리(인터페이스) MemberRepository'가 '다중상속' 하고 있는 인터페이스 2개 중에 하나가
//  바로 이 '사용자 정의 인터페이스 MemberRepositoryCustom'이고,
//  2.그 '사용자 정의 인터페이스 MemberRepositoryCustom' 내부의 추상 메소드들의 구현부들을 작성해주기 위해
//  '클래스 MemberRepositoryCustom'을 작성해줌.
//  3.'구체화하는 클래스 MemberRepositoryCustom'의 내부에 '구체화 메소드 findMemberCustom'을 작성해줌.

//- 이를 통해,
//  1.외부 클래스 어딘가(여기서는 '클래스 MemberRepsoitoryTest')에서 '레퍼지터리(인터페이스) MemberRepsotory'를 참조하여
//  2.'MemberRepositoryImpl의 내부 메소드 findMemberCustom'을 호출
//  ('memberRepository.findMemberCustom' 이렇게 작성)할 수 있게 된다!

//- 주로 복잡한 쿼리르 사용할 때 활용하는 'Query DSL'을 사용할 때, 이 '사용저 정의 인터페이스 ~~Custom'을 실무에서 자주 사용함.


//--------------------------------------------------------------------------------------------------

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    //'extends'가 아닌 'implements'이다!


    private final EntityManager em;

    //# '의존성 주입받는 객체의 생성자'를 통한 '객체(여기서는 'EntityManager 객체') 의존성 주입 DI'
    //- private final을 통해 의존성을 주입받으면, 아래처럼 '주입받는 현재 클래스의 생성자의 매개변수로',
    //  '주입하는 객체의 생성자를 그 매개변수로 넣고', '그 주입하는 객체를 생성함으로 의존성을 주입'함.
    //- 'MemberRepsoitoryImpl 객체'가 서버 실행하는 과정에서 생성될 때, 이 객체가 필요로 하는 의존성을 외부에서 주입받는 방식임.
    //  이는 객체 간의 결합도를 낮추고, 유연성과 재사용성을 향상시키는 데 도움을 줌.
    //- 생성자를 통한 의존성 주입은, 변경이 불가능한 불변성(Immutablility)을 가진 객체를 생성하는 데 도움을 줌.
    //- '필드주입('@Autowried')'보다 더 안정적임.
    //- 아래 생성자를 작성하는 것은 귀찮으니, 그 대신 현재 클래스 MemberRespositoryImpl 위에 '@RequiredConstructor'를 작성하면 된다.

//    public MemberRepositoryImpl(EntityManager em) {
//        this.em = em;
//    }


    //=========================================================================================================


    //[ '사용자 정의 리포지토리 구현'강  03:00 ] 실전! 스프링 데이터 JPA. pdf p47

    //- '순수 스프링 JPA'를 사용한 '사용자 정의 레퍼지터리 메소드'임.
    //  '사용자 정의 레퍼지터리 메소드'는 '그 메소드 내부 구현부(대괄호 {} 내부 부분)를 내가 원하는 로직으로 작성한 메소드'임
    //

    //< 순수 JPA를 사용하여 사용자 정의 레퍼지터리 만들기 >
    @Override
    public List<Member> findMemberCustom(){

        return em.createQuery("SELECT m FROM Member m")
                .getResultList();


    }




    //--------------------------------------------------------------------------------------------------



}
