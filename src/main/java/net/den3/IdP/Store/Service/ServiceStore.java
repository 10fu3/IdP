package net.den3.IdP.Store.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServiceBuilder;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceStore implements IServiceStore{
    private static final IDBAccess store = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);
    private static final List<String> columns = Arrays.asList("uuid","name","admin_id","secret","url","icon","description","read_uuid","read_mail","read_profile");

    public ServiceStore(){
        store.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement("CREATE TABLE IF NOT EXISTS service ("
                                +"uuid VARCHAR(256) PRIMARY KEY, "
                                +"name VARCHAR(256), "
                                +"admin_id VARCHAR(256), "
                                +"secret VARCHAR(256), "
                                +"url VARCHAR(256), "
                                +"icon VARCHAR(256), "
                                +"description VARCHAR(256), "
                                +ServicePermission.names.stream().map(p->p+" VARCHAR(5)").collect(Collectors.joining(","))
                                +");")));
            }catch (SQLException ex){
                ex.printStackTrace();
            }
            return Optional.empty();
        });
    }

    /**
     * 登録された外部連携サービスをすべてDBから取得する
     * @return サービスのリスト
     */
    @Override
    public Optional<List<IService>> getServices() {
        //List<String> columns = Arrays.asList("uuid","name","admin_id","url","icon","description","read_uuid","read_mail","read_profile","read_last_login_time");
        Optional<List<Map<String, String>>> lineBySQL = store.getLineBySQL(columns,(con) -> {
            try {
                return Optional.of(con.prepareStatement("SELECT * FROM service"));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
        return lineBySQL.map(maps -> maps
                .stream()
                .map(map -> {
                            ServiceBuilder s = new ServiceBuilder()
                            .setServiceID(map.get("uuid"))
                            .setServiceName(map.get("name"))
                            .setAdminID(map.get("admin_id"))
                            .setSecret(map.get("secret"))
                            .setRedirectURL(map.get("url"))
                            .setServiceIconURL(map.get("icon"))
                            .setServiceDescription(map.get("description"));
                    Arrays.stream(ServicePermission.values()).filter(p->"true".equalsIgnoreCase(p.getName())).forEach(s::setUsedPermission);
                    return s.build();
                })
                .collect(Collectors.toList()));
    }

    /**
     * 登録された外部連携サービスのうちサービス固有のIDが合致するものを返す
     * @param id 外部連携サービスのID
     * @return 外部連携サービスエンティティ
     */
    @Override
    public Optional<IService> getService(String id) {
        //List<String> columns = Arrays.asList("name","admin_id","url","icon","description","read_uuid","read_mail","read_profile","read_last_login_time");
        Optional<List<Map<String, String>>> lineBySQL = store.getLineBySQL(columns,(con) -> {
            try {
                PreparedStatement pS = con.prepareStatement("SELECT * FROM service where uuid = ?");
                pS.setString(1,id);
                return Optional.of(pS);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
        return lineBySQL.map(maps -> maps
                .stream()
                .map(map -> {
                    ServiceBuilder s = new ServiceBuilder()
                            .setServiceID(map.get("uuid"))
                            .setServiceName(map.get("name"))
                            .setAdminID(map.get("admin_id"))
                            .setSecret(map.get("secret"))
                            .setRedirectURL(map.get("url"))
                            .setServiceIconURL(map.get("icon"))
                            .setServiceDescription(map.get("description"));
                    Arrays.stream(ServicePermission.values()).filter(p->"true".equalsIgnoreCase(map.get(p.getName()))).forEach(s::setUsedPermission);
                    return s.build();
                })
                .collect(Collectors.toList()).stream().findFirst()).flatMap(l->l);
    }


    /**
     *　サービスの情報をアップデートする
     * @param service サービスエンティティ
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean updateService(IService service) {
        return store.controlSQL((con)->{
            try {
                String sql = "UPDATE service SET name=?, admin_id=?, secret=? , url=?, icon=?, description=?, ";
                sql = sql + Arrays.stream(ServicePermission.values()).map(p->p.getName().toLowerCase()+"=?").collect(Collectors.joining(","))+" WHERE uuid=?;";
                PreparedStatement pS = con.prepareStatement(sql);
                pS.setString(1,service.getServiceName());
                pS.setString(2,service.getAdminID());
                pS.setString(3,service.getSecretID());
                pS.setString(4,service.getRedirectURL());
                pS.setString(5,service.getServiceIconURL());
                pS.setString(6,service.getServiceDescription());

                ServicePermission[] perms = ServicePermission.values();
                for (int i = 0; i< perms.length;i++){
                    if(service.getUsedPermission().contains(perms[i])){
                        pS.setString(i+7,"true");
                    }else{
                        pS.setString(i+7,"false");
                    }
                }

                pS.setString(10,service.getServiceID());
                return Optional.of(Arrays.asList(pS));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * サービスを削除する
     * @param id サービス固有のUUID
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean deleteService(String id) {
        return store.controlSQL((con)->{
            List<PreparedStatement> sqlRequests = new ArrayList<>();
            try {
                PreparedStatement servicePS = con.prepareStatement("DELETE FROM service WHERE uuid = ?;");
                servicePS.setString(1,id);
                sqlRequests.add(servicePS);
                return Optional.of(sqlRequests);
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * 同じ管理者IDを持つ外部連携サービスをすべてDBから取得する
     * @return サービスのリスト
     */
    @Override
    public Optional<List<IService>> getServices(String adminID) {
        //List<String> columns = Arrays.asList("uuid","name","admin_id","url","icon","description","edit_uuid","edit_profile","read_profile","delete_self_account","read_uuid","read_mail","read_profile","read_last_login_time");
        Optional<List<Map<String, String>>> lineBySQL = store.getLineBySQL(columns,(con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM service where admin_id = ?");
                ps.setString(1,adminID);
                return Optional.of(ps);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
        return lineBySQL.map(maps -> maps
                .stream()
                .map(map -> {
                    ServiceBuilder s = new ServiceBuilder()
                            .setServiceID(map.get("uuid"))
                            .setServiceName(map.get("name"))
                            .setAdminID(map.get("admin_id"))
                            .setSecret(map.get("secret"))
                            .setRedirectURL(map.get("url"))
                            .setServiceIconURL(map.get("icon"))
                            .setServiceDescription(map.get("description"));
                    Arrays.stream(ServicePermission.values()).filter(p->"true".equalsIgnoreCase(map.get(p.getName()))).forEach(s::setUsedPermission);
                    return s.build();
                })
                .collect(Collectors.toList()));
    }

    /**
     * 外部連携サービスを登録する
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean addService(IService service) {
        return store.controlSQL((con)->{
            try {
                //INSET文の発行
                PreparedStatement pS = con.prepareStatement("INSERT INTO service VALUES (?,?,?,?,?,?,?,?,?,?) ;");
                //SQL文の個めの?にを代入する
                pS.setString(1, service.getServiceID());
                //SQL文の個めの?にを代入する
                pS.setString(2, service.getServiceName());
                //SQL文の個めの?にを代入する
                pS.setString(3, service.getAdminID());

                pS.setString(4,service.getSecretID());
                //SQL文の個めの?にを代入する
                pS.setString(5, service.getRedirectURL());
                //SQL文の個めの?にを代入する
                pS.setString(6, service.getServiceIconURL());
                //SQL文の個めの?にを代入する
                pS.setString(7, service.getServiceDescription());

                for (int i = 8; i < 11; i++) {
                    if(service.getUsedPermission().contains(ServicePermission.values()[i-8])){
                        pS.setString(i,"true");
                    }else{
                        pS.setString(i,"false");
                    }
                }
                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}
