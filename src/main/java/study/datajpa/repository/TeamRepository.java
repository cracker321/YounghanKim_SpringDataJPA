package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

//@Repository : 이 어노테이션 작성 안해줘도 됨.
//              '내장 레펏 JpaRepository'를 상속받는 순간, 스프링 데이터 JPA가 아래 '레펏 TeamRepository'를
//              자동으로 '레퍼지토리'로 인식하기 때문이다.
public interface TeamRepository extends JpaRepository<Team, Long> {
    //- 'Team': '팀 Team 객체의 타입'
    //- 'Long': '팀 Team 객체'의 '필드 pk의 타입(=Long)'
    //- '내장 레펏 JpaRepository'를 상속받아도 되고, '내장 레펏 CrudRepository'를 상속받아도 됨.
    //  보통은 '내장 레펏 JpaRepository'를 상속받음.



}
