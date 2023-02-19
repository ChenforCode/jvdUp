package cn.chenforcode.repository;

import cn.chenforcode.pojo.entity.JvdMethod;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author yumu
 * @date 2023/2/17 12:13
 * @description
 */
@Repository
public interface JvdMethodRepository extends Neo4jRepository<JvdMethod, String> {
    //删除库内的节点
    @Query("CALL apoc.periodic.iterate(\"match (n) return n\",\"detach delete n\", {batchSize:10000, iterateList:true, parallel:false}) yield total")
    int clearAll();


    @Query("CALL apoc.periodic.iterate(\"CALL apoc.load.json('file://\"+$path+\"', " +
            "{header:true, mapping:{ " +
            "IS_SOURCE: {type:'boolean'}, " +
            "IS_SINK: {type:'boolean'}, " +
            "IS_KNOW: {type:'boolean'}, " +
            "NAME: {type:'String'}, " +
            "SIGNATURE: {type:'String'}, " +
            "SUBSIGNATURE: {type:'String'}, " +
            "CLASSNAME:{type:'String'}}}) YIELD map AS row RETURN row\", \"MERGE(m:JvdMethod {ID:row.ID} ) ON CREATE SET m = row\", {batchSize:5000, iterateList:true, parallel:true}) yield total")
    int loadMethodFromJson(String path);
}
