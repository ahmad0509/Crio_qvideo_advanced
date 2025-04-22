package com.crio.qvideorentaladvanced.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crio.qvideorentaladvanced.Entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByAvailableTrue();
}