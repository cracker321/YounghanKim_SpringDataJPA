package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor //기본적으로 DTO에는 모든 필드를 갖는 생성자를 만드는 @AllArgsConstructor를 붙여줌.
@Data //기본적으로 DTO에는 '@Data'를 붙여줌.
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

}
