package study.datajpa.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@NoaArgsConstructor: 만약 내가 여기 객체 안에 '사용자 생성자'를 만든다면, 그에 따라 이제 내가 직접 입력해줘야 하는 '기본 생성자'는
//여기서 어노테이션으로 넣으면 안되고, 아래에서 내가 직접 'protected Member(){}' 이렇게 만들어줘야 함!
@Data //단, 이거는 예제이니깐 그런 것이고, 실무에서는 엔티티에 '@Setter'를 넣어주는 것은 지양해야 함
@Entity
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private int age;

    private Team team;



    //=========================================================================================================

    //< Test용으로 간단한 '사용자 생성자'를 만듦 >

    //'테스트 MemberJpaRepositoryTest'에서 '테스트용 새로운 회원 Member 객체'를 만들 때, 일단 간단히 '사용자이름 username'만
    //가지고 있는 테스트용 객체를 만들기 위해, 여기에서 아래 사용자 생성자를 만듦.
    //cf)여기서 이렇게 '사용자 생성자'를 만들었으니, 여기 '클래스 Member'에서 '기본 생성자'도 반드시 하나 만들어줘야 한다!
    //   그리고 그 기본 생성자를 만들기 위해 바로 아래에 'protected Member(){}'를 붙여줌.
    public Member(String username){
        this.username = username;
    }




    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 09:05~ ]. 실전! 스프링 데이터 JPA

    //< 기본 생성자 >
    //- '엔티티 객체의 기본 생성자'를 만들 때는 반.드.시 'protected'로 해놔야 한다.
    //  절대 'private'으로 하면 안된다!!
    //- 프록시 객체 관련해서 뭐 그런 것인듯..강의 해당 부분 참고하기.
    protected Member(){

    }

    //=========================================================================================================


}
