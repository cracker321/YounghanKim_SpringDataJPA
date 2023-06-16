package study.datajpa.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data //단, 이거는 예제이니깐 그런 것이고, 실무에서는 엔티티에 '@Setter'를 넣어주는 것은 지양해야 함
@Entity
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;


}
