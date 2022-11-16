package com.tsl.app.repository.server;

import android.content.res.AssetManager;
import android.util.Log;

import com.jowney.common.util.logger.L;
import com.tsl.app.APP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
    private static final int HTTP_PORT = 8080;//端口，尽量设置一个不常规的
    private static final String TAG = "httpServer";
    public HttpServer() {
        super(HTTP_PORT);
    }

    /**
     * 数据接收
     *
     * @param session
     * @return
     */
    @Override
    public Response serve(IHTTPSession session) {
        return parseRequest(session);
    }


    /**
     * 数据解析
     *
     * @param session
     * @return
     */
    private NanoHTTPD.Response parseRequest(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Response response = null;
        if (session.getMethod() == NanoHTTPD.Method.GET) {
            response = parseGetRequest(session);
        } else if (session.getMethod() == NanoHTTPD.Method.POST) {
            response = parsePostRequest(session);
        } else {
            response = responseForNotFound();
        }
        return response;
    }


    /**
     * 解析post请求
     * /xxx/接口1：代表 两端定义的访问接口，例如：/user/info
     *
     * @param session
     * @return
     */
    private Response parsePostRequest(IHTTPSession session) {
        //  final Map<String, String> files = parseBody(session);
        NanoHTTPD.Response response = null;
        String paramsMiss = "";
        Map<String, String> params = session.getParms();
        if (session.getParms() == null) {
            return responseParamsNotFound("服务器参数错误");
        }
        switch (session.getUri()) {
            case "/led/control":
                //根据自己业务返回不同的需求

                response = responseForConfig("接口1 调用成功");
                break;
            case "/xxx/接口2":
                response = responseForConfig("接口2 调用成功");
                break;
            default:
                response = responseForNotFound();
                break;
        }
        return response;
    }


    /**
     * 返回指令未知
     *
     * @return
     */
    private NanoHTTPD.Response responseForNotFound() {
        NanoHTTPD.Response.Status status = NanoHTTPD.Response.Status.NOT_FOUND;
        final String mimeType = "text/html";
        return NanoHTTPD.newFixedLengthResponse(status, mimeType, "NOT_FOUND");
    }

    /**
     * 返回指定的错误信息
     */
    private NanoHTTPD.Response responseParamsNotFound(String msg) {
        NanoHTTPD.Response.Status status = NanoHTTPD.Response.Status.NOT_FOUND;
        final String mimeType = "text/html";
        return NanoHTTPD.newFixedLengthResponse(status, mimeType, msg);
    }


    /**
     * 返回json串
     * result:可以是任意Bean转成的字符串，
     *
     * @return
     */
    private Response responseForConfig(String result) {
        NanoHTTPD.Response.Status status = NanoHTTPD.Response.Status.OK;
        String mimeType = "application/json;charset=UTF-8";
        L.v("正在向客户端发送数据 -> " + result);

        return NanoHTTPD.newFixedLengthResponse(status, mimeType, result);
    }


    /**
     * 解析get 请求
     *
     * @param session
     * @return
     */
    private Response parseGetRequest(IHTTPSession session) {

        NanoHTTPD.Response response = null;
        String paramsMiss = "";
        Map<String, String> params = session.getParms();
        if (session.getParms() == null) {
            return responseParamsNotFound("服务器参数错误");
        }

        switch (session.getUri()) {
            case "/led/control":
                //根据自己业务返回不同的需求
                L.v("value:" + params.get("color"));
                String paramColor = params.get("color");
                if (paramColor!=null && paramColor.contains("echo w "))
                adbcommand(params.get("color"));
              /*  switch (params.get("color")) {
                    case "r":
                        adbcommand("echo w 0x04 > ./sys/devices/platform/led_con_h/zigbee_reset");
                        break;
                    case "g":
                        adbcommand("echo w 0x06 > ./sys/devices/platform/led_con_h/zigbee_reset");
                        break;
                    case "b":
                        adbcommand("echo w 0x05 > ./sys/devices/platform/led_con_h/zigbee_reset");
                        break;
                }*/
                response = responseForConfig("OK");
                break;
            case "/xxx/接口2":
                response = responseForConfig("接口2 调用成功");
                break;
            default:
                response = responseForNotFound();
                break;
        }
        return response;
        //   return getHtmlFileStream(session);
    }

    /**
     * 解析访问的mintype类型
     *
     * @param session
     * @return
     */
    private Response getHtmlFileStream(IHTTPSession session) {
        String uri = session.getUri();
        String filename = uri.substring(1);
        boolean is_ascii = true;
        if (uri.equals("/")) {
            filename = "index.html";
        }

        String mimetype = "text/html";
        if (filename.contains(".html") || filename.contains(".htm")) {
            mimetype = "text/html";
            is_ascii = true;
        } else if (filename.contains(".js")) {
            mimetype = "text/javascript";
            is_ascii = true;
        } else if (filename.contains(".css")) {
            mimetype = "text/css";
            is_ascii = true;
        } else if (filename.contains(".gif")) {
            mimetype = "text/gif";
            is_ascii = false;
        } else if (filename.contains(".jpeg") || filename.contains(".jpg")) {
            mimetype = "text/jpeg";
            is_ascii = false;
        } else if (filename.contains(".png")) {
            mimetype = "image/png";
            is_ascii = false;
        } else if (filename.contains(".svg")) {
            mimetype = "image/svg+xml";
            is_ascii = false;
        } else {
            filename = "index.html";
            mimetype = "text/html";
        }
        if (is_ascii) {
            return loadHtml(filename, mimetype);
        } else {
            return loadOrder(filename, mimetype);
        }


    }


    /**
     * 加载非html 类型
     *
     * @param filename
     * @param mimetype
     * @return
     */
    private Response loadOrder(String filename, String mimetype) {
        AssetManager assetManager = APP.mContext.getAssets();
        InputStream isr;
        try {
            isr = assetManager.open(filename);
            return newFixedLengthResponse(Response.Status.OK, mimetype, isr, isr.available());
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.OK, mimetype, "");
        }
    }


    /**
     * 加载html
     *
     * @param filename
     * @param mimetype
     * @return
     */
    private Response loadHtml(String filename, String mimetype) {
        AssetManager assetManager = APP.mContext.getAssets();
        //通过AssetManager直接打开文件进行读取操作
        StringBuilder response = new StringBuilder();
        String line = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(assetManager.open(filename, AssetManager.ACCESS_BUFFER)));

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.OK, mimetype, "");
        }
        return newFixedLengthResponse(Response.Status.OK, mimetype, response.toString());

    }


    private String adbcommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        String excresult = "";
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + " ");
            }
            excresult = stringBuffer.toString();
            Log.d("Jessica2 ", excresult);


            os.close();
            // System.out.println(excresult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excresult;
    }
}
