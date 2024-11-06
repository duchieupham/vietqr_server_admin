package com.vietqradminbe.domain.repositories;

import com.vietqradminbe.domain.models.KeyActiveBankReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeyActiveBankReceiveRepository extends JpaRepository<KeyActiveBankReceive, String> {
    @Query(value = "SELECT key_active AS keyActive "
            + "FROM key_active_bank_receive "
            + "WHERE key_active IN (:keyActives) ", nativeQuery = true)
    List<String> checkDuplicatedKeyActives(@Param(value = "keyActives") List<String> keyActives);
}