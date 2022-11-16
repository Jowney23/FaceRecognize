package com.tsl.androidbase.repository.server;

/**
 * 此类属于完全自定义，根据自己业务写

 */
public class HttpResult<T> {

    private static final int RESULT_CODE_SUCCESS = 0;
    private static final int RESULT_CODE_FILE = 1;
    /**
     * 状态码
     */
    public int code;
    /**
     * 返回结果信息
     */
    public String msg;

    /**
     * 预留
     */
    /**
     * 结果信息
     **/
    public T results;


    /**
     * 获取成功
     *
     * @return
     */
    public static final String getSuccess() {
        final HttpResult httpResult = new HttpResult<>();
        httpResult.code = RESULT_CODE_SUCCESS;
       /* httpResult.msg = ResourceUtil.getString(
                R.string.server_result_success);
        return JsonUtil.objectToJson(httpResult);*/
        return "success";
    }

    /**
     * 请求失败
     *
     * @return
     */
    public static String getFail(String paramsMiss) {
        final HttpResult httpResult = new HttpResult<>();
        httpResult.code = RESULT_CODE_FILE;
       /* httpResult.msg = ResourceUtil.getString(
                R.string.server_result_fail) + (TextUtils.isEmpty(paramsMiss) ? "" : ("," + paramsMiss));
        return JsonUtil.objectToJson(httpResult);*/
        return  "Fail";
    }

}



