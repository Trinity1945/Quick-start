package com.zhangyh.management.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhangyh.management.admin.model.po.UserAccount;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.beans.BeanCopier;

/**
 * @author zhangyh
 * @Date 2023/4/6 16:37
 * @desc
 */
@Data
@NoArgsConstructor
public class UserAccountVo {

    private Integer id;

    private String username;

    private Integer state;

    @JsonIgnore
    transient final BeanCopier beanCopier = BeanCopier.create(UserAccount.class, UserAccountVo.class, false);

    public UserAccountVo(UserAccount userAccount) {
        beanCopier.copy(userAccount, this, null);
    }
}
