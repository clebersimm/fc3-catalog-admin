package com.fullcycle.admin.catalog.application.castmember.delete;

import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class DeleteCastMemberUseCaseIT {

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteId(){
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedId = aMember.getId();
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, this.castMemberRepository.count());
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
        Assertions.assertEquals(0, this.castMemberRepository.count());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk(){
        // given
        final var expectedId = CastMemberId.from("123");
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
        Assertions.assertEquals(1, this.castMemberRepository.count());
    }

    @Test
    public void givenAnValidId_whenCallsDeleteCastMemberAndGatewayThrowsException_shouldReceiveException(){
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedId = aMember.getId();
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, this.castMemberRepository.count());
        Mockito.doThrow(new IllegalStateException("Gateway error")).when(castMemberGateway).deleteById(any());
        // when
        Assertions.assertThrows(IllegalStateException.class,() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
        Assertions.assertEquals(1, this.castMemberRepository.count());
    }
}
