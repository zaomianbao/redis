package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 用户信息
 * @Author zaomianbao
 * @Date 2019/11/8
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Long personId;
    private String nickName;

}
