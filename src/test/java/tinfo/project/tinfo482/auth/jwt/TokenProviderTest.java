package tinfo.project.tinfo482.auth.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tinfo.project.tinfo482.functionalities.auth.jwt.TokenProvider;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;

@SpringBootTest
@Slf4j

@TestPropertySource(properties = {"jwt.secret=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6asdf3dadf13fdfadcq32gfadsv23rdaf684wdfads"})
class TokenProviderTest {

    @Autowired
   MemberRepository memberRepository;
    @Value("${jwt.secret}")
            private String secretKey;

    @Test
    public void testTokenGeneration(){
        TokenProvider tokenProvider = new TokenProvider(secretKey, memberRepository);

    }


}