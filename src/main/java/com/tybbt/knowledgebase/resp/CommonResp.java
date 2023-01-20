package com.tybbt.knowledgebase.resp;

// 通用返回类，用于同一返回数据，让前端拿到统一格式的数据，方便处理
// 例：
/**     登陆失败返回信息                                            登录成功返回信息
 * {                                                        {
 *     success: false                                           success: true
 *     message: '用户名或密码错误'                                 message: null
 *     content: null                                            content: {用户信息}
 * }                                                        }
 */
public class CommonResp<T> {
    /**
     * 业务上的成功或失败
     */
    private boolean success = true;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回泛型数据，自定义类型
     */
    private T content;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResponseDto{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", content=").append(content);
        sb.append('}');
        return sb.toString();
    }
}
