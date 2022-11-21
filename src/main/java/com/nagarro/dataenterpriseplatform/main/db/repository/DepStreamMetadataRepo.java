package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepStreamMetadataEntity;

@Repository
public interface DepStreamMetadataRepo extends JpaRepository<DepStreamMetadataEntity, String> {

    @Query(value = "select * from dep_stream_metadata where client_name =?1", nativeQuery = true)
    List<DepStreamMetadataEntity> findByClientName(String clientName);

    @Query(value = "select * from dep_stream_metadata where client_name =?1 and stream_name =?2", nativeQuery = true)
    DepStreamMetadataEntity findByClient_nameAndStream_name(String clientName, String streamName);

    @Query(value = "select * from dep_stream_metadata where stream_name =?1", nativeQuery = true)
    DepStreamMetadataEntity findByStream_name(String streamName);
}
