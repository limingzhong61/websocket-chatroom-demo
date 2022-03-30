package com.mars.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录后的响应.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {

    /**
     * 登录标志，成功/失败
     */
    private boolean flag;
    /**
     * 登录的响应消息
     */
    private String message;

}
