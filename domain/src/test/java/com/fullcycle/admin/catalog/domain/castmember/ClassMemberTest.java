package com.fullcycle.admin.catalog.domain.castmember;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassMemberTest {

    @Test
    public void givenAValidParams_whenCallsNewMember_thenInstantiateACastMember(){
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualMember = CastMember.newMember(expectedName, expectedType);

        Assertions.assertNotNull(actualMember);
        Assertions.assertNotNull(actualMember.getId());
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertNotNull(actualMember.getCreatedAt());
        Assertions.assertNotNull(actualMember.getUpdatedAt());
        Assertions.assertEquals(actualMember.getCreatedAt(),actualMember.getUpdatedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenCallsNewMember_shouldReceiveANotification(){
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> CastMember.newMember(expectedName, expectedType));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallsNewMember_shouldReceiveANotification(){
        final var expectedName = " ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> CastMember.newMember(expectedName, expectedType));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidInvalidNameWithLengthMoreThan255_whenCallsNewMember_shouldReceiveANotification(){
        final var expectedName = """
                Caros amigos, o fenômeno da Internet ainda não demonstrou convincentemente que vai participar na mudança do remanejamento dos quadros funcionais
                Caros amigos, o fenômeno da Internet ainda não demonstrou convincentemente que vai participar na mudança do remanejamento dos quadros funcionais
                """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 charecters";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> CastMember.newMember(expectedName, expectedType));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullType_whenCallsNewMember_shouldReceiveANotification(){
        final var expectedName = "Vin Diesel";
        final CastMember expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> CastMember.newMember(expectedName, expectedType));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
