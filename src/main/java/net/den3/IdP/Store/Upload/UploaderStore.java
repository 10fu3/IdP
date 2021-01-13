package net.den3.IdP.Store.Upload;

import net.den3.IdP.Entity.Upload.IUploadEntity;
import net.den3.IdP.Entity.Upload.UploadEntityBuilder;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class UploaderStore implements IUploaderStore{

    private final String TABLE = "upload_request_store";
    private final List<String> field = Arrays.asList("uuid","ip","url","delete_key");
    IDBAccess db = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    public UploaderStore(){
        db.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE IF NOT EXISTS "+TABLE+" ("
                                        //内部ID
                                        +"uuid VARCHAR(256) PRIMARY KEY,"
                                        +"ip VARCHAR(256),"
                                        +"url VARCHAR(256),"
                                        +"delete_key VARCHAR(256)"
                                        +")")));
            }catch (SQLException ex){
                return Optional.empty();
            }
        });
    }

    /**
     * 指定した列名から値の一致する行をすべて返す
     * @param targetField 列名
     * @param targetValue 値
     * @return 認可フローエンティティのリスト
     */
    private Optional<List<IUploadEntity>> getUploadEntity(String targetField, String targetValue) {
        if (!field.contains(targetField)) {
            return Optional.empty();
        }
        return db.getLineBySQL(field, (con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM "+TABLE+" WHERE " + targetField + " = ?");
                ps.setString(1, targetValue);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Optional.empty();
            }
        }).map(p -> p.stream().map(line -> {
                    UploadEntityBuilder builder = new UploadEntityBuilder();
                    builder.setUUID(line.get("uuid"));
                    builder.setRequestIp(line.get("ip"));
                    builder.setUploaderURL(line.get("url"));
                    builder.setDeleteKey(line.get("delete_key"));
                    return builder.build();
                }
        ).collect(Collectors.toList()));
    }

    /**
     * 登録されたアップロードリクエストをすべてDBから取得する
     *
     * @return アップロードリクエストのリスト
     */
    @Override
    public Optional<List<IUploadEntity>> getUploadEntity() {
        return db.getLineBySQL(field, (con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM "+TABLE);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Optional.empty();
            }
        }).map(p -> p.stream().map(line -> {
                    UploadEntityBuilder builder = new UploadEntityBuilder();
                    builder.setUUID(line.get("uuid"));
                    builder.setRequestIp(line.get("ip"));
                    builder.setUploaderURL(line.get("url"));
                    builder.setDeleteKey(line.get("delete_key"));
                    return builder.build();
                }
        ).collect(Collectors.toList()));
    }

    /**
     * 登録されたアップロードリクエストのうちアップロードリクエスト固有のIDが合致するものを返す
     *
     * @param id アップロードリクエストのID
     * @return アップロードリクエストエンティティ
     */
    @Override
    public Optional<IUploadEntity> getUploadEntity(String id) {
        return getUploadEntity("uuid",id).orElse(new ArrayList<>()).stream().findAny();
    }

    /**
     * 　アップロードリクエストの情報をアップデートする
     *
     * @param uploadEntity アップロードリクエストエンティティ
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean updateUploadEntity(IUploadEntity uploadEntity) {
        return db.controlSQL((con)->{
            try{
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE access_token_store SET "+
                                "ip = ?,"+
                                "url = ?,"+
                                "delete_key = ?"
                                +"where uuid=?"
                                +");");
                ps.setString(1,uploadEntity.getRequestIP());
                ps.setString(2,uploadEntity.getUploaderURL());
                ps.setString(3,uploadEntity.getDeleteKey());
                ps.setString(4,uploadEntity.getUUID());
                return Optional.of(Collections.singletonList(ps));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アップロードリクエストを削除する
     *
     * @param id アップロードリクエスト固有のUUID
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean deleteUploadEntity(String id) {
        return db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM "+TABLE+" where uuid = ?");
                ps.setString(1,id);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アップロードリクエストを登録する
     *
     * @param uploadEntity
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean addUploadEntity(IUploadEntity uploadEntity) {
        return db.controlSQL((con)->{
            try{
                PreparedStatement ps = con.prepareStatement("INSERT INTO "+TABLE+" VALUES (?,?,?,?)");
                ps.setString(1,uploadEntity.getUUID());
                ps.setString(2,uploadEntity.getRequestIP());
                ps.setString(3,uploadEntity.getUploaderURL());
                ps.setString(4,uploadEntity.getDeleteKey());
                return Optional.of(Collections.singletonList(ps));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}
