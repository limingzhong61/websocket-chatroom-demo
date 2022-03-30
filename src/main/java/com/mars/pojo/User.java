package com.mars.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * .
 * Package Name:   com.yyj.pojo
 *
 * @author: YYJ
 * Date Time:      2021/5/24 18:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String username;
    private String password;

}
