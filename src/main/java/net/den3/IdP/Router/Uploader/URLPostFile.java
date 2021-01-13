package net.den3.IdP.Router.Uploader;

import com.mashape.unirest.http.HttpResponse;
import io.javalin.http.UploadedFile;
import net.den3.IdP.Entity.Upload.UploadEntityBuilder;
import net.den3.IdP.Store.Upload.IUploaderStore;
import net.den3.IdP.Util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

public class URLPostFile {
    public static void mainFlow(io.javalin.http.Context ctx){
        Optional<UploadedFile> file = Optional.ofNullable(ctx.uploadedFile("file"));
        if(!file.isPresent()){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }
        Optional<byte[]> byteArray = ByteArrayUtil.convertInputStream(file.get().getContent());
        if(!byteArray.isPresent()){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }

        Future<HttpResponse<String>> upload = UploadFile.upload(byteArray.get());

        try {
            String body = upload.get().getBody();
            Map<String, Object> res = ParseJSON.convertToMap(body).orElse(new HashMap<>());
            String successFlag = res.get("success").toString();
            //imgurからsuccessの入っていないJSONが帰ってきた場合,アップロードに失敗している
            if(!"true".equalsIgnoreCase(successFlag)){
                ctx.status(StatusCode.BadRequest.code());
                return;
            }
            Map<String, Object> data = (Map<String, Object>) res.get("data");

            String link = data.get("link").toString();
            String delete = data.get("deletehash").toString();
            ctx.json(MapBuilder
                    .New()
                    .put("url", link)
                    .build());

            IUploaderStore
            .getInstance()
            .addUploadEntity
                 (new UploadEntityBuilder()
                     .setRequestIp(ctx.ip())
                     .setUploaderURL(link)
                     .setDeleteKey(delete)
                     .build());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
