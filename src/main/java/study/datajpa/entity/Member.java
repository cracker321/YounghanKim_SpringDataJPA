package study.datajpa.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//@NoaArgsConstructor: 만약 내가 여기 Member 객체 안에 '사용자 생성자'를 만든다면(예를 들어, @AllArgsConstructor를 사용할 경우,
//이것도 어쨌든 '사용자 생성자'를 만드는 것임), 그에 따라 이제 내가 직접 입력해줘야 하는 '기본 생성자'는 여기서 어노테이션으로 넣으면 안되고,
//아래에서 내가 직접 'protected Member(){}' 이렇게 만들어줘야 함!
//*****중요*****
//근데, 저~ 아래에 직접 'protected Member(){}'이렇게 작성하는 여기에 'NoArgsConstructor(access = AccessLevel.PROTECTED)'를 적어주면
//'protected 기본 생성자'를 작성한 것과 동일하다. 따라서, 어노테이션으로 대체하자!!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data //단, 이거는 예제이니깐 그런 것이고, 실무에서는 엔티티에 '@Setter'를 넣어주는 것은 지양해야 함
@ToString(of = {"id", "username", "age"}) //여기서 절대 '필드 team'은 추가하면 안됨. 연관관계를 타서 Team 객체도 다 출력해버리게 됨.
                                          //그럼 Team객체와 또 연관되어 있는 Member 객체가 또 호출되고, 계속 무한루프 돌게 됨.
//'@ToString'
//- 클래스 레벨에서 사용되며, 해당 클래스에  '자바 메소드 toString()'을 자동으로 생성해줌.
//  '@ToString'은 해당 클래스의 모든 인스턴스 변수를 포함하여 문자열을 생성해줌.
//- 메소드 toString()은 '객체를 문자열로 표현'할 때 사용됨.
//- 포스트맨으로 JSON객체 요청하고 응답받을 때, '반환되는 문자열은 필드 id, 필드 username, 필드 age의 '값''이다!
//  즉, JSON 객체 응답 결과는' Member(id = 1, username = John, age = 30)'와 같이 되는 것이다!
@Entity
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID") //DB테이블의 pk 이름은 이렇게 관례상 언더바(_)를 사용해서 만든다.
    private Long id;

    private String username;

    private int age;


    //< '회원 Member 객체(N. 주인)' : '팀 Team 객체(1)' '양방향' 매핑 >
    @ManyToOne(fetch = FetchType.LAZY)
    //'@ManyToOne', 'OneToOne'은 '기본설정이 즉시로딩 EAGER'이므로, 여기서 반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
    //*****중요*****
    //- JPA에서의 모든 연관관계는 기본적으로 다 지연로딩 LAZY로 해야 한다!
    //- '외부 클래스 어딘가에서' '여기의 Member 객체를 조회'할 때,
    // 'Member 객체의 내부의 필드(속성)인 Team team 객체는 조회하지 않고,
    //  Team 객체를 일단 가짜 객체로 '지연하여' 가지고 있다가, '이후' 'Team team 객체의 값이 실제 필요할 때(사용할 때)
    //  Member 객체 내부의 필드(속성)인 Team team 객체'를 db로부터 가져오는 것임.
    @JoinColumn(name = "TEAM_ID") //'주인이 아닌 테이블 TEAM의 PK 컬럼인 TEAM_ID'
                                  //= '주인인 테이블 MEMBER의 FK 컬럼인 TEAM_ID'
    private Team team; //'현재의 회원이 가지고 있는(소속되어 있는) 팀에 대한 정보'

    //=========================================================================================================

    //< Test용으로 간단한 '사용자 생성자 1'를 만듦 >

    //- '테스트 클래스 MemberJpaRepositoryTest'에서 '테스트용 새로운 회원 Member 객체'를 만들 때,
    //  일단 간단히 '사용자이름 username'만 가지고 있는 테스트용 객체를 만들기 위해, 여기에서 아래 사용자 생성자를 만듦.
    //cf)여기서 이렇게 '사용자 생성자'를 만들었으니, 여기 '클래스 Member'에서 '기본 생성자'도 반드시 하나 만들어줘야 한다!
    //   그리고 그 기본 생성자를 만들기 위해 바로 아래에 'protected Member(){}'를 붙여줌.
    public Member(String username){
        this.username = username;

    }



    //< Test용으로 간단한 '사용자 생성자 2'를 만듦 >

    //- '테스트 클래스 MemberTest'에서 사용할 '테스트용 새로운 회원 Member 객체'를 생성하기 위해 만든 사용자 생성자임
    public Member(String username, int age, Team team){

        this.username = username;
        this.age = age;
        this.team = team;


        if(team != null) {
            changeTeam(team);
        }

    }



    //< Test용으로 간단한 '사용자 생성자 2'를 만듦 >

    //- '테스트 클래스 MemberJpaRepository'에서 사용할 '테스트용 새로운 회원 Member 객체'를 생성하기 위해 만든 사용자 생성자임

    public Member(String username, int age) {

        this.username = username;
        this.age = age;

    }



    //=========================================================================================================

    //[ '스프링 데이터 JPA와 DB 설정, 동작확인'강. 09:05~ ]. 실전! 스프링 데이터 JPA

    //< 기본 생성자 >
    //- '엔티티 객체의 기본 생성자'를 만들 때는 반.드.시 'protected'로 해놔야 한다.
    //  절대 'private'으로 하면 안된다!!
    //- 프록시 객체 관련해서 뭐 그런 것인듯..강의 해당 부분 참고하기.
    //- *****중요*****
    //  근데, 아래처럼 여기에 작성하는 대신, 그냥 저~ 위에 'NoArgsConstructor(access = AccessLevel.PROTECTED)'를 적어주면
    //  아래와 같이 'protected 기본 생성자'를 작성한 것과 동일하다. 따라서, 어노테이션으로 대체하자!!

//    protected Member(){
//
//    }


    public void changeTeam(Team team){

        if(this.team != null){

            team.getMembers().remove(this);
            //- 연관관계 편의 메소드는, '여기 내 현재 엔티티 Member'의 변화에 따라 자동으로 그 변동을 주고 싶은
            //  '상대방 엔티티 객체 team'에 그 변화를 연동시키기 위함이니,
            //  당연히, 구체적인 실행 로직의 중심을 '상대방 엔티티 객체 team'으로 해서 작성해야 함.
        }


        this.team = team;


        if(team != null){
            team.getMembers().add(this);

        }

    }
    //=========================================================================================================

    /*

    < 연관관계 편의 메소드란? >

    - '양방향 매핑'에서 사용. 주로 '다대일', '일대다' 관계에서 사용됨.
    - '주인 엔티티(N)'가 변경될 때, '주인이 아닌 엔티티(1)'도 '자동으로 변경'됨.
      '주인 엔티티(N) 클래스에 작성된 연관관계 편의 메소드'가 호출되면, '주인이 아닌 엔티티(1)의 연관관계'도 함께 설정됨.
    - '주인 엔티티(N) 클래스'에 '반대편 엔티티를 인자값으로 받아 설정하는 메소드 로직'을 작성함.
      그 메소드의 이름은 일반적으로 'addXXX', 'removeXXX'와 같은 형태로 작성함.
    - 예를 들어, 게시물(Post)와 댓글(Comment)이 '일대다 관계'일 때, '게시물(주인. 1) 엔티티 클래스의
      내부에 '메소드 addComment()'를 작성'하여, '댓글 Comment 를 추가'할 수 있음.
    - 양방향 매핑 연관관계를 객체 지향적으로 다룰 수 있으며, 중복 코드를 줄이고 코드 가독성을 향상시킴.
    - 그러나, 잘못 사용할 경우, 무한루프나 스택오버플로우 등의 문제가 발생할 수 있음.


    //=========================================================================================================

    < 예시 >

    - 다대일 '양'방향 매핑.
      주인 엔티티(N. 회원 Member 객체) : 주인 아닌 엔티티(1. 팀 Team 객체)

    //=========================================================================================================

      < 주인 엔티티(N. 회원 Member 객체) >

      @Entity
      public class Member{

        ...

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TEAM_ID")
        private Team team;

        ...

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 1 >

        public void setTeam(Team team){
        //- 'Team team': '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 팀 team'
        //- 'void': 어차피, 특별한 반환값을 내놓지 않아도 되고, 그냥 서로 간의 관계를 재설정 또는..


            # Step 1: 이전에 해당 회원이 속해 있던 팀과의 관계를 해제함

            if(this.team != null){
                this.team.getMembers().remove(this);
                }

            //- 연관관계 편의 메소드는, '여기 내 현재 엔티티 Member'의 변화에 따라 자동으로 그 변동을 주고 싶은
            //  '상대방 엔티티 객체 team'에 그 변화를 연동시키기 위함이니,
            //  당연히, 구체적인 실행 로직의 중심을 '상대방 엔티티 객체 team'으로 해서 작성해야 함.
            //- 'if(this.team != null)': 현재 회원('this')이 기존에 어떤 팀이든 소속되어 있던 팀이 있는가 를 확인하는 것.
            //- 'this.team.getMembers().remove(this)': 현재 회원('this')을 이전 팀의 회원 목록에서 제거함.
            //    - 'this.team': 현재 회원('this')의 이전 팀('this.team')에 접근함.
            //    - 'getMembers()': 현재 회원의 이전 팀의 '회원 전체 목록'에 접근함.
            //    - 'remove(this)': 현재 회원의 이전 팀의 '회원 전체 목록'에서 '현재 회원'을 '제거'함.
            //                      '메소드 remove': '인터페이스 List'의 내장 메소드.
            //- 여기서 'this.team'은 '현재 다루고 있는 회원 Member 객체의 필드 team'을 의미함.
            //  즉, 'this'는 '현재 회원 Member 객체'을 의미하고,
            //  'this.team'은 '현재 회원이 현재 가지고 있는(소속되어 있는) 팀'을 의미함.
            //- *****중요*****
            // 'this'는 '해당 메소드(여기서는 메소드 setTeam())를 호출하는 객체, 즉 현재 인스턴스'를 가리킴.
            //  여기서 'this'는 '현재의 회원 Member 객체 그 자체'를 의미함.
            //- *****중요*****
            //  연관관계 편의 메소드는, '여기 내 현재 엔티티 Member'의 변화에 따라 자동으로 그 변동을 주고 싶은
            //  '상대방 엔티티 객체 team'에 그 변화를 연동시키기 위함이니,
            //  당연히, 구체적인 실행 로직의 중심을 '상대방 엔티티 객체 team'으로 해서 작성해야 함.
            //- *****중요*****
            //  'Step 1'에서의 'this.team'은 '외부 클래스 어딘가에서 현재 메소드를 새로운 인자값으로 들어온 새롭게 주어질 team이
            //   들어오기 '전'에 원래 현재 회원이 소속되어 있던 기존 팀'.
            //  'Step 3'에서의 'team'은 '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 team'.



            # Step 2: 외부 클래스 어디에선가 이 메소드 setMember를 새로운 인자값(= 새로운 팀 'team')으로 호출할 때,
                      그 때 주어질 새로운 팀('team')을 '현재 회원의 팀('this.team')'으로 새롭게 설정함.
            this.team = team;



            # Step 3: 새롭게 주어진 팀('team')에 현재 멤버('this')를 추가('add(this)')함
            if(team != null){
                team.getMembers().add(this);
            }

            //- 'if(team != null)': '새롭게 주어진 팀 Team 객체'가 null이 아닌 경우에 실행됨.
            //                      즉, '새롭게 주어진 팀 Team 객체의 실체'가 존재한다는 것을 의미함
            //                      ('Team 객체'를 외부에서 사용하려면, 당연히 '그 Team 객체'가 존재해야 하는 것이 기본 전제임)
            //현재 팀 객체가 null이라는 것은,
            //  (1) 새롭게 주어진 팀 Team 객체가 아직 생성되지 않은 경우.
            //      Team team = null;
            //      즉, '팀 엔티티 Team 클래스'는 존재하지만, 그 팀 클래스 붕어빵틀(=클래스)에 실제 붕어빵(=객체)을 만들지 않은 상태.
            //  (2) 새롭게 주어진 팀 Team 객체가 생성되었지만, 아직 어떤 값도 할당되지 않은 경우
            //      Team team = new Team(); //일단 비어 있는 깡통 Team 객체만 생성한 것임.
            //      이제 더 이상의 코드가 없음.
            //      어떤 값을 할당한다는 것은, 여기에 더해 'team.setName("Team A")'와 같은 것 추가로 작성함을 의미.
            //      '팀 엔티티 Team 클래스의 변수 name'에 'Team A'라는 값이 '메소드 setName()'을 통해 할당된 것임.
            //  (3) 새롭게 주어진 팀 Team 객체가 먼저 소멸되어버린 경우
            //      Team team = new Team(); //일단 비어 있는 깡통 Team 객체를 생성함.
            //      team = null; //그 비어 있는 깡통 Team 객체에 'null'을 할당하여 소멸시킴.

            //- 'team': 외부 클래스 어디에선 이 메소드 setMember를 새로운 인자값(새로운 팀 'team')으로 호출할 때의,
            //          '새로운 팀'. 이를 통해 새롭게 주어진 팀에 접근함.
            //- 'team.getMembers()': 새롭게 주어질 팀의 '전체 회원 목록'에 접근함.
            //- 'add(this)': 새롭게 주어질 팀의 '전체 회원 목록'에 '현재 회원('this')'을 새로운 회원으로 그 목록에 추가함.
            //- '메소드 add': '인터페이스 List'의 내장 메소드.
            //- *****중요*****
            //  'Step 1'에서의 'this.team'은 '외부 클래스 어딘가에서 현재 메소드를 새로운 인자값으로 들어온 새롭게 주어질 team이
            //   들어오기 '전'에 원래 현재 회원이 가지고 있는(소속되어 있는) 기존 팀'.
            //  'Step 3'에서의 'team'은 '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 team'.
         }
      }


    //=========================================================================================================


      < 주인 아닌 엔티티(1. 팀 Team 객체) >

      @Entity
      public class Team{

        ...

        @OneToMany(mappedBy = "team", cascade = CascadeType = ALL)
        private List<Member> members = new ArrayList<>();

        ...

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 2 >

        public void addMember(Member member){


            //# Step 1: 현재의 팀의 전체 회원 목록에 새롭게 들어온 회원을 추가하여, 새로운 회원으로 받아들임.
            members.add(member);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'을
            //  '현재의 팀의 '전체 회원 목록(members)'에 새롭게 추가함'.
            //- '메소드 add': '인터페이스 List'의 내장 메소드.




            //# Step 2: 스텝 1에서 팀에 새로운 회원을 추가해줬으니, 이제 그 새로운 회원이 현재 팀을 본인의 팀으로 인식하도록 설정하는 것.
            member.setTeam(this);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member가 소속될 팀'을
            //  '현재의 팀으로 지정함'.

        }

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 3 >

        public void removeMember(Member member){

            # Step 1:
            members.remove(member)
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'를
            //  '현재 팀의 '전체 회원 목록 members''에서 '삭제해버림'.
            //- '메소드 remove': '인터페이스 List'의 내장 메소드.


            # Step 2:
            member.setTeam(null);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'의
            //  '현재 소속 팀'을 '없음 null'로 설정함.
            //  이렇게 하면, 해당 회원 Member가 현재 Team 에서 제거되는 작업 완료됨.

        }
      }
     */


    //=========================================================================================================매


}
