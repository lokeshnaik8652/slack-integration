package com.sampleProject.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sampleProject.demo.Entity.CommitEntity;

public interface CommitRepository extends JpaRepository<CommitEntity, Long> {
}