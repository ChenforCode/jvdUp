package cn.chenforcode.endpoint;

import cn.chenforcode.pojo.entity.JvdMethod;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author yumu
 * @date 2023/2/14 16:45
 * @description
 */
public class Endpoints {
    public static Set<String> sources;
    public static Set<String> sinks;
    public static Map<String, JvdMethod> knowsMap;

    static {
        sources = new HashSet<>();
        sinks = new HashSet<>();
        knowsMap = new HashMap<>();
        sources.add("readObject");
        sinks.add("transform");
        FileReader fileReader = new FileReader("C:\\pkucoder\\projects\\ideap\\jvdUp\\jvdUp-core\\src\\main\\resources\\know.json");
        knowsMap = JSON.parseObject(fileReader.readString(), new TypeReference<Map<String, JvdMethod>>(){});
    }

    public static Boolean isSource(String name) {
        return sources.contains(name);
    }

    public static Boolean isSink(String name) {
        return sinks.contains(name);
    }
}
