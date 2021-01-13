package net.den3.IdP.Util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.den3.IdP.Config;

import java.util.concurrent.Future;

public class UploadFile {

    public static Future<HttpResponse<String>> upload(byte[] array){
        return Unirest
            .post("https://api.imgur.com/3/image/")
            .header("Authorization","Client-ID "+Config.get().getUploaderToken())
            .field("type","base64")
            .field("image",Base64Util.encode(array))
            .asStringAsync();
    }
}
