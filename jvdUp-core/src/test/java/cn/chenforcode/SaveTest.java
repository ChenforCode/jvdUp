package cn.chenforcode;

import cn.chenforcode.repository.JvdMethodRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yumu
 * @date 2023/2/18 18:01
 * @description
 */
@SpringBootTest
public class SaveTest {
    @Autowired
    private JvdMethodRepository jvdMethodRepository;

    @Test
    public void test() {
        jvdMethodRepository.loadMethodFromJson("C:\\pkucoder\\projects\\ideap\\jvdUp\\method.json");
    }
}
