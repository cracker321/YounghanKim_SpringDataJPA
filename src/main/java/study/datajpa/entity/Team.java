package study.datajpa.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


//@NoaArgsConstructor: 만약 내가 여기 Team 객체 안에 '사용자 생성자'를 만든다면(예를 들어, @AllArgsConstructor를 사용할 경우,
//이것도 어쨌든 '사용자 생성자'를 만드는 것임), 그에 따라 이제 내가 직접 입력해줘야 하는 '기본 생성자'는 여기서 어노테이션으로 넣으면 안되고,
//아래에서 내가 직접 'protected Team(){}' 이렇게 만들어줘야 함!
//*****중요*****
//근데, 저~ 아래에 직접 'protected Team(){}'이렇게 작성하는 여기에 'NoArgsConstructor(access = AccessLevel.PROTECTED)'를 적어주면
//'protected 기본 생성자'를 작성한 것과 동일하다. 따라서, 어노테이션으로 대체하자!!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data //단, 이거는 예제이니깐 그런 것이고, 실무에서는 엔티티에 '@Setter'를 넣어주는 것은 지양해야 함
@ToString(of = {"id", "name"})
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    //===================================================================================================

    //< '회원 Member 객체(N. 주인)' : '팀 Team 객체(1)' '양방향' 매핑 >
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>(); //'현재 팀에 소속된 전체 회원들의 목록 List'

    //- 'mappedBy = "team"'
    //: '주인인 Member 객체의 필드 team과 매핑됨'을 의미함.
    //  즉, '주인인 Member 객체의 필드 team'과 '여기 팀 Team 객체 자체'와의 '양방향 관계'를 매핑하는 역할임.

    //===================================================================================================


    //< Test용으로 간단한 '사용자 생성자'를 만듦 >

    //- '테스트 클래스 MemberTest'에서 '테스트용 새로운 팀 Team 객체'를 생성하기 위해 만든 사용자 생성자임
    public Team(String name){
        this.name = name;
    }


    //===================================================================================================


    //< cascade = CascadeType.ALL >

    //: 부모 엔티티(상위 엔티티. 1)에서 발생하는 CRUD, 병합(Merge), 영속화(Persist) 등의 모든 변경이
    //  자식 엔티티(하위 엔티티. N)에 바로 전파되도록 설정하는 옵션.
    //  여기서는 '새로운 팀 엔티티 Team 객체'를 'db에 저장 Insert'할 때, 그와 연관되어 매핑된 '회원 Member 객체'도 자동으로 함께 저장됨.
    //  e.g) db에 새로운 team 객체를 저장 Insert
    /*
    # 부모 엔티티 Team 객체(1) : 자식 엔티티 Member 객체(N)

    < db에 새로운 team 객체를 저장 Insert >

    Team team = new Team();

    team.setName("TeamA");

    Member member1 = new Member();
    member1.setUsername("John");
    member1.setTeam(team);

    Member member2 = new Member();
    member2.setUsername("Jane");
    member2.setTeam(team);

    team.getMembers().add(member1);
    team.getMembers().add(member2);

    entityManager.persist(team);

    이 경우, Team과 Member 객체는 모두 저장되며, Member 객체의 team 속성에는 Team 객체가 연결됩니다.
    따라서 Team 테이블과 Member 테이블에는 새로운 행이 추가되며, Member 테이블의 TEAM_ID 열은 Team 테이블의 기본 키(id)와 관계가
    형성됩니다.

    //===================================================================================================

    < db에 기존 저장되어 있는 Team 객체를 수정 Update >

    Team team = em.find(Team.class, 1L); //'팀 id가 1'인 '팀 Team 객체'를 db로부터 조회해서 가져옴.

    team.setName("New Team"); //수정사항 1) '팀 Team 객체'의 이름을 '수정(update)'함.

    Member member = new Member();
    member.setUsername("Mark");
    member.setTeam(team);

    team.getMembers().add(member); //수정사항 2) '새로운 회원 Member 객체(=이름이 Mark)'를 '기존 팀 Team 객체의 내부'에 추가함

    em.merge(team); //변경내용(수정사항 1)과 2))를 기존 db에 있는 Team 객체에 병합하여 최종 업데이트함.
                    //따라서 이제, 1) 기존 팀 객체의 '팀 이름'이 수정되었고, 2) 새로운 회원 객체가 기존 팀 내부에 추가됨.

    //===================================================================================================

    < db에 기존 저장되어 있는 Team 객체를 삭제 Delete >
    Team team = em.find(Team.class, 1L); //'팀 id가 1'인 팀 Team 객체를 db로부터 조회해서 가져옴

    em.remove(team); //db로부터 가져온 해당 Team 객체를 삭제함.

    : 팀 id가 1인 Team 객체를 삭체할 때, 그 해당 Team 객체에 속한(연관된) 모든 회원 Member 객체도 자동으로 연동되어 삭제됨.
      (즉, 여기 예시에서는 '이름이 Jane인 회원 객체', '이름이 John인 회원 객체', '이름이 Mark인 회원 객체'가 모두 다 연동되어 삭제됨)

    //===================================================================================================

    이렇게, CascadeType.ALL은 부모 엔티티(1. Team 객체)의 모든 변경이 자식 엔티티(N. Member 객체)에 전파되므로, 편리하게 관리 가능.
    */

    //===================================================================================================


    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 09:05~ ]. 실전! 스프링 데이터 JPA

    //< 기본 생성자 >
    //- '엔티티 객체의 기본 생성자'를 만들 때는 반.드.시 'protected'로 해놔야 한다.
    //  절대 'private'으로 하면 안된다!!
    //- 프록시 객체 관련해서 뭐 그런 것인듯..강의 해당 부분 참고하기.
    //- *****중요*****
    //  근데, 아래처럼 여기에 작성하는 대신, 그냥 저~ 위에 'NoArgsConstructor(access = AccessLevel.PROTECTED)'를 적어주면
    //  아래와 같이 'protected 기본 생성자'를 작성한 것과 동일하다. 어노테이션으로 대체하자!!

//    protected Team(){
//
//    }

    //===================================================================================================

}
