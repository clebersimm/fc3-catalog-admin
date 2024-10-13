package com.fullcycle.admin.catalog.infrastructure.castmember;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
public class CastMemberMySQLGatewayTest {
    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;
    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    public void testDependencies() {
        Assertions.assertNotNull(castMemberRepository);
        Assertions.assertNotNull(castMemberMySQLGateway);
    }
}