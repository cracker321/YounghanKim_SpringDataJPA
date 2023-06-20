package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {


    @PersistenceContext
    private EntityManager em;


    //=========================================================================================================

    //< 신규 팀 저장 Create >


    public Team save(Team team){

        em.persist(team);

        return team;

    }

    //=========================================================================================================


    //< v1. 팀 단건 조회 Read >: Optional<> 사용 X


    public Team find(Long id){ //매개변수의 타입이 중요하지, 그 매개변수의 이름은 아~무 상관 없다!

        return em.find(Team.class, id);

    }


    //=========================================================================================================


    //< v2. 팀 단건 조회 Read >: Optional 사용 O

    public Optional<Team> findById(Long id){


        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);

    }


    //=========================================================================================================


    //< 모든 팀 조회 Read >

    public List<Team> findAll(){

        return em.createQuery("select t from Team t", Team.class)
                .getResultList();

    }


    //=========================================================================================================


    //< 전체 팀의 수 카운트 Count >

    public Long count(){

        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();

    }


    //=========================================================================================================


    //< 팀 삭제 Delete >


    public void delete(Team team){

        em.remove(team);
    }

    //=========================================================================================================

}
