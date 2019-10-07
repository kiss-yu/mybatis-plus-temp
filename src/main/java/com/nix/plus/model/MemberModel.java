package com.nix.plus.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nix.plus.model.ienum.Sex;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author by keray
 * date:2019/7/26 16:03
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cu_member")
public class MemberModel extends BaseEntity {

    private String name;
    private String nickname;
    private String password;
    private String mobile;
    private Sex sex;
    private String email;
    private String headerUrl;
    private LocalDateTime loginDate;

}
