package com.armirene.empleados.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.armirene.empleados.infrastructure.entity.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer> {

}
