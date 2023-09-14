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


//  ****중요****
//  JPA 연관관계에서 '주인 객체'는 아래 기준에 따라 설정한다.
//  (1) 더 자주 접근되거나 변경되는 엔티티
//      : 자주 접근하거나 수정하는 엔티티 쪽을 주인으로 설정하는 것이 효율적임.
//        JPA가 변경감지(dirty checking)을 수행할 때, 더 자주 변화가 발생하는 쪽이 주인 엔티티라면, 불필요한 DB 접근을 줄일 수 있음.
//  (2) 비즈니스 로직상 중요한 엔티티
//  (3) 상대방 엔티티 객체의 PK를 내 엔티티 클래스 안에 FK로 소유하고 있는 엔티티

//  *****중요*****
//    < 1:1 양방향 매핑에서 주인 지정하는 방법 > 교재 p215~
//    - 주 테이블(e.g: User <--> UserProfile 이라면 User 객체를 주인으로,
//      Member <--> Locker 이라면 Member 객체를 주인으로 지정)에 상대방 테이블(대상 테이블)의 외래키를 넣기 때문에,
//      주 테이블을 사용하는 자바 엔티티 객체를 주인으로 지정해라!
//    - 즉, 반대편 엔티티 객체를 소유하고 있다고 볼 수 있는 엔티티 객체를 주인으로 지정하는 것이다!!

//  참고사항 cf)
//  N:1 양방향 매핑에서 알 수 있듯이, N:1 에서 주인객체는 주로 N이고, 비주인객체가 1임.
//  보통 비주인객체에 변화가 먼저 발생(e.g: 게시글 Post(1. 비주인)와 댓글 Comment(N. 주인) 간 관계)하기 때문에,
//  1:1 양방향 매핑의 예시로 User 객체, UserProfile 객체 간 관계에서,
//  비주인객체를 User객체로 지정하고, 주인객체를 UserProfile 객체로 지정하는 것도 마찬가지로 자연스럽다고 할 수 있음.



//===================================================================================================




//    [ Cascade ] 교재 p308~
//
//    Cascade는 1:1, N:1 양방향 매핑에서 1인 부분의 엔티티 클래스 내부에 아래 필드 및 어노테이션을 넣는 거임.
//    사실, cascade는 단방향, 양방향 또는 주인 객체, 주인 아닌 객체 여부와 관계 없이
//    두 객체 간의 관계에서, 상대 객체를 나한테 종속시켜버리는 '그 종속시키는 우월한 객체(= 더 먼저 발생하는, 더 우선되는 객체)의
//    클래스 내부'의 필드에 '영속성 전이 Cascade'를 넣는 것이다
//
//    e.g) 주문 Order 객체와 배송 Delivery 객체가 있다고 하면, 양, 단방향, 주인, 비주인 관계 없이
//         먼저 일단 '주문'이라는 사건이 발생해야 하고, 그에 따라 이어져서 '배송'이 발생하는 것이기 때문에,
//         주문 Order 객체가 배송 Delivery 객체를 종속시키고,
//         따라서, '주문 Order 객체 클래스의 내부'에 영속성 전이 Cascade를 넣는 것이다




//    < 1. 양방향 1:1 매핑에서, 1인 부모(상위) 엔티티 클래스(상위 엔티티. 주인, 비주인 무관)의 내부에 아래 필드 및 어노테이션을 넣는 거임 >


//    *****중요*****
//    1:1 양방향 매핑에서 Cascade는 정말 정말 정말로, 주인-비주인 객체 여부와 아~~~무 상관 없다!!!!!
//    먼저 발생되고, 먼저 일어나게 되는 그런 쪽 엔티티 클래스의 내부에 Cascade를 입력하는 것이다!
//    여기 아래 예시에서는, 당연히 User 객체를 먼저 변화시키는 것이 일반적이고, UserProfile 객체는 그 User 객체의
//    변화에 이어져서 나오는 것이기 떼문에, User 엔티티 클래스 내부에 Cascade를 입력한 것이다!!
//    즉, User 객체가 반대편 UserProfile 객체를 '소유'하고 있는 그런 느낌이기 때문에,
//    User 엔티티 객체의 내부에 Cascade를 작성해주는 것이다!!
//    https://www.inflearn.com/questions/15855/cascade-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4



//    e.g) User(1) : UserProfile(1). 1:1 양방향 매핑. 주인객체: User(1) 객체 & 부모(상위) 엔티티: User(1)
//      - Cascade 기능을 사용하면, 예를 들어 User 엔티티 클래스 내부에 CascadeType.REMOVE를 작성하여 사용하면,
//        사용자 User를 DB에서 삭제시킬 때, 해당 사용자와 연결되어 있는 해당 사용자의 UserProfile 객체도 한 번에 다 DB에서
//        삭제시킬 수 있음.
//        만약, CascadeType.PERSIST 를 작성하여 사용하면,
//        새로운 사용자 User를 DB에 저장시킬 때, 해당 사용자와 연결되어 있는 해당 사용자의 UserProfile 객체도
//        한 번에 다 DB에 저장시킬 수 있음.
//      - 즉, 부모(상위) 엔티티 User(1)에서 발생한 모든 데이터 변경사항이 자식(하위) 엔티티 UserProfile(1)로 전파되어 영향을 미친다는 것임.
//      - CascadeType 중 ALL이 가장 많이 사용되고, 나머지는 자주 사용되지 않음.
//        즉, User 객체의 모든 변경(생성, 수정, 삭제)사항을 DB에 적용시킬 때,
//        그 User 객체와 연결되어 있는 UserProfile 객체에도 동일하게 해당 변경사항이 적용되어 DB에 적용 반영되게 되는 것임.
//        왜냐하면, 부모(상위) 엔티티의 저장(PERSIST), 삭제(REMOVE)를 연관된 자식(하위) 엔티티도 DB에 저장, 삭제하는 경우는 많지만,
//        병합(MERGE), 갱신(REFRESH), 분리(DETACH) 하는 작업은 비즈니스 로직에 따라 다르기 때문임.


//    @Entity
//    public class User {       // 부모(상위) 엔티티 User(1). 주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        //...
//
//        @OneToOne(cascade = CascadeType.ALL)
//        @JoinColumn(name="profile_id") //여기 주인 User 엔티티가 비주인 객체인 반대편 UserProfile 엔티티 테이블의 PK를 소유학고 있음.
//        private UserProfile userProfile;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        public void setUserProfile(UserProfile userProfile) {
//            if (this.userProfile != null) {
//                this.userProfile.setUser(null);
//            }
//            this.userProfile = userProfile;
//            if (userProfile != null) {
//                userProfile.setUser(this);
//            }
//        }
//
//        // getters and setters...
//    }




//    @Entity
//    public class UserProfile {    // 자식(하위) 엔티티 UserProfile(1). 비주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        @OneToOne(mappedBy="user", fetch=FetchType.LAZY)
//        private User user;
//
//        public void setUser(User user) {
//            this.user = user;
//        }
//
//        // getters and setters...
//    }








//    < 2. 양방향 N:1 매핑에서, 1인 부모 엔티티 클래스(상위 엔티티. 주로 주인 아닌 객체)의 내부에 아래 필드 및 어노테이션을 넣는 것임 >

//      e.g) Comment(N) : Post(1). N:1 양방향 매핑. 주인객체: Comment 객체 & 부모(상위) 엔티티: Post(1)
//      - Cascade 기능을 사용하면, 예를 들어 Post 엔티티 클래스 내부에 CascadeType.REMOVE를 작성하여 사용하면,
//        게시글 Post를 DB에서 삭제시킬 때, 해당 게시글에 달린 모든 댓글 Comment도 한 번에 다 DB에서 삭제시킬 수 있음.
//        만약, CascadeType.PERSIST 를 작성하여 사용하면,
//        게시글 Post를 DB에 저장시킬 때, 해당 게시글에 달린 모든 댓글 Comment도 한 번에 다 DB에 저장시킬 수 있음.
//      - 즉, 부모(상위) 엔티티 Post(1)에서 발생한 모든 데이터 변경사항이 자식(하위) 엔티티 Comment(N)로 전파되어 영향을 미친다는 것임.
//      - CascadeType 중 ALL이 가장 많이 사용되고, 나머지는 자주 사용되지 않음.
//        왜냐하면, 부모(상위) 엔티티의 저장(PERSIST), 삭제(REMOVE)를 연관된 자식(하위) 엔티티도 DB에 저장, 삭제하는 경우는 많지만,
//        병합(MERGE), 갱신(REFRESH), 분리(DETACH) 하는 작업은 비즈니스 로직에 따라 다르기 때문임.


//        *****중요*****
//        1:1 양방향 매핑에서 Cascade는 정말 정말 정말로, 주인-비주인 객체 여부와 아~~~무 상관 없다!!!!!
//        먼저 발생되고, 먼저 일어나게 되는 그런 쪽 엔티티 클래스의 내부에 Cascade를 입력하는 것이다!
//        여기 아래 예시에서는, 당연히 Post 객체를 먼저 변화시키는 것이 일반적이고, Comment 객체는 그 Post 객체의
//        변화에 이어져서 나오는 것이기 떼문에, Post 엔티티 클래스 내부에 Cascade를 입력한 것이다!!
//        즉, Post 객체가 반대편 Comment 객체를 '소유'하고 있는 그런 느낌이기 때문에,
//        Post 엔티티 객체의 내부에 Cascade를 작성해주는 것이다!!
//        https://www.inflearn.com/questions/15855/cascade-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4



//    @Entity
//    public class Post {      // 부모(상위) 엔티티 Post(1). 비주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//        private List<Comment> comments = new ArrayList<>();
//
//        public void addComment(Comment comment) {
//            this.comments.add(comment);
//            comment.setPost(this);
//        }
//
//        // getters and setters...
//    }





//    @Entity
//    public class Comment {      // 자식(하위) 엔티티 Comment(1). 주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        //...
//
//        @ManyToOne(fetch=FetchType.LAZY)
//        @JoinColumn(name="post_id") //여기 주인 Comment 엔티티가 비주인 객체인 반대편 Post 엔티티 테이블의 PK를 소유학고 있음.
//        private Post post;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        public void setPost(Post post) {
//            this.post = post;
//        }
//
//        // getters and setters...
//    }



    //===================================================================================================


    //[ cascade = CascadeType.ALL ] 교재 p308~

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
