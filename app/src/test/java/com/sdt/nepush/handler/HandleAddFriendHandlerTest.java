package com.sdt.nepush.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class HandleAddFriendHandlerTest {
    @Test
    public void testParse() throws Exception {
        String content = "{\"tip\":\"我是tom,可以加个好友吗\",\"user\":{\"password\":\"test123\",\"mobile\":\"13055556666\",\"name\":\"tom\",\"id\":116,\"authorities\":[]}}";
        JsonParser parser = new JsonParser();
        //通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
        JsonElement el = parser.parse(content);
        JsonObject jsonObject = el.getAsJsonObject();
        JsonElement user = jsonObject.get("user");
        JsonElement tip = jsonObject.get("tip");
        String userStr =user.getAsJsonObject().toString();
        String tipStr =tip.getAsJsonPrimitive().toString();
        System.out.print("user:" + userStr);
        System.out.print("tip:" + tipStr);
    }
}