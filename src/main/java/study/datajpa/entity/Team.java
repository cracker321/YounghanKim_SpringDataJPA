package study.datajpa.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


//@NoaArgsConstructor: 만약 내가 여기 객체 안에 '사용자 생성자'를 만든다면, 그에 따라 이제 내가 직접 입력해줘야 하는 '기본 생성자'는
//여기서 어노테이션으로 넣으면 안되고, 아래에서 내가 직접 'protected Team(){}' 이렇게 만들어줘야 함!
@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "Member")
    private List<Member> members = new ArrayList<>();

    protected Team(){


    }

}
